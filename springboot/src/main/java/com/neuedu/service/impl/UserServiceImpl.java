package com.neuedu.service.impl;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.RoleEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserMapper;
import com.neuedu.pojo.User;
import com.neuedu.service.IUserService;
import com.neuedu.utils.MD5Utils;
import com.neuedu.utils.RedisApi;
import com.neuedu.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisApi redisApi;
    @Override
    public ServerResponse register(User user) {
        //1.参数校验
        if(user.getUsername() == "" || user.getPassword() == "" || user.getEmail() == "" ||
        user.getPhone() == "" || user.getQuestion() == "" || user.getAnswer() == ""){
            return ServerResponse.serverResponseByError(ResponseCode.PARAM_NOT_NULL,"参数不能为空");
        }

        //2.判断用户是否存在
        int result = userMapper.isExistsUsername(user.getUsername());
        if(result>0){
            return ServerResponse.serverResponseByError(ResponseCode.USERNAME_EXISTS,"用户名已存在");
        }

        //3.判断邮箱是否存在
        int resultEmail = userMapper.isExistsEmail(user.getEmail());
        if(resultEmail>0){
            return ServerResponse.serverResponseByError(ResponseCode.EMAIL_EXISTS,"邮箱已存在");
        }
        //4.密码加密，设置用户角色
        user.setPassword(MD5Utils.getMD5Code(user.getPassword()));
        //设置角色权限为普通用户
        user.setRole(RoleEnum.ROLE_USER.getRole());
        //5.注册
        int insertResult = userMapper.insert(user);
        if(insertResult <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"注册失败");
        }
        //6.返回
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse login(String username, String password,int role) {
        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户名不能为空");
        }
        if(password == null || password.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密码不能为空");
        }
        int result = userMapper.isExistsUsername(username);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户名不存在");
        }
        password = MD5Utils.getMD5Code(password);
        User user = userMapper.findUserByUsernameAndPassword(username, password);

        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密码错误");
        }

        if(role == 0){
            if(user.getRole()==RoleEnum.ROLE_USER.getRole()){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"登录权限不足");
            }
        }
        return ServerResponse.serverResponseBySuccess(user);
    }

    @Override
    public ServerResponse forget_get_question(String username) {
        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户名不能为空");
        }
        String question = userMapper.forget_get_question(username);
        if(question==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有查询到密保问题");
        }
        return ServerResponse.serverResponseBySuccess(question);
    }

    @Override
    public ServerResponse forget_check_answer(String username, String question, String answer) {
        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户名不能为空");
        }
        if(question == null || question.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密保问题不能为空");
        }
        if(answer == null || answer.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"答案不能为空");
        }
        int result = userMapper.forget_check_answer(username, question, answer);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"答案错误");
        }
        //生成token,token的过期时间是12小时
        String token = UUID.randomUUID().toString();
        //TokenCache.set("username:"+username, token );
        redisApi.setex("username:"+username,token,12*3600);
        return ServerResponse.serverResponseBySuccess(token);
    }

    @Override
    public ServerResponse forget_reset_password(String username, String passwordnew, String forgettoken) {
        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户名不能为空");
        }
        if(passwordnew == null || passwordnew.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"新密码不能为空");
        }
        if(forgettoken == null || forgettoken.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"token不能为空");
        }
        //通过token判断是否修改的是自己的账号
       // String token = TokenCache.get("username:" + username);
        String token = redisApi.get("username:" + username);
        if(token==null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"不能修改别人的密码或者token已经过期");
        }


        if(!token.equals(forgettoken)){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"无效的token");
        }

        int result = userMapper.forget_reset_password(username, MD5Utils.getMD5Code(passwordnew));
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密码修改失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse reset_password(String username, String password, String passwordnew) {
        if(username == null || username.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户名不能为空");
        }
        if(password == null || password.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"旧密码不能为空");
        }
        if(passwordnew == null || passwordnew.equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"新密码不能为空");
        }
        User user = userMapper.findUserByUsernameAndPassword(username, MD5Utils.getMD5Code(password));

        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"旧密码错误");
        }
        int result = userMapper.forget_reset_password(username, MD5Utils.getMD5Code(passwordnew));
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"密码修改失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse update_information(User user) {
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数不能为空");
        }

        int result = userMapper.updateUserByActivate(user);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户信息修改失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }
}
