package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello(){
        return "hello world!";
    }
    @GetMapping("/hello1")
    public List<String> hello1(){
        return List.of("hello","world!!!");
    }
}
