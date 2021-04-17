package com.xz.go.room.notify;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xz.go.room.domain.MessageDTO;
import com.xz.go.room.domain.WaitUser;
import com.xz.go.room.manager.MessageQueueManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * @author 良谨 2021/4/12 12:55 下午
 */
@Component
@Slf4j
public class NotifyManager {

    @Resource
    private MessageQueueManager messageQueueManager;

    private Multimap<String, WaitUser> userQueue = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("timeout-checker-%d").build();
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, threadFactory);

    public void enterWaitQueueWhenNoMessage(String topic, WaitUser waitUser) {
        userQueue.put(topic, waitUser);
        // 超时检测
        executorService.schedule(() -> {
            // 如果该用户还在超时返回
            if (!waitUser.isWaitEnd()) {
                waitUser.setWaitEnd(true);
                userQueue.remove(topic, waitUser);
                // 返回空结果
                AsyncContext asyncContext = waitUser.getAsyncContext();
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setMessages(null);
                messageDTO.setSeq(waitUser.getSeq());
                writeMsg(topic, waitUser, messageDTO);
                asyncContext.complete();
            }
        }, 5, TimeUnit.SECONDS);
    }

    public void notifyWaitUserWhenHasMessage(String topic) {
        Collection<WaitUser> waitUsers = userQueue.get(topic);
        if (CollectionUtils.isEmpty(waitUsers)) {
            return;
        }

        for (WaitUser waitUser : waitUsers) {
            readMessageAndEndUserWait(topic, waitUser);
        }

        userQueue.removeAll(topic);
    }

    private void readMessageAndEndUserWait(String topic, WaitUser waitUser) {
        if (waitUser.isWaitEnd()) {
            return;
        }
        MessageDTO messageDTO = messageQueueManager.readMessage(topic, waitUser.getSeq() + 1);
        writeMsg(topic, waitUser, messageDTO);
        waitUser.getAsyncContext().complete();
    }

    private void writeMsg(String topic, WaitUser waitUser, MessageDTO messageDTO) {
        AsyncContext asyncContext = waitUser.getAsyncContext();
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        try {
            waitUser.setWaitEnd(true);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println(JSON.toJSONString(messageDTO));
        } catch (IOException e) {
            log.error("write message error,topic={},waitUser={}", topic, JSON.toJSONString(waitUser));
        }
    }
}
