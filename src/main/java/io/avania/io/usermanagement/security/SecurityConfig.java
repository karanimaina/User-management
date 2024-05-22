package io.avania.io.usermanagement.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author David C Makuba
 * @created 12/02/2023
 **/
@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ReactiveBearerSecurityFilter reactiveBearerSecurityFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder ();
    }

    @Bean
    SecurityWebFilterChain bearerWebFilterChain(ServerHttpSecurity http) {
        return http
                .httpBasic ().disable ()
                .securityMatcher(new BearerTokenMatcher())
                .addFilterAt(reactiveBearerSecurityFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .requestCache()
                .requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/",
//                        " /api/v1/admin/role/all-roles",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/actuator/**",
                        "/swagger-resources/**",
                        "/v3/api-docs",
                        "/v2/api-docs"
                )
                .permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .build();
    }

    public static class BearerTokenMatcher implements ServerWebExchangeMatcher {
        @Override
        public Mono<MatchResult> matches(ServerWebExchange exchange) {
            String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (auth != null && auth.startsWith("Bearer")) {
                return MatchResult.match();
            }
            return MatchResult.notMatch();
        }
    }
}

