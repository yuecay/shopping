package com.neuedu.vo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductListVO {

    private Integer id;
    private Integer categoryId;
    private String  name;//
    private String  subtitle;//oppo促销进行中",
    private Integer status;// 1,
    private String  mainImage;//mainimage.jpg",
    private BigDecimal price;// 2999.11,
}
