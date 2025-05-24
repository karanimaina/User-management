package io.avania.io.usermanagement.workflowService.services;

import io.avania.io.usermanagement.workflowService.dto.WorkFlowDto;
import io.avania.io.usermanagement.workflowService.dto.WorkFlowStepDto;
import io.avania.io.usermanagement.wrapper.CommonWrapper;
import io.avania.io.usermanagement.wrapper.UniversalResponse;
import reactor.core.publisher.Mono;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
public interface WorkFlowService {
    Mono<UniversalResponse> createWorkFlow(WorkFlowDto workFlowDto);
    Mono<UniversalResponse> updateWorkFlow(WorkFlowDto workFlowDto);
    Mono<UniversalResponse> getWorkFlow(WorkFlowDto workFlowDto);
    Mono<UniversalResponse> getWorkFlows(CommonWrapper commonWrapper);
    Mono<UniversalResponse> deleteWorkFlow(WorkFlowDto workFlowDto);
    Mono<UniversalResponse> getStepsInWorkFlow(WorkFlowDto workFlowDto);
    Mono<UniversalResponse> addWorkFlowStep(WorkFlowStepDto workFlowStepDto);
    Mono<UniversalResponse> getWorkFlowStep(WorkFlowStepDto workFlowStepDto);

    Mono<UniversalResponse> updateWorkFlowStep(WorkFlowStepDto workFlowStepDto);
    Mono<UniversalResponse> removeWorkFlowStep(WorkFlowStepDto workFlowStepDto);
    Mono<UniversalResponse> reorderWorkFlowStep(WorkFlowDto workFlowDto);
}
