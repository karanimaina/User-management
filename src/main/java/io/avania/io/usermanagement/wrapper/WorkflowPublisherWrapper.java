package io.avania.io.usermanagement.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder

public class WorkflowPublisherWrapper {
    private  Long itemId;
    private  String uniqueId;
    private String process;
    private String approvalStatus = "PENDING";
    private Object stagingDetails;
    private Object approverInfo;
    private String destination;
}
