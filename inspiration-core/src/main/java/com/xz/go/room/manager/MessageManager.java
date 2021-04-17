package com.xz.go.room.manager;

import javax.annotation.Resource;

import com.xz.go.room.domain.MessageDTO;
import com.xz.go.room.domain.UserMessage;
import org.springframework.stereotype.Component;

/**
 * @author 良谨 2021/4/12 10:04 上午
 */
@Component
public class MessageManager {

    @Resource
    private MessageQueueManager messageQueueManager;
    @Resource
    private RoomUserManager roomUserManager;
    @Resource
    private SequenceManager sequenceManager;

    public MessageDTO pullMessage(String roomTopic, long seq) {
        long start = (seq == 0 ? seq : seq + 1);
        return messageQueueManager.readMessage(roomTopic, start);
    }

    public void sendMessage(String roomTopic, Long userId, String userName, String msg) {
        UserMessage userMessage = new UserMessage();
        userMessage.setUserId(userId);
        userMessage.setUserName(userName);
        userMessage.setMsg(msg);
        userMessage.setTimestamp(System.currentTimeMillis());

        messageQueueManager.saveMessage(roomTopic, userMessage);
    }

    public void clear(String topic) {
        messageQueueManager.clearMessage(topic);
        roomUserManager.clear();
        sequenceManager.clear(topic);
    }
}
