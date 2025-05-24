package io.avania.io.usermanagement.workflowService.services;
import io.avania.io.usermanagement.workflowService.constants.WorkFlowResponseStatus;
import io.avania.io.usermanagement.workflowService.dto.ApproveStagedActionWrapper;
import io.avania.io.usermanagement.workflowService.dto.StagingActionDto;
import io.avania.io.usermanagement.wrapper.UniversalResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StagingActionService {
    WorkFlowResponseStatus checkAndStageWorkFlow(StagingActionDto stagingActionDto);
    Mono<List<StagingActionDto>> getStagedActionsByGroupProcesses(StagingActionDto stagingActionDto);
    Mono<UniversalResponse> approveStagedActionByWorkFlowIdAndStagedActionId(ApproveStagedActionWrapper approveStagedActionWrapper);
    Mono<Void> resetAllStageApprovalsOnStepEdits(long workflowId);


}
