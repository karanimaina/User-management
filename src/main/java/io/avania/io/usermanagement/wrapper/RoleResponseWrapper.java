package io.avania.io.usermanagement.wrapper;


import io.avania.io.usermanagement.model.Role;

import java.util.List;

public record RoleResponseWrapper(
        String moduleName,
        List<Role> roles
) {
}
