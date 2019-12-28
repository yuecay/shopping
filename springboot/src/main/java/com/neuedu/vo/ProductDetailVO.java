package com.neuedu.vo;

import com.google.common.collect.Maps;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductDetailVO {

    private Integer id;
    private Integer categoryId;
    private Integer parentCategoryId;
    private String  name;//
    private String  subtitle;//oppo促销进行中",
    private String  imageHost;//http://img.business.com/",
    private String  mainImage;//mainimage.jpg",
    private String  subImages;//[\"business/aa.jpg\",\"business/bb.jpg\",\"business/cc.jpg\",\"business/dd.jpg\",\"business/ee.jpg\"]",
    private String  detail;//richtext",
    private BigDecimal price;// 2999.11,
    private Integer stock;// 71,
    private Integer status;// 1,
    private String createTime;// "2016-11-20 14:21:53",
    private String updateTime;//2016-11-20 14:21:53"


    public String toMap(ProductDetailVO productDetailVO,int i){
        Map<Integer,String> map = Maps.newHashMap();
        map.put(0,productDetailVO.getId()+"");
        map.put(1,productDetailVO.getCategoryId()+"");
        map.put(2,productDetailVO.getParentCategoryId()+"");
        map.put(3,productDetailVO.getName());
        map.put(4,productDetailVO.getSubtitle());
        map.put(5,productDetailVO.getImageHost());
        map.put(6,this.mainImage);
        map.put(7,this.subImages);
        map.put(8,this.detail);
        map.put(9,this.price.toString());
        map.put(10,this.stock+"");
        map.put(11,this.status+"");
        map.put(12,this.createTime);
        map.put(13,this.updateTime);
        return map.get(i);


    }
}
