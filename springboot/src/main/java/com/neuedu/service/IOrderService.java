package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Order;
import com.neuedu.pojo.OrderItem;
import com.neuedu.vo.OrderVO;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface IOrderService {

    /**
     * 创建订单
     */
    public ServerResponse creatOrder(Integer userId,Integer shippingId);
    /**
     * 根据用户id获取订单的商品信息
     */

    public ServerResponse<List<Order>> list(Integer userId,Integer pageNum,Integer pageSize);
    /**
     * 支付
     */
    ServerResponse pay(Integer userId,Long orderNo);
    /**
     * 支付回调接口
     */
    String callback(Map<String,String> requestParams);
    /**
     * 根据订单号查询订单支付状态
     */
    public ServerResponse query_order_pay_status( Long orderNo);
    /**
     * 根据订单号查询订单中商品信息
     */
    ServerResponse<List<OrderItem>> get_order_cart_product(Long orderNo);

    ServerResponse detail(Long orderNo);

    ServerResponse cancel(Long orderNo);

    ServerResponse<List<OrderVO>> selectOrderVOList(Integer pageNum, Integer pageSize);

    ServerResponse send_goods(Long orderNo);


    public ServerResponse findOrderListByStatus(Integer status);

    ServerResponse updateOrderStatusByOrderNo(Long orderNo);


}
