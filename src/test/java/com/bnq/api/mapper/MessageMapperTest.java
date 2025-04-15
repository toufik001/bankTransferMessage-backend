package com.bnq.api.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bnq.api.dto.MessageDTO;
import com.bnq.api.entity.Message;
import com.bnq.api.mapper.MessageMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageMapperTest {

    private MessageMapper messageMapper;
    private Message message;
    private MessageDTO messageDTO;
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        messageMapper = new MessageMapper();
        timestamp = LocalDateTime.now();

        message = new Message();
        message.setId(1L);
        message.setContent("Test content");
        message.setTimestamp(timestamp);
        message.setMessageId("MSG-001");
        message.setCorrelationId("CORR-001");

        messageDTO = MessageDTO.builder()
            .id(1L)
            .content("Test content")
            .timestamp(timestamp)
            .messageId("MSG-001")
            .correlationId("CORR-001")
            .build();
    }

    @Test
    void toDTO_ShouldMapAllFields() {
        // When
        MessageDTO result = messageMapper.toDTO(message);

        // Then
        assertNotNull(result);
        assertEquals(message.getId(), result.getId());
        assertEquals(message.getContent(), result.getContent());
        assertEquals(message.getTimestamp(), result.getTimestamp());
        assertEquals(message.getMessageId(), result.getMessageId());
        assertEquals(message.getCorrelationId(), result.getCorrelationId());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // When
        Message result = messageMapper.toEntity(messageDTO);

        // Then
        assertNotNull(result);
        assertEquals(messageDTO.getId(), result.getId());
        assertEquals(messageDTO.getContent(), result.getContent());
        assertEquals(messageDTO.getTimestamp(), result.getTimestamp());
        assertEquals(messageDTO.getMessageId(), result.getMessageId());
        assertEquals(messageDTO.getCorrelationId(), result.getCorrelationId());
    }
}