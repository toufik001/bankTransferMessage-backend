package com.bnq.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bnq.api.dto.MessageDTO;
import com.bnq.api.mapper.MessageMapper;
import com.bnq.api.repository.MessageRepository;
import com.bnq.api.service.MessageService;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDTO> getAllMessages(Pageable pageable) {
        return messageRepository.findAll(pageable)
            .map(messageMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDTO getMessage(Long id) {
        return messageRepository.findById(id)
            .map(messageMapper::toDTO)
            .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    @Override
    @Transactional
    public void processMessage(MessageDTO messageDTO) {
        messageRepository.save(messageMapper.toEntity(messageDTO));
    }
}