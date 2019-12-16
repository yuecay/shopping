package com.neuedu.controller.backend;

import com.neuedu.common.ServerResponse;
import com.neuedu.dao.OrderMapper;
import com.neuedu.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.controller.backend
 * @Author: 郝毓才
 * @CreateTime: 2019-10-28 15:33
 * @Description: 后台商家订单相关操作
 */
@RestController
@RequestMapping("/manage/order")
public class ManageOrderController {

    @Autowired
    IOrderService orderService;

    /**
     * 查看订单list
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse list( @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){

        return orderService.selectOrderVOList(pageNum,pageSize);
    }

    /**
     * 订单详情
     * @param orderNo
     * @return
     */
    @RequestMapping("detail/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse detail(@PathVariable("orderNo") Long orderNo){
        return orderService.detail(orderNo);
    }
    //发货
    @RequestMapping("send_goods/{orderNo}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse send_goods(@PathVariable("orderNo") Long orderNo){
        return orderService.send_goods(orderNo);
    }

    //根据订单状态查询订单列表
    @RequestMapping("/findOrderListByStatus/{status}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse findOrderListByStatus(@PathVariable("status") Integer status){
        return orderService.findOrderListByStatus(status);
    }
}
