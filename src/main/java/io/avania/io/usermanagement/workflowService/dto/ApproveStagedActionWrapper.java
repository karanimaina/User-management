package io.avania.io.usermanagement.workflowService.dto;

import lombok.*;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveStagedActionWrapper {
    private long stageId;
    private String workSpaceId;
    private boolean approved;
    private String approverDetails;
    private long approverId;
}
