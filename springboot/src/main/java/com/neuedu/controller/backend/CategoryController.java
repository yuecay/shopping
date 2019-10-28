package com.neuedu.controller.backend;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.RoleEnum;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.User;
import com.neuedu.service.ICategoryService;
import com.neuedu.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/category")
public class CategoryController {
    @Autowired
    ICategoryService categoryService;
    /**
     * 添加类别
     */
    @RequestMapping("/add_category.do")
    public ServerResponse add_category(Category category,HttpSession session){


        return  categoryService.add_category(category);
    }
    /**
     * 修改类别
     * categoryId
     * categoryName
     * categoryUrl
     */
    @RequestMapping("/set_category.do")
    public ServerResponse updateCategory(Category category,HttpSession session){


        return categoryService.updateCategory(category);
    }
    /**
     * 查看平级类别
     */
    @RequestMapping("/{categoryId}")
    public ServerResponse getCategoryById(@PathVariable("categoryId") Integer categoryId,HttpSession session){
       /* User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.serverResponseByError(ResponseCode.NOT_LOGIN,"未登录");
        }
        if(user.getRole() == RoleEnum.ROLE_USER.getRole()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"权限不足");
        }*/
        return categoryService.getCategoryById(categoryId);
    }
    /**
     * 获取当前分类id以及递归子节点categoryId
     *
     */
    @RequestMapping("/deep/{categoryId}")
    public ServerResponse deepCategory(@PathVariable("categoryId") Integer categoryId,HttpSession session){


        return categoryService.deepCategory(categoryId);
    }
}
