package io.avania.io.usermanagement.workflowService;

import io.avania.io.usermanagement.constants.SystemProcess;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WorkFlowFilter {
    SystemProcess processName ();
}
