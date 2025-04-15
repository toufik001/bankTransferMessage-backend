package com.bnq.api.service.impl;

import org.springframework.stereotype.Component;

import com.bnq.api.entity.Message;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.service.MessageProcessingStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationMessageStrategy implements MessageProcessingStrategy {
    
    @Override
    public ProcessedFlowType getType() {
        return ProcessedFlowType.NOTIFICATION;
    }

    @Override
    public void processMessage(Message message) {
        //log.debug("Processing notification message: {}", message.getMessageId());
        // Implement notification specific logic here
        message.setStatus(Message.Status.PROCESSED);
    }
}