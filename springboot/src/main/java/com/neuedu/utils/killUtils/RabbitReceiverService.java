package com.neuedu.utils.killUtils;

import com.neuedu.common.OrderStatusEum;
import com.neuedu.dao.ProductKillOrderMapper;
import com.neuedu.info.MailDto;
import com.neuedu.pojo.ProductKillOrder;
import com.neuedu.vo.KillSuccessUserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: kill
 * @BelongsPackage: com.sxu.kill.server.service
 * @Author: 郝毓才
 * @CreateTime: 2019-12-23 15:56
 * @Description: RabbitMQ接收消息服务
 */
@Service
public class RabbitReceiverService {
    public static final Logger log = LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private Environment env;

    @Autowired
    private ProductKillOrderMapper productKillOrderMapper;

    @Autowired
    private MailService mailService;

    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"},containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(KillSuccessUserInfo info){

        try {


            //TODO:真正的发送邮件....
            final String content = String.format(env.getProperty("mail.kill.item.success.content"),info.getProductName(),info.getOrderNo());
            MailDto dto=new MailDto(env.getProperty("mail.kill.item.success.subject"),content,new String[]{info.getEmail()});
           // mailService.sendSimpleEmail(dto);
            mailService.sendHTMLMail(dto);
        }catch (Exception e){
            System.out.println("秒杀异步邮件通知-接收消息-发生异常");
           e.printStackTrace();
        }
    }

    /**
     * 用户秒杀成功后超时未支付-监听者
     * @param info
     */
    @RabbitListener(queues = {"${mq.kill.item.success.kill.dead.real.queue}"},containerFactory = "singleListenerContainer")
    public void consumeExpireOrder(KillSuccessUserInfo info){
        try {
            System.out.println("用户秒杀成功后超时未支付-监听者-接收消息:{}"+info);


            if (info!=null){
                ProductKillOrder entity=productKillOrderMapper.selectByOrderNo(info.getOrderNo());
                if (entity!=null && entity.getStatus().intValue()==0){
                    //更新订单状态到失效
                    productKillOrderMapper.expireOrder(info.getOrderNo(), OrderStatusEum.ORDER_CANCEL.getStatus());
                }
            }
        }catch (Exception e){
            System.out.println("用户秒杀成功后超时未支付-监听者-发生异常");
            e.printStackTrace();
        }
    }
}
