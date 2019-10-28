package com.neuedu.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartProductVO implements Serializable {

    private Integer id;//购物车id
    private Integer userId;//用户Id
    private Integer productId;//商品id
    private Integer quantity;//商品数量
    private String productName;//商品名称
    private String productSubtitle;//商品标题
    private String productMainImage;//商品主图
    private BigDecimal productPrice;//商品价格
    private Integer productStatus;//商品状态
    private BigDecimal productTotalPrice;//商品总价格
    private Integer productStock;//商品库存
    private Integer productChecked;//是否选中
    private String limitQuantity;//商品库存描述信息
}
