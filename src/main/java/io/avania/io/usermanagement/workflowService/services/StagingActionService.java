package io.avania.io.usermanagement.workflowService.services;



import com.eclectics.io.usermodule.workflowService.constants.WorkFlowResponseStatus;
import com.eclectics.io.usermodule.workflowService.dto.ApproveStagedActionWrapper;
import com.eclectics.io.usermodule.workflowService.dto.StagingActionDto;
import com.eclectics.io.usermodule.wrapper.UniversalResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StagingActionService {
    WorkFlowResponseStatus checkAndStageWorkFlow(StagingActionDto stagingActionDto);
    Mono<List<StagingActionDto>> getStagedActionsByGroupProcesses(StagingActionDto stagingActionDto);
    Mono<UniversalResponse> approveStagedActionByWorkFlowIdAndStagedActionId(ApproveStagedActionWrapper approveStagedActionWrapper);
    Mono<Void> resetAllStageApprovalsOnStepEdits(long workflowId);


}
