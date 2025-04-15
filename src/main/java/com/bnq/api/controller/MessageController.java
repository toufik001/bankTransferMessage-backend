package com.bnq.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bnq.api.dto.MessageDTO;
import com.bnq.api.service.MessageService;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Message management APIs")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Get all messages with pagination")
    public Page<MessageDTO> getMessages(Pageable pageable) {
        return messageService.getAllMessages(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a message by ID")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessage(id));
    }
}