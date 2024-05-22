package io.avania.io.usermanagement.workflowService.repository;

import com.eclectics.io.usermodule.workflowService.model.WorkFlow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkFlowRepository extends JpaRepository<WorkFlow,Long> {
    Optional<WorkFlow> findByProcessAndSoftDeleteFalse(String process);
    Optional<WorkFlow> findByIdOrProcessEqualsIgnoreCaseAndSoftDeleteFalse(long workFlowId, String process);
    Optional<WorkFlow> findByIdAndSoftDeleteFalse(long workFlowId);
    Page<WorkFlow> findAllBySoftDeleteFalse(Pageable pageable);

}
