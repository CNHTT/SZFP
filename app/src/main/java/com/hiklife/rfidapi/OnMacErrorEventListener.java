package com.hiklife.rfidapi;

import java.util.EventListener;

/**
 * 作者：ct on 2017/6/26 10:53
 * 邮箱：cnhttt@163.com
 */

public interface OnMacErrorEventListener extends EventListener {
    void RadioMacError(MacErrorEvent event); //自定义的实现方法
}
