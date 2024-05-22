package io.avania.io.usermanagement.bootstrap;

import com.eclectics.io.usermodule.constants.MessageType;
import com.eclectics.io.usermodule.constants.SystemRoles;
import com.eclectics.io.usermodule.model.*;
import com.eclectics.io.usermodule.repository.*;
import com.eclectics.io.usermodule.service.impl.NotificationService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author David C Makuba
 * @created 02/02/2023
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitOperations {
    @Value("${default.admin.email}")
    private String adminEmail;

    private final MessageTemplateRepository messageTemplateRepository;
    private final Gson gson;
    private final SystemUserRepository systemUserRepository;
    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final RoleRepository roleRepository;
    private static final String SYSTEM_ADMIN_PROFILE_NAME = "SUPER-ADMIN";
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;


    @PostConstruct
    public void init() {
        Mono.fromRunnable (() -> {
                    createSystemRoles();
                    saveDefaultMessageTemplates ();
                    createSystemSuperProfile ();
                    saveDefaultUser();
                    addAllRolesToSuperAdministratorProfile();
                }).subscribeOn (Schedulers.boundedElastic ())
                .subscribe (null, err -> log.error ("An error occurred during initialization e=> {}",
                        err.getLocalizedMessage ()), null);
    }

    private void saveDefaultMessageTemplates(){
        if(messageTemplateRepository.findByMessageTypeAndDefaultTemplateTrueAndSoftDeleteFalse (MessageType.WELCOME_ADMIN_MESSAGE)==null) {
            MessageTemplate welcomeMessageTemplate = MessageTemplate.builder ()
                    .message (" Dear @name, you have been added as an ECLECTICS ESB Administrator" +
                            ".Use your email and password @password to login. %n Kind Regards %n ATLASMARA ESB TEAM")
                    .baseParams (gson.toJson (List.of ("@name", "@password")))
                    .active (true)
                    .defaultTemplate (true)
                    .messageType (MessageType.WELCOME_ADMIN_MESSAGE)
                    .build ();
            messageTemplateRepository.save(welcomeMessageTemplate);
        }
        if(messageTemplateRepository.findByMessageTypeAndDefaultTemplateTrueAndSoftDeleteFalse (MessageType.RESET_ADMIN_MESSAGE)==null) {
            MessageTemplate resetAdminMessageTemplate = MessageTemplate.builder ()
                    .message ("Dear @name, your account has been reset. User new password @password to login. %n Kind Regards %n ATLASMARA ESB TEAM ")
                    .baseParams (gson.toJson (List.of ("@name", "@password")))
                    .active (true)
                    .defaultTemplate (true)
                    .messageType (MessageType.RESET_ADMIN_MESSAGE)
                    .build ();

            messageTemplateRepository.save (resetAdminMessageTemplate);
        }
    }

    public void createSystemRoles(){
        Arrays.asList (SystemRoles.values ())
                .forEach (role-> {
                    if(roleRepository.findAllByName (role.name ()).isEmpty ()){
                        Role systemRole= new Role ();
                        systemRole.setSystemRole (true);
                        systemRole.setSoftDelete (false);
                        systemRole.setRemarks ("System Role");
                        systemRole.setName (role.name ());
                        systemRole.setOnCompletionTopic(role.getOnCompletionTopic ());
                        systemRole.setNextStepTopic (role.getNextStepTopic ());
                        systemRole.setWorkflowEnabled(false);
                        systemRole.setModuleName(role.getModuleName());
                        systemRole.setProcessName(role.getProcessName());
                        roleRepository.save (systemRole);
                    }
                });
    }

    public void createSystemSuperProfile() {
        if (profileRepository.findByNameAndSoftDeleteFalse (SYSTEM_ADMIN_PROFILE_NAME).isEmpty ()) {

            Profile profile = profileRepository.save (Profile
                    .builder ()
                    .name (SYSTEM_ADMIN_PROFILE_NAME)
                    .remarks ("SUPER ADMIN PROFILE")
                    .build ());

            roleRepository.findAllBySoftDeleteFalse ()
                    .stream ()
                    .map (role -> roleProfileMapper ().apply (profile, role))
                    .forEach (profileRoleRepository::save);
        }
    }

    BiFunction<Profile, Role, com.eclectics.io.usermodule.model.ProfileRoles> roleProfileMapper() {
        return (profile, role) -> {
          ProfileRoles profileRoles = ProfileRoles.builder ()
                    .role (role)
                    .profile (profile)
                    .build ();
            return profileRoleRepository.save (profileRoles);
        };
    }

    public void saveDefaultUser() {
        if (systemUserRepository.findTopByEmail (adminEmail).isEmpty ()) {
            Profile profile = profileRepository.findByNameAndSoftDeleteFalse (SYSTEM_ADMIN_PROFILE_NAME)
                    .orElse (null);
            if (profile != null) {
                String password = "12345678";
                SystemUser systemUser = SystemUser.builder ()
                        .email (adminEmail)
                        .profile (profile)
                        .firstName ("ESB-SYSTEM-ADMIN")
                        .lastName ("ESB-SYSTEM-ADMIN")
                        .password (passwordEncoder.encode (password))
                        .blocked (false)
                        .firstTimeLogin (true)
                        .loginAttempts (0)
                        .build ();
                String message = String.format ("Dear ESB SUPER ADMINISTRATOR , Use your email and first time pin  %s to login." +
                        " %n Kind Regards %n ECLECTICS ESB TEAM", password);
                systemUserRepository.save (systemUser);
                notificationService.sendEmailNotificationMessage (message, adminEmail, "SUPER ADMIN ACCOUNT CREDENTIALS");
            }
        }
    }

    public void addAllRolesToSuperAdministratorProfile() {
        Mono.fromRunnable (() -> {
            Profile profile = profileRepository.findByNameAndSoftDeleteFalse (SYSTEM_ADMIN_PROFILE_NAME)
                    .orElse (null);
            if (profile == null) {
                return;
            }
            roleRepository.findAllBySoftDeleteFalse ()
                    .forEach (role -> {
                        List<ProfileRoles> profileRoles = profileRoleRepository.findAllByProfileIdAndRoleId (profile.getId (), role.getId ());
                        if (profileRoles.isEmpty()) {
                            ProfileRoles profileRoles1 = ProfileRoles.builder ()
                                    .profile (profile)
                                    .role (role)
                                    .build ();
                            profileRoleRepository.save (profileRoles1);

                        }
                    });
        }).subscribeOn (Schedulers.boundedElastic ()).subscribe ();
    }
}
