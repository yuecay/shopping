package com.neuedu.vo;

import lombok.Data;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.vo
 * @Author: 郝毓才
 * @CreateTime: 2019-10-27 15:16
 * @Description: 支付返回前端的信息，订单号+二维码图片地址
 */
@Data
public class PayVO {
    private Long orderId;
    private String qrcode;

    public PayVO() {
    }

    public PayVO(Long orderId, String qrcode) {
        this.orderId = orderId;
        this.qrcode = qrcode;
    }
}
