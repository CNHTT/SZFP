package com.hiklife.rfidapi;

import java.util.EventListener;

/**
 * 作者：ct on 2017/6/26 10:52
 * 邮箱：cnhttt@163.com
 */

public interface OnInventoryEventListener extends EventListener {
    void RadioInventory(InventoryEvent event); //自定义的实现方法
}
