package com.bnq.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bnq.api.dto.MessageDTO;
import com.bnq.api.entity.Message;
import com.bnq.api.mapper.MessageMapper;
import com.bnq.api.repository.MessageRepository;
import com.bnq.api.service.impl.MessageServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Message message;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        message = new Message();
        message.setId(1L);
        message.setContent("Test content");
        message.setTimestamp(LocalDateTime.now());
        message.setMessageId("MSG-001");
        message.setCorrelationId("CORR-001");

        messageDTO = MessageDTO.builder()
            .id(1L)
            .content("Test content")
            .timestamp(LocalDateTime.now())
            .messageId("MSG-001")
            .correlationId("CORR-001")
            .build();
    }

    @Test
    void getAllMessages_ShouldReturnPageOfMessages() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Message> messagePage = new PageImpl<>(List.of(message));
        when(messageRepository.findAll(pageable)).thenReturn(messagePage);
        when(messageMapper.toDTO(any(Message.class))).thenReturn(messageDTO);

        // When
        Page<MessageDTO> result = messageService.getAllMessages(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(messageDTO, result.getContent().get(0));
        verify(messageRepository).findAll(pageable);
        verify(messageMapper).toDTO(message);
    }

    @Test
    void getMessage_WhenMessageExists_ShouldReturnMessage() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(messageMapper.toDTO(message)).thenReturn(messageDTO);

        // When
        MessageDTO result = messageService.getMessage(1L);

        // Then
        assertNotNull(result);
        assertEquals(messageDTO, result);
        verify(messageRepository).findById(1L);
        verify(messageMapper).toDTO(message);
    }

    @Test
    void getMessage_WhenMessageDoesNotExist_ShouldThrowException() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> messageService.getMessage(1L));
        verify(messageRepository).findById(1L);
        verify(messageMapper, never()).toDTO(any());
    }

    @Test
    void processMessage_ShouldSaveMessage() {
        // Given
        Message mappedMessage = new Message();
        when(messageMapper.toEntity(messageDTO)).thenReturn(mappedMessage);
        when(messageRepository.save(mappedMessage)).thenReturn(message);

        // When
        messageService.processMessage(messageDTO);

        // Then
        verify(messageMapper).toEntity(messageDTO);
        verify(messageRepository).save(mappedMessage);
    }
}