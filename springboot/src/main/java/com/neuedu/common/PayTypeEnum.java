package com.neuedu.common;


/**
 * 付款方式
 */
public enum PayTypeEnum {

    PAY_ONLINE(1,"在线支付"),
    PAY_OFFLINE(2,"货到付款")
    ;
    private int status;
    private String desc;
    PayTypeEnum(int status,String desc){
        this.status = status;
        this.desc = desc;
    }
    //枚举类的遍历
    public static PayTypeEnum codeOf(int status){
        for (PayTypeEnum payTypeEnum:values()){
            if(payTypeEnum.getStatus() == status){
                return payTypeEnum;
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
