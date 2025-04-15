package com.bnq.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bnq.api.entity.Message;
import com.bnq.api.entity.ProcessedFlowType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessor {

    private final Map<ProcessedFlowType, MessageProcessingStrategy> strategies;

    @Autowired
    public MessageProcessor(List<MessageProcessingStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                MessageProcessingStrategy::getType,
                Function.identity()
            ));
    }

    @Async("messageProcessingExecutor")
    public void processMessage(Message message) {
        log.debug("Processing message {} with flow type {}", message.getMessageId(), message.getProcessedFlowType());
        
        MessageProcessingStrategy strategy = strategies.get(message.getProcessedFlowType());
        if (strategy == null) {
            log.error("No strategy found for flow type: {}", message.getProcessedFlowType());
            throw new IllegalArgumentException("Unsupported flow type: " + message.getProcessedFlowType());
        }
        
        try {
            strategy.processMessage(message);
            log.info("Successfully processed message: {}", message.getMessageId());
        } catch (Exception e) {
            log.error("Error processing message: {}", message.getMessageId(), e);
            throw new RuntimeException("Message processing failed", e);
        }
    }
}