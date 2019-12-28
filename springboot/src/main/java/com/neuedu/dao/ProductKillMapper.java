package com.neuedu.dao;

import com.neuedu.pojo.ProductKill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductKillMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(ProductKill record);


    ProductKill selectByPrimaryKey(@Param("id")Integer id);


    List<ProductKill> selectAll();

    int updateByPrimaryKey(ProductKill record);

    ProductKill selectByKillId(@Param("killId") Integer killId);

    int updateKillItem(@Param("id") Integer id);
}