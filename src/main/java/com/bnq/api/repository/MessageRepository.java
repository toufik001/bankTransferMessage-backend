package com.bnq.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bnq.api.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}