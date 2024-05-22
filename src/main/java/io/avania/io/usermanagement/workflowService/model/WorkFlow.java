package io.avania.io.usermanagement.workflowService.model;

import com.eclectics.io.usermodule.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
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
@Where(clause = "soft_delete = false")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_staging_workflows")
@SQLDelete(sql = "update tb_staging_workflows set soft_delete=true")
public class WorkFlow extends BaseEntity {
    private String name;
    private String remarks;
    private String process;
    private String workflowStepsOrder;
}
