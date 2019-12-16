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
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse userSearch(@RequestParam(name = "productName",required = false) String productName,
                                 @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize) {

        return productService.userSearch(productName, pageNum, pageSize);
    }

    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    @RequestMapping(value = "/{productId}")
    public ServerResponse detail(@PathVariable("productId") Integer productId){
        return productService.detail(productId);
    }

    @RequestMapping(value = "/hotProductList")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse hotProductList(){
        return productService.hotProductList();
    }

    @RequestMapping(value = "/newProductList.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse newProductList(@RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                         @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        return productService.newProductList(pageNum,pageSize);
    }

    //按类别查看商品
    @RequestMapping(value = "/findProductListByCategory.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse findProductListByCategory(Integer categoryId,
                                                    @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                                    @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        return productService.findProductListByCategory(categoryId, pageNum, pageSize);
    }
    }
