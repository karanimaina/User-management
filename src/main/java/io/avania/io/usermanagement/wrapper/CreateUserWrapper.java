package io.avania.io.usermanagement.wrapper;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author David C Makuba
 * @created 02/02/2023
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserWrapper {
    @Email(message = "Email should be valid")
    public String email;
    @Size(min = 3, max = 15, message = "First Name should be between 3 and 15 characters")
    public String firstName;
    @Size(min=3,max=15, message = "Last Name should be between 3 and 15 characters")
    public String lastName;
    public long profileId;
    public boolean isCountryAdmin;
    public String phoneNumber;
    public String employeeNumber;
}
