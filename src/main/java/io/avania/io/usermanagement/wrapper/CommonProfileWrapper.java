package io.avania.io.usermanagement.wrapper;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * @author David C Makuba
 * @created 06/02/2023
 **/

public record CommonProfileWrapper(
        @NotBlank(message = "Remarks cannot be null")
        String remarks,
        @Min(value = 0, message = "Profile not found")
        long id) {
}
