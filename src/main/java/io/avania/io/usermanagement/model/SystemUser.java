package io.avania.io.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Date;

@Table(name="esb_system_users",indexes = @Index (columnList = "email"))
@Entity
@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@SQLDelete(sql = "update esb_system_users set soft_delete=true")
@Where(clause = "soft_delete = false")
public class SystemUser extends BaseEntity{
    @JsonIgnore
    private String password;
    private String email;
    private boolean firstTimeLogin= Boolean.TRUE;
    private String firstName;
    private String lastName;
    private String remarks;
    private boolean blocked=false;
    private String phoneNumber;
    private String employeeNumber;
    private Long itemId;
    private String blockedBy;
    private String blockedRemarks;
    private String status;
    @JsonIgnore
    private int loginAttempts;
    @JsonIgnore
    private int resetPasswordLoginCount;
    @JsonIgnore
    private Date passwordResetAt;
    @ManyToOne
    private Profile profile;
}
