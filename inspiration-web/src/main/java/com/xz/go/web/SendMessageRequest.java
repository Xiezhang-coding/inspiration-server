package com.xz.go.web;

import lombok.Data;

/**
 * @author 良谨 2021/4/15 10:30 下午
 */
@Data
public class SendMessageRequest {
    private String topic;
    private Long userId;
    private String msg;
    String userName;
}
