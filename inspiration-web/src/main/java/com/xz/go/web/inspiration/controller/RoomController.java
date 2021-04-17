package com.xz.go.web.inspiration.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.xz.go.room.RoomUserDTO;
import com.xz.go.room.domain.MessageDTO;
import com.xz.go.room.domain.WaitUser;
import com.xz.go.room.manager.MessageManager;
import com.xz.go.room.manager.RoomUserManager;
import com.xz.go.room.notify.NotifyManager;
import com.xz.go.web.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 良谨 2021/4/11 11:00 下午
 */
@Slf4j
@RestController("/")
public class RoomController {

    @Resource
    private MessageManager messageManager;
    @Resource
    private NotifyManager notifyManager;
    @Resource
    private RoomUserManager roomUserManager;

    @RequestMapping(value = "/message/send", method = RequestMethod.POST)
    public void sendMessage(@RequestBody SendMessageRequest request) {
        log.warn("sendMessage,{}|{}|{}", request.getTopic(), request.getMsg(), request.getUserId());
        messageManager.sendMessage(request.getTopic(), request.getUserId(), request.getUserName(), request.getMsg());
        notifyManager.notifyWaitUserWhenHasMessage(request.getTopic());
    }

    @RequestMapping("/message/pull")
    public void pullMessage(HttpServletRequest request, HttpServletResponse response, @RequestParam String topic,
        @RequestParam Long seq, @RequestParam Long userId) throws IOException {

        log.warn("pullMessage,{}|{}|{}", topic, seq, userId);
        MessageDTO message = messageManager.pullMessage(topic, seq);

        if (CollectionUtils.isNotEmpty(message.getMessages())) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println(JSON.toJSONString(message));
        } else {
            AsyncContext asyncContext = request.startAsync(request, response);
            notifyManager.enterWaitQueueWhenNoMessage(topic, new WaitUser(asyncContext, false, userId, seq));
        }
    }

    @RequestMapping("/message/clear")
    public void clearMessage(@RequestParam String topic) {
        log.warn("clearMessage,{}", topic);
        messageManager.clear(topic);
    }

    @RequestMapping("/user/login")
    public RoomUserDTO login() {
        return roomUserManager.user();
    }
}
