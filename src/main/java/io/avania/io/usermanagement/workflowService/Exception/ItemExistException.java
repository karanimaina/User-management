package io.avania.io.usermanagement.workflowService.Exception;

/**
 * @author David C Makuba
 * @created 07/02/2023
 **/
public class ItemExistException extends RuntimeException{
    public ItemExistException(String message) {
        super (message);
    }
}
