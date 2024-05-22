package io.avania.io.usermanagement.controller;

import com.eclectics.io.usermodule.service.IUserInterface;
import com.eclectics.io.usermodule.wrapper.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author David C Makuba
 * @created 07/02/2023
 **/
@RestController
@RequestMapping("/api/v1/admin/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final IUserInterface userInterface;
    @PostMapping("/create")
    @PreAuthorize ("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('CREATE_USER')")
    public Mono<ResponseEntity<UniversalResponse>> createAdministrator(@RequestBody CreateUserWrapper createUserWrapper){
        return userInterface.createSystemUser (createUserWrapper)
                .doOnError(err->{
                    log.error("Error: {}", err.getMessage());
                })
                .doOnNext(res->{
                    log.info("Response: {}", res);
                })
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_USER')")
    public Mono<ResponseEntity<UniversalResponse>> updateAdministrator(@RequestBody UpdateUserWrapper updateUserWrapper) {
        return userInterface.updateUser(updateUserWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/assign-country")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_USER')")
    public Mono<ResponseEntity<UniversalResponse>> assignCountryAdmin(@RequestBody AssignProfileWrapper updateUserWrapper) {
        return userInterface.assignCountryProfile(updateUserWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/reset")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('RESET_USER')")
    public Mono<ResponseEntity<UniversalResponse>> resetAdministrator(@Valid @RequestBody ResetUserPassword resetUserPassword) {
        return userInterface.resetUser(resetUserPassword)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/update/password")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') ")
    public Mono<ResponseEntity<UniversalResponse>> updateUserPassword(@RequestBody UpdatePasswordWrapper passwordWrapper, Authentication authentication) {
        return userInterface.updateUserPassword(passwordWrapper, authentication)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/id")
    @PreAuthorize("hasAnyRole('ESB-ADMIN', 'INTEGRATOR')")
    public Mono<ResponseEntity<UniversalResponse>> getUserById(@RequestBody CommonWrapper commonWrapper) {
        return userInterface.getUserById(commonWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/all")
    @PreAuthorize("hasAnyRole('ESB-ADMIN', 'INTEGRATOR')")
    public Mono<ResponseEntity<UniversalResponse>> getAllUsers(@RequestBody CommonWrapper commonWrapper) {
        return userInterface.getAllUsers(commonWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }
    @PostMapping("/users-in-role")
    @PreAuthorize("hasAnyRole('ESB-ADMIN', 'INTEGRATOR')")
    public Mono<ResponseEntity<UniversalResponse>> getUsersInRole(@RequestBody CommonWrapper commonWrapper) {
        return userInterface.findWorkflowUsers(commonWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('DELETE_USER')")
    public Mono<ResponseEntity<UniversalResponse>> deleteUser(@RequestBody CommonWrapper commonWrapper) {
        return userInterface.deleteUser(commonWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/block")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('DISABLE_USER')")
    public Mono<ResponseEntity<UniversalResponse>> disableUser(@RequestBody CommonWrapper commonWrapper) {
        return userInterface.disableUser(commonWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }

    @PostMapping("/unblock")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('ENABLE_USER')")
    public Mono<ResponseEntity<UniversalResponse>> enableUser(@RequestBody CommonWrapper commonWrapper) {
        return userInterface.enableUser(commonWrapper)
                .map(ResponseEntity::ok)
                .publishOn(Schedulers.boundedElastic());
    }
}
