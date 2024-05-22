package io.avania.io.usermanagement.workflowService.repository;

import com.eclectics.io.usermodule.workflowService.model.StagingActionApproval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StagingActionApprovalRepository extends JpaRepository<StagingActionApproval,Long> {
}
