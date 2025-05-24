package io.avania.io.usermanagement.workflowService.services.impl;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.avania.io.usermanagement.constants.SystemProcess;
import io.avania.io.usermanagement.workflowService.Exception.ItemExistException;
import io.avania.io.usermanagement.workflowService.Exception.ItemNotFoundException;
import io.avania.io.usermanagement.workflowService.WorkFlowFilter;
import io.avania.io.usermanagement.workflowService.dto.WorkFlowDto;
import io.avania.io.usermanagement.workflowService.dto.WorkFlowStepDto;
import io.avania.io.usermanagement.workflowService.model.WorkFlow;
import io.avania.io.usermanagement.workflowService.model.WorkFlowStep;
import io.avania.io.usermanagement.workflowService.repository.*;
import io.avania.io.usermanagement.workflowService.services.StagingActionService;
import io.avania.io.usermanagement.workflowService.services.WorkFlowService;
import io.avania.io.usermanagement.wrapper.CommonWrapper;
import io.avania.io.usermanagement.wrapper.UniversalResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.avania.io.usermanagement.constants.SystemProcess.*;


/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
@Service
@AllArgsConstructor
public class WorkFlowServiceImpl implements WorkFlowService {
    private final WorkFlowRepository workFlowRepository;
    private final WorkFlowStepRepository workFlowStepRepository;
    private final StagingActionService stagingActionService;

    @WorkFlowFilter(processName = SystemProcess.CREATE_WORKFLOW)
    @Override
    public Mono<UniversalResponse> createWorkFlow(WorkFlowDto workFlowDto) {
        return Mono.fromCallable (() -> {

            workFlowRepository.findByProcessAndSoftDeleteFalse (workFlowDto.getProcess ().toUpperCase ())
                    .ifPresent (s -> {
                        throw new ItemExistException("WorkFlow already exists");
                    });
            WorkFlow workFlow = WorkFlow.builder ()
                    .name (workFlowDto.getName ().toUpperCase ().trim ())
                    .remarks (workFlowDto.getRemarks ())
                    .process (workFlowDto.getProcess ())
                    .build ();
            return UniversalResponse.builder ().status (200).message ("Workflow details").data (workFlowRepository.save (workFlow)).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @WorkFlowFilter (processName = UPDATE_WORKFLOW)
    @Override
    public Mono<UniversalResponse> updateWorkFlow(WorkFlowDto workFlowDto) {
        return Mono.fromCallable (() -> {
            WorkFlow workFlow = workFlowRepository.findByIdAndSoftDeleteFalse (workFlowDto.getId ()).orElseThrow (() -> new ItemNotFoundException ("Work flow not found"));
            if (workFlowDto.getName () != null) workFlow.setName (workFlowDto.getName ());
            if (workFlowDto.getRemarks () != null) workFlow.setRemarks (workFlowDto.getRemarks ());
            return UniversalResponse.builder ().status (200).message ("Workflow updated successfully").data (workFlowRepository.save (workFlow)).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @Override
    public Mono<UniversalResponse> getWorkFlow(WorkFlowDto workFlowDto) {
        return Mono.fromCallable (() -> {
            WorkFlow workFlow = workFlowRepository
                    .findByIdOrProcessEqualsIgnoreCaseAndSoftDeleteFalse (workFlowDto.getId (), workFlowDto.getProcess ())
                    .orElseThrow (() -> new ItemNotFoundException("Workflow not found"));
            return UniversalResponse.builder ().status (200).message ("Workflow details").data (workFlow).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @Override
    public Mono<UniversalResponse> getWorkFlows(CommonWrapper commonWrapper) {
        return Mono.fromCallable (()-> {
            Pageable pageable= PageRequest.of (commonWrapper.getPage (), commonWrapper.getSize ());
            Page<WorkFlow> workFlows= workFlowRepository.findAllBySoftDeleteFalse (pageable);
            return UniversalResponse.builder ().status (200).message ("Workflows").data (workFlows).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @WorkFlowFilter (processName = DELETE_WORKFLOW)
    @Override
    public Mono<UniversalResponse> deleteWorkFlow(WorkFlowDto workFlowDto) {
        return Mono.fromCallable (() -> {
            WorkFlow workFlow = workFlowRepository.findByIdAndSoftDeleteFalse (workFlowDto.getId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Workflow not found"));

            workFlowStepRepository.findAllByWorkFlowIdAndSoftDeleteFalse (workFlow.getId ())
                            .forEach (workFlowStep -> {
                                workFlowStep.setSoftDelete (true);
                                workFlowStepRepository.save (workFlowStep);
                            });
            workFlow.setSoftDelete (true);
            return UniversalResponse.builder ().status (200).message ("Workflow deleted successfully").data (workFlowRepository.save (workFlow)).build ();
        }).publishOn (Schedulers.boundedElastic ());

    }

    @WorkFlowFilter (processName = ADD_WORKFLOW_STEP)
    @Override
    public Mono<UniversalResponse> addWorkFlowStep(WorkFlowStepDto workFlowStepDto) {
        return Mono.fromCallable (() -> {

            WorkFlow workFlow = workFlowRepository.findByIdAndSoftDeleteFalse (workFlowStepDto.getWorkFlowId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Work flow not present"));
            WorkFlowStep workFlowStep = workFlowStepRepository.findByStepNameEqualsIgnoreCaseAndWorkFlowIdAndSoftDeleteFalse (workFlowStepDto.getStepName ().toUpperCase (), workFlowStepDto.getWorkFlowId ())
                    .orElse (null);
            if (workFlowStep != null) throw new ItemExistException ("Work flow step already exists by name");
            WorkFlowStep newWorkFlowStep = WorkFlowStep.builder ()
                    .stepName (workFlowStepDto.getStepName ())
                    .remarks (workFlowStepDto.getRemarks ())
                    .requiredRoleId (workFlowStepDto.getRequiredRoleId ())
                    .workFlow (workFlow)
                    .notificationEmail (workFlowStepDto.getNotificationEmail ())
                    .notificationEmailMessage (workFlowStepDto.getNotificationEmailMessage ())
                    .build ();
            WorkFlowStep savedWorkFlow = workFlowStepRepository.save (newWorkFlowStep);
            Mono.fromRunnable (() -> {
                Type listType = new TypeToken<ArrayList<Long>> () {
                }.getType ();
                List<Long> workFlowStepOrderList = new Gson ().fromJson (workFlow.getWorkflowStepsOrder (), listType);
                if (workFlowStepOrderList == null) workFlowStepOrderList = new ArrayList<> ();
                workFlowStepOrderList.add (savedWorkFlow.getId ());
                workFlow.setWorkflowStepsOrder (new Gson ().toJson (workFlowStepOrderList));
                workFlowRepository.save (workFlow);
            }).publishOn (Schedulers.boundedElastic ()).subscribeOn (Schedulers.boundedElastic ()).subscribe ();

            return UniversalResponse.builder ().status (200).message ("Added workflow step").data (savedWorkFlow).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }
    @Override
    public Mono<UniversalResponse> getStepsInWorkFlow(WorkFlowDto workFlowDto) {
        return Mono.fromCallable (() -> {

            WorkFlow workFlow = workFlowRepository.findByIdAndSoftDeleteFalse (workFlowDto.getId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Workflow not found"));

            List<WorkFlowStep> workFlowSteps = workFlowStepRepository.findAllByWorkFlowIdAndSoftDeleteFalse (workFlow.getId ());
            return UniversalResponse.builder ().status (200).message ("Work flow steps").data (workFlowSteps).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @Override
    public Mono<UniversalResponse> getWorkFlowStep(WorkFlowStepDto workFlowStepDto) {
        return Mono.fromCallable (() -> {
            WorkFlowStep workFlowStep = workFlowStepRepository.findByIdAndWorkFlowIdEqualsAndSoftDeleteFalse (workFlowStepDto.getId (), workFlowStepDto.getWorkFlowId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Workflow step not found"));
            return UniversalResponse.builder ().status (200).message ("Work flow step details").data (workFlowStep).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @WorkFlowFilter (processName = UPDATE_WORKFLOW_STEP)
    @Override
    public Mono<UniversalResponse> updateWorkFlowStep(WorkFlowStepDto workFlowStepDto) {
        return Mono.fromCallable (() -> {
            WorkFlowStep workFlowStep = workFlowStepRepository.findByIdAndWorkFlowIdAndSoftDeleteFalse (workFlowStepDto.getId (), workFlowStepDto.getWorkFlowId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Workflow step not found"));
            if (workFlowStepDto.getNotificationEmail () != null)
                workFlowStep.setNotificationEmail (workFlowStepDto.getNotificationEmail ());
            if(workFlowStepDto.getStepName ()!=null)
                workFlowStep.setStepName (workFlowStepDto.getStepName ());
            if (workFlowStepDto.getRemarks () != null) workFlowStep.setRemarks (workFlowStepDto.getRemarks ());
            if(workFlowStepDto.getRequiredRoleId ()!=null)
                workFlowStep.setRequiredRoleId (workFlowStepDto.getRequiredRoleId ());
            if (workFlowStepDto.getNotificationEmailMessage () != null)
                workFlowStep.setNotificationEmailMessage (workFlowStepDto.getNotificationEmailMessage ());
            return UniversalResponse.builder ().status (200).message ("Workflow step updated successfully").data (workFlowStepRepository.save (workFlowStep)).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @WorkFlowFilter (processName = DELETE_WORKFLOW_STEP)
    @Override
    public Mono<UniversalResponse> removeWorkFlowStep(WorkFlowStepDto workFlowStepDto) {
            return Mono.fromCallable (() -> {
            WorkFlow workFlow = workFlowRepository.findByIdAndSoftDeleteFalse (workFlowStepDto.getWorkFlowId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Workflow not found"));
            WorkFlowStep workFlowStep = workFlowStepRepository.findByIdAndWorkFlowIdAndSoftDeleteFalse (workFlowStepDto.getId (), workFlowStepDto.getWorkFlowId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Step not found"));
            workFlowStep.setSoftDelete (true);
            Type listType = new TypeToken<ArrayList<Long>> () {
            }.getType ();
            List<Long> workFlowStepOrderList = new Gson ().fromJson (workFlow.getWorkflowStepsOrder (), listType);
            workFlowStepOrderList.remove (workFlowStep.getId ());
            workFlow.setWorkflowStepsOrder (new Gson ().toJson (workFlowStepOrderList));
            workFlowRepository.save (workFlow);
            Mono.fromRunnable (() -> stagingActionService.resetAllStageApprovalsOnStepEdits (workFlowStep.getWorkFlow ().getId ())
                            .subscribe ())
                    .publishOn (Schedulers.boundedElastic ()).subscribeOn (Schedulers.boundedElastic ()).subscribe ();

            return UniversalResponse.builder ().status (200).message ("Removed step from workflow").data (workFlowStepRepository.save (workFlowStep)).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }

    @Override
    public Mono<UniversalResponse> reorderWorkFlowStep(WorkFlowDto workFlowDto) {
        // check work flows by ids
        //reorder them
        return Mono.fromCallable (() -> {
            List<Long> workFlowList = workFlowDto.getWorkFlowStepsOrder ()
                    .stream ()
                    .map (workFlowStepId -> workFlowStepRepository.findByIdAndWorkFlowIdAndSoftDeleteFalse (workFlowStepId, workFlowDto.getId ()))
                    .filter (Optional::isPresent)
                    .map (Optional::get)
                    .map (WorkFlowStep::getId)
                    .toList ();

            WorkFlow workFlow = workFlowRepository.findByIdAndSoftDeleteFalse (workFlowDto.getId ())
                    .orElseThrow (() -> new ItemNotFoundException ("Workflow not found"));
            workFlow.setWorkflowStepsOrder (new Gson ().toJson (workFlowList));
            return UniversalResponse.builder ().status (200).message ("Re-ordered workflow step").data (workFlowRepository.save (workFlow)).build ();
        }).publishOn (Schedulers.boundedElastic ());
    }
}
