package io.avania.io.usermanagement.service.impl;

import com.eclectics.io.usermodule.wrapper.WorkflowPublisherWrapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author David C Makuba
 * @created 02/02/2023
 **/
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final StreamBridge streamBridge;
    private final Gson gson;
    private static final String EMAIL_TOPIC = "email-out-0";
    private static final String CREATE_ITEM_TOPIC = "esb-create-item";

    public void sendEmailNotificationMessage(String message, String email, String subject) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("sendTo", email);
        messageMap.put("message", message);
        messageMap.put("title", subject);
        messageMap.put("type", "Channel Manager Notification");
        streamBridge.send(EMAIL_TOPIC, new Gson().toJson(messageMap));
    }

    public void publishUserCreation(WorkflowPublisherWrapper workFlowWrapper) {
        streamBridge.send("create-item", gson.toJson(workFlowWrapper));
    }
    public void esbCreateItemPublisher(String workflowApprovalDtoJson){
        streamBridge.send(CREATE_ITEM_TOPIC, workflowApprovalDtoJson);
    }

}
