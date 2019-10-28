package com.neuedu.common;


import sun.awt.SunHints;

/**
 * 订单状态枚举类
 */
public enum OrderStatusEum {

    ORDER_CANCEL(0,"订单取消"),
    ORDER_NO_PAY(10,"未付款"),
    ORDER_PAYED(20,"已付款"),
    ORDER_SEND(40,"已发货"),
    ORDER_SUCCESS(50,"交易成功"),
    ORDER_CLOSED(60,"交易关闭")
    ;
    private int status;
    private String desc;

    OrderStatusEum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }


    //枚举遍历
    public static OrderStatusEum codeOf(Integer status){
        for(OrderStatusEum orderStatusEum: values()){
            if(orderStatusEum.getStatus() == status){
                return orderStatusEum;
            }
        }
        return null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
