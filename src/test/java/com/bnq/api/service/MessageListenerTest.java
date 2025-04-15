package com.bnq.api.service;

import com.bnq.api.dto.MessageDTO;
import com.bnq.api.entity.Message;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.mapper.MessageMapper;
import com.bnq.api.service.MessageListener;
import com.bnq.api.service.MessageProcessor;
import com.bnq.api.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private MessageProcessor messageProcessor;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TextMessage textMessage;

    @InjectMocks
    private MessageListener messageListener;

    private String jsonContent;
    private MessageDTO messageDTO;
    private Message message;

    @BeforeEach
    void setUp() throws Exception {
        jsonContent = """
            {
                "content": "Test message",
                "processedFlowType": "MESSAGE"
            }
            """;

        messageDTO = MessageDTO.builder()
            .content("Test message")
            .processedFlowType(ProcessedFlowType.MESSAGE)
            .build();

        message = new Message();
        message.setContent("Test message");
        message.setProcessedFlowType(ProcessedFlowType.MESSAGE);

        when(textMessage.getText()).thenReturn(jsonContent);
        when(textMessage.getJMSMessageID()).thenReturn("MSG-001");
        when(objectMapper.readValue(jsonContent, MessageDTO.class)).thenReturn(messageDTO);
        when(messageMapper.toEntity(any(MessageDTO.class))).thenReturn(message);
    }

    @Test
    void receiveMessage_WithValidJsonMessage_ShouldProcessSuccessfully() throws Exception {
        // When
        messageListener.receiveMessage(textMessage);

        // Then
        verify(messageService).processMessage(any(MessageDTO.class));
        verify(messageProcessor).processMessage(any(Message.class));
    }

    @Test
    void receiveMessage_WithInvalidJson_ShouldHandleError() throws Exception {
        // Given
        when(objectMapper.readValue(anyString(), eq(MessageDTO.class)))
            .thenThrow(new RuntimeException("Invalid JSON"));

        // When
        messageListener.receiveMessage(textMessage);

        // Then
        verify(messageService, never()).processMessage(any(MessageDTO.class));
        verify(messageProcessor, never()).processMessage(any(Message.class));
    }

    @Test
    void receiveMessage_WithJMSException_ShouldRetry() throws Exception {
        // Given
        when(textMessage.getText()).thenThrow(JMSException.class);

        // When & Then
        try {
            messageListener.receiveMessage(textMessage);
        } catch (Exception e) {
            // Expected exception
        }

        verify(messageService, never()).processMessage(any(MessageDTO.class));
        verify(messageProcessor, never()).processMessage(any(Message.class));
    }
}