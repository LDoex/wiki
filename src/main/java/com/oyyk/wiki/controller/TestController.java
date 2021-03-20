package com.oyyk.wiki.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class TestController {
    @RequestMapping("/hello") /**RequestMapping支持包括GET, POST, PUSH, DELETE在内的所有请求
                                  * 比如，要只响应GET请求，可以使用GetMapping
                                 */
    public String hello(){
        return "Hello World!";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World! Post. "+name;
    }
}
