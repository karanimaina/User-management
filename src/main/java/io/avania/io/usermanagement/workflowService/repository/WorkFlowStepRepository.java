package io.avania.io.usermanagement.workflowService.repository;

import io.avania.io.usermanagement.workflowService.model.WorkFlowStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkFlowStepRepository extends JpaRepository<WorkFlowStep,Long> {
    List<WorkFlowStep> findAllByWorkFlowIdAndSoftDeleteFalse(long workFlowId);
    Optional<WorkFlowStep> findByIdAndWorkFlowIdEqualsAndSoftDeleteFalse(long workflowStepId, long workFlowId);
    Optional<WorkFlowStep> findByStepNameEqualsIgnoreCaseAndWorkFlowIdAndSoftDeleteFalse(String name, long workFlowId);
    Optional<WorkFlowStep> findByIdAndWorkFlowIdAndSoftDeleteFalse(long workFlowStepId, long workFlowId);
}
