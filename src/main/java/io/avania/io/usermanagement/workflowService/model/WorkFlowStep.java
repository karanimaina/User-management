package io.avania.io.usermanagement.workflowService.model;

import com.eclectics.io.usermodule.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "tb_staging_workflows_steps")
@SQLDelete(sql = "update tb_staging_workflows_steps set soft_delete=true")
@Entity
public class WorkFlowStep extends BaseEntity {
    private String stepName;
    private String remarks;
    private Long   requiredRoleId;
    private String notificationEmail;
    private String notificationEmailMessage;
    @ManyToOne
    private WorkFlow workFlow;
}
