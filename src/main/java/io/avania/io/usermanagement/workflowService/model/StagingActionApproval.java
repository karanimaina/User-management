package io.avania.io.usermanagement.workflowService.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.avania.io.usermanagement.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_workflow_staging_action_approval")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SQLDelete(sql = "update tb_workflow_staging_action_approval set soft_delete=true")
@Where(clause = "soft_delete = false")
@Entity
public class StagingActionApproval extends BaseEntity {
    private String approvalAction;
    private String  checkerDetails;
    private String remarks;
    @ManyToOne
    private StagingAction stagingAction;
}
