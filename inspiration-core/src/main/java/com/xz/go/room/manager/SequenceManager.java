package com.xz.go.room.manager;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

/**
 * @author 良谨 2021/4/13 9:28 上午
 */
@Component
public class SequenceManager {

    public static final String KEY = "room:seq:";

    @Resource
    private JedisCluster jedisCluster;

    public long get(String topic) {
        return jedisCluster.incr(KEY + topic);
    }

    public void clear(String topic) {
        jedisCluster.del(KEY + topic);
    }
}
