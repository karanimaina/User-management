package io.avania.io.usermanagement.service.impl;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.avania.io.usermanagement.model.Profile;
import io.avania.io.usermanagement.model.SystemUser;
import io.avania.io.usermanagement.repository.*;
import io.avania.io.usermanagement.serviceConfig.RedisStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author David C Makuba
 * @created 09/02/2023
 **/
@RequiredArgsConstructor
@Service
public class RolesPublisher {
    private final ProfileRoleRepository profileRoleRepository;
    private final RedisStoreRepository redisStoreRepository;
    private final ProfileRepository profileRepository;
    private final SystemUserRepository systemUserRepository;
    private final Gson gson;

    @Bean
    @SuppressWarnings({"UnstableApiUsage"})
    public Consumer<String> publishRoles() {
        return userAuthInfo -> {
            JsonObject userMap = gson.fromJson(userAuthInfo, JsonObject.class);
            long userId = userMap.get("userId").getAsLong();
            String jti = userMap.get("jti").getAsString();
            long expiry = userMap.get("expiryTime").getAsLong();
            SystemUser systemUser = systemUserRepository.findTopById(userId)
                    .orElse(null);
            if (systemUser == null)
                return;

            List<String> userRoles = new java.util.ArrayList<>(profileRoleRepository.findAllByProfileIdAndSoftDeleteFalse(systemUser.getProfile().getId())
                    .stream().map(profileRoles -> profileRoles.getRole().getName()).toList());
//            is country admin
            Profile customProfile = profileRepository.findById(systemUser.getProfile().getId()).orElse(null);
            if (customProfile!= null && customProfile.getName().matches("COUNTRY_ADMIN_[A-Z]{2}")) {
                userRoles.add(customProfile.getName());

            }
            redisStoreRepository.saveAuthDetails(jti, userRoles, expiry)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();
        };
    }

}
