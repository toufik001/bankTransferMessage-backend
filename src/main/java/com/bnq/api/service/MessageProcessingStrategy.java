package com.bnq.api.service;

import com.bnq.api.entity.Message;
import com.bnq.api.entity.ProcessedFlowType;

public interface MessageProcessingStrategy {
    ProcessedFlowType getType();
    void processMessage(Message message);
}