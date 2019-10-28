package com.neuedu.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class OrderItemVO implements Serializable {

    private Long  orderNo;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal currentUnitPrice;
    private Integer  quantity;
    private BigDecimal totalPrice;
    private String createTime;

}
