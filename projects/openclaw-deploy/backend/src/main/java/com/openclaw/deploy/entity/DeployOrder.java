package com.openclaw.deploy.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "deploy_order")
public class DeployOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String plan;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(columnDefinition = "TEXT")
    private String skills;
    
    @Column(nullable = false)
    private String status = "pending"; // pending, deploying, completed, failed
    
    private String accessUrl;
    
    private String containerId;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}