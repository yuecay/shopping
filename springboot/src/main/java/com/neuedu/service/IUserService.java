package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public interface IUserService {
    /**
     * 注册接口
     * @param user
     * @return ServerResponse
     */
    public ServerResponse register(User user);
    /**
     * 登录接口
     * @param username
     *          role  0:管理员 1:普通用户
     * @param password
     * @return ServerResponse
     */
    public ServerResponse login(String username,String password,int role);
    /**
     * 根据username获取密保问题
     */

    public ServerResponse forget_get_question(@PathVariable("username") String username);

    /**
     * 提交答案
     */

    public ServerResponse forget_check_answer(String username,String question,String answer);

    /**
     * 修改密码
     */

    public ServerResponse forget_reset_password(String username,String passwordnew,String forgettoken);
    /**
     * 登录状态重置密码
     */
    public ServerResponse reset_password(String username, String password, String passwordnew);
    /**
     * 修改用户信息
     */
    public ServerResponse update_information(User user);
}
