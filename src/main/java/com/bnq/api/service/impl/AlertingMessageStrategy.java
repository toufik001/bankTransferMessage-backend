package com.bnq.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.bnq.api.entity.Message;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.service.MessageProcessingStrategy;

@Slf4j
@Component
public class AlertingMessageStrategy implements MessageProcessingStrategy {
    
    @Override
    public ProcessedFlowType getType() {
        return ProcessedFlowType.ALERTING;
    }

    @Override
    public void processMessage(Message message) {
        log.debug("Processing alerting message: {}", message.getMessageId());
        // Implement alerting specific logic here
        message.setStatus(Message.Status.PROCESSED);
    }
}