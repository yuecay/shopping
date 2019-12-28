package com.neuedu.controller;


import com.neuedu.common.WebSocket;
import com.neuedu.utils.RedisApi;
import com.neuedu.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class HelloController {

    @Autowired
    private WebSocket webSocket;
    @Value("${server.port}")
    int port;
    @RequestMapping(value = "/hello")
    public String hello(){
        return "nihao";
    }



    @Autowired
    RedisApi redisApi;
    @RequestMapping("/testJedis")
    public String testJedis(){
        redisApi.set("redisapi","nihao");
        return redisApi.get("redisapi");
    }

    @GetMapping("/sendAllWebSocket")
    public String sendAllWebSocket() {
        String text="你们好！这是websocket群体发送！";
        webSocket.sendAllMessage(text);
        return text;
    }

    @GetMapping("/sendOneWebSocket/{userName}")
    public String sendOneWebSocket(@PathVariable("userName") String userName) {
        String text=userName+" 你好！ 这是websocket单人发送！";
        webSocket.sendOneMessage(userName,text);
        return text;
    }

    @RequestMapping("/ExcelDownload")
    public void excelDownload(HttpServletResponse response) throws IOException {

        System.out.println("开始");

    }




}
