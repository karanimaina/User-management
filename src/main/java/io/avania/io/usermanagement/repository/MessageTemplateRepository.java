package io.avania.io.usermanagement.repository;



import io.avania.io.usermanagement.constants.MessageType;
import io.avania.io.usermanagement.model.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate,Long> {
    Optional<MessageTemplate> findByMessageTypeAndActiveTrueAndSoftDeleteFalse(MessageType messageType);
    MessageTemplate findByMessageTypeAndDefaultTemplateTrueAndSoftDeleteFalse(MessageType messageType);
}
