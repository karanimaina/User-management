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
public class WorkSpaceDto {
    private String name;
    private String description;
    private String workSpaceId;
    private boolean isActive;
}
