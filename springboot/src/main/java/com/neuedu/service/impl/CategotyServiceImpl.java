package com.neuedu.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.pojo.Category;
import com.neuedu.service.ICategoryService;
import com.neuedu.utils.DateUtils;
import com.neuedu.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategotyServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;
    @Override
    public ServerResponse add_category(Category category) {
        if(category == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数不能为空");
        }
        int result = categoryMapper.insert(category);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"添加品类失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse updateCategory(Category category) {
        if(category == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数不能为空");
        }
        if(category.getId() == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"类别id必须要传");
        }
        int result = categoryMapper.updateByPrimaryKey(category);
        if(result<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"更新品类失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse getCategoryById(Integer categoryId) {
        if(categoryId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"类别id必须要传");
        }
        List<Category> categoryList = categoryMapper.selectCategoryById(categoryId);
        List<CategoryVO> categoryVOList = Lists.newArrayList();
        for (Category category : categoryList) {
            CategoryVO categoryVO = assembleCategoryVO(category);
            categoryVOList.add(categoryVO);
        }

        return ServerResponse.serverResponseBySuccess(categoryVOList,"成功");
    }

    @Override
    public ServerResponse deepCategory(Integer categoryId) {
        if(categoryId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"类别id必须要传");
        }
        Set<Category> categorySet = Sets.newHashSet();//使用guava创建HashSet
        //递归查询
        Set<Category> categorySet1 = findAllChildCategory(categorySet,categoryId);
        Set<Integer> categoryIds = Sets.newHashSet();
        Iterator<Category> it = categorySet1.iterator();
        while (it.hasNext()){
            Category category = it.next();
            categoryIds.add(category.getId());
        }
        return ServerResponse.serverResponseBySuccess(categoryIds);
    }

    @Override
    public ServerResponse<Category> selectCategory(Integer categoryId) {
        if(categoryId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"类别id必须要传");
        }
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        return ServerResponse.serverResponseBySuccess(category);
    }

    @Override
    public ServerResponse deleteCategory(Integer categoryId) {
        if(categoryId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"类别id必须要传");
        }
        int result = categoryMapper.deleteByPrimaryKey(categoryId);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"删除失败！");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    public Set<Category> findAllChildCategory(Set<Category> categorySet,Integer categoryId){
        //查看categoryId的类别信息
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查看categoryId的平级子类
        List<Category> categoryList = categoryMapper.selectCategoryById(categoryId);
        if(categoryList !=null && categoryList.size() > 0){
            for (Category category1 : categoryList){
                findAllChildCategory(categorySet,category1.getId());
            }
        }
        return categorySet;
    }


    public CategoryVO assembleCategoryVO( Category category){
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(category.getId());
        categoryVO.setMainImage(category.getMainImage());
        categoryVO.setName(category.getName());
        categoryVO.setParentId(category.getParentId());
        categoryVO.setSortOrder(category.getSortOrder());
        categoryVO.setStatus(category.getStatus());
        categoryVO.setCreateTime(DateUtils.dateToStr(category.getCreateTime()));
        categoryVO.setUpdateTime(DateUtils.dateToStr(category.getUpdateTime()));
        return categoryVO;
    }
}
