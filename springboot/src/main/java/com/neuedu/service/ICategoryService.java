package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;
import org.springframework.web.bind.annotation.PathVariable;

public interface ICategoryService {
    /**
     * 添加类别
     */
    public ServerResponse add_category(Category category);
    /**
     * 修改类别
     * categoryId
     * categoryName
     * categoryUrl
     */
    public ServerResponse updateCategory(Category category);
    /**
     * 查看平级类别
     */
    public ServerResponse getCategoryById(Integer categoryId);
    /**
     * 获取当前分类id以及递归子节点categoryId
     *
     */
    public ServerResponse deepCategory(Integer categoryId);

    /**
     * 根据id查看类别
     */
    ServerResponse<Category> selectCategory(Integer categoryId);

    ServerResponse deleteCategory(Integer categoryId);
}
