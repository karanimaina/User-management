package io.avania.io.usermanagement.serviceConfig;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;

/**
 * @author David C Makuba
 * @created 01/02/2023
 **/
@RequiredArgsConstructor
@Slf4j
@Repository
public class RedisStoreRepository {
    private static final String STORE_HASH_KEY = "ESB_USER_HASH";
    public static final String USERXHASH = "XS_ESB_USER_HASH:";
    public static  final String ROLES_HASH= "XS_ESB_ROLES:";

    private final Gson gson;
    private ReactiveHashOperations<String, String, String> hashOperations;
    private final ReactiveStringRedisTemplate redisOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisOperations.opsForHash ();
    }

    public Mono<Boolean> saveAuthDetails(String userHash, List<String> userRoles, long expiryTimeMinutes) {
        String userRolesString = gson.toJson (userRoles);
        return hashOperations.put (ROLES_HASH+userHash, STORE_HASH_KEY, userRolesString)
                .doOnError (throwable -> log.error ("Failed to save user credentials===> {}", throwable.getLocalizedMessage ()))
                .flatMap (bool -> redisOperations.expire (userHash, Duration.ofMinutes (expiryTimeMinutes)));
    }

    public void removeUserSession(String username) {
            hashOperations.delete (USERXHASH+username)
                    .subscribeOn (Schedulers.boundedElastic ()).subscribe ();
    }


}
