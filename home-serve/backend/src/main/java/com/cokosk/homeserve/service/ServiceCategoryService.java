package com.cokosk.homeserve.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cokosk.homeserve.entity.ServiceCategory;
import com.cokosk.homeserve.mapper.ServiceCategoryMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 服务分类服务类
 * 使用Redis缓存热点数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceCategoryService extends ServiceImpl<ServiceCategoryMapper, ServiceCategory> {
    
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String CACHE_KEY = "service:category:list";
    private static final Duration CACHE_EXPIRE = Duration.ofMinutes(30);
    
    /**
     * 获取分类列表（带Redis缓存）
     */
    public List<ServiceCategory> getCategoryList() {
        // 1. 尝试从Redis获取
        String cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            try {
                log.debug("从Redis缓存获取分类列表");
                return objectMapper.readValue(cached, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ServiceCategory.class));
            } catch (JsonProcessingException e) {
                log.error("解析缓存数据失败", e);
            }
        }
        
        // 2. 从数据库查询
        QueryWrapper<ServiceCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderByAsc("sort");
        List<ServiceCategory> list = this.list(queryWrapper);
        
        // 3. 存入Redis缓存
        if (!list.isEmpty()) {
            try {
                String json = objectMapper.writeValueAsString(list);
                redisTemplate.opsForValue().set(CACHE_KEY, json, CACHE_EXPIRE);
                log.debug("分类列表已缓存");
            } catch (JsonProcessingException e) {
                log.error("序列化缓存数据失败", e);
            }
        }
        
        return list;
    }
    
    /**
     * 清除分类缓存（数据更新时调用）
     */
    public void clearCache() {
        redisTemplate.delete(CACHE_KEY);
        log.info("分类缓存已清除");
    }
    
    /**
     * 更新分类后清除缓存
     */
    public boolean updateCategory(ServiceCategory category) {
        boolean updated = this.updateById(category);
        if (updated) {
            clearCache();
        }
        return updated;
    }
    
    /**
     * 新增分类后清除缓存
     */
    public boolean saveCategory(ServiceCategory category) {
        boolean saved = this.save(category);
        if (saved) {
            clearCache();
        }
        return saved;
    }
}