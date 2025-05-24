package io.avania.io.usermanagement.wrapper;

import io.avania.io.usermanagement.model.SystemUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author David C Makuba
 * @created 28/09/2022
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkFlowUserWrapper {
    private long userId;
    private String userEmail;
    private long adminId;
    private String firstName;
    private String lastName;
    private String middleName;


    public WorkFlowUserWrapper(SystemUser systemUser) {
        this.userId = systemUser.getId ();
        this.userEmail = systemUser.getEmail ();
        this.firstName = systemUser.getFirstName ();
        this.lastName = systemUser.getLastName ();
    }
}
