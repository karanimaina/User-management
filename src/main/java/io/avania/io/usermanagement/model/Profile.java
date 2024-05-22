package io.avania.io.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "esb_profiles")
@Builder
@SQLDelete(sql = "update esb_profiles set soft_delete=true")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Where(clause = "soft_delete = false")
public class Profile extends BaseEntity {
    private String name;
    private String remarks;
}
