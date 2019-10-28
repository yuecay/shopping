package com.neuedu.service;


import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

public interface IProductService {
        //添加、更新商品
        ServerResponse addOrUpdate(Product product);

        ServerResponse updateStatu(Integer productId,Integer status);

        //后台商品搜索
        ServerResponse search(String productName, Integer productId, Integer pageNum, Integer pageSize);

        ServerResponse detail( Integer productId);

        //前台商品搜索
        ServerResponse userSearch(String productName,Integer pageNum, Integer pageSize);

        //根据商品id查询
        ServerResponse<Product> findProductById(Integer productId);

        //扣库存
        ServerResponse reduceStock(Integer productId,Integer stock);
}
