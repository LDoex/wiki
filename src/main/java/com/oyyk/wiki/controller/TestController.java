package com.oyyk.wiki.controller;


import com.oyyk.wiki.domain.Test;
import com.oyyk.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {

    @Value("${test.hello: Test}")
    private String testHello;

    @Resource
    private TestService testService;


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

    @GetMapping("/test/list")
    public List<Test> list(){return testService.list();}
}