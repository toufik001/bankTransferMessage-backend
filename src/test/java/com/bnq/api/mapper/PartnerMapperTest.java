package com.bnq.api.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bnq.api.dto.PartnerDTO;
import com.bnq.api.entity.Direction;
import com.bnq.api.entity.Partner;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.mapper.PartnerMapper;

import static org.junit.jupiter.api.Assertions.*;

class PartnerMapperTest {

    private PartnerMapper partnerMapper;
    private Partner partner;
    private PartnerDTO partnerDTO;

    @BeforeEach
    void setUp() {
        partnerMapper = new PartnerMapper();

        partner = new Partner();
        partner.setId(1L);
        partner.setAlias("test-partner");
        partner.setType("TEST");
        partner.setDirection(Direction.INBOUND);
        partner.setApplication("TestApp");
        partner.setProcessedFlowType(ProcessedFlowType.MESSAGE);
        partner.setDescription("Test Partner");

        partnerDTO = PartnerDTO.builder()
            .id(1L)
            .alias("test-partner")
            .type("TEST")
            .direction(Direction.INBOUND)
            .application("TestApp")
            .processedFlowType(ProcessedFlowType.MESSAGE)
            .description("Test Partner")
            .build();
    }

    @Test
    void toDTO_ShouldMapAllFields() {
        // When
        PartnerDTO result = partnerMapper.toDTO(partner);

        // Then
        assertNotNull(result);
        assertEquals(partner.getId(), result.getId());
        assertEquals(partner.getAlias(), result.getAlias());
        assertEquals(partner.getType(), result.getType());
        assertEquals(partner.getDirection().name(), result.getDirection().name());
        assertEquals(partner.getApplication(), result.getApplication());
        assertEquals(partner.getProcessedFlowType().name(), result.getProcessedFlowType().name());
        assertEquals(partner.getDescription(), result.getDescription());
    }

    @Test
    void toDTO_WhenProcessedFlowTypeIsNull_ShouldMapOtherFields() {
        // Given
        partner.setProcessedFlowType(null);

        // When
        PartnerDTO result = partnerMapper.toDTO(partner);

        // Then
        assertNotNull(result);
        assertNull(result.getProcessedFlowType());
        assertEquals(partner.getId(), result.getId());
        assertEquals(partner.getAlias(), result.getAlias());
        assertEquals(partner.getType(), result.getType());
        assertEquals(partner.getDirection().name(), result.getDirection().name());
        assertEquals(partner.getApplication(), result.getApplication());
        assertEquals(partner.getDescription(), result.getDescription());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // When
        Partner result = partnerMapper.toEntity(partnerDTO);

        // Then
        assertNotNull(result);
        assertEquals(partnerDTO.getId(), result.getId());
        assertEquals(partnerDTO.getAlias(), result.getAlias());
        assertEquals(partnerDTO.getType(), result.getType());
        assertEquals(partnerDTO.getDirection().name(), result.getDirection().name());
        assertEquals(partnerDTO.getApplication(), result.getApplication());
        assertEquals(partnerDTO.getProcessedFlowType().name(), result.getProcessedFlowType().name());
        assertEquals(partnerDTO.getDescription(), result.getDescription());
    }

    @Test
    void toEntity_WhenProcessedFlowTypeIsNull_ShouldMapOtherFields() {
        // Given
        partnerDTO.setProcessedFlowType(null);

        // When
        Partner result = partnerMapper.toEntity(partnerDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getProcessedFlowType());
        assertEquals(partnerDTO.getId(), result.getId());
        assertEquals(partnerDTO.getAlias(), result.getAlias());
        assertEquals(partnerDTO.getType(), result.getType());
        assertEquals(partnerDTO.getDirection().name(), result.getDirection().name());
        assertEquals(partnerDTO.getApplication(), result.getApplication());
        assertEquals(partnerDTO.getDescription(), result.getDescription());
    }
}