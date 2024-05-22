package com.avania.io.usermanagement.bootstrap;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class InitOperations {
private String adminEmail;
private final MessageTemplateRepository messageTemplateRepository;
private Gson gson;
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

private void saveDefaultMessageTemplates () {
	if (messageTemplateRepository.findByMessageTypeAndDefaultTempleteTrueAndSoftDeleteFalse(MessageType.WELCOME_ADMIN_MESSAGE)==null){
		MessageTemplate welcomeMessageTemplate = MessageTemplate.builder ()
				                                         .message (" Dear @name, you have been added as an ECLECTICS ESB Administrator" +
						                                                   ".Use your email and password @password to login. %n Kind Regards %n ECLECTICS ESB TEAM")
				                                         .baseParams (gson.toJson (List.of ("@name", "@password")))
				                                         .active (true)
				                                         .defaultTemplate (true)
				                                         .messageType (MessageType.WELCOME_ADMIN_MESSAGE)
				                                         .build ();
		messageTemplateRepository.save(welcomeMessageTemplate);
	}
	
}
public void createSystemRoles(){

}
}
