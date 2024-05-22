package io.avania.io.usermanagement.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author David C Makuba
 * @created 25/01/2023
 **/
@RequiredArgsConstructor
@Getter
public enum SystemRoles {
    CREATE_USER("USER_MANAGEMENT", "CREATE USER"),
    UPDATE_USER("USER_MANAGEMENT", "UPDATE USER"),
    ENABLE_USER("USER_MANAGEMENT", "ENABLE USER"),
    DISABLE_USER("USER_MANAGEMENT", "DISABLE USER"),
    RESET_USER("USER_MANAGEMENT", "RESET USER"),
    DELETE_USER("USER_MANAGEMENT", "DELETE USER"),
    CREATE_PROFILE("USER_MANAGEMENT", "CREATE PROFILE"),
    UPDATE_PROFILE("USER_MANAGEMENT", "UPDATE PROFILE"),
    DISABLE_PROFILE("USER_MANAGEMENT", "DISABLE PROFILE"),
    DELETE_PROFILE("USER_MANAGEMENT", "DELETE PROFILE"),
    CREATE_ROLE("USER_MANAGEMENT", "CREATE ROLE"),
    BLOCK_USER("USER_MANAGEMENT", "BLOCK USER"),
    UNBLOCK_USER("USER_MANAGEMENT", "UNBLOCK USER"),
    ADD_MESSAGE_TEMPLATE("USER_MANAGEMENT", "ADD MESSAGE TEMPLATE"),
    DELETE_ROLE("USER_MANAGEMENT", "DELETE ROLE");


    final String moduleName;
    final String processName;
    final String onCompletionTopic = "userCompletion-in-0";
    final String nextStepTopic = "userNextStep-in-0";

   
}

