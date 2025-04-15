package com.bnq.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bnq.api.dto.PartnerDTO;
import com.bnq.api.entity.Direction;
import com.bnq.api.entity.Partner;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.mapper.PartnerMapper;
import com.bnq.api.repository.PartnerRepository;
import com.bnq.api.service.impl.PartnerServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceImplTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private PartnerMapper partnerMapper;

    @InjectMocks
    private PartnerServiceImpl partnerService;

    private Partner partner;
    private PartnerDTO partnerDTO;

    @BeforeEach
    void setUp() {
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
    void getAllPartners_ShouldReturnListOfPartners() {
        // Given
        when(partnerRepository.findAll()).thenReturn(List.of(partner));
        when(partnerMapper.toDTO(partner)).thenReturn(partnerDTO);

        // When
        List<PartnerDTO> result = partnerService.getAllPartners();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(partnerDTO, result.get(0));
        verify(partnerRepository).findAll();
        verify(partnerMapper).toDTO(partner);
    }

    @Test
    void createPartner_WhenAliasDoesNotExist_ShouldCreatePartner() {
        // Given
        when(partnerRepository.existsByAlias(partnerDTO.getAlias())).thenReturn(false);
        when(partnerMapper.toEntity(partnerDTO)).thenReturn(partner);
        when(partnerRepository.save(partner)).thenReturn(partner);
        when(partnerMapper.toDTO(partner)).thenReturn(partnerDTO);

        // When
        PartnerDTO result = partnerService.createPartner(partnerDTO);

        // Then
        assertNotNull(result);
        assertEquals(partnerDTO, result);
        verify(partnerRepository).existsByAlias(partnerDTO.getAlias());
        verify(partnerMapper).toEntity(partnerDTO);
        verify(partnerRepository).save(partner);
        verify(partnerMapper).toDTO(partner);
    }

    @Test
    void createPartner_WhenAliasExists_ShouldThrowException() {
        // Given
        when(partnerRepository.existsByAlias(partnerDTO.getAlias())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> partnerService.createPartner(partnerDTO));
        verify(partnerRepository).existsByAlias(partnerDTO.getAlias());
        verify(partnerMapper, never()).toEntity(any());
        verify(partnerRepository, never()).save(any());
    }

    @Test
    void deletePartner_WhenPartnerExists_ShouldDeletePartner() {
        // Given
        when(partnerRepository.existsById(1L)).thenReturn(true);

        // When
        partnerService.deletePartner(1L);

        // Then
        verify(partnerRepository).existsById(1L);
        verify(partnerRepository).deleteById(1L);
    }

    @Test
    void deletePartner_WhenPartnerDoesNotExist_ShouldThrowException() {
        // Given
        when(partnerRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> partnerService.deletePartner(1L));
        verify(partnerRepository).existsById(1L);
        verify(partnerRepository, never()).deleteById(any());
    }

    @Test
    void existsByAlias_ShouldDelegateToRepository() {
        // Given
        String alias = "test-partner";
        when(partnerRepository.existsByAlias(alias)).thenReturn(true);

        // When
        boolean result = partnerService.existsByAlias(alias);

        // Then
        assertTrue(result);
        verify(partnerRepository).existsByAlias(alias);
    }
}