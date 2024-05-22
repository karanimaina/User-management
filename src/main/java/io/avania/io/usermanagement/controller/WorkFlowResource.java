package io.avania.io.usermanagement.controller;


import com.eclectics.io.usermodule.service.impl.UserService;
import com.eclectics.io.usermodule.workflowService.dto.ApproveStagedActionWrapper;
import com.eclectics.io.usermodule.workflowService.dto.StagingActionDto;
import com.eclectics.io.usermodule.workflowService.dto.WorkFlowDto;
import com.eclectics.io.usermodule.workflowService.dto.WorkFlowStepDto;
import com.eclectics.io.usermodule.workflowService.services.StagingActionService;
import com.eclectics.io.usermodule.workflowService.services.WorkFlowService;
import com.eclectics.io.usermodule.wrapper.CommonWrapper;
import com.eclectics.io.usermodule.wrapper.UniversalResponse;
import lombok.RequiredArgsConstructor;
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
 * @created 19/07/2022
 **/
@RequestMapping("/api/v1/admin/workflow")
@RequiredArgsConstructor
@RestController
public class WorkFlowResource {
    private final WorkFlowService workFlowService;
    private final StagingActionService stagingActionService;
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('CREATE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> createWorkFlow(@RequestBody WorkFlowDto workFlowDto) {
        return workFlowService.createWorkFlow (workFlowDto).map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/get/id")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('CREATE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> getWorkFlowById(@RequestBody WorkFlowDto workFlowDto) {
        return workFlowService.getWorkFlow (workFlowDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/get/workflows")
    @PreAuthorize("hasAnyRole('ESB-ADMIN')")
    public Mono<ResponseEntity<UniversalResponse>> getWorkFlows(@RequestBody CommonWrapper commonWrapper) {
        return workFlowService.getWorkFlows (commonWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> updateWorkFlow(@RequestBody WorkFlowDto workFlowDto) {
        return workFlowService.updateWorkFlow (workFlowDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('DELETE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> deleteWorkFlow(@RequestBody WorkFlowDto workFlowDto) {
        return workFlowService.deleteWorkFlow (workFlowDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());

    }

    @PostMapping("/create/step")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> createWorkFlowStep(@RequestBody WorkFlowStepDto workFlowStepDto) {
        return workFlowService.addWorkFlowStep (workFlowStepDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/order/steps")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> orderWorkFlowSteps(@RequestBody WorkFlowDto workFlowDto) {
        return workFlowService.reorderWorkFlowStep (workFlowDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/get/step")
    @PreAuthorize("hasAnyRole('ESB-ADMIN')")

    public Mono<ResponseEntity<UniversalResponse>> getWorkFlowStepById(@RequestBody WorkFlowStepDto workFlowStepDto) {
        return workFlowService.getWorkFlowStep (workFlowStepDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/get/workflow/steps/")
    @PreAuthorize("hasAnyRole('ESB-ADMIN')")
    public Mono<ResponseEntity<UniversalResponse>> getWorkFlowStepsByWorkFlowId(@RequestBody WorkFlowDto workFlowDto) {
        return workFlowService.getStepsInWorkFlow (workFlowDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/update/step")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> updateWorkFlowSteps(@RequestBody WorkFlowStepDto workFlowStepDto) {
        return workFlowService.updateWorkFlowStep (workFlowStepDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());

    }

    @PostMapping("/delete/step")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('UPDATE_WORKFLOW')")
    public Mono<ResponseEntity<UniversalResponse>> deleteWorkFlowStep(@RequestBody WorkFlowStepDto workFlowStepDto) {
        return workFlowService.removeWorkFlowStep (workFlowStepDto)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/approve/staged")
    @PreAuthorize("hasAnyRole('ESB-ADMIN')")
    public Mono<ResponseEntity<UniversalResponse>> approveWorkflow(@RequestBody ApproveStagedActionWrapper approveStagedActionWrapper) {
        return stagingActionService.approveStagedActionByWorkFlowIdAndStagedActionId (approveStagedActionWrapper)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/staged")
    @PreAuthorize("hasAnyRole('ESB-ADMIN')")
    public Mono<ResponseEntity<UniversalResponse>> getStagedWorkflows(@RequestBody StagingActionDto stagingActionDto, Authentication authentication) {
        return userService.getUserProfile (authentication.getName ())
                .flatMap (profile -> {
                    stagingActionDto.setApproverId (profile.getId ());
                    return Mono.just (stagingActionDto);
                })
                .flatMap (stagingActionService::getStagedActionsByGroupProcesses)
                .flatMap (response -> Mono.just (UniversalResponse.builder ().data (response).message ("Staged workflows").status (200).build ()))
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }


}
