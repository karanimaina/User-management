package io.avania.io.usermanagement.workflowService.Exception;

/**
 * @author David C Makuba
 * @created 07/02/2023
 **/
public class InvalidOperation extends RuntimeException{
    public InvalidOperation(String message) {
        super (message);
    }
}
