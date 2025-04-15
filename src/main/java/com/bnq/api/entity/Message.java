package com.bnq.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String messageId;
    
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    private ProcessedFlowType processedFlowType;

    public enum Status {
        PENDING, PROCESSED, ERROR
    }
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}