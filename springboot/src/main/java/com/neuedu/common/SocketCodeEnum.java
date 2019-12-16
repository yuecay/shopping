package com.neuedu.common;

/**
 * WebSocket状态码
 */
public enum SocketCodeEnum {
    MESSAGE(1,"普通消息通知"),
    IMPORT_MESSAGE(10,"重要消息通知"),
    FINISH_PAY(50,"支付完成"),
    REMIND_SEND(60,"提醒发货"),
    REMIND_PAY(70,"提醒付款")
    ;

    private int check;
    private String desc;

    SocketCodeEnum(int check, String desc) {
        this.check = check;
        this.desc = desc;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
