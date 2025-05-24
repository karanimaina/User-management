package io.avania.io.usermanagement.service;


import io.avania.io.usermanagement.constants.MessageType;
import io.avania.io.usermanagement.model.MessageTemplate;
import io.avania.io.usermanagement.model.Profile;
import io.avania.io.usermanagement.model.SystemUser;
import io.avania.io.usermanagement.wrapper.*;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * @author David C Makuba
 * @created 08/02/2023
 **/
public interface IUserInterface {
    Mono<UniversalResponse> createSystemUser(CreateUserWrapper createUserWrapper);
    Mono<UniversalResponse> updateUser(UpdateUserWrapper updateUserWrapper);
    Mono<UniversalResponse> resetUser(ResetUserPassword resetUserPassword);
    Mono<UniversalResponse> updateUserPassword(UpdatePasswordWrapper passwordWrapper, Authentication authentication);
    Mono<UniversalResponse> getUserById(CommonWrapper commonWrapper);
    Mono<UniversalResponse> getAllUsers(CommonWrapper commonWrapper);
    Mono<UniversalResponse> findWorkflowUsers(CommonWrapper commonWrapper);
    Mono<UniversalResponse> deleteUser(CommonWrapper commonWrapper);
    Mono<UniversalResponse> disableUser(CommonWrapper commonWrapper);
    Mono<UniversalResponse> enableUser(CommonWrapper commonWrapper);
    Mono<UniversalResponse> createProfile(CreateProfileWrapper profileWrapper);
    Mono<UniversalResponse> updateProfile(CommonProfileWrapper profileWrapper);
    Mono<UniversalResponse> archiveProfile(CommonProfileWrapper commonProfileWrapper);
    Mono<UniversalResponse> getProfile(CommonProfileWrapper commonProfileWrapper);
    Mono<UniversalResponse> getProfiles(CommonWrapper commonWrapper);
    Mono<UniversalResponse> getAllRoles(CommonWrapper commonWrapper);
    Mono<UniversalResponse> addRole(CommonRoleWrapper roleWrapper);
    Mono<UniversalResponse> deleteRole(CommonRoleWrapper roleWrapper);
    Mono<UniversalResponse> addRolesToProfile(CommonRoleWrapper commonRoleWrapper);
    Mono<UniversalResponse> removeRolesFromProfile(CommonRoleWrapper commonRoleWrapper);
    Mono<UniversalResponse> addMessageTemplate(MessageTemplate messageTemplate);
    Mono<UniversalResponse> getMessageTypes();
    Mono<UniversalResponse> getMessageTemplates();
    MessageTemplate getMessageTemplateByType(MessageType messageType);
    Mono<Profile> getUserProfile(String username);
    Mono<SystemUser> getSystemUserByUsername(String username);
    Mono<UniversalResponse> assignCountryProfile(AssignProfileWrapper commonWrapper);

    Mono<UniversalResponse> fetchRoles(CommonWrapper commonWrapper);
}
