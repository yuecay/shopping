package com.neuedu.service.impl;

import com.google.common.collect.Lists;
import com.neuedu.common.CheckEnum;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICartService;
import com.neuedu.service.IProductService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.vo.CartProductVO;
import com.neuedu.vo.CartVO;
import com.neuedu.vo.ProductDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    IProductService productService;
    @Autowired
    CartMapper cartMapper;
    @Override
    public ServerResponse addCart(Integer userId, Integer productId, Integer count) {

        //1 参数非空
        if(productId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品id必须传");
        }
        if(count == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品数量不能为0");
        }
        //2 判断此时商品是否还有库存
        ServerResponse<ProductDetailVO> detail = productService.detail(productId);
        if(!detail.isSuccess()){
            return  ServerResponse.serverResponseByError(detail.getStatus(),detail.getMsg());
        }else {
            ProductDetailVO productDetailVO = detail.getData();
            if(productDetailVO.getStock() <= 0){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品已售空");
            }
        }

        //3 判断商品是否在购物车，是：更新；否：添加
        Cart cart = cartMapper.findCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            Cart newCart = new Cart(userId,productId,count, CheckEnum.CART_PRODUCT_CHECK.getCheck());
            int result = cartMapper.insert(newCart);
            if(result <= 0){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"添加购物车失败");
            }
        }else{//更新商品在购物车中的数量
            cart.setQuantity(cart.getQuantity()+count);
            int result = cartMapper.updateByPrimaryKey(cart);
            if(result <= 0){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"更新购物车失败");
            }
        }
        //4 封装购物车对象CartVO
        CartVO cartVO = getCartVO(userId);
        //5 返回CartVO
        return ServerResponse.serverResponseBySuccess(cartVO);
    }

    @Override
    public ServerResponse<List<Cart>> findCartByUserIdAndChecked(Integer userId) {

        List<Cart> cartList = cartMapper.findCartByUserIdAndChecked(userId);
        if(cartList == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"根据用户id查看已选中的商品失败");
        }
        return ServerResponse.serverResponseBySuccess(cartList);
    }

    @Override
    public ServerResponse deleteBatch(List<Cart> cartList) {
        if(cartList == null || cartList.size() == 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"要删除的购物车商品不能为空");
        }
        int result = cartMapper.deleteBatch(cartList);
        if(result != cartList.size()){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"有些购物车数据删除失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse<List<Cart>> findCartByUserId(Integer id) {
        if(id == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"用户id必须传");
        }
        List<Cart> cartList = cartMapper.findCartsByUserId(id);
        if(cartList == null || cartList.size() <= 0){
            return ServerResponse.serverResponseBySuccess("购物车为空！");
        }
        return ServerResponse.serverResponseBySuccess(cartList);
    }

    @Override
    public ServerResponse deleteOne(Integer productId,Integer userId) {
        if(productId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品id必须传");
        }
        int result = cartMapper.deleteOne(productId, userId);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"删除失败");
        }

        return findCartByUserId(userId);
    }

    @Override
    public ServerResponse selectCartVOByProductId(Integer productId,Integer userId) {
        if(productId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品id必须传");
        }
        CartVO cartVO = getOneCartVO(productId, userId);
        if(cartVO == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"查询出错");
        }
        return ServerResponse.serverResponseBySuccess(cartVO);
    }

    private CartVO getOneCartVO(Integer productId,Integer userId){
        CartVO cartVO = new CartVO();
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        Cart cart = cartMapper.findCartByUserIdAndProductId(userId,productId );
        int limit_quantity = 0;
        String limitQuantity = null;
        //定义购物车商品总价格
        BigDecimal cartTotalPrice = new BigDecimal("0");
        CartProductVO cartProductVO = new CartProductVO();
        cartProductVO.setId(cart.getId());
        cartProductVO.setUserId(userId);
        cartProductVO.setProductId(cart.getProductId());

        ServerResponse<Product> serverResponse = productService.findProductById(cart.getProductId());
        if(serverResponse.isSuccess()){
            Product product = serverResponse.getData();
            if(cart.getQuantity() <= product.getStock()){//如果购物车里商品数量小于等于商品库存
                limit_quantity = cart.getQuantity();
                limitQuantity = "LIMIT_NUM_SUCCESS";
            }else {
                limit_quantity = product.getStock();
                limitQuantity = "LIMIT_NUM_FAIL";
            }
            cartProductVO.setQuantity(limit_quantity);
            cartProductVO.setLimitQuantity(limitQuantity);
            cartProductVO.setProductName(product.getName());
            cartProductVO.setProductSubtitle(product.getSubtitle());
            cartProductVO.setProductMainImage(product.getMainImage());
            cartProductVO.setProductPrice(product.getPrice());
            cartProductVO.setProductStatus(product.getStatus());
            cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
            cartProductVO.setProductStock(product.getStock());
            cartProductVO.setProductChecked(cart.getChecked());
            cartProductVOList.add(cartProductVO);
            //3 计算购物车总价格
            if(cart.getChecked() == CheckEnum.CART_PRODUCT_CHECK.getCheck()){//商品被选中
                cartTotalPrice = BigDecimalUtils.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
            }
        }
        cartVO.setCarttotalprice(cartTotalPrice);
        cartVO.setCartProductVOList(cartProductVOList);


        //4 判断是否全选
        Integer isAllChecked = cartMapper.isAllChecked(userId);
        if(isAllChecked == 0){//全选
            cartVO.setIsallchecked(true);
        }else{
            cartVO.setIsallchecked(false);
        }

        //5 构建CartVO

        return cartVO;
    }
    private CartVO getCartVO(Integer userId){

        CartVO cartVO = new CartVO();
        //1 根据userId查询该用户的购物信息 --->list<Cart>
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        List<Cart> cartList = cartMapper.findCartsByUserId(userId);
        if(cartList == null || cartList.size()==0){
            return cartVO;
        }

        int limit_quantity = 0;
        String limitQuantity = null;
        //定义购物车商品总价格
        BigDecimal cartTotalPrice = new BigDecimal("0");
        //2 将list<Cart>转为CartProductVO
        for (Cart cart : cartList){
            CartProductVO cartProductVO = new CartProductVO();
            cartProductVO.setId(cart.getId());
            cartProductVO.setUserId(userId);
            cartProductVO.setProductId(cart.getProductId());

            ServerResponse<Product> serverResponse = productService.findProductById(cart.getProductId());
            if(serverResponse.isSuccess()){
                Product product = serverResponse.getData();
                if(cart.getQuantity() <= product.getStock()){//如果购物车里商品数量小于等于商品库存
                    limit_quantity = cart.getQuantity();
                    limitQuantity = "LIMIT_NUM_SUCCESS";
                }else {
                    limit_quantity = product.getStock();
                    limitQuantity = "LIMIT_NUM_FAIL";
                }
                cartProductVO.setQuantity(limit_quantity);
                cartProductVO.setLimitQuantity(limitQuantity);
                cartProductVO.setProductName(product.getName());
                cartProductVO.setProductSubtitle(product.getSubtitle());
                cartProductVO.setProductMainImage(product.getMainImage());
                cartProductVO.setProductPrice(product.getPrice());
                cartProductVO.setProductStatus(product.getStatus());
                cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
                cartProductVO.setProductStock(product.getStock());
                cartProductVO.setProductChecked(cart.getChecked());
                cartProductVOList.add(cartProductVO);
                //3 计算购物车总价格
                if(cart.getChecked() == CheckEnum.CART_PRODUCT_CHECK.getCheck()){//商品被选中
                    cartTotalPrice = BigDecimalUtils.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
            }
        }


        cartVO.setCarttotalprice(cartTotalPrice);
        cartVO.setCartProductVOList(cartProductVOList);


        //4 判断是否全选
        Integer isAllChecked = cartMapper.isAllChecked(userId);
        if(isAllChecked == 0){//全选
            cartVO.setIsallchecked(true);
        }else{
            cartVO.setIsallchecked(false);
        }

        //5 构建CartVO

        return cartVO;
    }
}
