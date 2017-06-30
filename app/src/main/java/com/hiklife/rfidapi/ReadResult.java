package com.hiklife.rfidapi;

/**
 * 作者：ct on 2017/6/26 10:57
 * 邮箱：cnhttt@163.com
 */

public class ReadResult {
    private String flagid = "";
    public short[] epc;
    public short[] readData;
    public backscatterError backscatterErrorCode;
    public macAccessError macAccessErrorCode;
    public tagMemoryOpResult result;

    public String getFlagID()
    {
        if (flagid != "")
        {
            return flagid;
        }
        else
        {
            if (epc != null)
            {
                for (int i = 0; i < epc.length; i++)
                {
                    flagid += Integer.toHexString(((epc[i] >> 8) & 0x000000FF) | 0xFFFFFF00).substring(6) + Integer.toHexString((epc[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
                }

                return flagid;
            }
            else
            {
                return "";
            }
        }
    }
}
