package com.neuedu.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.common
 * @Author: 郝毓才
 * @CreateTime: 2019-11-06 18:16
 * @Description: 封装Redis连接池
 */
public class RedisPool {

    private JedisPool jedisPool;

    public RedisPool() {
    }

    public RedisPool(Integer maxTotal,Integer maxIdle,Integer minIdle,
                     boolean testBorrow,boolean testReturn,
                     String ip,Integer port,String password,Integer timeout) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);//最大连接数
        jedisPoolConfig.setMaxIdle(maxIdle);//最大空闲数
        jedisPoolConfig.setMinIdle(minIdle);//最小空闲数
        jedisPoolConfig.setTestOnBorrow(testBorrow);//当连接池获取连接时检测连接是否有效
        jedisPoolConfig.setTestOnReturn(testReturn);//当放回连接到连接池时检测连接是否有效
        jedisPoolConfig.setBlockWhenExhausted(true);//当连接池的连接耗尽时，会等待直到超时
        jedisPool = new JedisPool(jedisPoolConfig,ip,port,timeout,password);
    }

    public Jedis getJedis(){
        return jedisPool.getResource();
    }

    public void returnJedis(Jedis jedis){
        jedisPool.returnResource(jedis);
    }

    public void returnBrokenResource(Jedis jedis){
        jedisPool.returnBrokenResource(jedis);
    }
}
