package com.bnq.api.service;

import com.bnq.api.batch.BatchMessageWriter;
import com.bnq.api.dto.MessageDTO;
import com.bnq.api.entity.Message;
import com.bnq.api.mapper.MessageMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageListener {

    private final BatchMessageWriter batchMessageWriter;
    private final MessageProcessor messageProcessor;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = "${ibm.mq.queue}")
    @Retryable(
        value = JMSException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public void receiveMessage(jakarta.jms.Message jmsMessage) throws JMSException {
        log.debug("Received message from MQ");
        
        String content = extractContent(jmsMessage);
        if (content == null) {
            log.warn("Unsupported message type received");
            return;
        }

        try {
            MessageDTO messageDTO = objectMapper.readValue(content, MessageDTO.class);
            messageDTO.setMessageId(jmsMessage.getJMSMessageID());
            messageDTO.setCorrelationId(jmsMessage.getJMSCorrelationID());
            
            Message messageEntity = messageMapper.toEntity(messageDTO);

            messageProcessor.processMessage(messageEntity);
            batchMessageWriter.enqueue(messageEntity);

            log.info("Message processed successfully: {}", messageDTO.getMessageId());
        } catch (Exception e) {
            log.error("Error processing message: {}", jmsMessage.getJMSMessageID(), e);
            batchMessageWriter.saveErrorMessage(jmsMessage.getJMSMessageID(), e);
        }
    }
    
    private String extractContent(jakarta.jms.Message message) throws JMSException {
        log.debug("Extracting content from message");
        
        if (message instanceof TextMessage) {
            String content = ((TextMessage) message).getText();
            log.debug("Extracted text content: {}", content);
            return content;
        }
        
        if (message instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) message;
            byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(bytes);
            String content = new String(bytes);
            log.debug("Extracted bytes content: {}", content);
            return content;
        }
        
        log.warn("Unsupported message type: {}", message.getClass().getName());
        return null;
    }
}