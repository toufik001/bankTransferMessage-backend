package com.bnq.api.mapper;

import org.springframework.stereotype.Component;

import com.bnq.api.dto.MessageDTO;
import com.bnq.api.entity.Message;

@Component
public class MessageMapper {
    
    public MessageDTO toDTO(Message entity) {
        return MessageDTO.builder()
            .id(entity.getId())
            .content(entity.getContent())
            .timestamp(entity.getTimestamp())
            .messageId(entity.getMessageId())
            .correlationId(entity.getCorrelationId())
            .processedFlowType(entity.getProcessedFlowType())
            .build();
    }
    
    public Message toEntity(MessageDTO dto) {
        Message entity = new Message();
        entity.setId(dto.getId());
        entity.setContent(dto.getContent());
        entity.setTimestamp(dto.getTimestamp());
        entity.setMessageId(dto.getMessageId());
        entity.setCorrelationId(dto.getCorrelationId());
        entity.setProcessedFlowType(dto.getProcessedFlowType());
        return entity;
    }
}