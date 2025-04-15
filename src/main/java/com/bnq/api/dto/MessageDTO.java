package com.bnq.api.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

import com.bnq.api.entity.ProcessedFlowType;

@Data
@Builder
public class MessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private String messageId;
    private String correlationId;
    private ProcessedFlowType processedFlowType;
}