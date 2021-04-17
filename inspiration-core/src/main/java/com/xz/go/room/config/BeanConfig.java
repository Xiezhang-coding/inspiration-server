package com.xz.go.room.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 良谨 2021/4/12 10:25 下午
 */
@Configuration
public class BeanConfig {

    private static final String HOST = "";
    private static final String PASSWORD = "";

    @Bean
    public JedisCluster jedisCluster() {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大空闲连接数，需自行评估，不超过Redis实例的最大连接数
        config.setMaxIdle(200);
        //最大连接数，需自行评估，不超过Redis实例的最大连接数
        config.setMaxTotal(300);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);

        HostAndPort hostAndPort = new HostAndPort(HOST, 6379);
        return new JedisCluster(Collections.singleton(hostAndPort), 2000, 2000, 3,
            PASSWORD, config);

    }
}
