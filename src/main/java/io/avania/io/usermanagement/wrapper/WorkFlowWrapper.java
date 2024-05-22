package io.avania.io.usermanagement.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * @author David C Makuba
 * @created 20/07/2022
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkFlowWrapper {
    private  long id;
    private String name;
    private String remarks;
    private String process;
    private String workFlowStepsOrder;
    private boolean active=true;
}
