package com.neuedu.controller.backend;

import com.neuedu.common.WebSocket;
import com.neuedu.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.controller.backend
 * @Author: 郝毓才
 * @CreateTime: 2019-11-27 15:23
 * @Description: 后台管理员使用websocket进行消息推送
 */
@RestController
@RequestMapping("/message")
public class SendMessageController {
    @Autowired
    private WebSocket webSocket;
    @RequestMapping("/sendMessage.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public void sendMessage(String text){
        webSocket.sendAllMessage(text);
    }
}
