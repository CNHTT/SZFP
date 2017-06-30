package com.hiklife.rfidapi;

/**
 * 作者：ct on 2017/6/26 10:50
 * 邮箱：cnhttt@163.com
 */

public class commonFun {
    public static class HostPacketTypes {
        public static final int HOST_PACKET_TYPE_CANCEL_COMMAND = ((short)0x0001) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_RESET_COMMAND = ((short)0x0002) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_ABORT_COMMAND = ((short)0x0003) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_PAUSE_COMMAND = ((short)0x0004) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_RESUME_COMMAND = ((short)0x0005) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GETSN_COMMAND = ((short)0x0006) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_RESET_BL_COMMAND = ((short)0x0007) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_INVENTORY_COMMAND = ((short)0x0011) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_TAG_READ_COMMAND = ((short)0x0012) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_TAG_WRITE_COMMAND = ((short)0x0013) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_TAG_KILL_COMMAND = ((short)0x0014) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_TAG_LOCK_COMMAND = ((short)0x0015) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_TAG_GET_PASSWORD_COMMAND = ((short)0x0016) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_TAG_SET_PASSWORD_COMMAND = ((short)0x0017) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_SET_ANTENNA_PARAM_COMMAND = ((short)0x0018) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GET_ANTENNA_PARAM_COMMAND = ((short)0x0019) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_SET_ANTENNA_STATUS_COMMAND = ((short)0x001A) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GET_ANTENNA_STATUS_COMMAND = ((short)0x001B) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GET_SWR_COMMAND = ((short)0x001C) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GET_LINK_PROFILE_COMMAND = ((short)0x001D) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_SET_LINK_PROFILE_COMMAND = ((short)0x001E) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_SET_GROUP_SESSION_COMMAND = ((short)0x001F) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GET_GROUP_SESSION_COMMAND = ((short)0x0020) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_SET_SINGULATION_ALGORITHM_COMMAND = ((short)0x0021) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GET_SINGULATION_ALGORITHM_COMMAND = ((short)0x0022) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_SET_MASK_ENABLE_COMMAND = ((short)0x0023) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_SET_MASK_DISABLE_COMMAND = ((short)0x0024) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_GET_MASK_SETTING_COMMAND = ((short)0x0025) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_18K6C_TAG_TIDUSER_READ_COMMAND = ((short)0x5001) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_REGISTER_WRITE_COMMAND = ((short)0x5002) & 0x0FFFF;
        public static final int HOST_PACKET_TYPE_REGISTER_READ_COMMAND = ((short)0x5003) & 0x0FFFF;
    }

    public static class RfidPacketTypes {
        public static final int RFID_PACKET_TYPE_COMMAND_BEGIN = ((short)0x0000) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_COMMAND_END = ((short)0x0001) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_18K6C_INVENTORY = ((short)0x0005) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_18K6C_TAG_ACCESS = ((short)0x0006) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_COMMAND_ACTIVE = ((short)0x000E) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_GETSN = ((short)0x000F) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_RESET = ((short)0x0010) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_ABORT = ((short)0x0011) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_SET_ANTENNA_PARAM = ((short)0x0012) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_GET_ANTENNA_PARAM = ((short)0x0013) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_SET_ANTENNA_STATUS = ((short)0x0014) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_GET_ANTENNA_STATUS = ((short)0x0015) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_SWR = ((short)0x0016) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_SET_LINK_PROFILE = ((short)0x0017) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_GET_LINK_PROFILE = ((short)0x0018) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_SET_GROUP_SESSION = ((short)0x0019) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_GET_GROUP_SESSION = ((short)0x001A) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_SET_SINGULATION_ALGORITHM = ((short)0x001B) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_GET_SINGULATION_ALGORITHM = ((short)0x001C) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_REGISTER_READ = ((short)0x001D) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_SET_MASK = ((short)0x001E) & 0x0FFFF;
        public static final int RFID_PACKET_TYPE_GET_MASK_SETTING = ((short)0x001F) & 0x0FFFF;
    }

    /**
     * 转换特定字符信息
     * @param buf 待转换的字节数组
     * @return 转换后的字节数组
     */
    public static final byte[] Add0x99(byte[] buf)
    {
        byte[] byTmp = new byte[4096];
        int count = 0;
        int lastIndex = 0;///标志上一次出现0x99的位置，也就是已经拷贝数据的位置
        int CRCindex = buf.length - 3; ///校验和的下标(下标从0开始)

        for (int i = 2; i < CRCindex; i++)
        {
            if ((buf[i]& 0x00FF) == 0x5A || (buf[i]& 0x00FF) == 0x6A || (buf[i]& 0x00FF) == 0x99)
            {
                // 拷贝之前的数据
                System.arraycopy(buf, lastIndex, byTmp, lastIndex + count, i - lastIndex);
                lastIndex = i + 1;
                byTmp[i + count] = (byte)0x99;
                byTmp[i + count + 1] = (byte)(0xFF - buf[i]);//反码
                count++;
            }
        }

        // 拷贝最后的数据
        System.arraycopy(buf, lastIndex, byTmp, lastIndex + count, buf.length - 1 - lastIndex + 1);
        int newLength = count + buf.length;
        byte[] newBuf = new byte[newLength];
        System.arraycopy(byTmp, 0, newBuf, 0, newLength);

        // 数据变化后需要作校验和更新
        if (count > 0)
        {
            byte CRC32 = 0;
            for (int i = 0; i < newLength - 3; i++)
            {
                CRC32 += newBuf[i];
            }

            newBuf[newLength - 3] = CRC32;
        }

        return newBuf;
    }

    /**
     * 还原特定字符信息
     * @param buf 待还原的字节数组
     * @return 还原后的字节数组
     */
    public static final byte[] Del0x99(byte[] buf)
    {
        // 先进行CRC校验
        byte checksum = 0;
        for (int i = 0; i < buf.length - 3; i++)
        {
            checksum += buf[i];
        }

        if (checksum != buf[buf.length - 3])
        {
            return null;
        }

        byte[] byTmp = new byte[4096];
        int count = 0;
        int lastIndex = 0;///标志上一次出现0x99的位置，也就是已经拷贝数据的位置
        int CRCindex = buf.length - 3; ///校验和的下标(下标从0开始)

        //从长度下标开始，进行DEL0x99处理
        for (int i = 2; i < CRCindex; i++)
        {
            if ((buf[i] & 0x00FF) == 0x99)
            {
                if (((buf[i + 1] & 0x00FF) != 0xFF - 0x99) &&
                        ((buf[i + 1] & 0x00FF) != 0xFF - 0x5A) &&
                        ((buf[i + 1] & 0x00FF) != 0xFF - 0x6A) &&
                        ((buf[i + 1] & 0x00FF) != 0x6A))
                {
                    return null;
                }

                // 拷贝之前的数据
                System.arraycopy(buf, lastIndex, byTmp, lastIndex - count, i - lastIndex);
                lastIndex = i + 2;
                byTmp[i - count] = (byte)(0xFF - (buf[i + 1] & 0x00FF));
                count++;
            }
        }

        // 拷贝最后的数据
        System.arraycopy(buf, lastIndex, byTmp, lastIndex - count, buf.length - 1 - lastIndex + 1);
        int newLength = buf.length - count;
        byte[] newBuf = new byte[newLength];
        System.arraycopy(byTmp, 0, newBuf, 0, newLength);
        if (newBuf[0] != 0x5A || newBuf[1] != 0x55 || newLength >= 256 || newBuf[newLength - 1] != 0x69 || newBuf[newLength - 2] != 0x6A)
        {
            return null;
        }

        if (newLength - 4 != (newBuf[2] & 0x00FF))
        {
            return null;
        }

        return newBuf;
    }

    /**
     * 组建要发送数据包
     * @param buf 数据载荷
     * @param packetType 数据包类型
     * @return 组包后的数据
     */
    public static final byte[] MakePacket(byte[] buf, int packetType)
    {
        int bufLength = 0;
        if (buf != null)
        {
            bufLength = buf.length;
        }

        byte[] newPack = new byte[bufLength + 10];
        newPack[0] = 0x5A;
        newPack[1] = 0x55;
        ByteUtil.putReverseBytesShort(newPack, (short)(bufLength + 6), 2);
        newPack[4] = 0x0D;
        ByteUtil.putReverseBytesShort(newPack, (short)packetType, 5);
        newPack[newPack.length - 2] = 0x6A;
        newPack[newPack.length - 1] = 0x69;

        if (buf != null)
        {
            System.arraycopy(buf, 0, newPack, 7, buf.length);
        }

        byte CRC32 = 0;
        for (int i = 0; i < newPack.length - 3; i++)
        {
            CRC32 += newPack[i];
        }

        newPack[newPack.length - 3] = CRC32;
        return commonFun.Add0x99(newPack);
    }
}
