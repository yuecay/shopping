package com.neuedu.controller.front;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;
import com.neuedu.pojo.User;
import com.neuedu.service.IShippingService;
import com.neuedu.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    IShippingService shippingService;
    @RequestMapping("/add.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse add(Shipping shipping, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录！");
        }
        shipping.setUserId(user.getId());
        return shippingService.add(shipping);
    }

    @RequestMapping("/del.do")
    public ServerResponse del(Integer shippingId){
        return shippingService.del(shippingId);
    }

    @RequestMapping("/select.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse select(Integer shippingId){
        return shippingService.select(shippingId);
    }

    @RequestMapping("/list.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse selectAddressList(@RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                            @RequestParam(name = "pageNum",required = false,defaultValue = "10") Integer pageSize,
                                            HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录！");
        }
        return shippingService.selectAddressList(user.getId(),pageNum,pageSize);
    }


}
