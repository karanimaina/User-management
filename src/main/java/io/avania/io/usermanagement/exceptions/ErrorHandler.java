package io.avania.io.usermanagement.exceptions;

import io.avania.io.usermanagement.wrapper.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author David C Makuba
 * @created 21/02/2023
 **/
@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({WorkFlowPresentException.class, AuthException.class})
    public Mono<ResponseEntity<UniversalResponse>> resolveCustomExceptions(Exception e) {
        return Mono.fromCallable (() -> {
            log.info ("workflow is present, notify caller request is staged");
            return UniversalResponse.builder ().status (200).message ("Request has been staged for approval").build ();
        }).map (ResponseEntity::ok).publishOn (Schedulers.boundedElastic ());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, WebExchangeBindException.class})
    public Mono<ResponseEntity<UniversalResponse>> resolveMethodArgumentException(Exception e) {
        return Mono.fromCallable (() -> {
            log.error ("Invalid method arguments passed", e);
            return UniversalResponse.builder ().status (400).message ("Invalid payload params")
                    .build ();
        }).map (ResponseEntity::ok).publishOn (Schedulers.boundedElastic ());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<UniversalResponse>> handleAccessDeniedException(Exception e){
        return Mono.fromCallable (()-> UniversalResponse.builder ().status (403).message ("Invalid permissions to perform action")
                .build ()).map (ResponseEntity::ok).publishOn (Schedulers.boundedElastic ());
    }
    @ExceptionHandler({Exception.class})
    public Mono<ResponseEntity<UniversalResponse>> resolveGlobalException(Exception e) {
        return Mono.fromCallable (() -> {
            log.error ("Server error===> {}", e.getMessage (), e);
            return UniversalResponse.builder ().status (400).message ("An error occurred").build ();
        }).map (ResponseEntity::ok).publishOn (Schedulers.boundedElastic ());
    }
}


