package com.xz.go.room.manager;

import javax.annotation.Resource;

import com.xz.go.room.RoomUserDTO;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

/**
 * @author 良谨 2021/4/17 12:10 下午
 */
@Component
public class RoomUserManager {

    public static final String KEY_ID = "room:user:id:";
    @Resource
    private JedisCluster jedisCluster;

    public RoomUserDTO user() {
        Long id = jedisCluster.incr(KEY_ID);
        RoomUserDTO roomUserDTO = new RoomUserDTO();
        roomUserDTO.setUserId(id);
        roomUserDTO.setUserName("匿名用户" + "（" + id + ")");
        return roomUserDTO;
    }

    public void clear() {
        jedisCluster.del(KEY_ID);
    }

}
