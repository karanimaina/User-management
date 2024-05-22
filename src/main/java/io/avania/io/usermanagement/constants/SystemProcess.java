package io.avania.io.usermanagement.constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author David C Makuba
 * @created 02/02/2023
 **/
@Getter
@RequiredArgsConstructor
public enum SystemProcess{
    CREATE_ADMIN(true),
    UPDATE_ADMIN(true),
    ENABLE_ADMIN(true),
    RESET_ADMIN(true),
    DISABLE_ADMIN(true),
    DELETE_ADMIN(true),
    CREATE_PROFILE(true),
    DELETE_PROFILE(true),
    UPDATE_PROFILE(true),
    CREATE_ROLE(true),
    DELETE_ROLE(true),
    ADD_MESSAGE_TEMPLATE(true),
    CREATE_WORKFLOW(true),
    UPDATE_WORKFLOW(true),
    DELETE_WORKFLOW(true),
    ADD_WORKFLOW_STEP(true),
    UPDATE_WORKFLOW_STEP(true),
    DELETE_WORKFLOW_STEP(true),

    CREATE_CATEGORY(true),
    UPDATE_CATEGORY(true),
    DISABLE_PRODUCT(true),
    ENABLE_PRODUCT(true),

    UPDATE_PRODUCT(true),
    DELETE_PRODUCT(true);

    final boolean canCreateWorkFlow;
}
