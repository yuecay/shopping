package com.neuedu.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.vo
 * @Author: 郝毓才
 * @CreateTime: 2019-10-24 14:32
 * @Description: 前端显示的地址详情
 */
@Data
public class ShippingVO implements Serializable {
    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;
}
