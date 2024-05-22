package io.avania.io.usermanagement.workflowService.Exception;

/**
 * @author David C Makuba
 * @created 07/02/2023
 **/
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super (message);
    }
}
