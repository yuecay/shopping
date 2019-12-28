package com.neuedu.service;

/**
 * @BelongsProject: kill
 * @BelongsPackage: com.sxu.kill.server.service
 * @Author: 郝毓才
 * @CreateTime: 2019-12-19 15:12
 * @Description: 秒杀逻辑接口层
 */
public interface IKillService {

    Boolean killProduct(Integer killId, Integer userId) throws Exception;

}
