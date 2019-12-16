package com.neuedu.controller.backend;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.RoleEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.User;
import com.neuedu.service.IProductService;
import com.neuedu.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/product")
public class ProductController  {

    @Autowired
    IProductService productService;
    /**
     * 商品添加&更新
     */
    @RequestMapping("/save.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse addOrUpdate(Product product, HttpSession session){


        return productService.addOrUpdate(product);
    }

    /**
     * 商品上下架
     */
@RequestMapping("/updateStatu/{productId}/{status}")
    public ServerResponse updateStatu(@PathVariable("productId") Integer productId,@PathVariable("status") Integer status, HttpSession session){

        return productService.updateStatu(productId,status);
    }

    /**
     * 搜索商品
     *@RequestParam(name = "productName",required = false) required表示参数可传可不传
     */

    @RequestMapping(value = "search.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse search(@RequestParam(name = "productName",required = false) String productName,
                                 @RequestParam(name = "productId",required = false) Integer productId,
                                 @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                 @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        return productService.search(productName, productId, pageNum, pageSize);
    }

    @RequestMapping(value = "/{productId}")
    public ServerResponse detail(@PathVariable("productId") Integer productId){
        return productService.detail(productId);
    }

    //查询所有下架商品
    @RequestMapping(value = "findProductDown.do")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse findProductDown(@RequestParam(name = "productName",required = false) String productName,
                                          @RequestParam(name = "productId",required = false) Integer productId,
                                          @RequestParam(name = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        return productService.findProductDown(productName, productId, pageNum, pageSize);
    }

}
