package io.avania.io.usermanagement.wrapper;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * @author David C Makuba
 * @created 04/02/2023
 **/

public record UpdatePasswordWrapper (
    @NotBlank(message = "Old password cannot be empty")
     String oldPassword,
    @NotBlank(message = "New password cannot be blank")
     String newPassword,
    @Min (value=1)
     long id
){}
