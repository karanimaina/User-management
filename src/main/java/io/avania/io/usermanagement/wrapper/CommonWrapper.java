package io.avania.io.usermanagement.wrapper;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author David C Makuba
 * @created 06/02/2023
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonWrapper {
    private long id;
    private String param;
    private String remarks;
    private String username;
    private int page;
    @Max (value = 100, message = "Maximum query per page is 100")
    private int size;
    private String filter;
}
