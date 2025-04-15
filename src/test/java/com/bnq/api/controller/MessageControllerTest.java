package com.bnq.api.controller;

import com.bnq.api.controller.MessageController;
import com.bnq.api.dto.MessageDTO;
import com.bnq.api.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)    
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        messageDTO = MessageDTO.builder()
            .id(1L)
            .content("Test message")
            .timestamp(LocalDateTime.now())
            .messageId("MSG-001")
            .correlationId("CORR-001")
            .build();
    }

    @Test
    void getMessages_ShouldReturnPageOfMessages() throws Exception {
        // Given
        Page<MessageDTO> messagePage = new PageImpl<>(
            List.of(messageDTO),
            PageRequest.of(0, 10),
            1
        );
        when(messageService.getAllMessages(any())).thenReturn(messagePage);

        // When & Then
        mockMvc.perform(get("/messages")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content[0].id").value(messageDTO.getId()))
            .andExpect(jsonPath("$.content[0].content").value(messageDTO.getContent()))
            .andExpect(jsonPath("$.content[0].messageId").value(messageDTO.getMessageId()));
    }

    @Test
    void getMessage_WhenExists_ShouldReturnMessage() throws Exception {
        // Given
        when(messageService.getMessage(1L)).thenReturn(messageDTO);

        // When & Then
        mockMvc.perform(get("/messages/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(messageDTO.getId()))
            .andExpect(jsonPath("$.content").value(messageDTO.getContent()))
            .andExpect(jsonPath("$.messageId").value(messageDTO.getMessageId()));
    }

}