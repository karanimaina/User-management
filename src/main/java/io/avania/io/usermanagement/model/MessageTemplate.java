package io.avania.io.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.avania.io.usermanagement.constants.MessageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "esb_message_templates")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "update esb_message_templates set soft_delete=true")
@Where(clause = "soft_delete = false")
public class MessageTemplate extends BaseEntity{
    @Lob
    private String message;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    @JsonIgnore
    @Lob
    private String baseParams;
    private Boolean active = Boolean.TRUE;
    private boolean defaultTemplate=false;
}
