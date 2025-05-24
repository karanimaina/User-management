package io.avania.io.usermanagement.workflowService.services.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.avania.io.usermanagement.service.impl.NotificationService;
import io.avania.io.usermanagement.workflowService.Exception.InvalidOperation;
import io.avania.io.usermanagement.workflowService.Exception.ItemNotFoundException;
import io.avania.io.usermanagement.workflowService.constants.ApprovalAction;
import io.avania.io.usermanagement.workflowService.constants.WorkFlowResponseStatus;
import io.avania.io.usermanagement.workflowService.dto.ApproveStagedActionWrapper;
import io.avania.io.usermanagement.workflowService.dto.StagingActionDto;
import io.avania.io.usermanagement.workflowService.model.WorkFlow;
import io.avania.io.usermanagement.workflowService.model.*;
import io.avania.io.usermanagement.workflowService.repository.*;
import io.avania.io.usermanagement.workflowService.services.StagingActionService;
import io.avania.io.usermanagement.wrapper.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
@Service
@RequiredArgsConstructor
public class StagingActionServiceImpl implements StagingActionService {
    private final StagingActionApprovalRepository stagingActionApprovalRepository;
    private final StagingActionRepository stagingActionRepository;
    private final WorkFlowRepository workFlowRepository;
    private final WorkFlowStepRepository workFlowStepRepository;
    private final NotificationService notificationService;
    private final Gson gson;

    @Override
    public WorkFlowResponseStatus checkAndStageWorkFlow(StagingActionDto stagingActionDto) {
                    WorkFlow workFlow = workFlowRepository.findByProcessAndSoftDeleteFalse (stagingActionDto.getProcess ()).orElse (null);
                    if (workFlow == null) {
                        return WorkFlowResponseStatus.NOT_PRESENT;
                    }
                    Type listType = new TypeToken<ArrayList<Long>> () {
                    }.getType ();
                    List<Long> workFlowStepOrderList = gson.fromJson (workFlow.getWorkflowStepsOrder (), listType);
                    if (workFlowStepOrderList.isEmpty ()) {
                        return WorkFlowResponseStatus.NOT_PRESENT;
                    }
                    StagingAction stagingAction = StagingAction.builder ()
                            .stagingUserDetails (stagingActionDto.getStagingUserDetails ())
                            .stagingCurrentData (stagingActionDto.getStagingCurrentData ())
                            .stagingPreviousData (stagingActionDto.getStagingPreviousData ())
                            .workflow (workFlow)
                            .currentStepIndex (0)
                            .process (stagingActionDto.getProcess ())
                            .processed (false)
                            .finalized (false)
                            .approved (false)
                            .build ();
                    stagingActionRepository.save (stagingAction);
                    workFlowStepRepository.findByIdAndWorkFlowIdAndSoftDeleteFalse (workFlowStepOrderList.get (0),workFlow.getId ())
                            .ifPresent (this::sendNotificationEmail);
                 return WorkFlowResponseStatus.STAGED;
    }

    private void sendNotificationEmail(WorkFlowStep workFlowStep) {
        Mono.fromRunnable (() -> {
            String title = " ESB STAGED REQUEST REQUIRING APPROVAL";
            if (workFlowStep.getNotificationEmail () != null && workFlowStep.getNotificationEmailMessage () != null) {
                notificationService.sendEmailNotificationMessage ( workFlowStep.getNotificationEmailMessage (),  workFlowStep.getNotificationEmail (),title);
            }
        }).subscribeOn (Schedulers.boundedElastic ()).subscribe ();
    }

    @Override
    public Mono<List<StagingActionDto>> getStagedActionsByGroupProcesses(StagingActionDto stagingActionDto) {
        return Mono.fromCallable (() -> {
            List<StagingActionDto> stagingActionsList = new ArrayList<> ();
            stagingActionDto.getProcesses ().forEach (process -> {
                        List<StagingActionDto> stagedActions = stagingActionRepository.findAllStagedDataByProcessNameAndSoftDeleteFalse (process)
                                .stream ()
                                .map (stagingAction -> {
                                    WorkFlow workFlow = stagingAction.getWorkflow ();
                                    int currStep = stagingAction.getCurrentStepIndex ();
                                    Type listType = new TypeToken<ArrayList<Long>> () {
                                    }.getType ();
                                    List<Long> stagingActionList = new Gson ().fromJson (workFlow.getWorkflowStepsOrder (), listType);
                                    WorkFlowStep workFlowStep = workFlowStepRepository.findByIdAndWorkFlowIdAndSoftDeleteFalse (stagingActionList.get (currStep), workFlow.getId ()).orElse (null);
                                    if (workFlowStep == null) return null;
                                    StagingActionDto response = new StagingActionDto ();
                                    BeanUtils.copyProperties (stagingAction, response);
                                    response.setFinalized (stagingAction.isFinalized ());
                                    response.setCanApprove (stagingActionDto.getApproverId () == workFlowStep.getRequiredRoleId ());
                                    return response;
                                })
                                .filter (Objects::nonNull).toList ();
                        stagingActionsList.addAll (stagedActions);
                    }
            );
            return stagingActionsList;
        }).publishOn (Schedulers.boundedElastic ());
    }

    @Override
    public Mono<UniversalResponse> approveStagedActionByWorkFlowIdAndStagedActionId(ApproveStagedActionWrapper approveStagedActionWrapper) {
        return Mono.fromCallable (() -> {
                    StagingAction stagingAction = stagingActionRepository.findByIdAndFinalizedFalseAndProcessedFalseAndApprovedFalse (approveStagedActionWrapper.getStageId ())
                            .orElseThrow (() -> new ItemNotFoundException("Staged Action Item not found"));

                    WorkFlow workFlow = stagingAction.getWorkflow ();
                    Type listType = new TypeToken<ArrayList<Long>> () {}.getType ();
                    List<Long> stagingActionList = new Gson ().fromJson (workFlow.getWorkflowStepsOrder (), listType);
                    int currIndex = stagingAction.getCurrentStepIndex ();

                    WorkFlowStep workFlowStep = workFlowStepRepository.findByIdAndWorkFlowIdEqualsAndSoftDeleteFalse (stagingActionList.get (currIndex), workFlow.getId ())
                            .orElseThrow (() -> new ItemNotFoundException ("Workflow step not found"));

                    if (workFlowStep.getRequiredRoleId () != approveStagedActionWrapper.getApproverId ()) {
                        throw new InvalidOperation("You dont have adequate permissions to perform approve this action");
                    }
                    //check if staged action is the last in the index
                    long stepId = stagingActionList.get (currIndex);
                    if (stagingActionList.indexOf (stepId) == stagingActionList.size () - 1) {
                        stagingAction.setFinalized (true);
                    } else if (approveStagedActionWrapper.isApproved ()) {
                        stagingAction.setCurrentStepIndex (currIndex + 1);
                        workFlowStepRepository.findByIdAndWorkFlowIdAndSoftDeleteFalse (stagingActionList.get (currIndex + 1),workFlow.getId ())
                                .ifPresent (this::sendNotificationEmail);
                    } else {
                        stagingAction.setFinalized (true);
                        stagingAction.setProcessed (true);
                    }
                    stagingAction.setApproved (approveStagedActionWrapper.isApproved ());
                    StagingActionDto stagingActionDto = new StagingActionDto ();
                    StagingActionApproval stagingActionApproval = StagingActionApproval.builder ()
                            .checkerDetails (approveStagedActionWrapper.getApproverDetails ())
                            .approvalAction (approveStagedActionWrapper.isApproved () ? ApprovalAction.APPROVED.name () : ApprovalAction.REJECTED.name ())
                            .stagingAction (stagingAction)
                            .build ();
                    StagingAction savedStagingAction = stagingActionRepository.save (stagingAction);
                    stagingActionApprovalRepository.save (stagingActionApproval);
                    BeanUtils.copyProperties (savedStagingAction, stagingActionDto);
                    return UniversalResponse.builder ().status (200).message ("Approved successfully").data (stagingActionDto).build ();
                })
                .publishOn (Schedulers.boundedElastic ());

    }

    @Override
    public Mono<Void> resetAllStageApprovalsOnStepEdits(long workflowId) {
        return Mono.fromRunnable (() -> stagingActionRepository.findAllByWorkflowIdAndFinalizedFalseAndProcessedFalse(workflowId)
                .forEach (stagingAction -> {
                    stagingAction.setCurrentStepIndex (0);
                    WorkFlow workFlow= stagingAction.getWorkflow ();
                    Type listType = new TypeToken<ArrayList<Long>> () {}.getType ();
                    List<Long> stagingActionList = new Gson ().fromJson (workFlow.getWorkflowStepsOrder (), listType);
                    workFlowStepRepository.findByIdAndWorkFlowIdAndSoftDeleteFalse (stagingActionList.get (0),workFlow.getId ())
                            .ifPresent (this::sendNotificationEmail);
                    Mono.fromCallable (() -> stagingActionRepository.save (stagingAction)).subscribeOn (Schedulers.boundedElastic ()).subscribe ();
                }));
    }

}
