package io.avania.io.usermanagement.security;

import com.eclectics.io.usermodule.exceptions.AuthException;
import com.eclectics.io.usermodule.wrapper.UniversalResponse;
import com.google.gson.Gson;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author David C Makuba
 * @created 21/02/2023
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveBearerSecurityFilter implements WebFilter {
    private final Gson gson;
    private final JwtUtil jwtUtil;
    private static final String BEARER = "Bearer ";
    private static final String INTERNAL_TOKEN_HEADER ="INTERNAL_TOKEN";
    private static final Function<String, Mono<String>> isolateBearerValue = authValue -> Mono.justOrEmpty (authValue.substring (BEARER.length ()));

    public static Mono<String> extract(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty (serverWebExchange.getRequest ()
                .getHeaders ()
                .getFirst (INTERNAL_TOKEN_HEADER));
    }

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        String requestPath = exchange.getRequest ().getPath ().toString ();
//        if (requestPath.contains(" /api/v1/admin/role/all-roles")){
//            return chain.filter(exchange);
//        }
        return Mono.justOrEmpty (exchange)
                .flatMap (ReactiveBearerSecurityFilter::extract)
                .flatMap (isolateBearerValue)
                .switchIfEmpty (Mono.defer (() -> Mono.error (new AuthException ("Invalid session"))))
                .flatMap (bearerToken -> jwtUtil.validateToken (bearerToken)
                                .flatMap (res->{
                                    if(Boolean.TRUE.equals(res)){
                                        try {
                                            Map<String,Object> decodedTokenResult= jwtUtil.decodeToken (bearerToken);
                                            List<SimpleGrantedAuthority> authorities= ((List<String>) decodedTokenResult.get ("authorities"))
                                                    .stream ()
                                                    .map(SimpleGrantedAuthority::new)
                                                    .toList ();
                                            Authentication authentication =
                                                    new UsernamePasswordAuthenticationToken (decodedTokenResult.get ("username"),
                                                            null, authorities);
                                            return chain.filter (exchange)
                                                    .contextWrite (ReactiveSecurityContextHolder.withAuthentication (authentication));
                                        } catch (ParseException e) {
                                            log.error ("Token parse exception",e);
                                            return Mono.error (new RuntimeException ("Could not pass JWT"));
                                        }
                                    }
                                    return Mono.error (new RuntimeException ("Invalid token"));
                                }))
                .onErrorResume (err-> {
                    log.error ("Error validating token",err);
                    DataBuffer bodyDataBuffer = exchange.getResponse ().bufferFactory ()
                            .wrap (gson.toJson (UniversalResponse.builder ()
                                    .status (400).message ("Invalid token.").build ()).getBytes ());
                    ServerHttpResponse response= exchange.getResponse ();
                    response.setStatusCode (HttpStatus.UNAUTHORIZED);
                    response.getHeaders ().setContentType (MediaType.APPLICATION_JSON);
                    return exchange.getResponse ().writeWith (Mono.just (bodyDataBuffer))
                            .flatMap (exc -> exchange.getResponse ().setComplete ());
                });
    }
}
