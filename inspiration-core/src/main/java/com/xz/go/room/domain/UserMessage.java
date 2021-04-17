package com.xz.go.room.domain;

import lombok.Data;

/**
 * @author 良谨 2021/4/17 12:24 下午
 */
@Data
public class UserMessage {
    private Long userId;
    private String userName;
    private String msg;
    private Long num;
    private Long timestamp;
}
