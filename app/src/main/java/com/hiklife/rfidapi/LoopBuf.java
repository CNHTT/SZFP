package com.hiklife.rfidapi;


import android_serialport_api.LooperBuffer;

/**
 * 作者：ct on 2017/6/26 10:51
 * 邮箱：cnhttt@163.com
 */

public class LoopBuf implements LooperBuffer {
    /**
     * 数据操作互斥量
     */
    private static Object lock = new Object();

    /**
     * 接收缓存队列
     */
    private byte[] LocalBuffer = new byte[204800];

    /**
     * 开始索引
     */
    private int startIndex = 0;

    /**
     * 结束索引
     */
    private int endIndex = 0;

    /**
     * 添加数据到缓冲区
     * @param buf 要添加的数据
     */
    public void add(byte[] buf)
    {
        try
        {
            synchronized(lock)
            {
                if (startIndex <= endIndex)
                {
                    if (((LocalBuffer.length - 1) - endIndex) >= buf.length)
                    {
                        // 缓存足够存下当前的数据
                        System.arraycopy(buf, 0, LocalBuffer, endIndex, buf.length);
                        endIndex += buf.length;
                    }
                    else if (startIndex + ((LocalBuffer.length - 1) - endIndex) >= buf.length)
                    {
                        // 缓存足够存下当前的数据
                        int copyLen = ((LocalBuffer.length - 1) - endIndex + 1);
                        System.arraycopy(buf, 0, LocalBuffer, endIndex, copyLen);
                        System.arraycopy(buf, copyLen, LocalBuffer, 0, buf.length - copyLen);
                        endIndex = buf.length - copyLen;
                    }
                    else
                    {
                        // 缓存不足无法存放数据，则说明目前上层处理速度不足，则进行缓存扩展
                        byte[] newBuf = new byte[LocalBuffer.length + buf.length * 2];
                        System.arraycopy(LocalBuffer, 0, newBuf, 0, LocalBuffer.length);
                        System.arraycopy(buf, 0, newBuf, endIndex, buf.length);
                        endIndex += buf.length;
                        LocalBuffer = newBuf;
                    }
                }
                else
                {
                    // 缓存处于循环状态
                    if ((startIndex - endIndex) >= buf.length)
                    {
                        // 缓存足够存下当前的数据
                        System.arraycopy(buf, 0, LocalBuffer, endIndex, buf.length);
                        endIndex += buf.length;
                    }
                    else
                    {
                        //缓存不足无法存放数据，则说明目前上层处理速度不足，则进行缓存扩展
                        byte[] newBuf = new byte[LocalBuffer.length + buf.length * 2];
                        System.arraycopy(LocalBuffer, 0, newBuf, 0, endIndex - 1);
                        System.arraycopy(buf, 0, newBuf, endIndex, buf.length);
                        endIndex += buf.length;
                        System.arraycopy(LocalBuffer, startIndex, newBuf, newBuf.length - (LocalBuffer.length - startIndex), LocalBuffer.length - startIndex);
                        startIndex = newBuf.length - (LocalBuffer.length - startIndex);
                        LocalBuffer = newBuf;
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个完整的解析包
     * @return 包含完整数据包的数组
     */
    public byte[] getFullPacket()
    {
        try
        {
            synchronized (lock)
            {
                int findStartIndex = startIndex;
                int findEndIndex = startIndex;
                boolean findStart = false;
                boolean findEnd = false;

                // 遍历缓存查找包头
                if (startIndex < endIndex)
                {
                    while (findStartIndex < endIndex - 1) // 只剩包长和包尾可以不必检查
                    {
                        if ((LocalBuffer[findStartIndex]& 0x00FF) == 0x5A && (LocalBuffer[findStartIndex + 1]& 0x00FF) == 0x55)
                        {
                            // 找到了包头，则继续找包尾
                            findEndIndex = findStartIndex + 1;
                            findStart = true;
                            break;
                        }

                        findStartIndex++;
                    }

                    if (findStart)
                    {
                        while (findEndIndex < endIndex - 1)
                        {
                            if ((LocalBuffer[findEndIndex]& 0x00FF) == 0x6A && (LocalBuffer[findEndIndex + 1]& 0x00FF) == 0x69)
                            {
                                // 找到了包尾，则进行后续的数据验证
                                findEndIndex += 1;
                                findEnd = true;
                                break;
                            }
                            else if ((LocalBuffer[findEndIndex]& 0x00FF) == 0x5A && (LocalBuffer[findEndIndex + 1]& 0x00FF) == 0x55)
                            {
                                findStartIndex = findEndIndex;
                            }

                            findEndIndex++;
                        }

                        if (findEnd)
                        {
                            // 都完整找到了，进行校验
                            byte[] tempbuf = new byte[findEndIndex - findStartIndex + 1];
                            System.arraycopy(LocalBuffer, findStartIndex, tempbuf, 0, findEndIndex - findStartIndex + 1);
                            // 数据校验
                            tempbuf = commonFun.Del0x99(tempbuf);

                            if (tempbuf == null)
                            {
                                // 数据包数据有丢失，则舍弃
                                startIndex = findEndIndex + 1;
                                if (startIndex > endIndex)
                                {
                                    startIndex = endIndex;
                                }

                                return null;
                            }
                            else
                            {
                                startIndex = findEndIndex + 1;
                                if (startIndex > endIndex)
                                {
                                    startIndex = endIndex;
                                }

                                return tempbuf;
                            }
                        }
                        else
                        {
                            return null;
                        }
                    }
                    else
                    {
                        // 除去错误的数据包
                        if ((LocalBuffer[startIndex]& 0x00FF) != 0x5A)
                        {
                            startIndex++;
                        }

                        return null;
                    }
                }
                else if (startIndex > endIndex)
                {
                    // 先将buffer进行重组
                    byte[] tempBuf = new byte[LocalBuffer.length - startIndex + 1 + endIndex + 1];
                    System.arraycopy(LocalBuffer, startIndex, tempBuf, 0, LocalBuffer.length - startIndex);
                    System.arraycopy(LocalBuffer, 0, tempBuf, LocalBuffer.length - startIndex, endIndex + 1);

                    findStartIndex = 0;
                    while (findStartIndex < tempBuf.length - 2) // 只剩包长和包尾可以不必检查
                    {
                        if ((tempBuf[findStartIndex]& 0x00FF) == 0x5A && (tempBuf[findStartIndex + 1]& 0x00FF) == 0x55)
                        {
                            // 找到了包头，则继续找包尾
                            findEndIndex = findStartIndex + 1;
                            findStart = true;
                            break;
                        }

                        findStartIndex++;
                    }

                    if (findStart)
                    {
                        while (findEndIndex < tempBuf.length - 2)
                        {
                            if ((tempBuf[findEndIndex]& 0x00FF) == 0x6A && (tempBuf[findEndIndex + 1]& 0x00FF) == 0x69)
                            {
                                // 找到了包尾，则进行后续的数据验证
                                findEndIndex += 1;
                                findEnd = true;
                                break;
                            }
                            else if ((tempBuf[findEndIndex]& 0x00FF) == 0x5A && (tempBuf[findEndIndex + 1]& 0x00FF) == 0x55)
                            {
                                findStartIndex = findEndIndex;
                            }

                            findEndIndex++;
                        }

                        if (findEnd)
                        {
                            // 都完整找到了，那么开始校验
                            byte[] tempcheckbuf = new byte[findEndIndex - findStartIndex + 1];
                            System.arraycopy(tempBuf, findStartIndex, tempcheckbuf, 0, findEndIndex - findStartIndex + 1);
                            tempcheckbuf = commonFun.Del0x99(tempcheckbuf);

                            if (tempcheckbuf == null)
                            {
                                // 数据包数据有丢失，则舍弃
                                startIndex += (findEndIndex + 1);
                                if (startIndex > LocalBuffer.length)
                                {
                                    startIndex = startIndex - LocalBuffer.length - 1;
                                    if (startIndex > endIndex)
                                    {
                                        startIndex = endIndex;
                                    }
                                }

                                return null;
                            }
                            else
                            {
                                startIndex += (findEndIndex + 1);
                                if (startIndex > LocalBuffer.length)
                                {
                                    startIndex = startIndex - LocalBuffer.length - 1;
                                    if (startIndex > endIndex)
                                    {
                                        startIndex = endIndex;
                                    }
                                }

                                return tempcheckbuf;
                            }
                        }
                        else
                        {
                            return null;
                        }
                    }
                    else
                    {
                        // 除去错误的数据包
                        if ((LocalBuffer[startIndex]& 0x00FF) != 0x5A)
                        {
                            startIndex++;
                            if (startIndex > LocalBuffer.length)
                            {
                                startIndex = 0;
                                if (startIndex > endIndex)
                                {
                                    startIndex = endIndex;
                                }
                            }
                        }

                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
