package io.avania.io.usermanagement.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
@Builder
public class WorkflowCreationWrapper {
    private  Long itemId;
    private  String uniqueId;
    private String process;
    private String approvalStatus = "PENDING";

    private Object stagingDetails;
    private Object approverInfo;
    private String onCompletionTopic;
    private String nextStepTopic;


    private int page;
    private int size;

}




















