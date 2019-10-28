package com.neuedu.controller.front;

import com.neuedu.common.ServerResponse;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/product")
public class UserProductController {

    @Autowired
    IProductService productService;
    /**
     * 搜索商品
     *@RequestParam(name = "productName",required = false) required表示参数可传可不传
     */
    @RequestMapping(value = "search.do")
    public ServerResponse userSearch(@RequestParam(name = "productName",required = false) String productName,
                                 @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize) {

        return productService.userSearch(productName, pageNum, pageSize);
    }

    @RequestMapping(value = "/{productId}")
    public ServerResponse detail(@PathVariable("productId") Integer productId){
        return productService.detail(productId);
    }
    }
