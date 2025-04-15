package com.bnq.api.controller;

import com.bnq.api.controller.PartnerController;
import com.bnq.api.dto.PartnerDTO;
import com.bnq.api.entity.Direction;
import com.bnq.api.entity.ProcessedFlowType;
import com.bnq.api.service.PartnerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartnerController.class)
class PartnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartnerService partnerService;

    @Autowired
    private ObjectMapper objectMapper;

    private PartnerDTO partnerDTO;

    @BeforeEach
    void setUp() {
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
    void getPartners_ShouldReturnListOfPartners() throws Exception {
        // Given
        when(partnerService.getAllPartners()).thenReturn(List.of(partnerDTO));

        // When & Then
        mockMvc.perform(get("/partners"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(partnerDTO.getId()))
            .andExpect(jsonPath("$[0].alias").value(partnerDTO.getAlias()))
            .andExpect(jsonPath("$[0].type").value(partnerDTO.getType()));
    }

    @Test
    void createPartner_WithValidData_ShouldCreatePartner() throws Exception {
        // Given
        when(partnerService.createPartner(any(PartnerDTO.class))).thenReturn(partnerDTO);

        // When & Then
        mockMvc.perform(post("/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partnerDTO)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(partnerDTO.getId()))
            .andExpect(jsonPath("$.alias").value(partnerDTO.getAlias()));
    }

    @Test
    void createPartner_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        PartnerDTO invalidPartner = PartnerDTO.builder()
            .alias("")  // Invalid: empty alias
            .type("")   // Invalid: empty type
            .build();

        // When & Then
        mockMvc.perform(post("/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPartner)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createPartner_WithDuplicateAlias_ShouldReturn400() throws Exception {
        // Given
        when(partnerService.createPartner(any(PartnerDTO.class)))
            .thenThrow(new RuntimeException("Partner with this alias already exists"));

        // When & Then
        mockMvc.perform(post("/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partnerDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deletePartner_WhenExists_ShouldDeletePartner() throws Exception {
        // Given
        doNothing().when(partnerService).deletePartner(1L);

        // When & Then
        mockMvc.perform(delete("/partners/{id}", 1L))
            .andExpect(status().isOk());

        verify(partnerService).deletePartner(1L);
    }

    @Test
    void deletePartner_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        doThrow(new RuntimeException("Partner not found"))
            .when(partnerService).deletePartner(99L);

        // When & Then
        mockMvc.perform(delete("/partners/{id}", 99L))
            .andExpect(status().isNotFound());
    }
}