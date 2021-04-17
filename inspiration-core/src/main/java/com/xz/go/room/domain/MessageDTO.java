package com.xz.go.room.domain;

import java.util.List;

import lombok.Data;

/**
 * @author 良谨 2021/4/13 9:37 上午
 */
@Data
public class MessageDTO {
    private List<UserMessage> messages;
    private Long seq;
}
