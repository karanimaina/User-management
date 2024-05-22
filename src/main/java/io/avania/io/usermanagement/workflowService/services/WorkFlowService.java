package io.avania.io.usermanagement.workflowService.services;


import com.eclectics.io.usermodule.workflowService.dto.WorkFlowDto;
import com.eclectics.io.usermodule.workflowService.dto.WorkFlowStepDto;
import com.eclectics.io.usermodule.wrapper.CommonWrapper;
import com.eclectics.io.usermodule.wrapper.UniversalResponse;
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
