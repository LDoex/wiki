package com.oyyk.wiki.controller;


import com.oyyk.wiki.domain.Test;
import com.oyyk.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Value("${test.hello: Test}")
    private String testHello;

    @Resource
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;


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

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
}
