package com.openclaw.deploy.service;

import com.openclaw.deploy.dto.DeployRequest;
import com.openclaw.deploy.entity.DeployOrder;
import com.openclaw.deploy.repository.DeployOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployService {
    
    private final DeployOrderRepository deployOrderRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public DeployOrder createOrder(DeployRequest request) {
        DeployOrder order = new DeployOrder();
        order.setEmail(request.getEmail());
        order.setPlan(request.getPlan());
        order.setNotes(request.getNotes());
        
        try {
            order.setSkills(objectMapper.writeValueAsString(request.getSkills()));
        } catch (Exception e) {
            log.error("序列化 skills 失败", e);
        }
        
        order.setStatus("pending");
        order.setCreatedAt(LocalDateTime.now());
        
        return deployOrderRepository.save(order);
    }
    
    public List<DeployOrder> getOrdersByEmail(String email) {
        return deployOrderRepository.findByEmail(email);
    }
    
    public DeployOrder getOrderById(Long id) {
        return deployOrderRepository.findById(id).orElse(null);
    }
    
    public List<DeployOrder> getPendingOrders() {
        return deployOrderRepository.findByStatus("pending");
    }
    
    @Transactional
    public void updateOrderStatus(Long id, String status, String accessUrl, String containerId) {
        DeployOrder order = deployOrderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            order.setAccessUrl(accessUrl);
            order.setContainerId(containerId);
            if ("completed".equals(status)) {
                order.setCompletedAt(LocalDateTime.now());
            }
            deployOrderRepository.save(order);
        }
    }
}