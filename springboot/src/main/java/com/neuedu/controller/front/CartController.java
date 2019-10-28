package com.neuedu.controller.front;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.RoleEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.ICartService;
import com.neuedu.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    ICartService cartService;
    /**
     * 添加商品到购物车
     */

    @RequestMapping("/add/{productId}/{count}")
    public ServerResponse addCart(@PathVariable("productId") Integer productId,
                                  @PathVariable("count") Integer count,
                                  HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }

        return cartService.addCart(user.getId(),productId, count);
    }

    /**
     * 购物车List列表
     * @param session
     * @return
     */
    @RequestMapping("/list.do")
    public ServerResponse list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        return cartService.findCartByUserId(user.getId());
    }

    /**
     * 购物车删除某个产品
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping("/deleteProduct/{productId}")
    public ServerResponse deleteProduct(@PathVariable("productId") Integer productId,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        return cartService.deleteOne(productId,user.getId());
    }

    /**
     * 购物车选中某个商品
     * @param productId
     * @param session
     * @return
     */
    @RequestMapping("/select/{productId}")
    public ServerResponse select(@PathVariable("productId") Integer productId,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        return cartService.selectCartVOByProductId(productId,user.getId());
    }


}
