package com.oyyk.wiki.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${test.hello: Test}")
    private String testHello;

    @RequestMapping("/hello") /**RequestMapping支持包括GET, POST, PUSH, DELETE在内的所有请求
                                  * 比如，要只响应GET请求，可以使用GetMapping
                                 */
    public String hello(){
        return "Hello World!" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World! Post. "+name;
    }
}
