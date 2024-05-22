package com.avania.io.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Entity
@Table (name = "esb_roles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete (sql = "update esb_roles set soft_delete=true")
@Where (clause = "soft_delete = false")
public class Role extends BaseEntity {
private String name;
private String remarks;
private String moduleName;
private String processName;
private String nextStepTopic;
private String onCompletionTopic;
private boolean isWorkflowEnabled;
private boolean isSystemRole=true;
}

