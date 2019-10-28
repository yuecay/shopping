package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;

public interface IShippingService {

    public ServerResponse add(Shipping shipping);

    public ServerResponse del(Integer shippingId);

    ServerResponse select(Integer shippingId);

    ServerResponse selectAddressList(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse findShippingById(Integer shippingId);

}
