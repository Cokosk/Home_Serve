package com.cokosk.homeserve.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cokosk.homeserve.entity.User;
import com.cokosk.homeserve.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {
    
    private final StringRedisTemplate redisTemplate;
    
    /**
     * 用户登录
     * 使用Redis缓存用户信息
     */
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        // 先从Redis缓存获取
        String cacheKey = "user:token:" + username;
        String cachedToken = redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedToken != null) {
            result.put("code", 200);
            result.put("token", cachedToken);
            result.put("message", "登录成功(缓存)");
            return result;
        }
        
        // 查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", password);
        queryWrapper.eq("status", 1);
        
        User user = this.getOne(queryWrapper);
        
        if (user == null) {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
            return result;
        }
        
        // 生成简单token（实际项目应使用JWT）
        String token = "token_" + System.currentTimeMillis() + "_" + user.getId();
        
        // 缓存用户信息到Redis
        redisTemplate.opsForValue().set(cacheKey, token, Duration.ofHours(24));
        // 缓存用户详情
        String userKey = "user:info:" + user.getId();
        redisTemplate.opsForHash().putAll(userKey, Map.of(
            "id", String.valueOf(user.getId()),
            "username", user.getUsername(),
            "nickname", user.getNickname() != null ? user.getNickname() : "",
            "role", String.valueOf(user.getRole())
        ));
        redisTemplate.expire(userKey, Duration.ofHours(24));
        
        result.put("code", 200);
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("role", user.getRole());
        result.put("message", "登录成功");
        
        log.info("用户登录成功: username={}, userId={}", username, user.getId());
        
        return result;
    }
    
    /**
     * 用户注册
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户名是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (this.count(queryWrapper) > 0) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            return result;
        }
        
        // 设置默认值
        user.setRole(0); // 普通用户
        user.setStatus(1); // 正常
        user.setCreditScore(100); // 默认信用分
        
        boolean saved = this.save(user);
        
        if (saved) {
            result.put("code", 200);
            result.put("message", "注册成功");
            result.put("userId", user.getId());
        } else {
            result.put("code", 500);
            result.put("message", "注册失败");
        }
        
        return result;
    }
    
    /**
     * 根据ID获取用户信息（优先从Redis缓存）
     */
    public User getUserById(Long userId) {
        String userKey = "user:info:" + userId;
        
        // 尝试从Redis获取
        Map<Object, Object> cached = redisTemplate.opsForHash().entries(userKey);
        if (cached != null && !cached.isEmpty()) {
            User user = new User();
            user.setId(Long.parseLong((String) cached.get("id")));
            user.setUsername((String) cached.get("username"));
            user.setNickname((String) cached.get("nickname"));
            user.setRole(Integer.parseInt((String) cached.get("role")));
            return user;
        }
        
        // 从数据库获取并缓存
        User user = this.getById(userId);
        if (user != null) {
            redisTemplate.opsForHash().putAll(userKey, Map.of(
                "id", String.valueOf(user.getId()),
                "username", user.getUsername(),
                "nickname", user.getNickname() != null ? user.getNickname() : "",
                "role", String.valueOf(user.getRole())
            ));
            redisTemplate.expire(userKey, Duration.ofHours(24));
        }
        
        return user;
    }
    
    /**
     * 清除用户缓存
     */
    public void clearCache(Long userId) {
        redisTemplate.delete("user:info:" + userId);
    }
}