package com.neuedu.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Shipping;
import com.neuedu.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Shipping shipping) {
        if(shipping == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数不能为空");
        }
        if(shipping.getId() == null){//添加
            int result = shippingMapper.insert(shipping);
            if(result <= 0){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"添加地址失败");
            }else {
                return ServerResponse.serverResponseBySuccess(shipping.getId());
            }
        }else {//更新
            int result = shippingMapper.updateByPrimaryKey(shipping);
            if(result <= 0){
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"更新地址失败");
            }else {
                return ServerResponse.serverResponseBySuccess(shipping.getId());
            }
        }


    }

    @Override
    public ServerResponse del(Integer shippingId) {
        if(shippingId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }
        int result = shippingMapper.deleteByPrimaryKey(shippingId);
        if(result <= 0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"删除失败");
        }
        return ServerResponse.serverResponseBySuccess();
    }

    @Override
    public ServerResponse<Shipping> select(Integer shippingId) {
        if(shippingId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"查询失败");
        }
        return ServerResponse.serverResponseBySuccess(shipping);
    }

    @Override
    public ServerResponse selectAddressList(Integer userId, Integer pageNum, Integer pageSize) {

        if(userId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectAddressList(userId);

        if(shippings == null || shippings.size() ==0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有地址");
        }
        PageInfo pageInfo = new PageInfo(shippings);
        return ServerResponse.serverResponseBySuccess(pageInfo);
    }

    @Override
    public ServerResponse findShippingById(Integer shippingId) {
        if(shippingId == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"参数必传");
        }
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping == null){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"没有地址");
        }
        return ServerResponse.serverResponseBySuccess(shipping);
    }



}
