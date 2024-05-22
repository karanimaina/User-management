package io.avania.io.usermanagement.workflowService.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author David C Makuba
 * @created 23/09/2022
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkFlowDto {
    private long id;
    private String name;
    private String remarks;
    private String process;
   // private String workSpaceId;
    private Date createdDate;
    private Date updateTime;
    private boolean active=true;
    private List<Long> workFlowStepsOrder;
}

