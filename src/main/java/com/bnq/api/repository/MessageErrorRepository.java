package com.bnq.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bnq.api.entity.MessageError;

@Repository
public interface MessageErrorRepository extends JpaRepository<MessageError, Long> {
    // Custom queries can be added here if needed
}