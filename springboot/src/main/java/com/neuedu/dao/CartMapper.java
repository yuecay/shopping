package com.neuedu.dao;

import com.neuedu.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_cart
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_cart
     *
     * @mbg.generated
     */
    int insert(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_cart
     *
     * @mbg.generated
     */
    Cart selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_cart
     *
     * @mbg.generated
     */
    List<Cart> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_cart
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Cart record);

    /**
     * 根据用户id和商品id查询购物车
     */

    Cart findCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    /**
     * 根据用户id查询本用户的购物车信息
     */

    List<Cart> findCartsByUserId(@Param("userId")Integer userId);

    /**
     * 统计用户购物车中未选中商品的数量
     */
    Integer isAllChecked(@Param("userId")Integer userId);

    /**
     * 根据用户id查看购物车中用户勾选的商品
     */

    List<Cart> findCartByUserIdAndChecked(@Param("userId")Integer userId);

    /**
     * 清空购物车已选中的商品
     */
    int deleteBatch(@Param("cartList")List<Cart> cartList);

    int deleteOne(@Param("productId")Integer productId,@Param("userId") Integer userId);
}