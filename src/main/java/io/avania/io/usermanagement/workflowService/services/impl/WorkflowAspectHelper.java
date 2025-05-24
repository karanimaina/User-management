package io.avania.io.usermanagement.workflowService.services.impl;


import com.google.gson.Gson;
import io.avania.io.usermanagement.service.impl.UserService;
import io.avania.io.usermanagement.workflowService.WorkFlowFilter;
import io.avania.io.usermanagement.workflowService.constants.WorkFlowResponseStatus;
import io.avania.io.usermanagement.workflowService.dto.StagingActionDto;
import io.avania.io.usermanagement.workflowService.services.StagingActionService;
import io.avania.io.usermanagement.wrapper.UniversalResponse;
import io.avania.io.usermanagement.wrapper.WorkFlowUserWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author David C Makuba
 * @created 12/02/2023
 **/
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowAspectHelper {
    private final UserService userService;
    private final StagingActionService stagingActionService;
    private final Gson gson;

    @Around("@annotation(io.avania.io.usermanagement.workflowService.WorkFlowFilter)")
    public Mono<UniversalResponse> checkAndStageWorkFlow(ProceedingJoinPoint point) {
        MethodSignature method = (MethodSignature) point.getSignature ();
        Object[] obj = point.getArgs ();
        String request = gson.toJson (obj[0]);
        WorkFlowFilter workFlowFilter = method.getMethod ().getAnnotation (WorkFlowFilter.class);
        String process = workFlowFilter.processName ().name ();
        return ReactiveSecurityContextHolder.getContext ()
                .flatMap (ctx-> Mono.just (ctx.getAuthentication ()))
                .flatMap (auth -> userService.getSystemUserByUsername (auth.getName ()))
                .flatMap (user -> {
                    StagingActionDto stagingActionDto = StagingActionDto.builder ()
                            .stagingUserDetails (gson.toJson (new WorkFlowUserWrapper(user)))
                            .stagingCurrentData (gson.toJson (request))
                            .stagingPreviousData (null)
                            .process (process)
                            .build ();
                    WorkFlowResponseStatus workFlowResponseStatus = stagingActionService.checkAndStageWorkFlow (stagingActionDto);
                    if (workFlowResponseStatus == WorkFlowResponseStatus.STAGED) {
                        return Mono.just (UniversalResponse.builder ().status (200).message ("Request staged for approval").build ());
                    }
                    try {
                        return (Mono<UniversalResponse>) point.proceed ();
                    } catch (Throwable e) {
                        throw new RuntimeException (e);
                    }
                })
                .doOnError (err-> log.error ("An error occurred at workflow filter ===> {}",err.getMessage ()))
                .publishOn (Schedulers.boundedElastic ());
    }
}
