package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisTestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/redis/test")
    public String testRedis(@RequestParam String value) {
        redisTemplate.opsForValue().set("myKey", value);

        String result = redisTemplate.opsForValue().get("myKey");

        return "Success! Wrote '" + value + "' to Redis and read back: " + result;
    }
}