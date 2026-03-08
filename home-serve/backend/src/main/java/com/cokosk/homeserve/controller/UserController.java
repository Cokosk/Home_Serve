package com.cokosk.homeserve.controller;

import com.cokosk.homeserve.entity.User;
import com.cokosk.homeserve.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        log.info("用户登录: username={}", username);
        
        return userService.login(username, password);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        log.info("用户注册: username={}", user.getUsername());
        return userService.register(user);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userService.getUserById(userId);
        if (user != null) {
            result.put("code", 200);
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        
        return result;
    }
}