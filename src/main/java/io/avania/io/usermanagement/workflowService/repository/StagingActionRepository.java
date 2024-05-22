package io.avania.io.usermanagement.workflowService.repository;

import com.eclectics.io.usermodule.workflowService.model.StagingAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
public interface StagingActionRepository  extends JpaRepository<StagingAction,Long> {
    List<StagingAction> findAllByWorkflowIdAndFinalizedFalseAndProcessedFalse(long workFlowId);

    @Query(value = "SELECT sa  from StagingAction sa INNER JOIN WorkFlow w  on sa.workflow.id= w.id where  sa.softDelete=false and sa.processed=false  and w.process=?1")
    List<StagingAction> findAllStagedDataByProcessNameAndSoftDeleteFalse( String processName);

    @Query(value = "select  sa  from StagingAction sa inner join WorkFlow w on w.id= sa.workflow.id where sa.finalized=false and sa.approved=false and sa.processed=false" +
            " and sa.softDelete=false and sa.id=?1")
    Optional<StagingAction> findByIdAndFinalizedFalseAndProcessedFalseAndApprovedFalse(long id);

    @Query(value = "select  sa from StagingAction sa  where sa.finalized=true and sa.approved=true and sa.processed=false and sa.softDelete=false ")
    List<StagingAction> findAllFinalizedTrueAndApprovedTrueAndProcessedFalse();
}
