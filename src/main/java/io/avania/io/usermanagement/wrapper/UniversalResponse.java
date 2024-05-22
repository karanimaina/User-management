package io.avania.io.usermanagement.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * @author David C Makuba
 * @created 16/05/2022
 **/

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UniversalResponse(
        int status,
        String message,
        Object data,
        List<String> errors,
        Integer totalItems) {
    public static UniversalResponseBuilder builder() {
        return new UniversalResponseBuilder();

    }
}
