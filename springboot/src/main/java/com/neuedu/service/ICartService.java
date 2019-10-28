package com.neuedu.service;

import com.google.common.collect.Lists;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Cart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface ICartService {

    /**
     * 添加商品到购物车
     */


    ServerResponse addCart(Integer userId,Integer productId, Integer count);

    /**
     * 根据用户id查看已选中的商品
     *
     */
    ServerResponse<List<Cart>> findCartByUserIdAndChecked(Integer userId);

    /**
     * 批量删除购物车商品
     */
    ServerResponse deleteBatch(List<Cart> cartList);

    ServerResponse<List<Cart>> findCartByUserId(Integer id);

    ServerResponse deleteOne(Integer productId,Integer userId);

    ServerResponse selectCartVOByProductId(Integer productId,Integer userId);
}
