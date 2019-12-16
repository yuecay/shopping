package com.neuedu.common;

/**
 * 维护状态码
 */
public class ResponseCode {
    /**
     * 成功的状态码
     */
    public static final int SUCCESS = 0;
    /**
     * 失败时通用状态码
     */
    public static final int ERROR = 100;
    /**
     * 参数不能为空
     */
    public static final int PARAM_NOT_NULL=1;
    /**
     * 用户名已存在
     */
    public static final int USERNAME_EXISTS=2;
    /**
     * 邮箱已存在
     */
    public static final int EMAIL_EXISTS=3;
    /**
     * 格式错误
     */
    public static final int STYLE_ERROR=4;
    /**
     * 未登录状态
     */
    public static final int NOT_LOGIN=99;
    /**
     * 权限不足
     */
    public static final int NO_ROLE=20;


}
