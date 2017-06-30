package com.hiklife.rfidapi;

/**
 * 作者：ct on 2017/6/26 10:49
 * 邮箱：cnhttt@163.com
 */

public class ByteUtil {
    public static void putShort(byte b[], short s, int index) {
        b[index] = (byte)((s >> 8) & 0x00ff);
        b[index + 1] = (byte)((s >> 0) & 0x00ff);
    }

    public static void putReverseBytesShort(byte b[], short s, int index) {
        b[index] = (byte) ((s >> 0) & 0x00ff);
        b[index + 1] = (byte) ((s >> 8) & 0x00ff);
    }

    public static short getShort(byte[] b, int index) {
        return (short) ((((b[index] << 8)& 0xff00) | (b[index + 1] & 0x00ff)));
    }

    public static short getReverseBytesShort(byte[] b, int index) {
        return (short) ((((b[index+1] << 8)& 0xff00) | (b[index] & 0x00ff)));
    }

    // ///////////////////////////////////////////////////////
    public static void putInt(byte[] bb, int x, int index) {
        bb[index + 0] = (byte) ((x >> 24)& 0x00ff);
        bb[index + 1] = (byte) ((x >> 16)& 0x00ff);
        bb[index + 2] = (byte) ((x >> 8)& 0x00ff);
        bb[index + 3] = (byte) ((x >> 0)& 0x00ff);
    }

    public static void putReverseBytesInt(byte[] bb, int x, int index) {
        bb[index + 3] = (byte) ((x >> 24)& 0x00ff);
        bb[index + 2] = (byte) ((x >> 16)& 0x00ff);
        bb[index + 1] = (byte) ((x >> 8)& 0x00ff);
        bb[index + 0] = (byte) ((x >> 0)& 0x00ff);
    }

    public static int getInt(byte[] bb, int index) {
        return (int) ((((bb[index + 0] & 0x00ff) << 24)
                | ((bb[index + 1] & 0x00ff) << 16)
                | ((bb[index + 2] & 0x00ff) << 8) | ((bb[index + 3] & 0x00ff) << 0)));
    }

    public static int getReverseBytesInt(byte[] bb, int index) {
        return (int) ((((bb[index + 3] & 0x00ff) << 24)
                | ((bb[index + 2] & 0x00ff) << 16)
                | ((bb[index + 1] & 0x00ff) << 8) | ((bb[index + 0] & 0x00ff) << 0)));
    }
    // /////////////////////////////////////////////////////////
    public static void putLong(byte[] bb, long x, int index) {
        bb[index + 0] = (byte) ((x >> 56)& 0x00ff);
        bb[index + 1] = (byte) ((x >> 48)& 0x00ff);
        bb[index + 2] = (byte) ((x >> 40)& 0x00ff);
        bb[index + 3] = (byte) ((x >> 32)& 0x00ff);
        bb[index + 4] = (byte) ((x >> 24)& 0x00ff);
        bb[index + 5] = (byte) ((x >> 16)& 0x00ff);
        bb[index + 6] = (byte) ((x >> 8)& 0x00ff);
        bb[index + 7] = (byte) ((x >> 0)& 0x00ff);
    }

    public static void putReverseBytesLong(byte[] bb, long x, int index) {
        bb[index + 7] = (byte) ((x >> 56)& 0x00ff);
        bb[index + 6] = (byte) ((x >> 48)& 0x00ff);
        bb[index + 5] = (byte) ((x >> 40)& 0x00ff);
        bb[index + 4] = (byte) ((x >> 32)& 0x00ff);
        bb[index + 3] = (byte) ((x >> 24)& 0x00ff);
        bb[index + 2] = (byte) ((x >> 16)& 0x00ff);
        bb[index + 1] = (byte) ((x >> 8)& 0x00ff);
        bb[index + 0] = (byte) ((x >> 0)& 0x00ff);
    }

    public static long getLong(byte[] bb, int index) {
        return ((((long) bb[index + 0] & 0x00ff) << 56)
                | (((long) bb[index + 1] & 0x00ff) << 48)
                | (((long) bb[index + 2] & 0x00ff) << 40)
                | (((long) bb[index + 3] & 0x00ff) << 32)
                | (((long) bb[index + 4] & 0x00ff) << 24)
                | (((long) bb[index + 5] & 0x00ff) << 16)
                | (((long) bb[index + 6] & 0x00ff) << 8) | (((long) bb[index + 7] & 0x00ff) << 0));
    }

    public static long getReverseBytesLong(byte[] bb, int index) {
        return ((((long) bb[index + 7] & 0x00ff) << 56)
                | (((long) bb[index + 6] & 0x00ff) << 48)
                | (((long) bb[index + 5] & 0x00ff) << 40)
                | (((long) bb[index + 4] & 0x00ff) << 32)
                | (((long) bb[index + 3] & 0x00ff) << 24)
                | (((long) bb[index + 2] & 0x00ff) << 16)
                | (((long) bb[index + 1] & 0x00ff) << 8) | (((long) bb[index + 0] & 0x00ff) << 0));
    }
}
