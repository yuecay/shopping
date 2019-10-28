package com.neuedu.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.alipay.AliPay;
import com.neuedu.alipay.Main;
import com.neuedu.common.*;
import com.neuedu.dao.OrderItemMapper;
import com.neuedu.dao.OrderMapper;
import com.neuedu.dao.PayInfoMapper;
import com.neuedu.pojo.*;
import com.neuedu.service.ICartService;
import com.neuedu.service.IOrderService;
import com.neuedu.service.IProductService;
import com.neuedu.service.IShippingService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.utils.Const;
import com.neuedu.utils.DateUtils;
import com.neuedu.vo.OrderItemVO;
import com.neuedu.vo.OrderVO;
import com.neuedu.vo.ShippingVO;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    ICartService cartService;
    @Autowired
    IProductService productService;
    @Autowired
    IShippingService shippingService;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    PayInfoMapper payInfoMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Value("${springboot.imageHost}")
    String imageHost;
    @Override
    //创建订单
    public ServerResponse creatOrder(Integer userId, Integer shippingId) {

        //1 参数非空判断
        if(shippingId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"收货地址必传");
        }
        //判断shippingid是否存在
        ServerResponse select = shippingService.select(shippingId);
        if(!select.isSuccess()){
            return select;
        }
        //2 查看用户购物车中已选中的商品
        ServerResponse<List<Cart>> serverResponse = cartService.findCartByUserIdAndChecked(userId);

        List<Cart> cartList = serverResponse.getData();
        if(cartList == null || cartList.size()==0){
            return serverResponse.serverResponseByError(ResponseCode.ERROR,"购物车为空或者未选中");
        }
        //3 List<cart>--->List<OrderItem>
        ServerResponse response = getCartOrderItem(userId, cartList);
        //4 创建order实体类并保存到数据库
        if(!response.isSuccess()){
            return response;
        }
        List<OrderItem> orderItems = (List<OrderItem>) response.getData();
        ServerResponse<Order> orderServerResponse = creat(userId, shippingId, orderItems);
        if(!orderServerResponse.isSuccess()){
            return orderServerResponse;
        }
        Order order = orderServerResponse.getData();

        //5 保存订单明细
        ServerResponse response1 = saveOrderItems(orderItems, order);
        if(!response1.isSuccess()){
            return response1;
        }
        //6 扣库存
        ServerResponse reduceProductStock = reduceProductStock(orderItems);
        if(!reduceProductStock.isSuccess()){
            return reduceProductStock;
        }
        //7清空购物车下单的商品(批量删除)
        ServerResponse response2 = cartService.deleteBatch(cartList);
        if(!response2.isSuccess()){
            return response2;
        }
        //8 返回OrderVO
        return assembleOrderVO(order,orderItems,shippingId);
    }

    @Override
    public ServerResponse<List<Order>> list(Integer userId,Integer pageNum,Integer pageSize) {
        if(userId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户id必传");
        }
        Page page = PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.findAllOrderByUserId(userId);
        if(orderList == null || orderList.size() <= 0){
            return ServerResponse.serverResponseBySuccess("订单为空");
        }
        PageInfo pageInfo = new PageInfo(orderList);
        return ServerResponse.serverResponseBySuccess(pageInfo);
    }

    //支付功能
    public ServerResponse pay(Integer userId, Long orderNo) {
        //参数校验
        if(orderNo == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必须传");
        }
        Order order = orderMapper.findOrderByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(orderNo);
        if(orderItemList == null || orderItemList.size()==0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有可购买的商品");
        }
        AliPay aliPay = new AliPay();
        return aliPay.pay(order,orderItemList,imageHost);
    }

    @Override
    public String callback(Map<String, String> requestParams) {
        //获取订单号
        String out_trade_no = requestParams.get("out_trade_no");
        //获取流水号
        String trade_no = requestParams.get("trade_no");
        //获取支付状态：成功，失败
        String trade_status = requestParams.get("trade_status");
        //支付时间
        String gmt_payment = requestParams.get("gmt_payment");
        //根据订单号查订单
        Order order = orderMapper.findOrderByOrderNo(Long.valueOf(out_trade_no));
        if(order == null){
            return "fail";
        }
        if(trade_status.equals("TRADE_SUCCESS")){
            //支付成功
            //修改订单状态
            Order order1 = new Order();
            order1.setOrderNo(Long.valueOf(out_trade_no));
            order1.setStatus(OrderStatusEum.ORDER_PAYED.getStatus());
            order1.setPaymentTime(DateUtils.strToDate(gmt_payment));
            int result = orderMapper.updateOrderStatusAndPaymentTimeByOrderNo(order1);
            if(result <= 0){
                return "fail";
            }
        }
        //添加支付记录
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(Long.valueOf(out_trade_no));
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(PayTypeEnum.PAY_ONLINE.getStatus());
        payInfo.setPlatformNumber(trade_no);
        payInfo.setPlatformStatus(trade_status);
        int result = payInfoMapper.insert(payInfo);
        if(result <= 0){
            return "fail";
        }

        return "success";
    }

    @Override
    public ServerResponse query_order_pay_status(Long orderNo) {
        if(orderNo == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"必须传订单号");
        }
        Order order = orderMapper.findOrderByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有此订单");
        }
        return ServerResponse.serverResponseBySuccess(order);
    }

    @Override
    public ServerResponse<List<OrderItem>> get_order_cart_product(Long orderNo) {
        if(orderNo == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"必须传订单号");
        }
        List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(orderNo);
        if(orderItemList == null || orderItemList.size()==0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有商品");
        }
        return ServerResponse.serverResponseBySuccess(orderItemList);
    }

    @Override
    public ServerResponse detail(Long orderNo) {
        if(orderNo == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"必须传订单号");
        }
        Order order = orderMapper.findOrderByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有此订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(orderNo);
        if(orderItemList == null || orderItemList.size()==0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有商品");
        }
        Integer shippingId = order.getShippingId();

        return assembleOrderVO(order,orderItemList,shippingId);
    }

    @Override
    public ServerResponse cancel(Long orderNo) {
        if(orderNo == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"必须传订单号");
        }
        Order order = orderMapper.findOrderByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"该用户没有此订单");
        }
        if(order.getStatus() == OrderStatusEum.ORDER_PAYED.getStatus()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"此订单已付款，无法被取消");
        }
        Order order1 = new Order();
        order1.setStatus(OrderStatusEum.ORDER_CANCEL.getStatus());
        System.out.println(OrderStatusEum.ORDER_CANCEL.getStatus());
        order1.setOrderNo(orderNo);
        int result = orderMapper.updateOrderStatusAndPaymentTimeByOrderNo(order1);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"订单更新状态发生错误");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse<List<OrderVO>> selectOrderVOList(Integer pageNum, Integer pageSize) {
        List<Order> orders = orderMapper.selectAll();
        List<OrderVO> orderVOList = Lists.newArrayList();
        for (Order order1 : orders) {
            Order order = orderMapper.findOrderByOrderNo(order1.getOrderNo());
            List<OrderItem> orderItemList = orderItemMapper.findOrderItemByOrderNo(order1.getOrderNo());
            Integer shippingId = order.getShippingId();
            ServerResponse<OrderVO> response = assembleOrderVO(order, orderItemList, shippingId);
            orderVOList.add(response.getData());
        }
        return ServerResponse.serverResponseBySuccess(orderVOList);
    }

    @Override
    public ServerResponse send_goods(Long orderNo) {
        if(orderNo == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"必须传订单号");
        }
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatusEum.ORDER_SEND.getStatus());
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String s = simpleDateFormat.format(date);
        order.setSendTime(DateUtils.strToDate(s));
        int result = orderMapper.updateOrderStatusAndPaymentTimeByOrderNo(order);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"订单状态更新失败");
        }
        return ServerResponse.serverResponseBySuccess("发货成功");
    }


    //扣库存
    private ServerResponse reduceProductStock(List<OrderItem> orderItems){
        for (OrderItem orderItem : orderItems) {
            Integer productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();
            ServerResponse<Product> detail = productService.findProductById(productId);
            Product product = detail.getData();
            ServerResponse response = productService.reduceStock(productId, product.getStock() - quantity);
            if(!response.isSuccess()){
                return response;
            }
        }
        return ServerResponse.serverResponseBySuccess();
    }
    //创建order实体类并保存到数据库
    private ServerResponse<Order> creat(Integer userId, Integer shippingId, List<OrderItem> orderItems){

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setOrderNo(generatorOrderNo());//生成订单编号
        order.setPayment(getOrderTotalPrice(orderItems));//计算订单的总价格
        order.setPaymentType(PayTypeEnum.PAY_ONLINE.getStatus());
        order.setPostage(Const.POSTAGE);//初始邮费10块钱
        order.setStatus(OrderStatusEum.ORDER_NO_PAY.getStatus());
        int result = orderMapper.insert(order);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"订单保存失败");
        }
        return ServerResponse.serverResponseBySuccess(order);
    }

    //生成订单号
    private Long generatorOrderNo(){
        return System.currentTimeMillis()+new Random().nextInt(100);
    }

    //计算订单的总价格
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItems){

        BigDecimal orderTotalPrice = new BigDecimal("0");
        for (OrderItem orderItem : orderItems) {
            orderTotalPrice = BigDecimalUtils.add(orderTotalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return orderTotalPrice;
    }

    //保存订单明细
    private ServerResponse saveOrderItems( List<OrderItem> orderItems,Order order){
        //订单明细赋值订单编号
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        //订单明细批量插入数据库
        int result = orderItemMapper.insertBatch(orderItems);
        if(result != orderItems.size()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"有些订单明细没有插入成功");
        }
        return ServerResponse.serverResponseBySuccess();
    }


    private  ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){


        List<OrderItem> orderItemList= Lists.newArrayList();

        for(Cart cart:cartList){

            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(userId);
            ServerResponse<Product> serverResponse=productService.findProductById(cart.getProductId());
            if(!serverResponse.isSuccess()){
                return  serverResponse;
            }
            Product product= serverResponse.getData();
            if(product==null){
                return  ServerResponse.serverResponseByError("id为"+cart.getProductId()+"的商品不存在");
            }
            if(product.getStatus()!= ProductStatusEnum.PRODUCT_SALE.getStatus()){//商品下架
                return ServerResponse.serverResponseByError("id为"+product.getId()+"的商品已经下架");
            }
            if(product.getStock()<cart.getQuantity()){//库存不足
                return ServerResponse.serverResponseByError("id为"+product.getId()+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));

            orderItemList.add(orderItem);
        }

        return  ServerResponse.serverResponseBySuccess(orderItemList);
    }
    //转orderVO
    private ServerResponse assembleOrderVO(Order order, List<OrderItem> orderItemList, Integer shippingId) {
        OrderVO orderVO=new OrderVO();

        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO= assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVoList(orderItemVOList);
        orderVO.setImageHost(imageHost);
        ServerResponse<Shipping> serverResponse= shippingService.findShippingById(shippingId);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        Shipping shipping=serverResponse.getData();
        if(shipping!=null){
            orderVO.setShippingId(shippingId);
            ShippingVO shippingVO= assmbleShippingVO(shipping);
            orderVO.setShippingVo(shippingVO);
            orderVO.setReceiverName(shipping.getReceiverName());
        }

        orderVO.setStatus(order.getStatus());
        OrderStatusEum orderStatusEnum= OrderStatusEum.codeOf(order.getStatus());
        if(orderStatusEnum!=null){
            orderVO.setStatusDesc(orderStatusEnum.getDesc());
        }

        orderVO.setPostage(Const.POSTAGE); //邮费默认是10
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        PayTypeEnum payTypeEnum=PayTypeEnum.codeOf(order.getPaymentType());
        if(payTypeEnum!=null){
            orderVO.setPaymentTypeDesc(payTypeEnum.getDesc());
        }
        orderVO.setOrderNo(order.getOrderNo());

        return ServerResponse.serverResponseBySuccess(orderVO);
    }


    //shipping转shippingVO
    private ShippingVO assmbleShippingVO(Shipping shipping) {
        ShippingVO shippingVO = new ShippingVO();

        shippingVO.setReceiverAddress(shipping.getReceiverAddress());
        shippingVO.setReceiverCity(shipping.getReceiverCity());
        shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVO.setReceiverMobile(shipping.getReceiverMobile());
        shippingVO.setReceiverName(shipping.getReceiverName());
        shippingVO.setReceiverPhone(shipping.getReceiverPhone());
        shippingVO.setReceiverProvince(shipping.getReceiverProvince());
        shippingVO.setReceiverZip(shipping.getReceiverZip());
      return shippingVO;
    }

    //OrderItem 转 OrderItemVO
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO=new OrderItemVO();
        if(orderItem != null){
            orderItemVO.setQuantity(orderItem.getQuantity());
            orderItemVO.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVO.setOrderNo(orderItem.getOrderNo());
            orderItemVO.setProductId(orderItem.getProductId());
            orderItemVO.setProductImage(orderItem.getProductImage());
            orderItemVO.setProductName(orderItem.getProductName());
            orderItemVO.setTotalPrice(orderItem.getTotalPrice());

        }
        return orderItemVO;
    }

}
