package com.bnq.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.bnq.api.entity.Message;
import com.bnq.api.repository.MessageRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void save_ShouldPersistMessage() {
        // Given
        Message message = new Message();
        message.setContent("Test content");
        message.setMessageId("MSG-001");
        message.setCorrelationId("CORR-001");
        message.setTimestamp(LocalDateTime.now());
        
        // When
        Message savedMessage = messageRepository.save(message);
        entityManager.flush();

        // Then
        Message foundMessage = entityManager.find(Message.class, savedMessage.getId());
        assertNotNull(foundMessage);
        assertEquals(message.getContent(), foundMessage.getContent());
        assertEquals(message.getMessageId(), foundMessage.getMessageId());
        assertEquals(message.getCorrelationId(), foundMessage.getCorrelationId());
    }

    @Test
    void findAll_WithPagination_ShouldReturnPageOfMessages() {
        // Given
        Message message1 = new Message();
        message1.setContent("Message 1");
        message1.setMessageId("MSG-001");
        message1.setTimestamp(LocalDateTime.now());
        entityManager.persist(message1);

        Message message2 = new Message();
        message2.setContent("Message 2");
        message2.setMessageId("MSG-002");
        message2.setTimestamp(LocalDateTime.now());
        entityManager.persist(message2);

        entityManager.flush();

        // When
        Page<Message> messagePage = messageRepository.findAll(PageRequest.of(0, 10));

        // Then
        assertNotNull(messagePage);
        assertEquals(2, messagePage.getTotalElements());
        assertTrue(messagePage.getContent().stream()
            .anyMatch(m -> m.getMessageId().equals("MSG-001")));
        assertTrue(messagePage.getContent().stream()
            .anyMatch(m -> m.getMessageId().equals("MSG-002")));
    }

    @Test
    void findById_WhenExists_ShouldReturnMessage() {
        // Given
        Message message = new Message();
        message.setContent("Test content");
        message.setMessageId("MSG-001");
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = entityManager.persist(message);
        entityManager.flush();

        // When
        Message foundMessage = messageRepository.findById(savedMessage.getId()).orElse(null);

        // Then
        assertNotNull(foundMessage);
        assertEquals(message.getContent(), foundMessage.getContent());
        assertEquals(message.getMessageId(), foundMessage.getMessageId());
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        // When
        var result = messageRepository.findById(999L);

        // Then
        assertTrue(result.isEmpty());
    }
}