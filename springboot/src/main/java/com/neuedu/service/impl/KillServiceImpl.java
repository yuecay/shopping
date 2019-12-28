package com.neuedu.service.impl;

import com.neuedu.common.OrderStatusEum;
import com.neuedu.dao.ProductKillMapper;
import com.neuedu.dao.ProductKillOrderMapper;
import com.neuedu.pojo.ProductKill;
import com.neuedu.pojo.ProductKillOrder;
import com.neuedu.service.IKillService;
import com.neuedu.utils.SnowFlake;
import com.neuedu.utils.killUtils.RabbitSenderService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: kill
 * @BelongsPackage: com.sxu.kill.server.service.impl
 * @Author: 郝毓才
 * @CreateTime: 2019-12-19 15:13
 * @Description: 秒杀业务逻辑实现类
 */
@Service
public class KillServiceImpl implements IKillService {
    private static final Logger log= LoggerFactory.getLogger(KillServiceImpl.class);

    private SnowFlake snowFlake=new SnowFlake(2,3);

    @Autowired
    private ProductKillOrderMapper productKillOrderMapper;

    @Autowired
    private RabbitSenderService rabbitSenderService;
    @Autowired
    private ProductKillMapper productKillMapper;


    @Autowired
   private RedissonClient redissonClient;

    /**
     * 通用的方法-记录用户秒杀成功后生成的订单-并进行异步邮件消息的通知
     * @param kill
     * @param userId
     * @throws Exception
     */
    private void commonRecordKillSuccessInfo(ProductKill kill, Integer userId) throws Exception{
        //TODO:记录抢购成功后生成的秒杀订单记录

        ProductKillOrder entity=new ProductKillOrder();
        String orderNo=String.valueOf(snowFlake.nextId());

        //entity.setCode(RandomUtil.generateOrderCode());   //传统时间戳+N位随机数
        entity.setOrderNo(orderNo); //雪花算法生成订单id
        entity.setProductKillId(kill.getId());
        entity.setProductId(kill.getProductId());
        entity.setUserId(userId);
        entity.setStatus(OrderStatusEum.ORDER_NO_PAY.getStatus());
        //TODO:学以致用，举一反三 -> 仿照单例模式的双重检验锁写法
        if (productKillOrderMapper.countByKillUserId(kill.getId(),userId) <= 0){
            int res=productKillOrderMapper.insert(entity);

            if (res>0){
               //TODO:进行异步邮件消息的通知=rabbitmq+mail
                rabbitSenderService.sendKillSuccessEmailMsg(orderNo);
                //TODO:入死信队列，用于 “失效” 超过指定的TTL时间时仍然未支付的订单
                rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
            }
        }
    }



    /**
     * 商品秒杀核心业务逻辑的处理-redission的分布式锁
     * @param killId
     * @param userId
     * @return
     * @throws Exception
     */

    public Boolean killProduct(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        final String key=new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();
        RLock lock = redissonClient.getLock(key);

        try {
            boolean cacheRes = lock.tryLock(30, 10, TimeUnit.SECONDS);
            if(cacheRes){
                if (productKillOrderMapper.countByKillUserId(killId,userId) <= 0){
                    ProductKill itemKill=productKillMapper.selectByKillId(killId);
                    System.out.println(itemKill);

                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal()>0){
                        int res=productKillMapper.updateKillItem(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            System.out.println("抢购成功");
                            result=true;
                        }
                    }
                }else{

                    log.error("redisson您已经抢购过该商品了!");
                }
            }
        }finally {
            lock.unlock();
        }

        return result;
    }
}
