package com.neuedu.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICategoryService;
import com.neuedu.service.IProductService;
import com.neuedu.utils.DateUtils;
import com.neuedu.vo.ProductDetailVO;
import com.neuedu.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Value("${springboot.imageHost}")
    private String imageHost;
    @Autowired
    ICategoryService categoryService;
    @Autowired
    ProductMapper productMapper;
    public ServerResponse addOrUpdate(Product product) {

        if(product == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }

        //主图是子图的第一个图，子图：x.png,y.jpg...
        String subImages = product.getSubImages();
        if(subImages!=null && !subImages.equals("")){
            String[] split = subImages.split(",");
            if(split.length>0){
                product.setMainImage(split[0]);
            }
        }


        Integer proId = product.getId();
        if(proId == null){
            //添加
            int insert = productMapper.insert(product);
            if(insert <= 0){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"添加失败");
            }else {
                return ServerResponse.serverResponseBySuccess();
            }
        }else{
            //更新
            int update = productMapper.updateByPrimaryKey(product);
            if(update <= 0){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"更新失败");
            }else {
                return ServerResponse.serverResponseBySuccess();
            }
        }

    }

    @Override
    public ServerResponse updateStatu(Integer productId,Integer status) {
        if(productId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }
        int result = productMapper.updateStatuById(productId,status);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"更新失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse search(String productName, Integer productId, Integer pageNum, Integer pageSize) {

        if(productName!=null ){
            productName = "%"+productName+"%";

        }
        int count = productMapper.getCountByProductName(productId, productName);
        //分页一定要在执行查询之前调用
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.findProductByNameAndId(productId, productName);
        List<ProductDetailVO> productDetailVOS = Lists.newArrayList();
        //productList转ProductListVOS，规定前台显示的东西
        if(productList != null && productList.size() > 0){
            for (Product product:productList){
                ProductDetailVO productDetailVO = assembleProductDetailVO(product);
                productDetailVOS.add(productDetailVO);
            }
        }

        PageInfo pageInfo = new PageInfo(productDetailVOS);


        return ServerResponse.serverResponseBySuccess(pageInfo,count);
    }

    @Override
    public ServerResponse detail(Integer productId) {
        if(productId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.serverResponseBySuccess();
        }

        //product -> productdetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);

        return ServerResponse.serverResponseBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse userSearch(String productName,Integer pageNum, Integer pageSize) {
        if(productName!=null){
            productName = "%"+productName+"%";
        }
        Integer status = 1;
        //分页一定要在执行查询之前调用
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.findProductByName(productName,status);
        List<ProductListVO> productListVOS = Lists.newArrayList();
        //productList转ProductListVOS，规定前台显示的东西
        if(productList != null && productList.size() > 0){
            for (Product product:productList){
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOS.add(productListVO);
            }
        }

        PageInfo pageInfo = new PageInfo(productListVOS);
        return ServerResponse.serverResponseBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<Product> findProductById(Integer productId) {
        if(productId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseBySuccess(product);
    }

    @Override
    public ServerResponse reduceStock(Integer productId, Integer stock) {
        if(productId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }
        if(stock == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"库存参数必传");
        }
        int result = productMapper.reduceStock(productId, stock);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"扣库存失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse<List<ProductDetailVO>> hotProductList() {
        List<ProductDetailVO> productDetailVOList = Lists.newArrayList();
        List<Product> hotProductList = productMapper.findHotProduct();
        if(hotProductList == null || hotProductList.size() <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有热门产品");
        }
        for (Product product : hotProductList) {
            ProductDetailVO productDetailVO = assembleProductDetailVO(product);
            productDetailVOList.add(productDetailVO);
        }
        return ServerResponse.serverResponseBySuccess(productDetailVOList);
    }

    @Override
    public ServerResponse<List<ProductDetailVO>> newProductList(Integer pageNum, Integer pageSize) {
        int count = productMapper.findProductCount();
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Product> newProductList = productMapper.findNewProduct();
        List<ProductDetailVO> productDetailVOList = Lists.newArrayList();

        if(newProductList == null || newProductList.size() <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有新产品");
        }
        for (Product product : newProductList) {
            ProductDetailVO productDetailVO = assembleProductDetailVO(product);
            productDetailVOList.add(productDetailVO);
        }
        PageInfo pageInfo = new PageInfo(productDetailVOList);
        return ServerResponse.serverResponseBySuccess(pageInfo,count);
    }

    @Override
    public ServerResponse<List<ProductDetailVO>> findProductListByCategory(Integer categoryId,Integer pageNum, Integer pageSize) {
        int count = productMapper.findCountBycategory(categoryId);
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Product> ProductList = productMapper.findProductListByCategory(categoryId);

        List<ProductDetailVO> productDetailVOList = Lists.newArrayList();

        if(ProductList == null || ProductList.size() <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有产品");
        }
        for (Product product : ProductList) {
            ProductDetailVO productDetailVO = assembleProductDetailVO(product);
            productDetailVOList.add(productDetailVO);
        }
        PageInfo pageInfo = new PageInfo(productDetailVOList);

        return ServerResponse.serverResponseBySuccess(pageInfo,count);
    }

    @Override
    public ServerResponse findProductDown(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        if(productName!=null ){
            productName = "%"+productName+"%";
        }
        int count = productMapper.getCountByProductNameAndStatus(productId, productName);
        //分页一定要在执行查询之前调用
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.findProductByNameAndIdAndStatus(productId, productName);
        List<ProductDetailVO> productDetailVOS = Lists.newArrayList();
        //productList转ProductListVOS，规定前台显示的东西
        if(productList != null && productList.size() > 0){
            for (Product product:productList){
                ProductDetailVO productDetailVO = assembleProductDetailVO(product);
                productDetailVOS.add(productDetailVO);
            }
        }
        PageInfo pageInfo = new PageInfo(productDetailVOS);
        return ServerResponse.serverResponseBySuccess(pageInfo,count);
    }

    @Override
    public List<ProductDetailVO> findAllProduct() {
        List<Product> products = productMapper.selectAll();
        List<ProductDetailVO> productDetailVOS = Lists.newArrayList();
        for (int i = 0; i < products.size(); i++) {
            ProductDetailVO productDetailVO = assembleProductDetailVO(products.get(i));
            productDetailVOS.add(productDetailVO);
        }
        return productDetailVOS;
    }


    //productList转ProductListVO
    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO=new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());


        return  productListVO;
    }

    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO=new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setImageHost(imageHost);
        productDetailVO.setName(product.getName());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        // Category category= categoryMapper.selectByPrimaryKey(product.getCategoryId());
        ServerResponse<Category> serverResponse=categoryService.selectCategory(product.getCategoryId());
        Category category=serverResponse.getData();
        //父类id
        if(category!=null){
            productDetailVO.setParentCategoryId(category.getParentId());
        }
        return productDetailVO;
    }
}
