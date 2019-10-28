package com.neuedu.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    @RequestMapping(value = "/hello")
    public String hello(){
        return "nihao";
    }


}
