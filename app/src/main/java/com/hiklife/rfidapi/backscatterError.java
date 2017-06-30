package com.hiklife.rfidapi;

/**
 * 作者：ct on 2017/6/26 10:49
 * 邮箱：cnhttt@163.com
 */

public enum backscatterError {
    Ok,

    /* PC值不存在，不受支持的标签                                               **/
    PCValueNotExist,

    /* 特殊内存区块被锁住或不可写                                               **/
    SpecifiedMemoryLocationLocked,

    /* 标签没有足够的能量进行写操作                                             **/
    InsufficientPower,

    /* 标签不支持特殊错误码                                                     **/
    NotSupportErrorSpecificCodes
}
