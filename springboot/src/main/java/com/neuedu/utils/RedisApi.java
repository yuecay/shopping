package com.neuedu.utils;

import com.neuedu.common.RedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.utils
 * @Author: 郝毓才
 * @CreateTime: 2019-11-06 19:10
 * @Description: 常用的RedisApi
 */
public class RedisApi {

    @Autowired
    RedisPool redisPool;
    /**
     * 字符串相关
     */

    public String set(String key,String value){
        Jedis jedis = redisPool.getJedis();
        String result = null;
        try {
            result = jedis.set(key, value);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
           redisPool.returnBrokenResource(jedis);
        }
        return result;
    }
    public  String get(String key){
        Jedis jedis = redisPool.getJedis();
        String result = null;
        try {
            result = jedis.get(key);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
            redisPool.returnBrokenResource(jedis);
        }
        return result;
    }
    //设置过期时间
    public String setex(String key,String value,int timeout){
        Jedis jedis = redisPool.getJedis();
        String result = null;
        try {
            result = jedis.setex(key,timeout, value);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
            redisPool.returnBrokenResource(jedis);
        }
        return result;
    }
    //设置key的过期时间
    public Long expire(String key,int timeout){
        Jedis jedis = redisPool.getJedis();
        Long result = null;
        try {
            result = jedis.expire(key,timeout);
            redisPool.returnJedis(jedis);
        }catch (Exception e){
            redisPool.returnBrokenResource(jedis);
        }
        return result;
    }



}
