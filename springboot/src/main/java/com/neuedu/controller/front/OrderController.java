package com.neuedu.controller.front;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.neuedu.common.*;
import com.neuedu.dao.OrderMapper;
import com.neuedu.pojo.Order;
import com.neuedu.pojo.User;
import com.neuedu.service.IOrderService;
import com.neuedu.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping(value = "/order")
public class OrderController {
    @Autowired
    private WebSocket webSocket;
    @Autowired
    IOrderService orderService;

    /**
     * 创建订单接口
     */
    @RequestMapping("{shippingId}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse creatOrder(@PathVariable("shippingId") Integer shippingId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录！");
        }
        return orderService.creatOrder(user.getId(), shippingId);
    }

    /**
     * 用户获取订单的商品信息列表
     */
    @RequestMapping("/list.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse list(HttpSession session,
                               @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                               @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录！");
        }

        return orderService.list(user.getId(),pageNum,pageSize);
    }

    /**
     * 获取订单的商品信息
     */
    @RequestMapping("get_order_cart_product/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse get_order_cart_product(@PathVariable("orderNo") Long orderNo){
        return orderService.detail(orderNo);
    }
    /**
     * 订单详情detail
     */
    @RequestMapping("detail/{orderNo}")
    public ServerResponse detail(@PathVariable("orderNo") Long orderNo){
        return orderService.detail(orderNo);
    }
    /**
     * 取消订单
     */
    @RequestMapping("/cancel/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse cancel(@PathVariable("orderNo") Long orderNo){
        return orderService.cancel(orderNo);
    }
    /**
     * （支付宝）支付接口
     */
    @RequestMapping("/pay/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse pay(@PathVariable("orderNo") Long orderNo,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录！");
        }
        return orderService.pay(user.getId(),orderNo);
    }

    /**
     * 支付宝服务器回调商家服务器接口
     * @return
     */
    @RequestMapping("/callback.do")
    public String alipay_callback(HttpServletRequest request,HttpSession session){

        Map<String, String[]> callbackParameter = request.getParameterMap();
        Map<String,String> signParams = Maps.newHashMap();
        Iterator<String> it = callbackParameter.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            String[] values = callbackParameter.get(key);
            StringBuffer stringBuffer = new StringBuffer();
            if(values != null && values.length > 0){
                for (int i = 0;i < values.length;i++) {
                    stringBuffer.append(values[i]);
                    if(i!=values.length-1){
                        stringBuffer.append(",");
                    }
                }
            }
            signParams.put(key,stringBuffer.toString());
        }
        System.out.println(signParams);


        //验证签名，保证接口只被支付宝服务器调用
        try {
            signParams.remove("sign_type");
            boolean result = AlipaySignature.rsaCheckV2(signParams, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if(result){
                //验证通过
                System.out.println("支付宝验证签名通过");
                return orderService.callback(signParams);
            }else {
                return "fail";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "success";

    }

    /**
     * 根据订单号查询订单支付状态
     */
    @RequestMapping("/query_order_pay_status/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse query_order_pay_status(@PathVariable("orderNo") Long orderNo){
        return orderService.query_order_pay_status(orderNo);
    }

    /**
     * 根据订单号修改订单状态
     */
    @RequestMapping("/updateOrderStatusByOrderNo/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse updateOrderStatusByOrderNo(@PathVariable("orderNo") Long orderNo,
                                                     HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录！");
        }
        return orderService.updateOrderStatusByOrderNo(orderNo);
    }

    @RequestMapping("/remindSend/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public void remindSend(@PathVariable("orderNo") String orderNo,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ;
        }
        String text= "请将订单号为： "+orderNo+" 的订单发货！";
        webSocket.sendOneMessage("2",text);//2表示李四的id
        System.out.println("执行成功!!!!!!!!");
    }
}
