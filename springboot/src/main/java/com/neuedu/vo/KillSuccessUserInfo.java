package com.neuedu.vo;/**
 * Created by Administrator on 2019/6/21.
 */

import com.neuedu.pojo.ProductKillOrder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2019/6/21 22:02
 **/
@Data
public class KillSuccessUserInfo extends ProductKillOrder implements Serializable{

    private String userName;

    private String phone;

    private String email;

    private String productName;

    @Override
    public String toString() {
        return super.toString()+"\nKillSuccessUserInfo{" +
                "userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}