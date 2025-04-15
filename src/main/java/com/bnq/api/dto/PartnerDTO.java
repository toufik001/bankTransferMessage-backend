package com.bnq.api.dto;

import com.bnq.api.entity.Direction;
import com.bnq.api.entity.ProcessedFlowType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerDTO {
    private Long id;
    
    @NotBlank(message = "Alias is required")
    private String alias;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotNull(message = "Direction is required")
    private Direction direction;
    
    private String application;
    
    private ProcessedFlowType processedFlowType;
    
    @NotBlank(message = "Description is required")
    private String description;

}