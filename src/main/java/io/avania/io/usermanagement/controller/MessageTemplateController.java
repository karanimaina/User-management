package io.avania.io.usermanagement.controller;

import com.eclectics.io.usermodule.model.MessageTemplate;
import com.eclectics.io.usermodule.service.IUserInterface;
import com.eclectics.io.usermodule.wrapper.UniversalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author David C Makuba
 * @created 08/02/2023
 **/
@RestController
@RequestMapping("/api/v1/admin/template")
@RequiredArgsConstructor
public class MessageTemplateController {
    private final IUserInterface userInterface;

    @PostMapping("/types")
    @PreAuthorize("hasAnyRole('ESB-ADMIN')")
    public Mono<ResponseEntity<UniversalResponse>> getTemplatesTypes(){
        return userInterface.getMessageTypes ()
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/get")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') ")
    public Mono<ResponseEntity<UniversalResponse>> getTemplates(){
        return userInterface.getMessageTemplates ()
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ESB-ADMIN') AND hasAnyAuthority('ADD_MESSAGE_TEMPLATE')")
    public Mono<ResponseEntity<UniversalResponse>> addTemplate(@RequestBody  MessageTemplate messageTemplate){
        return userInterface.addMessageTemplate (messageTemplate)
                .map (ResponseEntity::ok)
                .publishOn (Schedulers.boundedElastic ());
    }


}
