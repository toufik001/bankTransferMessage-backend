package com.bnq.api.mapper;

import org.springframework.stereotype.Component;

import com.bnq.api.dto.PartnerDTO;
import com.bnq.api.entity.Direction;
import com.bnq.api.entity.Partner;
import com.bnq.api.entity.ProcessedFlowType;

@Component
public class PartnerMapper {
    
    public PartnerDTO toDTO(Partner entity) {
        return PartnerDTO.builder()
            .id(entity.getId())
            .alias(entity.getAlias())
            .type(entity.getType())
            .direction(Direction.valueOf(entity.getDirection().name()))
            .application(entity.getApplication())
            .processedFlowType(entity.getProcessedFlowType() != null ? 
                ProcessedFlowType.valueOf(entity.getProcessedFlowType().name()) : null)
            .description(entity.getDescription())
            .build();
    }
    
    public Partner toEntity(PartnerDTO dto) {
        Partner entity = new Partner();
        entity.setId(dto.getId());
        entity.setAlias(dto.getAlias());
        entity.setType(dto.getType());
        entity.setDirection(Direction.valueOf(dto.getDirection().name()));
        entity.setApplication(dto.getApplication());
        entity.setProcessedFlowType(dto.getProcessedFlowType() != null ? 
            ProcessedFlowType.valueOf(dto.getProcessedFlowType().name()) : null);
        entity.setDescription(dto.getDescription());
        return entity;
    }
}