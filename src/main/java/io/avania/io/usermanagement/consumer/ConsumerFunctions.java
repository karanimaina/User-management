package io.avania.io.usermanagement.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.avania.io.usermanagement.model.SystemUser;
import io.avania.io.usermanagement.repository.SystemUserRepository;
import io.avania.io.usermanagement.service.impl.NotificationService;
import io.avania.io.usermanagement.util.UtilFunctions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ConsumerFunctions {
    public final SystemUserRepository systemUserRepository;
    private final Gson gson;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

@Bean
@SuppressWarnings({"UnstableApiUsage"})
    public Consumer<String> userCreation() {
        return user -> {
            JsonObject userMap = gson.fromJson(user, JsonObject.class);
            long userId = userMap.get("itemId").getAsLong();
            String decision = userMap.get("decision").getAsString();
            String password = UtilFunctions.generate8CharactersComplexPassword();
            SystemUser systemUser = systemUserRepository.findTopByItemId(userId)
                    .orElse(null);
            if (systemUser == null)
                return;
            systemUser.setStatus(decision);
            systemUser.setPassword(passwordEncoder.encode(password));
            systemUserRepository.save(systemUser);
            notificationService.sendEmailNotificationMessage(systemUser.getEmail(), "Account Creation", "Your account has been created successfully. Your password is " + password + ". Please change your password on first login.");

        };
    }

}
