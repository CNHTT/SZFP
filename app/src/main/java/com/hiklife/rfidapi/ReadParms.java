package com.hiklife.rfidapi;

/**
 * 作者：ct on 2017/6/26 10:57
 * 邮箱：cnhttt@163.com
 */

public class ReadParms {
    public MemoryBank memBank;
    public short offset;
    public short length;
    public int accesspassword;

    public ReadParms()
    {
        memBank = MemoryBank.Reserved;
        offset = 0;
        length = 0;
        accesspassword = 0;
    }
}
