package com.neuedu.dao;

import com.neuedu.pojo.ProductKillOrder;
import com.neuedu.vo.KillSuccessUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductKillOrderMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(ProductKillOrder record);


    ProductKillOrder selectByPrimaryKey(Integer id);

    ProductKillOrder selectByOrderNo(String orderNo);

    int expireOrder(@Param("orderNo") String orderNo,@Param("status")Integer status);

    List<ProductKillOrder> selectAll();


    int updateByPrimaryKey(ProductKillOrder record);

    KillSuccessUserInfo selectByCode(@Param("orderNo") String orderNo);

    int countByKillUserId(@Param("productKillId") Integer productKillId, @Param("userId") Integer userId);
}