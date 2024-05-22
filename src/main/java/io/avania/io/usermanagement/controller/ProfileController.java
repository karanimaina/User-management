package io.avania.io.usermanagement.controller;

import com.eclectics.io.usermodule.service.IUserInterface;
import com.eclectics.io.usermodule.wrapper.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author David C Makuba
 * @created 08/02/2023
 **/
@RestController
@RequestMapping("/api/v1/admin/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final IUserInterface userInterface;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('CREATE_PROFILE')")
    public Mono<ResponseEntity<UniversalResponse>> addProfile(@Valid @RequestBody CreateProfileWrapper createProfileWrapper){
        return userInterface.createProfile (createProfileWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_PROFILE')")
    public Mono<ResponseEntity<UniversalResponse>> editProfile(@Valid @RequestBody CommonProfileWrapper profileWrapper){
        return userInterface.updateProfile (profileWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('DELETE_PROFILE')")
    public Mono<ResponseEntity<UniversalResponse>> deleteProfile(@RequestBody CommonProfileWrapper commonProfileWrapper ){
        return userInterface.archiveProfile (commonProfileWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/get")
    //GET PROFILE PLUS ITS ROLES;
    @PreAuthorize("hasAnyRole('ESB-ADMIN','INTEGRATOR')")
    public Mono<ResponseEntity<UniversalResponse>> getProfile(@RequestBody CommonProfileWrapper commonProfileWrapper){
        return userInterface.getProfile (commonProfileWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/get/all")
    @PreAuthorize("hasAnyRole('ESB-ADMIN', 'INTEGRATOR')")
    public Mono<ResponseEntity<UniversalResponse>> getProfiles(@RequestBody CommonWrapper commonWrapper){
        return userInterface.getProfiles (commonWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/add/roles")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_PROFILE')")
    public  Mono<ResponseEntity<UniversalResponse>> addRolesToProfile(@RequestBody CommonRoleWrapper commonRoleWrapper){
        return userInterface.addRolesToProfile (commonRoleWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/remove/roles")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_PROFILE')")
    public Mono<ResponseEntity<UniversalResponse>> removeRolesFromProfile(@RequestBody CommonRoleWrapper commonRoleWrapper){
        return  userInterface.removeRolesFromProfile (commonRoleWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }
}
