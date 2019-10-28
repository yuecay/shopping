package com.neuedu.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.vo
 * @Author: 郝毓才
 * @CreateTime: 2019-10-24 14:31
 * @Description: 前端显示的订单详情
 */
@Data
public class OrderVO implements Serializable {
    private Long orderNo;
    private BigDecimal payment;
    private Integer paymentType;
    private String paymentTypeDesc;
    private Integer postage;
    private Integer status;
    private String statusDesc;
    private String paymentTime;
    private String sendTime;
    private String endTime;
    private String closeTime;
    private String createTime;

    private List<OrderItemVO> orderItemVoList;
    private String  imageHost;
    private Integer shippingId;
    private String receiverName;
    private ShippingVO shippingVo;
}
