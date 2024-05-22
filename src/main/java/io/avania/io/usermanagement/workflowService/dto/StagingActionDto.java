package io.avania.io.usermanagement.workflowService.dto;

import lombok.*;

import java.util.List;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StagingActionDto {
    private long id;
    private long workflowId;
    private String stagingUserDetails;
    private String stagingCurrentData;
    private String stagingPreviousData;
    private String process;
    private long approverId;
    private Boolean canApprove;
    private boolean isFinalized;
    private int page;
    private int size;
    private List<String> processes;
}
