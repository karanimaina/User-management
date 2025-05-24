package io.avania.io.usermanagement.security;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.avania.io.usermanagement.exceptions.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.*;

/**
 * @author David C Makuba
 * @created 13/03/2023
 **/
@Configuration
@RequiredArgsConstructor
public class JwtUtil {
    private final KeysConfig keysConfig;

    public Mono<Boolean> validateToken(String bearerToken) {
        return Mono.fromCallable (() -> {
            RSASSAVerifier rsassaVerifier = new RSASSAVerifier (keysConfig.publicKey ());
            SignedJWT signedJWT = SignedJWT.parse (bearerToken);
            if (!signedJWT.verify (rsassaVerifier)) {
                throw new AuthException("Failed to verify token!");
            }
            return true;
        });
    }

    public Map<String,Object> decodeToken(String bearerToken) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse (bearerToken);
        Map<String,Object> decodeMap= new HashMap<> ();
        decodeMap.put ("username",signedJWT.getJWTClaimsSet ().getSubject ());
        List<String> authorities = new ArrayList<> (Arrays.asList (signedJWT.getJWTClaimsSet ()
                .getStringArrayClaim ("authorities")));
        Arrays.stream (signedJWT.getJWTClaimsSet ().getStringArrayClaim ("roles"))
                        .forEach (role-> authorities.add ("ROLE_"+role));
        decodeMap.put ("authorities", authorities);
        return decodeMap;
    }
}
