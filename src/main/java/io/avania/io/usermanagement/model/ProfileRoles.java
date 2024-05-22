package io.avania.io.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * @author David C Makuba
 * @created 25/01/2023
 **/
@Table(name = "esb_profile_roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@SQLDelete(sql = "update esb_profile_roles set soft_delete=true")
@Where(clause = "soft_delete = false")
public class ProfileRoles extends BaseEntity {
    @ManyToOne
    private Profile profile;
    @ManyToOne
    private Role role;
    private String moduleName;
}
