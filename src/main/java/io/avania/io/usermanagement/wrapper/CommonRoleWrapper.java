package io.avania.io.usermanagement.wrapper;

import lombok.*;

import java.util.List;

/**
 * @author David C Makuba
 * @created 06/02/2023
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonRoleWrapper {
    private Long roleId;
    private String name;
    private  String remarks;
    private Boolean systemRole;
    private Long profileId;
    private List<Long> roleList;
}
