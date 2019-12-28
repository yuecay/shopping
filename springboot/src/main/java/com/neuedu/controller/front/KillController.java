package com.neuedu.controller.front;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.service.IKillService;
import com.neuedu.vo.KillVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/killItem")
public class KillController {
    private static final Logger log = LoggerFactory.getLogger(KillController.class);

    @Autowired
    private IKillService killService;
    /**
     * 秒杀业务逻辑压力测试
     * @param dto
     * @param result
     * @param session
     * @return
     */
    @RequestMapping(value = "/execute/lock",method = RequestMethod.GET,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ServerResponse executeLock(@RequestBody @Validated KillVO dto, BindingResult result, HttpSession session){
        if (result.hasErrors() || dto.getKillId()<=0){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"秒杀出错！");
        }
        try {
            //加redisson分布式锁的前提
            Boolean res=killService.killProduct(dto.getKillId(),dto.getUserId());
            if (!res){
                System.out.println("商品已抢购完毕或");
                return ServerResponse.serverResponseByError(ResponseCode.ERROR,"商品已抢购完毕或者不在抢购时间段哦!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ServerResponse.serverResponseBySuccess("抢购成功！");
    }
}
