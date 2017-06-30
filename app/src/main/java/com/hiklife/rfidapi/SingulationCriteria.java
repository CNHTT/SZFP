package com.hiklife.rfidapi;

/**
 * 作者：ct on 2017/6/26 10:59
 * 邮箱：cnhttt@163.com
 */

public class SingulationCriteria {
    public SingulationCriteriaStatus status;
    public matchType match;
    public int offset;
    public int count;
    public byte[] mask;

    public SingulationCriteria()
    {
        status = SingulationCriteriaStatus.Disabled;
        match = matchType.Inverse;
        offset = 0;
        count = 0;
        mask = new byte[62];
    }
}
