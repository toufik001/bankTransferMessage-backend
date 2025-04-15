package com.bnq.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.bnq.api.entity.Message;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.service.MessageProcessingStrategy;

@Slf4j
@Component
public class StandardMessageStrategy implements MessageProcessingStrategy {
    
    @Override
    public ProcessedFlowType getType() {
        return ProcessedFlowType.MESSAGE;
    }

    @Override
    public void processMessage(Message message) {
        log.debug("Processing standard message: {}", message.getMessageId());
        // Implement standard message processing logic here
        message.setStatus(Message.Status.PROCESSED);
    }
}