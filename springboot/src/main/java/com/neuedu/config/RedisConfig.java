package com.neuedu.config;

import com.neuedu.common.RedisPool;
import com.neuedu.utils.RedisApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: springboot
 * @BelongsPackage: com.neuedu.config
 * @Author: 郝毓才
 * @CreateTime: 2019-11-06 18:40
 * @Description: 将redisPool交给spring容器管理
 */
@Configuration
public class RedisConfig {

    @Value("${redis.max.total}")
    private Integer maxTotal;
    @Value("${redis.max.idle}")
    private Integer maxIdle;
    @Value("${redis.min.idle}")
    private Integer minIdle;
    @Value("${redis.test.borrow}")
    private boolean testBorrow;
    @Value("${redis.test.return}")
    private boolean testReturn;
    @Value("${redis.ip}")
    private String ip;
    @Value("${redis.port}")
    private Integer port;
    @Value("${redis.password}")
    String password;
    @Value("${redis.timeout}")
    Integer timeout;
    @Bean
    public RedisPool redisPool(){

        return new RedisPool( maxTotal, maxIdle, minIdle,
         testBorrow, testReturn,
         ip, port, password, timeout);
    }

    @Bean
    public RedisApi redisApi(){
        return new RedisApi();
    }
    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public boolean isTestBorrow() {
        return testBorrow;
    }

    public void setTestBorrow(boolean testBorrow) {
        this.testBorrow = testBorrow;
    }

    public boolean isTestReturn() {
        return testReturn;
    }

    public void setTestReturn(boolean testReturn) {
        this.testReturn = testReturn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
