package io.avania.io.usermanagement.workflowService.constants;

import lombok.Getter;

/**
 * @author David C Makuba
 * @created 03/08/2022
 **/
@Getter

public enum WorkFlowResponseStatus {
    STAGED(204),NOT_PRESENT(200);
    private final int status;
    WorkFlowResponseStatus (int status){
        this.status= status;
    }
}
