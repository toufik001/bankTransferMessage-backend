package com.bnq.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "partners")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String alias;
    
    private String type;
    
    @Enumerated(EnumType.STRING)
    private Direction direction;
    
    private String application;
    
    @Enumerated(EnumType.STRING)
    private ProcessedFlowType processedFlowType;
    
    private String description;
}