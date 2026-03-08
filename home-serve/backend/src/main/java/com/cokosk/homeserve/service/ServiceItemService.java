package com.cokosk.homeserve.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cokosk.homeserve.entity.ServiceItem;
import com.cokosk.homeserve.mapper.ServiceItemMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 服务项目服务类
 * 使用Redis缓存热点数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceItemService extends ServiceImpl<ServiceItemMapper, ServiceItem> {
    
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取热门服务列表（缓存）
     */
    public List<ServiceItem> getHotServices() {
        String cacheKey = "service:hot";
        
        // 尝试从缓存获取
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                log.debug("从缓存获取热门服务");
                return objectMapper.readValue(cached, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ServiceItem.class));
            } catch (JsonProcessingException e) {
                log.error("解析热门服务缓存失败", e);
            }
        }
        
        // 从数据库查询热门服务（取前10个）
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.last("LIMIT 10");
        List<ServiceItem> list = this.list(queryWrapper);
        
        // 缓存结果
        if (!list.isEmpty()) {
            try {
                String json = objectMapper.writeValueAsString(list);
                redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(30));
            } catch (JsonProcessingException e) {
                log.error("序列化热门服务缓存失败", e);
            }
        }
        
        return list;
    }
    
    /**
     * 根据分类ID获取服务列表（缓存）
     */
    public List<ServiceItem> getServicesByCategory(Long categoryId) {
        String cacheKey = "service:category:" + categoryId;
        
        // 尝试从缓存获取
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                log.debug("从缓存获取分类服务: categoryId={}", categoryId);
                return objectMapper.readValue(cached, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ServiceItem.class));
            } catch (JsonProcessingException e) {
                log.error("解析分类服务缓存失败", e);
            }
        }
        
        // 从数据库查询
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", categoryId);
        queryWrapper.eq("status", 1);
        List<ServiceItem> list = this.list(queryWrapper);
        
        // 缓存结果（30分钟）
        if (!list.isEmpty()) {
            try {
                String json = objectMapper.writeValueAsString(list);
                redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(30));
            } catch (JsonProcessingException e) {
                log.error("序列化分类服务缓存失败", e);
            }
        }
        
        return list;
    }
    
    /**
     * 获取服务详情（缓存）
     */
    public ServiceItem getServiceDetail(Long serviceId) {
        String cacheKey = "service:detail:" + serviceId;
        
        // 尝试从缓存获取
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                log.debug("从缓存获取服务详情: serviceId={}", serviceId);
                return objectMapper.readValue(cached, ServiceItem.class);
            } catch (JsonProcessingException e) {
                log.error("解析服务详情缓存失败", e);
            }
        }
        
        // 从数据库查询
        ServiceItem service = this.getById(serviceId);
        
        // 缓存结果（1小时）
        if (service != null) {
            try {
                String json = objectMapper.writeValueAsString(service);
                redisTemplate.opsForValue().set(cacheKey, json, Duration.ofHours(1));
            } catch (JsonProcessingException e) {
                log.error("序列化服务详情缓存失败", e);
            }
        }
        
        return service;
    }
    
    /**
     * 分页查询服务列表
     */
    public Page<ServiceItem> getServicePage(Long categoryId, Integer pageNum, Integer pageSize) {
        Page<ServiceItem> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        queryWrapper.eq("status", 1);
        
        return this.page(page, queryWrapper);
    }
    
    /**
     * 清除服务相关缓存
     */
    public void clearServiceCache(Long categoryId, Long serviceId) {
        if (categoryId != null) {
            redisTemplate.delete("service:category:" + categoryId);
        }
        if (serviceId != null) {
            redisTemplate.delete("service:detail:" + serviceId);
        }
        // 清除热门服务缓存
        redisTemplate.delete("service:hot");
    }
    
    /**
     * 更新服务后清除缓存
     */
    public boolean updateService(ServiceItem service) {
        boolean updated = this.updateById(service);
        if (updated) {
            clearServiceCache(service.getCategoryId(), service.getId());
        }
        return updated;
    }
}