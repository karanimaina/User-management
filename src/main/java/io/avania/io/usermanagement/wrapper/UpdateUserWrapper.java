package io.avania.io.usermanagement.wrapper;

import org.springframework.lang.Nullable;

/**
 * @author David C Makuba
 * @created 02/02/2023
 **/
public record UpdateUserWrapper(
        @Nullable
        String firstName,
        @Nullable
        String lastName,
        long id,
        @Nullable
        Long profileId
        ){
}
