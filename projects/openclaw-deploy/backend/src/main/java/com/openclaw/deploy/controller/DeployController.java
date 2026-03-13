package com.openclaw.deploy.controller;

import com.openclaw.deploy.dto.ApiResponse;
import com.openclaw.deploy.dto.DeployRequest;
import com.openclaw.deploy.entity.DeployOrder;
import com.openclaw.deploy.service.DeployService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeployController {
    
    private final DeployService deployService;
    
    @PostMapping("/deploy")
    public ApiResponse<DeployOrder> deploy(@Valid @RequestBody DeployRequest request) {
        try {
            DeployOrder order = deployService.createOrder(request);
            return ApiResponse.success(order);
        } catch (Exception e) {
            return ApiResponse.error("部署申请失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/orders")
    public ApiResponse<List<DeployOrder>> getOrders(@RequestParam String email) {
        List<DeployOrder> orders = deployService.getOrdersByEmail(email);
        return ApiResponse.success(orders);
    }
    
    @GetMapping("/orders/{id}")
    public ApiResponse<DeployOrder> getOrder(@PathVariable Long id) {
        DeployOrder order = deployService.getOrderById(id);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        return ApiResponse.success(order);
    }
    
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}