package com.bnq.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bnq.api.dto.MessageDTO;

public interface MessageService {
    Page<MessageDTO> getAllMessages(Pageable pageable);
    MessageDTO getMessage(Long id);
    void processMessage(MessageDTO message);
}