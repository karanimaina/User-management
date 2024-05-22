package io.avania.io.usermanagement.controller;

import com.eclectics.io.usermodule.service.IUserInterface;
import com.eclectics.io.usermodule.wrapper.CommonRoleWrapper;
import com.eclectics.io.usermodule.wrapper.CommonWrapper;
import com.eclectics.io.usermodule.wrapper.UniversalResponse;
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
@RequestMapping("/api/v1/admin/role")
@RequiredArgsConstructor
public class RoleController {
    private final IUserInterface userInterface;

@PostMapping("/add")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('CREATE_ROLE')")
    public Mono<ResponseEntity<UniversalResponse>> addRole(@RequestBody CommonRoleWrapper commonRoleWrapper){
        return userInterface.addRole (commonRoleWrapper)
                .map(ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

//    @PostMapping("/all")
//    @PreAuthorize("hasAnyRole('ESB-ADMIN') ")
//    public Mono<ResponseEntity<UniversalResponse>> getRoles(@RequestBody CommonWrapper commonWrapper){
//        return userInterface.getAllRoles (commonWrapper)
//                .map (ResponseEntity::ok)
//                .publishOn (Schedulers.boundedElastic ());
//    }
    @PostMapping("/all")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') ")
    public Mono<ResponseEntity<UniversalResponse>> fetchRoles(@RequestBody CommonWrapper commonWrapper){
        return userInterface.fetchRoles (commonWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }



    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('DELETE_ROLE')")
    public Mono<ResponseEntity<UniversalResponse>> deleteRole(@RequestBody CommonRoleWrapper commonRoleWrapper){
        return userInterface.deleteRole (commonRoleWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }
}
