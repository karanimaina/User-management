package io.avania.io.usermanagement.workflowService.model;

import com.eclectics.io.usermodule.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
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
@Table(name = "tb_workflow_staging_action")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@SQLDelete(sql = "update tb_workflow_staging_action set soft_delete=true")
@Where(clause = "soft_delete = false")
public class StagingAction  extends BaseEntity {
    @Lob
    private String stagingUserDetails;
    @Lob
    private String stagingCurrentData;
    @Lob
    private String stagingPreviousData;
    private String metaData;
    private boolean finalized= false;
    private boolean approved=false;
    private boolean processed=false;
    private int currentStepIndex=0;
    private String process;
    @ManyToOne
    private WorkFlow workflow;

}
