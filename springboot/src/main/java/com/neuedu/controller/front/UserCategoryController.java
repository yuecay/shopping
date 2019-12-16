package com.neuedu.controller.front;

import com.neuedu.common.ServerResponse;
import com.neuedu.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.controller.front
 * @Author: 郝毓才
 * @CreateTime: 2019-11-05 16:09
 * @Description: 前台页面需要的类别信息，不需要登录
 */
@RestController
@RequestMapping("/category")
public class UserCategoryController {

    @Autowired
    ICategoryService categoryService;

    @RequestMapping("/{categoryId}")
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    public ServerResponse getCategoryById(@PathVariable("categoryId") Integer categoryId, HttpSession session) {

        return categoryService.getCategoryById(categoryId);
    }

    /**
     * 根据类别号查看类别
     */
    @CrossOrigin(origins="http://localhost:8081",allowCredentials = "true")
    @RequestMapping("/categoryDetail/{categoryId}")
    public ServerResponse getCategoryDetailById(@PathVariable("categoryId") Integer categoryId){
        return categoryService.selectCategory(categoryId);
    }
}
