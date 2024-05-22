package io.avania.io.usermanagement.repository;

import com.eclectics.io.usermodule.constants.MessageType;
import com.eclectics.io.usermodule.model.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate,Long> {
    Optional<MessageTemplate> findByMessageTypeAndActiveTrueAndSoftDeleteFalse(MessageType messageType);
    MessageTemplate findByMessageTypeAndDefaultTemplateTrueAndSoftDeleteFalse(MessageType messageType);
}
