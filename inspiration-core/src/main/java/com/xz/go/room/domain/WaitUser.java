package com.xz.go.room.domain;

import javax.servlet.AsyncContext;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 良谨 2021/4/12 12:54 下午
 */
@Data
@AllArgsConstructor
public class WaitUser {
    private AsyncContext asyncContext;
    private boolean waitEnd;
    private Long userId;
    private Long seq;
}
