package io.avania.io.usermanagement.wrapper;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author David C Makuba
 * @created 02/02/2023
 **/
@Data
public class ResetUserPassword {
    @Min (value = 1, message = "User not found")
    private long userId;

}
