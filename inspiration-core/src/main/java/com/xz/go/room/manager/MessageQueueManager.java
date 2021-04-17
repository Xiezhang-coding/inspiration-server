package com.xz.go.room.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.xz.go.room.domain.MessageDTO;
import com.xz.go.room.domain.UserMessage;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

/**
 * @author 良谨 2021/4/12 10:24 下午
 */
@Component
public class MessageQueueManager {

    @Resource
    private JedisCluster jedisCluster;
    @Resource
    private SequenceManager sequenceManager;

    public MessageDTO readMessage(String roomTopic, Long seq) {
        Set<Tuple> tuples = jedisCluster.zrangeByScoreWithScores(roomTopic, seq, Double.MAX_VALUE);

        MessageDTO messageDTO = new MessageDTO();
        List<UserMessage> messages = new ArrayList<>();

        long maxSeq = 0;
        int count = 0;
        for (Tuple tuple : tuples) {
            count++;
            if (count == tuples.size()) {
                maxSeq = (long)tuple.getScore();
            }
            UserMessage userMessage = JSON.parseObject(tuple.getElement(), UserMessage.class);
            messages.add(userMessage);
        }

        messageDTO.setMessages(messages);
        messageDTO.setSeq(maxSeq);

        return messageDTO;
    }

    public void saveMessage(String roomTopic, UserMessage userMessage) {
        long seq = sequenceManager.get(roomTopic);
        userMessage.setNum(seq);
        jedisCluster.zadd(roomTopic, seq, JSON.toJSONString(userMessage));
    }

    public void clearMessage(String topic) {
        jedisCluster.del(topic);
    }
}
