package com.neuedu.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO implements Serializable {
    //购物信息集合
    private List<CartProductVO> cartProductVOList;
    //是否全选
    private boolean isallchecked;
    //总价格
    private BigDecimal carttotalprice;
}
