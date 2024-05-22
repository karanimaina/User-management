package io.avania.io.usermanagement.wrapper;

import com.eclectics.io.usermodule.model.Role;

import java.util.List;

public record RoleResponseWrapper(
        String moduleName,
        List<Role> roles
) {
}
