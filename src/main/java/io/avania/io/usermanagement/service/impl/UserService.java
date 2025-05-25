package io.avania.io.usermanagement.service.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.avania.io.usermanagement.constant.SystemRoles;
import io.avania.io.usermanagement.constants.MessageType;
import io.avania.io.usermanagement.constants.SystemProcess;
import io.avania.io.usermanagement.model.*;
import io.avania.io.usermanagement.repository.*;
import io.avania.io.usermanagement.service.IUserInterface;
import io.avania.io.usermanagement.serviceConfig.RedisStoreRepository;
import io.avania.io.usermanagement.util.UtilFunctions;
import io.avania.io.usermanagement.workflowService.WorkFlowFilter;
import io.avania.io.usermanagement.wrapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author David C Makuba
 * @created 25/01/2023
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserInterface {
    private final SystemUserRepository systemUserRepository;
    private final Gson gson;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRolesRepository;
    private final RoleRepository roleRepository;
    private final MessageTemplateRepository messageTemplateRepository;
    private final RedisStoreRepository redisStoreRepository;

    private  static final String PROFILE_NOT_FOUND = "Profile not found";
    private static final String  PENDING_APPROVAL = "PENDING_APPROVAL";
    /**
     * Create System Admin
     *
     * @param createUserWrapper UserWrapper
     * @return Universal response of success if user is saved
     */
//    @WorkFlowFilter(processName = SystemProcess.CREATE_ADMIN)
    @Override
    public Mono<UniversalResponse> createSystemUser(CreateUserWrapper createUserWrapper) {
        return Mono.fromCallable(() -> {
            if (systemUserRepository.findTopByEmail(createUserWrapper.getEmail()).isPresent()) {
                return UniversalResponse.builder().status(400)
                        .message("User already exists by email " + createUserWrapper.getEmail()).build();
            }
            Profile profile = profileRepository.findByIdAndSoftDeleteFalse(createUserWrapper.getProfileId())
                    .orElse(null);
            if (profile == null) {
                return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
            }
            Long itemUniqueId= System.currentTimeMillis();

            SystemUser.SystemUserBuilder usersBuilder = SystemUser.builder()
                    .firstName(createUserWrapper.getFirstName().toUpperCase())
                    .itemId(itemUniqueId)
                    .lastName(createUserWrapper.getLastName().toUpperCase())
                    .email(createUserWrapper.getEmail().toLowerCase())
                    .phoneNumber(createUserWrapper.getPhoneNumber())
                    .profile(profile)
                    .status(PENDING_APPROVAL)
                    .employeeNumber(createUserWrapper.getEmployeeNumber())
                    .firstTimeLogin(true);
            SystemUser users = usersBuilder.build();
            systemUserRepository.save(users);

           WorkflowCreationWrapper workflowCreationWrapper = WorkflowCreationWrapper.builder()
                   .itemId(itemUniqueId)
                   .uniqueId(UUID.randomUUID().toString())
                   .process(SystemRoles.CREATE_USER.getProcessName())
                   .stagingDetails(users)
                   .onCompletionTopic(SystemRoles.CREATE_USER.getOnCompletionTopic())
                   .nextStepTopic(SystemRoles.CREATE_USER.getNextStepTopic())
                    .build();
           String bodyToWorkFlowEngine = gson.toJson(workflowCreationWrapper);
           notificationService.esbCreateItemPublisher(bodyToWorkFlowEngine);
            return UniversalResponse.builder().status(200).message("System admin created successfully pending approval").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    /**
     * @param messageType Message type that will be loaded
     * @return Configured message Template or Default Template if no template is found
     */
    public MessageTemplate getMessageTemplateByType(MessageType messageType) {
        return messageTemplateRepository.findByMessageTypeAndActiveTrueAndSoftDeleteFalse(messageType)
                .orElse(messageTemplateRepository.findByMessageTypeAndDefaultTemplateTrueAndSoftDeleteFalse(messageType));
    }


    @Override
    public Mono<Profile> getUserProfile(String username) {
        return Mono.fromCallable(() -> {
            SystemUser systemUser = systemUserRepository.findTopByEmail(username)
                    .orElse(null);
            if (systemUser == null)
                throw new IllegalStateException("User does not have a profile");

            return systemUser.getProfile();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<SystemUser> getSystemUserByUsername(String username) {
        return Mono.fromCallable(() -> {
            SystemUser systemUser = systemUserRepository.findTopByEmail(username)
                    .orElse(null);
            if (systemUser == null)
                throw new IllegalStateException("User does not exist");

            return systemUser;
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> assignCountryProfile(AssignProfileWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            String profileName = String.format("%s_%s_%s", "COUNTRY", "ADMIN", commonWrapper.getCountryCode().toUpperCase().substring(0, 2));

            Optional<Profile> profile = profileRepository.findByNameAndSoftDeleteFalse(profileName);
            Profile userProfile = profile.orElse(null);
            if (profile.isEmpty()) {
                userProfile = profileRepository.save(Profile.builder()
                        .name(profileName)
                        .remarks("Manager for country: " + commonWrapper.getCountryCode().toUpperCase())
                        .build());
            }
            SystemUser systemUser = systemUserRepository.findTopByEmail(commonWrapper.getEmail())
                    .orElse(null);
            if (systemUser == null) {
                return UniversalResponse.builder().status(404).message("User not found").build();
            }
            systemUser.setProfile(userProfile);
            String password = UtilFunctions.generate8CharactersComplexPassword();
            String message = getMessageTemplateByType(MessageType.WELCOME_ADMIN_MESSAGE).getMessage().replace("@name", systemUser.getFirstName())
                    .replace("@password", password);
            systemUser.setPassword(passwordEncoder.encode(password));
            systemUserRepository.save(systemUser);
            Mono.fromRunnable(() -> {
                notificationService.sendEmailNotificationMessage(message, systemUser.getEmail(), "ACCOUNT REGISTRATION");
            }).subscribeOn(Schedulers.boundedElastic()).subscribe();
            return UniversalResponse.builder()
                    .status(200)
                    .message("User created successfully")
                    .build();
        }).publishOn(Schedulers.boundedElastic())
                ;
    }

    @Override
    public Mono<UniversalResponse> fetchRoles(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
//            Pageable pageable = PageRequest.of(commonWrapper.getPage(), commonWrapper.getSize());
            List<Role> roles = roleRepository.findAllBySoftDeleteFalse();
            Map<String, List<Role>> rolesMap = roles.stream()
                    .collect(Collectors.groupingBy(Role::getModuleName));
            return UniversalResponse.builder().status(200).message("Roles fetched successfully")
                    .data(rolesMap).totalItems((int) roles.size()).build();

        }).publishOn(Schedulers.boundedElastic());
    }

    //    @WorkFlowFilter(processName = SystemProcess.UPDATE_ADMIN)
    @Override
    public Mono<UniversalResponse> updateUser(UpdateUserWrapper updateUserWrapper) {
        return Mono.fromCallable(() -> {

            SystemUser systemUser = systemUserRepository
                    .findTopById(updateUserWrapper.id()).orElse(null);
            if (systemUser == null) {
                return UniversalResponse.builder().status(400).message("User not found").build();
            }
            if (updateUserWrapper.firstName() != null) {
                systemUser.setFirstName(updateUserWrapper.firstName());
            }
            if (updateUserWrapper.lastName() != null) {
                systemUser.setLastName(updateUserWrapper.lastName());
            }
            if (updateUserWrapper.profileId() != null) {
                Profile profile = profileRepository.findByIdAndSoftDeleteFalse(updateUserWrapper.profileId())
                        .orElse(null);
                if (profile == null) {
                    return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
                }
                systemUser.setProfile(profile);
            }
            systemUserRepository.save(systemUser);
            return UniversalResponse.builder().status(200).message("User updated successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> resetUser(ResetUserPassword resetUserPassword) {
        return Mono.fromCallable(() -> {

            SystemUser systemUser = systemUserRepository
                    .findTopById(resetUserPassword.getUserId()).orElse(null);
            if (systemUser == null) {
                return UniversalResponse.builder().status(400).message("User not found").build();
            }
            String newPassword = UtilFunctions.generate8CharactersComplexPassword();
            String message = getMessageTemplateByType(MessageType.RESET_ADMIN_MESSAGE).getMessage()
                    .replace("@name", systemUser.getFirstName())
                    .replace("@password", newPassword);
            systemUser.setPassword(passwordEncoder.encode(newPassword));
            systemUser.setPasswordResetAt(new Date());
            systemUserRepository.save(systemUser);
            notificationService.sendEmailNotificationMessage(message, systemUser.getEmail(), "ACCOUNT RESET");
            return UniversalResponse.builder().status(200).message("User reset successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> updateUserPassword(UpdatePasswordWrapper passwordWrapper, Authentication authentication) {
        return Mono.fromCallable(() -> {
            SystemUser systemUser = systemUserRepository.findTopByEmail(authentication.getName())
                    .orElse(null);
            if (systemUser == null) {
                return UniversalResponse.builder().status(400).message("User not found").build();
            }
            if (!passwordEncoder.matches(passwordWrapper.oldPassword(), systemUser.getPassword())) {
                if (systemUser.getResetPasswordLoginCount() > 3) {
                    systemUser.setBlockedBy("SYSTEM");
                    systemUser.setBlockedRemarks("Wrong old password exceeding 3 times ");
                    systemUser.setBlocked(true);
                    systemUserRepository.save(systemUser);
                }
                return UniversalResponse.builder().status(400).message("Invalid old pin").build();
            }
            systemUser.setPassword(passwordEncoder.encode(passwordWrapper.newPassword()));
            systemUser.setPasswordResetAt(new Date());
            systemUser.setFirstTimeLogin(false);
            systemUser.setResetPasswordLoginCount(0);
            systemUser.setBlocked(false);
            systemUser.setLoginAttempts(0);
            systemUserRepository.save(systemUser);
            return UniversalResponse.builder().status(200).message("Password update successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getUserById(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            SystemUser systemUser;
            if (commonWrapper.getUsername() != null && !commonWrapper.getUsername().isEmpty()) {
                systemUser = systemUserRepository.findTopByEmail(commonWrapper.getUsername())
                        .orElse(null);
            } else {
                systemUser = systemUserRepository.findTopById(commonWrapper.getId())
                        .orElse(null);
            }

            if (systemUser == null) {
                return UniversalResponse.builder().status(400).message("User not found")
                        .build();
            }
            return UniversalResponse.builder().status(200).message("User information retrieved successfully")
                    .data(systemUser).build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getAllUsers(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            if (commonWrapper.getUsername() != null && !commonWrapper.getUsername().isEmpty()) {
                SystemUser systemUser = systemUserRepository.findTopByEmail(commonWrapper.getUsername())
                        .orElse(null);
                if (systemUser == null) {
                    return UniversalResponse.builder().status(400).message("User not found").build();
                }
                return UniversalResponse.builder().status(200).message("System administrators").data(List.of(systemUser))
                        .totalItems(1).build();
            }
            Pageable pageable = PageRequest.of(commonWrapper.getPage(), commonWrapper.getSize());
            Page<SystemUser> systemUsers = switch (commonWrapper.getFilter()) {
                case "all" -> systemUserRepository.findAll(pageable);
                case "active" -> systemUserRepository.findAllByBlockedFalse(pageable);
                case "inactive" -> systemUserRepository.findAllByBlockedTrue(pageable);
                default -> Page.empty();
            };

            return UniversalResponse.builder().status(200).message("System administrators").data(systemUsers.toList())
                    .totalItems((int) systemUsers.getTotalElements()).build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> findWorkflowUsers(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            Pageable pageable = PageRequest.of(0, 10);
            if (commonWrapper.getParam() == null || commonWrapper.getParam().isEmpty()) {
                return UniversalResponse.builder().status(400).message("Invalid search parameter").build();
            }
            List<SystemUser> systemUsers;
            switch (commonWrapper.getParam()) {
                case "user":
                    Optional<SystemUser> systemUser = systemUserRepository.findById(commonWrapper.getId());
                    if (systemUser.isEmpty()) {
                        return UniversalResponse.builder().status(400).message("User not found").build();
                    }
                    systemUsers = List.of(systemUser.get());
                    return UniversalResponse.builder().status(200).message("System administrators").data(systemUsers)
                            .totalItems(systemUsers.size()).build();
                case "profile":
                    Profile profile = profileRepository.findById(commonWrapper.getId()).orElse(null);
                    if (profile == null) {
                        return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
                    }
                    Page<SystemUser> systemProfileUsers = systemUserRepository.findAllByProfileIdAndSoftDeleteFalseAndBlockedFalse(pageable, profile.getId());
                    return UniversalResponse.builder().status(200).message("System administrators").data(systemProfileUsers.toList())
                            .totalItems((int) systemProfileUsers.getTotalElements()).build();
                case "role":
                    ProfileRoles profileRoles = profileRolesRepository.findTopByRoleId(commonWrapper.getId());
                    if (profileRoles == null) {
                        return UniversalResponse.builder().status(400).message("Role not found").build();
                    }
                    Page<SystemUser> systemRoleUsers = systemUserRepository.findAllByProfileIdAndSoftDeleteFalseAndBlockedFalse(pageable, profileRoles.getProfile().getId());
                    return UniversalResponse.builder().status(200).message("System administrators").data(systemRoleUsers.toList())
                            .totalItems((int) systemRoleUsers.getTotalElements()).build();
            }
            return UniversalResponse.builder().status(400).message("Invalid search parameter").build();

        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.DELETE_ADMIN)
    @Override
    public Mono<UniversalResponse> deleteUser(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            SystemUser systemUser = systemUserRepository.findTopById(commonWrapper.getId())
                    .orElse(null);
            if (systemUser == null) {
                return UniversalResponse.builder().status(400).message("system administrator not found").build();
            }
            systemUser.setBlocked(true);
            systemUser.setSoftDelete(true);
            systemUser.setRemarks(commonWrapper.getRemarks());
            systemUserRepository.save(systemUser);
            redisStoreRepository.removeUserSession(systemUser.getEmail());
            return UniversalResponse.builder().status(200).message("System administrator deleted successful")
                    .build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.DISABLE_ADMIN)
    @Override
    public Mono<UniversalResponse> disableUser(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            SystemUser systemUser = systemUserRepository.findById(commonWrapper.getId())
                    .orElse(null);
            if (systemUser == null) {
                return UniversalResponse.builder().status(400).message("User not found").build();
            }
            systemUser.setBlocked(true);
            systemUser.setRemarks(commonWrapper.getRemarks());
            systemUserRepository.save(systemUser);
            redisStoreRepository.removeUserSession(systemUser.getEmail());
            return UniversalResponse.builder().status(200).message("System administrator disabled successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.ENABLE_ADMIN)
    @Override
    public Mono<UniversalResponse> enableUser(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            SystemUser systemUser = systemUserRepository.findTopById(commonWrapper.getId())
                    .orElse(null);
            if (systemUser == null) {
                return UniversalResponse.builder().status(400).message("User not found").build();
            }
            systemUser.setBlocked(false);
            systemUser.setRemarks(commonWrapper.getRemarks());
            systemUserRepository.save(systemUser);
            return UniversalResponse.builder().status(200).message("System administrator enabled successful")
                    .build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.CREATE_PROFILE)
    @Override
    public Mono<UniversalResponse> createProfile(CreateProfileWrapper profileWrapper) {
        return Mono.fromCallable(() -> {
            if (profileRepository.findByNameAndSoftDeleteFalse(profileWrapper.name()).isPresent()) {
                return UniversalResponse.builder().status(400).message("Profile already exists").build();
            }
            Profile profile = Profile.builder()
                    .name(profileWrapper.name().toUpperCase())
                    .remarks(profileWrapper.remarks()).build();
            profileRepository.save(profile);
            return UniversalResponse.builder().status(200).message("Profile saved successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.UPDATE_PROFILE)
    @Override
    public Mono<UniversalResponse> updateProfile(CommonProfileWrapper profileWrapper) {
        return Mono.fromCallable(() -> {
            Profile profile = profileRepository.findByIdAndSoftDeleteFalse(profileWrapper.id())
                    .orElse(null);
            if (profile == null) {
                return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
            }
            profile.setRemarks(profileWrapper.remarks());
            profileRepository.save(profile);
            return UniversalResponse.builder().status(200).message("Profile updated successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.DELETE_PROFILE)
    @Override
    public Mono<UniversalResponse> archiveProfile(CommonProfileWrapper commonProfileWrapper) {
        return Mono.fromCallable(() -> {
            Profile profile = profileRepository.findByIdAndSoftDeleteFalse(commonProfileWrapper.id())
                    .orElse(null);
            if (profile == null) {
                return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
            }
            if (systemUserRepository.existsByProfileId(commonProfileWrapper.id())) {
                return UniversalResponse.builder().status(400)
                        .message("Cannot archive profile, profile is assigned to users'").build();
            }
            profile.setSoftDelete(true);
            profileRepository.save(profile);
            return UniversalResponse.builder().status(200).message("Profile archived successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getProfile(CommonProfileWrapper commonProfileWrapper) {
        return Mono.fromCallable(() -> {
            Profile profile = profileRepository.findByIdAndSoftDeleteFalse(commonProfileWrapper.id())
                    .orElse(null);
            if (profile == null) {
                return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
            }
            List<CommonRoleWrapper> profileRolesList = profileRolesRepository
                    .findAllByProfileIdAndSoftDeleteFalse(profile.getId()).stream()
                    .map(profileRoles -> CommonRoleWrapper.builder()
                            .roleId(profileRoles.getRole().getId())
                            .name(profileRoles.getRole().getName())
                            .systemRole(profileRoles.getRole().isSystemRole())
                            .build()).toList();

            return UniversalResponse.builder().status(200)
                    .message("profile info")
                    .data(Map.of("profile", profile, "roles", profileRolesList)).build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getProfiles(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            Pageable pageable = PageRequest.of(commonWrapper.getPage(), commonWrapper.getSize());
            Page<Profile> profilesPage = profileRepository.findAllBySoftDeleteFalse(pageable);
            return UniversalResponse.builder().status(200).message("Profile list")
                    .data(profilesPage.toList()).totalItems(profilesPage.getNumberOfElements()).build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getAllRoles(CommonWrapper commonWrapper) {
        return Mono.fromCallable(() -> {
            Pageable pageable = PageRequest.of(commonWrapper.getPage(), commonWrapper.getSize());
            Page<Role> roles = roleRepository.findAllBySoftDeleteFalse(pageable);
            return UniversalResponse.builder().status(200).message("All roles").data(roles.toList())
                    .totalItems((int) roles.getTotalElements())
                    .build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.DELETE_ROLE)
    @Override
    public Mono<UniversalResponse> deleteRole(CommonRoleWrapper roleWrapper) {
        return Mono.fromCallable(() -> {
            Role role = roleRepository.findByIdAndSoftDeleteFalse(roleWrapper.getRoleId())
                    .orElse(null);
            if (role == null)
                return UniversalResponse.builder().status(400).message("Role not found").build();
            if (role.isSystemRole()) {
                return UniversalResponse.builder().status(400).message("Cannot delete a system role").build();
            }
            if (profileRolesRepository.existsByRoleIdAndSoftDeleteFalse(role.getId())) {
                return UniversalResponse.builder().status(400).message("Cannot delete role, role is assigned to a profile").build();
            }
            role.setSoftDelete(true);
            roleRepository.save(role);
            return UniversalResponse.builder().status(200).message("Role deleted successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.CREATE_ROLE)
    @Override
    public Mono<UniversalResponse> addRole(CommonRoleWrapper roleWrapper) {
        return Mono.fromCallable(() -> {
            if (roleRepository.findByName(roleWrapper.getName()).isPresent()) {
                return UniversalResponse.builder().status(400).message("Role by name " + roleWrapper.getName()
                        + " already exists").build();
            }
            Role role = Role.builder()
                    .isSystemRole(false)
                    .remarks(roleWrapper.getRemarks())
                    .name(UtilFunctions.capitalizeAndRemoveSpaces().apply(roleWrapper.getName().toUpperCase()))
                    .build();
            roleRepository.save(role);
            return UniversalResponse.builder().status(200).message("Role saved successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.UPDATE_PROFILE)
    @Override
    public Mono<UniversalResponse> addRolesToProfile(CommonRoleWrapper commonRoleWrapper) {
        return Mono.fromCallable(() -> {
            Profile profile = profileRepository.findByIdAndSoftDeleteFalse(commonRoleWrapper.getProfileId())
                    .orElse(null);
            if (profile == null) {
                return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
            }
            StringJoiner invalidRoles = new StringJoiner(",", "[", "]");
            commonRoleWrapper.getRoleList()
                    .forEach(roleId -> {
                        Role role = roleRepository.findByIdAndSoftDeleteFalse(roleId)
                                .orElse(null);
                        if (role == null) {
                            invalidRoles.add(String.format("Role by id %s does not exists", roleId));
                        } else if (profileRolesRepository.existsByProfileIdAndRoleIdAndSoftDeleteFalse(profile.getId(), roleId)) {
                            invalidRoles.add(String.format("Role by id %s already exists in profile", roleId));
                        } else {
                            ProfileRoles profileRoles = ProfileRoles.builder()
                                    .profile(profile)
                                    .role(role)
                                    .moduleName(role.getModuleName())
                                    .build();
                            profileRolesRepository.save(profileRoles);
                        }
                    });
            if (invalidRoles.length() > 3) {
                return UniversalResponse.builder().status(200)
                        .message("Added roles but found errors:  " + invalidRoles).build();
            }
            return UniversalResponse.builder().status(200).message("Added roles to profile successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.UPDATE_PROFILE)
    @Override
    public Mono<UniversalResponse> removeRolesFromProfile(CommonRoleWrapper commonRoleWrapper) {
        return Mono.fromCallable(() -> {
            Profile profile = profileRepository.findByIdAndSoftDeleteFalse(commonRoleWrapper.getProfileId())
                    .orElse(null);
            if (profile == null) {
                return UniversalResponse.builder().status(400).message(PROFILE_NOT_FOUND).build();
            }
            StringJoiner invalidRoles = new StringJoiner(",", "[", "]");
            commonRoleWrapper.getRoleList()
                    .forEach(roleId -> {
                        ProfileRoles profileRoles = profileRolesRepository.findByProfileIdAndRoleIdAndSoftDeleteFalse(profile.getId(), roleId)
                                .orElse(null);
                        if (profileRoles == null) {
                            invalidRoles.add(String.format("Role by id %s not found in profile", roleId));
                        } else {
                            profileRoles.setSoftDelete(true);
                            profileRolesRepository.save(profileRoles);
                        }
                    });
            if (invalidRoles.length() > 3) {
                return UniversalResponse.builder().status(200)
                        .message("Removed roles but found errors:  " + invalidRoles).build();
            }

            return UniversalResponse.builder().status(200).message("Removed roles successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @WorkFlowFilter(processName = SystemProcess.ADD_MESSAGE_TEMPLATE)
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public Mono<UniversalResponse> addMessageTemplate(MessageTemplate messageTemplate) {
        return Mono.fromCallable(() -> {
            MessageTemplate oldMessageTemplate = messageTemplateRepository
                    .findByMessageTypeAndActiveTrueAndSoftDeleteFalse(messageTemplate.getMessageType())
                    .orElse(messageTemplateRepository
                            .findByMessageTypeAndDefaultTemplateTrueAndSoftDeleteFalse(messageTemplate.getMessageType()));
            if (oldMessageTemplate == null) {
                return UniversalResponse.builder().status(400).message("Invalid Message type passed").build();
            }
            String newMessage = messageTemplate.getMessage();
            List<String> params =
                    gson.fromJson(oldMessageTemplate.getBaseParams(), new TypeToken<List<String>>() {
                    }.getType());
            StringJoiner stringJoiner = new StringJoiner(",");
            params.forEach(param -> {
                if (!newMessage.contains(param)) {
                    stringJoiner.add(param);
                }
            });

            if (stringJoiner.length() > 1) {
                return UniversalResponse.builder().status(400).message("Missing following template params " + stringJoiner)
                        .build();
            }
            MessageTemplate newTemplate = MessageTemplate.builder()
                    .message(messageTemplate.getMessage())
                    .baseParams(oldMessageTemplate.getBaseParams())
                    .active(true)
                    .messageType(messageTemplate.getMessageType())
                    .build();
            oldMessageTemplate.setActive(false);
            messageTemplateRepository.saveAll(List.of(newTemplate, oldMessageTemplate));
            return UniversalResponse.builder().status(200).message("Message template updated successfully").build();
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UniversalResponse> getMessageTypes() {
        return Mono.just(UniversalResponse.builder().status(200).message("Message types")
                .data(MessageType.values()).build()).cache(Duration.ofMinutes(3));
    }

    @Override
    public Mono<UniversalResponse> getMessageTemplates() {
        return Mono.fromCallable(() -> {
            List<MessageTemplate> messageTemplates = new ArrayList<>();
            Arrays.asList(MessageType.values()).forEach(type -> {
                MessageTemplate messageTemplate = getMessageTemplateByType(type);
                if (messageTemplate != null) {
                    messageTemplates.add(messageTemplate);
                }
            });
            return UniversalResponse.builder().status(200).message("Message templates")
                    .data(messageTemplates).build();
        }).cache(Duration.ofMinutes(3));
    }

}
