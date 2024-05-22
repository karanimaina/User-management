package io.avania.io.usermanagement.wrapper;

import jakarta.validation.constraints.Size;

/**
 * @author David C Makuba
 * @created 06/02/2023
 **/

public record CreateProfileWrapper (
    @Size(min = 4, max = 20, message = "Profile name must be within 4-10 characters long")
     String name,
    @Size(min = 4, max = 100, message = "Profile Description must be within 4-100 characters long")
     String remarks
    ){
}
