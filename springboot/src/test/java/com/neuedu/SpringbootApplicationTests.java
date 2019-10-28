package com.neuedu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class SpringbootApplicationTests {
    @Resource
    private DataSource dataSource;
    @Test
    public void contextLoads() {
        System.out.println(this.dataSource);

    }


}
