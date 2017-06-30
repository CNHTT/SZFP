package com.hiklife.rfidapi;

import android.util.Log;

import android_serialport_api.SerialPortManager;
import com.szfp.szfplib.utils.DataUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * * RFID 控制接口
 * 作者：ct on 2017/6/26 10:54
 * 邮箱：cnhttt@163.com
 */
public class RadioCtrl {

    /**
     * 串口接收数据缓存
     */
    private LoopBuf recvBuffer = new LoopBuf();

    /**
     * 当前是否有操作未完成
     */
    private boolean isBusy = false;

    public boolean isBusy() {
        return isBusy;
    }

    /**
     * 停止盘点处理线程，主要为了异常处理
     */
    private boolean stopInventory = true;

    /**
     * 判断当前是否是盘点操作
     */
    private boolean isInventory = false;

    /**
     * 当前的连接状态
     */
    private boolean isConnent = false;

    /**
     * 盘点模式, 0 非连续, 1连续
     */
    private int invMode = 1;

    /**
     * 盘点间隔，在盘点模式为非连续盘点模式时有效
     */
    private int interval = 200;

    /**
     * 停止盘点操作互斥量
     */
    private static Object syn_stop = new Object();

    /**
     * 接收处理线程停止标记
     */
    private boolean stopRetrieve = true;

    /**
     * 错误事件监听集合
     */
    private List<OnMacErrorEventListener> mMacErrorListeners = new ArrayList<OnMacErrorEventListener>();

    /**
     * 添加一个错误事件监听
     *
     * @param e
     */
    public void setMacErrorEventListener(OnMacErrorEventListener e) {
        this.mMacErrorListeners.add(e);
    }

    /**
     * 激活错误事件
     *
     * @param event
     */
    private void fireMacErrorEventListener(MacErrorEvent event) {
        for (OnMacErrorEventListener listener : this.mMacErrorListeners) {
            listener.RadioMacError(event);
        }
    }

    /**
     * 盘点事件监听集合
     */
    private List<OnInventoryEventListener> mInventoryListeners = new ArrayList<OnInventoryEventListener>();

    /**
     * 添加一个盘点事件监听
     *
     * @param e
     */
    public void setInventoryEventListener(OnInventoryEventListener e) {
        this.mInventoryListeners.add(e);
    }

    /**
     * 激活盘点事件
     *
     * @param event
     */
    private void fireInventoryEventListener(InventoryEvent event) {
        for (OnInventoryEventListener listener : this.mInventoryListeners) {
            listener.RadioInventory(event);
        }
    }

    /**
     * 盘点处理线程
     */
    private InventoryThread invThread = null;

    /**
     * 盘点处理线程执行体
     */
    private class InventoryThread extends Thread {
        @Override
        public void run() {
            boolean stopThread = false;
            int waitCount = 0;
            while (!stopThread) {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null) {
                    switch (ByteUtil.getReverseBytesShort(packet, 5)) {
                        case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_BEGIN: {
                            break;
                        }

                        case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END: {
                            long errorInfo = ByteUtil.getReverseBytesInt(packet, 8);
                            if (invMode == 1) {
                                stopThread = true;
                                Log.i("whw", "InventoryThread  invMode == 1@@@@@@@@@@@@@@@@@");
                            } else {
                                Log.i("whw", "InventoryThread  else@@@@@@@@@@@@@@@@@");
                                synchronized (syn_stop) {
                                    if (stopInventory == true) {
                                        stopThread = true;
                                        Log.i("whw", "InventoryThread  stopInventory == true @@@@@@@@@@@@@@@@@");
                                    } else {
                                        Log.i("whw", "InventoryThread  stopInventory == false @@@@@@@@@@@@@@@@@");
                                        try {
                                            Thread.sleep(interval);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        // 非连续模式，继续发送盘点指令
                                        try {
                                            // 发送盘点指令
                                            byte[] buffer = new byte[2];
                                            buffer[0] = 0x00;
                                            buffer[1] = 0x00;
                                            buffer = commonFun
                                                    .MakePacket(
                                                            buffer,
                                                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_INVENTORY_COMMAND);
                                            SerialPortManager.getInstance().write(
                                                    buffer);
                                            Log.i("whw", "InventoryThread@@@@@@@@@@@@@@@"+ DataUtils.toHexString(buffer));
                                        } catch (SecurityException e) {
                                            errorInfo = 0x0801;
                                        }
                                    }
                                }
                            }

                            if (errorInfo != 0) {
                                MacErrorInfo error = new MacErrorInfo();
                                error.macError = errorInfo;
                                fireMacErrorEventListener(new MacErrorEvent(this,
                                        error));
                            }

                            break;
                        }

                        case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_INVENTORY: {
                            Log.i("whw", "InventoryThread@@@@@@@@@@@@@@@ RFID_PACKET_TYPE_18K6C_INVENTORY:");
                            int pktEpc_start = 17;
                            int PktRssi = ByteUtil.getReverseBytesShort(packet, 13);
                            int epcLength = ByteUtil.getReverseBytesShort(packet,
                                    15);

                            InventoryTagInfo inventoryInfo = new InventoryTagInfo();
                            inventoryInfo.epc = new short[epcLength / 2];
                            for (int i = pktEpc_start, j = 0; i < (pktEpc_start + epcLength)
                                    && j < inventoryInfo.epc.length; i += 2, j++) {
                                inventoryInfo.epc[j] = ByteUtil.getShort(packet, i);
                            }

                            inventoryInfo.rssi = (float) (PktRssi / 10.0);

                            fireInventoryEventListener(new InventoryEvent(this,
                                    inventoryInfo));
                            break;
                        }

                        default:
                            break;
                    }
                } else {
                    // 当发送了停止指令时，为了防止串口通讯异常导致死循环
                    if (stopInventory) {
                        waitCount++;
                        if (waitCount > 200) {
                            stopThread = true;
                        }
                    }

                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            isInventory = true;
            isBusy = false;
        }
    }

    /**
     * 标签访问结果处理（除读取操作之外）
     *
     * @return 访问结果
     * @throws radioFailException
     */
    private List<TagOperResult> TagMemoryOperate() throws radioFailException {
        TagOperResult tempResult = new TagOperResult();
        List<TagOperResult> resultList = new ArrayList<TagOperResult>();

        int retryCount = 0;
        do {
            byte[] packet = recvBuffer.getFullPacket();
            if (packet != null) {
                retryCount = 0;
                switch (ByteUtil.getReverseBytesShort(packet, 5)) {
                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_BEGIN: {
                        resultList.clear();
                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END: {
                        long errorInfo = ByteUtil.getReverseBytesInt(packet, 8);
                        if (errorInfo != 0) {
                            MacErrorInfo error = new MacErrorInfo();
                            error.macError = errorInfo;
                            fireMacErrorEventListener(new MacErrorEvent(this, error));
                            throw new radioFailException("Tag Access Fail");
                        }

                        return resultList;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_INVENTORY: {
                        int pktEpc_start = 17;
                        int epcLength = ByteUtil.getReverseBytesShort(packet, 15);
                        tempResult.epc = new short[epcLength / 2];
                        for (int i = pktEpc_start, j = 0; i < (pktEpc_start + epcLength)
                                && j < tempResult.epc.length; i += 2, j++) {
                            tempResult.epc[j] = ByteUtil.getShort(packet, i);
                        }

                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_TAG_ACCESS: {
                        short errorcode = ByteUtil.getReverseBytesShort(packet, 14);
                        tempResult.backscatterErrorCode = packet[13] == 0x00 ? backscatterError.Ok
                                : (packet[13] == 0x03 ? backscatterError.PCValueNotExist
                                : (packet[13] == 0x04 ? backscatterError.SpecifiedMemoryLocationLocked
                                : (packet[13] == 0x0B ? backscatterError.InsufficientPower
                                : (packet[13] == 0x0F ? backscatterError.NotSupportErrorSpecificCodes
                                : backscatterError.NotSupportErrorSpecificCodes))));
                        switch (errorcode) {
                            case 0x0000: {
                                tempResult.macAccessErrorCode = macAccessError.Ok;
                                break;
                            }

                            case 0x0001: {
                                tempResult.macAccessErrorCode = macAccessError.HandleMismatch;
                                break;
                            }

                            case 0x0002: {
                                tempResult.macAccessErrorCode = macAccessError.CRCErrorOnTagResponse;
                                break;
                            }

                            case 0x0003: {
                                tempResult.macAccessErrorCode = macAccessError.NoTagReply;
                                break;
                            }

                            case 0x0004: {
                                tempResult.macAccessErrorCode = macAccessError.InvalidPassword;
                                break;
                            }

                            case 0x0005: {
                                tempResult.macAccessErrorCode = macAccessError.ZeroKillPassword;
                                break;
                            }

                            case 0x0006: {
                                tempResult.macAccessErrorCode = macAccessError.TagLost;
                                break;
                            }

                            case 0x0007: {
                                tempResult.macAccessErrorCode = macAccessError.CommandFormatError;
                                break;
                            }

                            case 0x0008: {
                                tempResult.macAccessErrorCode = macAccessError.ReadCountInvalid;
                                break;
                            }

                            case 0x0009: {
                                tempResult.macAccessErrorCode = macAccessError.OutOfRetries;
                                break;
                            }

                            default: {
                                tempResult.macAccessErrorCode = macAccessError.OperationFailed;
                                break;
                            }
                        }

                        if (tempResult.backscatterErrorCode != backscatterError.Ok
                                || tempResult.macAccessErrorCode != macAccessError.Ok) {
                            tempResult.result = tagMemoryOpResult.OperationFailed;
                        } else {
                            tempResult.result = tagMemoryOpResult.Ok;
                        }

                        if (IsAlreadyExistsInMemoryOPResults(resultList, tempResult)) {
                            break;
                        }

                        TagOperResult newReadResult = new TagOperResult();
                        newReadResult.epc = tempResult.epc.clone();
                        newReadResult.backscatterErrorCode = tempResult.backscatterErrorCode;
                        newReadResult.macAccessErrorCode = tempResult.macAccessErrorCode;
                        newReadResult.result = tempResult.result;

                        resultList.add(newReadResult);
                        break;
                    }

                    default:
                        break;
                }
            } else {
                retryCount++;

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (retryCount < 300);

        return resultList;
    }

    /**
     * 检测标签是否重复
     *
     * @param readResults
     *            原结果集
     * @param tag
     *            预增加的标签
     * @return 验证结果
     */
    private boolean IsAlreadyExistsInMemoryOPResults(
            List<TagOperResult> readResults, TagOperResult tag) {
        boolean isAlreadyExists = false;
        for (int i = 0; i < readResults.size(); i++) {
            if (readResults.get(i).getFlagID() == tag.getFlagID()) {
                isAlreadyExists = true;
                if (readResults.get(i).result == tagMemoryOpResult.OperationFailed
                        && tag.result == tagMemoryOpResult.Ok) {
                    readResults.get(i).backscatterErrorCode = backscatterError.Ok;
                    readResults.get(i).macAccessErrorCode = macAccessError.Ok;
                    readResults.get(i).result = tagMemoryOpResult.Ok;
                }

                break;
            }
        }

        return isAlreadyExists;
    }

    /**
     * 标签访问处理（读取相关）
     *
     * @return 处理结果
     * @throws radioFailException
     */
    private List<ReadResult> TagAccessOperate() throws radioFailException {
        ReadResult tempResult = new ReadResult();
        List<ReadResult> resultList = new ArrayList<ReadResult>();

        int retryCount = 0;
        do {
            byte[] packet = recvBuffer.getFullPacket();
            if (packet != null) {
                retryCount = 0;
                switch (ByteUtil.getReverseBytesShort(packet, 5)) {
                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_BEGIN: {
                        resultList.clear();
                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END: {
                        long errorInfo = ByteUtil.getReverseBytesInt(packet, 8);
                        if (errorInfo != 0) {
                            MacErrorInfo error = new MacErrorInfo();
                            error.macError = errorInfo;
                            fireMacErrorEventListener(new MacErrorEvent(this, error));
                            throw new radioFailException("Tag Access Fail");
                        }

                        return resultList;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_INVENTORY: {
                        int pktEpc_start = 17;
                        int epcLength = ByteUtil.getReverseBytesShort(packet, 15);
                        tempResult.epc = new short[epcLength / 2];
                        for (int i = pktEpc_start, j = 0; i < (pktEpc_start + epcLength)
                                && j < tempResult.epc.length; i += 2, j++) {
                            tempResult.epc[j] = ByteUtil.getShort(packet, i);
                        }

                        break;
                    }

                    case commonFun.RfidPacketTypes.RFID_PACKET_TYPE_18K6C_TAG_ACCESS: {
                        short errorcode = ByteUtil.getReverseBytesShort(packet, 14);
                        tempResult.backscatterErrorCode = packet[13] == 0x00 ? backscatterError.Ok
                                : (packet[13] == 0x03 ? backscatterError.PCValueNotExist
                                : (packet[13] == 0x04 ? backscatterError.SpecifiedMemoryLocationLocked
                                : (packet[13] == 0x0B ? backscatterError.InsufficientPower
                                : (packet[13] == 0x0F ? backscatterError.NotSupportErrorSpecificCodes
                                : backscatterError.NotSupportErrorSpecificCodes))));
                        switch (errorcode) {
                            case 0x0000: {
                                tempResult.macAccessErrorCode = macAccessError.Ok;
                                break;
                            }

                            case 0x0001: {
                                tempResult.macAccessErrorCode = macAccessError.HandleMismatch;
                                break;
                            }

                            case 0x0002: {
                                tempResult.macAccessErrorCode = macAccessError.CRCErrorOnTagResponse;
                                break;
                            }

                            case 0x0003: {
                                tempResult.macAccessErrorCode = macAccessError.NoTagReply;
                                break;
                            }

                            case 0x0004: {
                                tempResult.macAccessErrorCode = macAccessError.InvalidPassword;
                                break;
                            }

                            case 0x0005: {
                                tempResult.macAccessErrorCode = macAccessError.ZeroKillPassword;
                                break;
                            }

                            case 0x0006: {
                                tempResult.macAccessErrorCode = macAccessError.TagLost;
                                break;
                            }

                            case 0x0007: {
                                tempResult.macAccessErrorCode = macAccessError.CommandFormatError;
                                break;
                            }

                            case 0x0008: {
                                tempResult.macAccessErrorCode = macAccessError.ReadCountInvalid;
                                break;
                            }

                            case 0x0009: {
                                tempResult.macAccessErrorCode = macAccessError.OutOfRetries;
                                break;
                            }

                            default: {
                                tempResult.macAccessErrorCode = macAccessError.OperationFailed;
                                break;
                            }
                        }

                        if (tempResult.backscatterErrorCode != backscatterError.Ok
                                || tempResult.macAccessErrorCode != macAccessError.Ok) {
                            tempResult.result = tagMemoryOpResult.OperationFailed;
                        } else {
                            tempResult.result = tagMemoryOpResult.Ok;
                        }

                        int packLen = ByteUtil.getReverseBytesShort(packet, 2);
                        int readLength = packLen - 0x11;
                        tempResult.readData = new short[readLength / 2];
                        for (int i = 18, j = 0; i < (18 + readLength)
                                && j < tempResult.readData.length; i += 2, j++) {
                            tempResult.readData[j] = ByteUtil.getShort(packet, i);
                        }

                        if (IsAlreadyExistsInAccessResults(resultList, tempResult)) {
                            break;
                        }

                        ReadResult newReadResult = new ReadResult();
                        newReadResult.epc = tempResult.epc.clone();
                        newReadResult.readData = tempResult.readData.clone();
                        newReadResult.backscatterErrorCode = tempResult.backscatterErrorCode;
                        newReadResult.macAccessErrorCode = tempResult.macAccessErrorCode;
                        newReadResult.result = tempResult.result;

                        resultList.add(newReadResult);
                        break;
                    }

                    default:
                        break;
                }
            } else {
                retryCount++;

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (retryCount < 300);

        return resultList;
    }

    /**
     * 检测标签是否重复
     *
     * @param readResults
     *            原结果集
     * @param tag
     *            预增加的标签
     * @return 验证结果
     */
    private boolean IsAlreadyExistsInAccessResults(
            List<ReadResult> readResults, ReadResult tag) {
        boolean isAlreadyExists = false;
        for (int i = 0; i < readResults.size(); i++) {
            if (readResults.get(i).getFlagID() == tag.getFlagID()) {
                isAlreadyExists = true;
                if (readResults.get(i).result == tagMemoryOpResult.OperationFailed
                        && tag.result == tagMemoryOpResult.Ok) {
                    readResults.get(i).readData = tag.readData.clone();
                    readResults.get(i).backscatterErrorCode = backscatterError.Ok;
                    readResults.get(i).macAccessErrorCode = macAccessError.Ok;
                    readResults.get(i).result = tagMemoryOpResult.Ok;
                }

                break;
            }
        }

        return isAlreadyExists;
    }

    /**
     * 存储区域权限枚举值转换
     *
     * @param permission
     *            带转换的存储区域信息
     * @return 转换后的数值
     */
    private byte GetPermissionWithMemoryBank(MemoryPermission permission) {
        if (permission == MemoryPermission.Writeable) {
            return 0;
        } else if (permission == MemoryPermission.AlwaysWriteable) {
            return 1;
        } else if (permission == MemoryPermission.SecuredWriteable) {
            return 2;
        } else if (permission == MemoryPermission.AlwaysNotWriteable) {
            return 3;
        } else if (permission == MemoryPermission.NoChange) {
            return 4;
        } else {
            return (byte) 0xFF;
        }
    }

    private byte GetPermissionWithPassword(PasswordPermission permission) {
        if (permission == PasswordPermission.Accessible) {
            return 0;
        } else if (permission == PasswordPermission.AlwaysAccessible) {
            return 1;
        } else if (permission == PasswordPermission.SecuredAccessible) {
            return 2;
        } else if (permission == PasswordPermission.AlwaysNotAccessible) {
            return 3;
        } else if (permission == PasswordPermission.NoChange) {
            return 4;
        } else {
            return (byte) 0xFF;
        }
    }

    /**
     * 获取当前连接状态
     *
     * @return true表示已连接，false表示未连接
     */
    public boolean IsConnented() {
        return isConnent;
    }

    /**
     * 获取当前盘点状态
     *
     * @return true表示正在盘点，false表示盘点已结束
     */
    public boolean IsInventory() {
        return isInventory;
    }

    /**
     * 修改寄存器基础指令操作
     *
     * @param registerAddress
     *            需要操作的寄存器地址
     * @param value
     *            要写入的值
     */
    private void WriteRegister(int registerAddress, int value) {
        try {
            byte[] buffer = new byte[6];
            ByteUtil.putReverseBytesShort(buffer, (short) registerAddress, 0);
            ByteUtil.putReverseBytesInt(buffer, value, 2);

            // 发送写信息
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_REGISTER_WRITE_COMMAND);

            SerialPortManager.getInstance().write(buffer);
        } catch (SecurityException e) {
        }
    }

    /**
     * 读取寄存器基础指令操作
     *
     * @param registerAddress
     *            需要操作的寄存器地址
     * @return 读取的结果
     */
    private int ReadRegister(int registerAddress) throws radioFailException {
        int regValue = 0;

        try {
            byte[] buffer = new byte[2];
            ByteUtil.putReverseBytesShort(buffer, (short) registerAddress, 0);

            // 发送读取信息
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_REGISTER_READ_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();

                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_REGISTER_READ) {
                    short reg_addr = ByteUtil.getReverseBytesShort(packet, 7);
                    if (reg_addr != registerAddress) {
                        throw new radioFailException("INVALID PARAMETER");
                    } else {
                        regValue = ByteUtil.getReverseBytesInt(packet, 9);
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            throw new radioFailException("ERROR");
        }

        return regValue;
    }

    /**
     * 连接设备
     *
     *            串口号
     *            波特率
     * @return 连接结果
     */
    public ctrlOperateResult ConnectRadio() throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;
        isBusy = true;

        try {
            stopRetrieve = false;
            SerialPortManager.getInstance().setLoopBuffer(recvBuffer);
            // 发送连接信息
            byte[] buffer = commonFun.MakePacket(null,
                    commonFun.HostPacketTypes.HOST_PACKET_TYPE_GETSN_COMMAND);

            // 客户机器定制的特定标记协议，不通用，其他客户需要自行屏蔽这个crtlFlag的发送
            byte[] ctrlFlag = { 'D', '&', 'C', '0', '0', '0', '4', '0', '1',
                    '0', '9' };
            SerialPortManager.getInstance().write(ctrlFlag);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SerialPortManager.getInstance().write(buffer);
            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();

                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GETSN) {
                    isConnent = true;
                    isSuccess = ctrlOperateResult.OK;
                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        isBusy = false;
        return isSuccess;
    }

    /**
     * 断开连接
     *
     * @return 处理结果
     */
    public ctrlOperateResult DisconnectRadio() throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        stopRetrieve = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.OK;
        byte[] ctrlFlag = { 'D', '&', 'C', '0', '0', '0', '4', '0', '1', '0',
                'A' };
        SerialPortManager.getInstance().write(ctrlFlag);
        SerialPortManager.getInstance().setLoopBuffer(null);
        isConnent = false;
        isBusy = false;
        return isSuccess;
    }

    /**
     * 开始盘点
     *
     * @param inventoryMode
     *            盘点模式(0:周期性盘点，1：连续性盘点)
     * @param intervalTick
     *            盘点间隔，在盘点模式为周期性盘点时有效，单位毫秒
     * @return 操作结果
     */
    public ctrlOperateResult StartInventory(int inventoryMode, int intervalTick)
            throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.OK;
        invMode = inventoryMode;
        interval = intervalTick;

        try {
            byte[] buffer = new byte[2];
            buffer[0] = 0x00;
            buffer[1] = (byte) invMode;
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_INVENTORY_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            stopInventory = false;
            isInventory = true;
            invThread = new InventoryThread();
            invThread.start();
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        return isSuccess;
    }

    /**
     * 结束盘
     *
     * @return 操作结果
     */
    public ctrlOperateResult StopInventory() {
        ctrlOperateResult isSuccess = ctrlOperateResult.OK;

        if (isInventory) {
            try {
                Log.i("whw", "StopInventory  start!!!!!!!!!!!!!!!!");
                synchronized (syn_stop) {
                    stopInventory = true;
                    byte[] buffer = commonFun
                            .MakePacket(
                                    null,
                                    commonFun.HostPacketTypes.HOST_PACKET_TYPE_CANCEL_COMMAND);
                    SerialPortManager.getInstance().write(buffer);
                    Log.i("whw", "StopInventory!!!!!!!!!!!!!!!!"+DataUtils.toHexString(buffer));
                }
                Log.i("whw", "StopInventory  end!!!!!!!!!!!!!!!!");
				/*
				 * int tryCount = 0; while (isInventory && tryCount < 300) { try
				 * { Thread.sleep(10); } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 *
				 * tryCount++; }
				 */
            } catch (SecurityException e) {
                stopInventory = true;
                isSuccess = ctrlOperateResult.SERIALPORTERROR;
            }

            // 等待盘点处理完全退出
			/*
			 * int tryCount = 0; while(isInventory && tryCount < 10) { try {
			 * Thread.sleep(100); } catch(Exception e) { e.printStackTrace(); }
			 *
			 * tryCount++; }
			 */
        }

        return isSuccess;
    }

    /**
     * 获取天线状态
     *
     * @param antennaID
     *            天线号
     * @return 天线状态
     * @throws radioBusyException
     */
    public antennaPortState GetAntennaPortStatus(int antennaID)
            throws radioBusyException {
        antennaPortState portState = antennaPortState.UNKNOWN;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送获取天线状态指令
            byte[] buffer = new byte[1];
            buffer[0] = (byte) antennaID;
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_ANTENNA_STATUS_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_ANTENNA_STATUS) {
                    if (packet[7] == 0x01) {
                        portState = packet[8] == 0x00 ? antennaPortState.DISABLED
                                : antennaPortState.ENABLED;
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return portState;
    }

    /**
     * 设置天线状态
     *
     * @param antennaID
     *            天线号
     * @param portState
     *            天线状态
     * @return 操作结果
     * @throws radioBusyException
     */
    public ctrlOperateResult SetAntennaPortState(int antennaID,
                                                 antennaPortState portState) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            // 发送设置天线状态指令
            byte[] buffer = new byte[2];
            buffer[0] = (byte) antennaID;
            buffer[1] = (byte) (portState == antennaPortState.ENABLED ? 0x01
                    : 0x00);
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_ANTENNA_STATUS_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_ANTENNA_STATUS) {
                    if (packet[7] == 0x01) {
                        isSuccess = ctrlOperateResult.OK;
                    } else {
                        isSuccess = ctrlOperateResult.ERROR;
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    /**
     * 获取天线配置信息
     *
     * @param antennaID
     *            天线号
     * @return 天线配置信息
     * @throws radioBusyException
     * @throws radioFailException
     */
    public AntennaPortConfiguration GetAntennaPortConfiguration(int antennaID)
            throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        AntennaPortConfiguration portconfiguration = null;

        try {
            // 发送获取天线参数指令
            byte[] buffer = new byte[1];
            buffer[0] = (byte) antennaID;
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_ANTENNA_PARAM_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_ANTENNA_PARAM) {
                    if (packet[7] == 0x01) {
                        portconfiguration = new AntennaPortConfiguration();
                        portconfiguration.powerLevel = ByteUtil
                                .getReverseBytesInt(packet, 8);
                        portconfiguration.dwellTime = ByteUtil
                                .getReverseBytesInt(packet, 12);
                        portconfiguration.numberInventoryCycles = ByteUtil
                                .getReverseBytesInt(packet, 16);
                    } else {
                        throw new radioFailException(
                                "Get antenna configuration failed");
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return portconfiguration;
    }

    /**
     * 设置天线配置信息
     *
     * @param antennaID
     *            天线号
     * @param antennaPortConfiguration
     *            天线配置信息
     * @return 操作结果
     * @throws radioBusyException
     */
    public ctrlOperateResult SetAntennaPortConfiguration(int antennaID,
                                                         AntennaPortConfiguration antennaPortConfiguration)
            throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            // 发送设置天线参数指令
            byte[] buffer = new byte[13];
            buffer[0] = (byte) antennaID;
            ByteUtil.putReverseBytesInt(buffer,
                    antennaPortConfiguration.powerLevel, 1);
            ByteUtil.putReverseBytesInt(buffer,
                    antennaPortConfiguration.dwellTime, 5);
            ByteUtil.putReverseBytesInt(buffer,
                    antennaPortConfiguration.numberInventoryCycles, 9);
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_ANTENNA_PARAM_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_ANTENNA_PARAM) {
                    if (packet[7] == 0x01) {
                        isSuccess = ctrlOperateResult.OK;
                    } else {
                        isSuccess = ctrlOperateResult.ERROR;
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    /**
     * 获取Profile信息
     *
     * @return Profile信息索引
     * @throws radioBusyException
     * @throws radioFailException
     */
    public int GetCurrentLinkProfile() throws radioBusyException,
            radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        int profile = -1;

        try {
            // 发送获取Profile指令
            byte[] buffer = commonFun
                    .MakePacket(
                            null,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_LINK_PROFILE_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_LINK_PROFILE) {
                    if (packet[7] == 0x01) {
                        profile = packet[8];
                    } else {
                        throw new radioFailException("Get profile index failed");
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return profile;
    }

    /**
     * 设置Profile信息
     *
     * @param profile
     *            Profile信息索引
     * @return 操作结果
     * @throws radioBusyException
     */
    public ctrlOperateResult SetCurrentLinkProfile(int profile)
            throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
			/*
			 * if (mSerialPort != null) { //
			 * 由于设置profile需要先打开过载波，因此使用获取驻波比功能来修正这个bug isBusy = false; try {
			 * GetAntennaSWR(0, 100); } catch(Exception e) {} isBusy = true;
			 *
			 * // 发送设置Profile指令 byte[] buffer = new byte[1]; buffer[0] =
			 * (byte)profile; buffer = commonFun.MakePacket(buffer,
			 * commonFun.HostPacketTypes
			 * .HOST_PACKET_TYPE_SET_LINK_PROFILE_COMMAND);
			 *
			 * try { synchronized (mOutputStream) { mOutputStream.write(buffer);
			 * mOutputStream.flush(); } }catch (IOException e) {
			 * e.printStackTrace(); }
			 *
			 * int retryCount = 0; do { byte[] packet =
			 * recvBuffer.GetFullPackBuf(); if (packet != null &&
			 * ByteUtil.getReverseBytesShort(packet, 5) ==
			 * commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_LINK_PROFILE) { if
			 * (packet[7] == 0x01) { isSuccess = ctrlOperateResult.OK; } else {
			 * isSuccess = ctrlOperateResult.ERROR; }
			 *
			 * break; } else { retryCount++; try { Thread.sleep(100); }
			 * catch(Exception e) { e.printStackTrace(); } } } while (retryCount
			 * < 10); } else { isBusy = false; isSuccess =
			 * ctrlOperateResult.SERIALPORTERROR; }
			 */

            int currentProfile = ReadRegister(((short) 0x0B60) & 0x0FFFF);

            try {
                int registerValue = currentProfile;
                registerValue &= ~((((int) 0xFFFFFFFF) >> (32 - (8))) << (0));
                registerValue |= ((int) (((currentProfile) & (((int) 0xFFFFFFFF) >> (32 - (8)))) << (0)));
                WriteRegister(((short) 0x0B60) & 0x0FFFF, registerValue);
                WriteRegister(((short) 0xF000) & 0x0FFFF, 0x19);

                try {
                    TagMemoryOperate();
                    isSuccess = ctrlOperateResult.OK;
                } catch (Exception e) {
                    isSuccess = ctrlOperateResult.SERIALPORTERROR;
                }
            } catch (Exception e) {
                WriteRegister(((short) 0x0B60) & 0x0FFFF, currentProfile);
            }
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        } catch (radioFailException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.ERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    /**
     * 获取通话配置信息
     *
     * @return 通话配置信息
     * @throws radioBusyException
     * @throws radioFailException
     */
    public Session GetTagGroupSession() throws radioBusyException,
            radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        Session session = Session.UNKNOWN;

        try {
            // 发送获取Session指令
            byte[] buffer = commonFun
                    .MakePacket(
                            null,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_GROUP_SESSION_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_GROUP_SESSION) {
                    if (packet[7] == 0x01) {
                        switch (packet[8]) {
                            case 0x00: {
                                session = Session.S0;
                                break;
                            }

                            case 0x01: {
                                session = Session.S1;
                                break;
                            }

                            case 0x02: {
                                session = Session.S2;
                                break;
                            }

                            case 0x03: {
                                session = Session.S3;
                                break;
                            }

                            default: {
                                break;
                            }
                        }
                    } else {
                        throw new radioFailException("Get group session failed");
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return session;
    }

    /**
     * 设置通话配置信息
     *
     * @param session
     *            通话配置信息
     * @return 操作结果
     * @throws radioBusyException
     */
    public ctrlOperateResult SetTagGroupSession(Session session)
            throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            // 发送设置Session指令
            byte[] buffer = new byte[1];
            if (session == Session.S0) {
                buffer[0] = (byte) 0x00;
            } else if (session == Session.S1) {
                buffer[0] = (byte) 0x01;
            } else if (session == Session.S2) {
                buffer[0] = (byte) 0x02;
            } else if (session == Session.S3) {
                buffer[0] = (byte) 0x03;
            } else {
                isBusy = false;
                return isSuccess;
            }

            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_GROUP_SESSION_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_GROUP_SESSION) {
                    if (packet[7] == 0x01) {
                        isSuccess = ctrlOperateResult.OK;
                    } else {
                        isSuccess = ctrlOperateResult.ERROR;
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    /**
     * 获取单化算法配置信息
     *
     * @return 单化算法配置信息
     * @throws radioBusyException
     * @throws radioFailException
     */
    public SingulationAlgorithmParms GetCurrentSingulationAlgorithm()
            throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        SingulationAlgorithmParms parm = null;

        try {
            // 发送获取单化算法指令
            byte[] buffer = commonFun
                    .MakePacket(
                            null,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_SINGULATION_ALGORITHM_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_SINGULATION_ALGORITHM) {
                    if (packet[7] == 0x01) {
                        parm = new SingulationAlgorithmParms();
                        parm.qValue = (int) packet[9];
                        parm.startQValue = (int) packet[9];
                        parm.retryCount = (int) packet[10];
                        parm.toggleTarget = (int) packet[11];
                        parm.repeatUntilNoTags = (int) packet[12];
                        parm.minQValue = (int) packet[13];
                        parm.maxQValue = (int) packet[14];
                        parm.thresholdMultiplier = (int) packet[15];
                        switch (packet[8]) {
                            case 0x00: {
                                parm.singulationAlgorithmType = SingulationAlgorithm.FIXEDQ;
                                break;
                            }

                            case 0x01: {
                                parm.singulationAlgorithmType = SingulationAlgorithm.DYNAMICQ;
                                break;
                            }

                            default: {
                                parm.singulationAlgorithmType = SingulationAlgorithm.UNKNOWN;
                                break;
                            }
                        }
                    } else {
                        throw new radioFailException(
                                "Get SingulationAlgorithm failed");
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return parm;
    }

    /**
     * 设置单化算法配置信息
     *
     * @param singulationAlgoithmParms
     *            单化算法配置信息
     * @return singulationAlgoithmParms
     * @throws radioBusyException
     */
    public ctrlOperateResult SetCurrentSingulationAlgorithm(
            SingulationAlgorithmParms singulationAlgoithmParms)
            throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            // 发送设置单化算法指令
            byte[] buffer = new byte[8];
            if (singulationAlgoithmParms.singulationAlgorithmType == SingulationAlgorithm.FIXEDQ) {
                buffer[0] = (byte) 0x00;
            } else if (singulationAlgoithmParms.singulationAlgorithmType == SingulationAlgorithm.DYNAMICQ) {
                buffer[0] = (byte) 0x01;
            } else {
                isBusy = false;
                return isSuccess;
            }

            buffer[1] = (byte) (singulationAlgoithmParms.singulationAlgorithmType == SingulationAlgorithm.FIXEDQ ? singulationAlgoithmParms.qValue
                    : singulationAlgoithmParms.startQValue);
            buffer[2] = (byte) singulationAlgoithmParms.retryCount;
            buffer[3] = (byte) singulationAlgoithmParms.toggleTarget;
            buffer[4] = (byte) singulationAlgoithmParms.repeatUntilNoTags;
            buffer[5] = (byte) singulationAlgoithmParms.minQValue;
            buffer[6] = (byte) singulationAlgoithmParms.maxQValue;
            buffer[7] = (byte) singulationAlgoithmParms.thresholdMultiplier;

            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_SINGULATION_ALGORITHM_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_SINGULATION_ALGORITHM) {
                    if (packet[7] == 0x01) {
                        isSuccess = ctrlOperateResult.OK;
                    } else {
                        isSuccess = ctrlOperateResult.ERROR;
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    /**
     * 重置设备
     *
     * @return 操作结果
     * @throws radioBusyException
     */
    public ctrlOperateResult RadioReset() throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            // 发送重置指令
            byte[] buffer = commonFun.MakePacket(null,
                    commonFun.HostPacketTypes.HOST_PACKET_TYPE_RESET_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_RESET) {
                    if (packet[7] == 0x01) {
                        isSuccess = ctrlOperateResult.OK;
                    } else {
                        isSuccess = ctrlOperateResult.ERROR;
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }

    /**
     * 获取天线驻波比
     *
     * @param antennaID
     *            天线号
     * @param power
     *            天线功率
     * @return 获取结果
     * @throws radioBusyException
     * @throws radioFailException
     */
    public float GetAntennaSWR(int antennaID, int power)
            throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        float antennaSWR = 100;

        try {
            // 发送获取驻波指令
            byte[] buffer = new byte[5];
            buffer[0] = (byte) antennaID;
            ByteUtil.putReverseBytesInt(buffer, power, 1);
            buffer = commonFun.MakePacket(buffer,
                    commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_SWR_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SWR) {
                    if (packet[7] == 0x01) {
                        antennaSWR = (float) (ByteUtil.getReverseBytesInt(
                                packet, 8) / 100.0);
                    } else {
                        throw new radioFailException("Get SWR failed");
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return antennaSWR;
    }

    /**
     * 关闭载波，配合天线驻波获取接口来关闭载波
     *
     * @param antennaID
     *            天线号
     * @throws radioBusyException
     * @throws radioFailException
     */
    public void WaveCtrlOff(int antennaID) throws radioBusyException,
            radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送关闭载波命令
            byte[] buffer = new byte[6];
            ByteUtil.putReverseBytesShort(buffer, (short) 0xF000, 0);
            ByteUtil.putReverseBytesInt(buffer, 0x18, 2);
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_REGISTER_WRITE_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_COMMAND_END) {
                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return;
    }

    /**
     * 标签写入
     *
     * @param writeParms
     *            写入参数
     * @param writeBuf
     *            待写入的数据
     * @return 进行写操作的标签的处理结果
     * @throws radioBusyException
     * @throws radioFailException
     */
    public List<TagOperResult> TagInfoWrite(WriteParms writeParms,
                                            short[] writeBuf) throws radioBusyException, radioFailException {
        List<TagOperResult> tagOperResults = null;

        // 必须保证写入的长度一致
        if (writeParms.length != writeBuf.length) {
            throw new radioFailException("Write data length error");
        }

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送写指令
            byte[] buffer = new byte[9 + writeParms.length * 2];
            buffer[0] = (byte) (writeParms.memBank == MemoryBank.Reserved ? 0x00
                    : (writeParms.memBank == MemoryBank.EPC ? 0x01
                    : (writeParms.memBank == MemoryBank.TID ? 0x02
                    : (writeParms.memBank == MemoryBank.USER ? 0x03
                    : 0x00))));
            ByteUtil.putReverseBytesShort(buffer, writeParms.offset, 1);
            ByteUtil.putReverseBytesShort(buffer, writeParms.length, 3);
            ByteUtil.putReverseBytesInt(buffer, writeParms.accesspassword, 5);
            int bufOffset = 9;

            for (int i = 0; i < writeBuf.length; i++) {
                ByteUtil.putReverseBytesShort(buffer, writeBuf[i], bufOffset);
                bufOffset += 2;
            }

            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_WRITE_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            try {
                tagOperResults = TagMemoryOperate();
            } catch (radioFailException e) {
                throw new radioFailException("RFID module write fail");
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    /**
     * 标签读取
     *
     * @param readParms
     *            读取参数
     * @return 读取标签内容
     * @throws radioBusyException
     * @throws radioFailException
     */
    public List<ReadResult> TagInfoRead(ReadParms readParms)
            throws radioBusyException, radioFailException {
        List<ReadResult> readResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送读指令
            byte[] buffer = new byte[11];

            buffer[0] = (byte) (readParms.memBank == MemoryBank.Reserved ? 0x00
                    : (readParms.memBank == MemoryBank.EPC ? 0x01
                    : (readParms.memBank == MemoryBank.TID ? 0x02
                    : (readParms.memBank == MemoryBank.USER ? 0x03
                    : 0x00))));
            ByteUtil.putReverseBytesShort(buffer, readParms.offset, 1);
            ByteUtil.putReverseBytesShort(buffer, readParms.length, 3);
            ByteUtil.putReverseBytesInt(buffer, readParms.accesspassword, 5);
            buffer[9] = 0;
            buffer[10] = 0;
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_READ_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            try {
                readResults = TagAccessOperate();
            } catch (radioFailException e) {
                throw new radioFailException("RFID module write fail");
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return readResults;
    }

    /**
     * 标签锁定
     *
     * @param lockParms
     *            锁定参数
     * @param accessPassword
     *            访问密码
     * @return 标签锁定操作结果
     * @throws radioBusyException
     * @throws radioFailException
     */
    public List<TagOperResult> TagLock(LockParms lockParms, int accessPassword)
            throws radioBusyException, radioFailException {
        List<TagOperResult> tagOperResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送锁定指令
            byte[] buffer = new byte[9];
            buffer[0] = GetPermissionWithPassword(lockParms.killPasswordPermission);
            buffer[1] = GetPermissionWithPassword(lockParms.accessPasswordPermission);
            buffer[2] = GetPermissionWithMemoryBank(lockParms.EPCMemoryBankPermissions);
            buffer[3] = GetPermissionWithMemoryBank(lockParms.TIDMemoryBankPermissions);
            buffer[4] = GetPermissionWithMemoryBank(lockParms.USERMemoryBankPermissions);
            ByteUtil.putReverseBytesInt(buffer, accessPassword, 5);
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_LOCK_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            try {
                tagOperResults = TagMemoryOperate();
            } catch (radioFailException e) {
                throw new radioFailException("RFID module tag lock fail");
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    /**
     * 标签销毁
     *
     * @param accessPassword
     *            访问密码
     * @param killPassword
     *            销毁密码
     * @return 标签销毁处理结果
     * @throws radioBusyException
     * @throws radioFailException
     */
    public List<TagOperResult> TagKill(int accessPassword, int killPassword)
            throws radioBusyException, radioFailException {
        List<TagOperResult> tagOperResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送销毁指令
            byte[] buffer = new byte[8];
            ByteUtil.putReverseBytesInt(buffer, accessPassword, 0);
            ByteUtil.putReverseBytesInt(buffer, killPassword, 4);
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_KILL_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            try {
                tagOperResults = TagMemoryOperate();
            } catch (radioFailException e) {
                throw new radioFailException("RFID module tag lock fail");
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    /**
     * 获取标签密码
     *
     * @param type
     *            要获取的密码类型
     * @param accessPassword
     *            访问密码
     * @return 获取结果列表
     * @throws radioBusyException
     * @throws radioFailException
     */
    public List<PasswordResult> GetTagPassword(PasswordType type,
                                               int accessPassword) throws radioBusyException, radioFailException {
        List<PasswordResult> passwordValues = null;
        List<ReadResult> readResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送读指令
            byte[] buffer = new byte[5];
            buffer[0] = (byte) (type == PasswordType.AccessPassword ? 0x00
                    : 0x01);
            ByteUtil.putReverseBytesInt(buffer, accessPassword, 1);
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_GET_PASSWORD_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            try {
                readResults = TagAccessOperate();
                if (readResults != null) {
                    passwordValues = new ArrayList<PasswordResult>();
                    for (int i = 0; i < readResults.size(); i++) {
                        PasswordResult newPassword = new PasswordResult();
                        newPassword.epc = readResults.get(i).epc.clone();
                        newPassword.accessPasswordValue = (int) ((readResults
                                .get(i).readData[0] & 0x0000FFFF) << 16)
                                + (readResults.get(i).readData[1] & 0x0000FFFF);
                        newPassword.backscatterErrorCode = readResults.get(i).backscatterErrorCode;
                        newPassword.macAccessErrorCode = readResults.get(i).macAccessErrorCode;
                        newPassword.result = readResults.get(i).result;
                        passwordValues.add(newPassword);
                    }
                }
            } catch (radioFailException e) {
                throw new radioFailException("RFID module write fail");
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return passwordValues;
    }

    /**
     * 设置标签密码
     *
     * @param type
     *            要修改的密码类型
     * @param accessPassword
     *            访问密码
     * @param newPassword
     *            新密码
     * @return 标签密码修改处理结果
     * @throws radioBusyException
     * @throws radioFailException
     */
    public List<TagOperResult> ModifyTagPassword(PasswordType type,
                                                 int accessPassword, int newPassword) throws radioBusyException,
            radioFailException {
        List<TagOperResult> tagOperResults = null;

        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;

        try {
            // 发送设置密码指令
            byte[] buffer = new byte[9];
            buffer[0] = (byte) (type == PasswordType.AccessPassword ? 0x00
                    : 0x01);
            ByteUtil.putReverseBytesInt(buffer, accessPassword, 1);
            ByteUtil.putReverseBytesInt(buffer, newPassword, 5);
            buffer = commonFun
                    .MakePacket(
                            buffer,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_18K6C_TAG_SET_PASSWORD_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            try {
                tagOperResults = TagMemoryOperate();
            } catch (radioFailException e) {
                throw new radioFailException("RFID module write fail");
            }
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return tagOperResults;
    }

    /**
     * 获取Mask设置信息
     *
     * @return >Mask设置信息
     * @throws radioBusyException
     * @throws radioFailException
     */
    public SingulationCriteria Get18K6CPostMatchCriteria()
            throws radioBusyException, radioFailException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        SingulationCriteria criteria = new SingulationCriteria();

        try {
            // 发送获取Mask配置信息指令
            byte[] buffer = commonFun
                    .MakePacket(
                            null,
                            commonFun.HostPacketTypes.HOST_PACKET_TYPE_GET_MASK_SETTING_COMMAND);

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_GET_MASK_SETTING) {
                    if (packet[7] == 0x01) {
                        criteria.status = packet[8] == 0x01 ? SingulationCriteriaStatus.Enabled
                                : SingulationCriteriaStatus.Disabled;
                        if (criteria.status == SingulationCriteriaStatus.Enabled) {
                            criteria.match = packet[9] == 0x00 ? matchType.Inverse
                                    : matchType.Regular;
                            criteria.offset = ByteUtil.getReverseBytesInt(
                                    packet, 10);
                            criteria.count = ByteUtil.getReverseBytesInt(
                                    packet, 14);
                            for (int i = 0; i < criteria.mask.length; i++) {
                                criteria.mask[i] = packet[18 + i];
                            }
                        }
                    } else {
                        throw new radioFailException("Get SWR failed");
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 10);
        } catch (SecurityException e) {
            isBusy = false;
        }

        isBusy = false;
        return criteria;
    }

    // / <summary>
    // / 设置Mask信息
    // / </summary>
    // / <param name="criteria">Mask信息</param>
    // / <returns>操作结果</returns>

    public ctrlOperateResult Set18K6CPostMatchCriteria(
            SingulationCriteria criteria) throws radioBusyException {
        if (isBusy) {
            throw new radioBusyException("RFID module is busy");
        }

        isBusy = true;
        ctrlOperateResult isSuccess = ctrlOperateResult.ERROR;

        try {
            // 发送设置Mask配置信息指令
            byte[] buffer = null;
            if (criteria.status == SingulationCriteriaStatus.Enabled) {
                buffer = new byte[71];
                buffer[0] = (byte) (criteria.match == matchType.Inverse ? 0x00
                        : 0x01);
                ByteUtil.putReverseBytesInt(buffer, criteria.offset, 1);
                ByteUtil.putReverseBytesInt(buffer, criteria.count, 5);
                System.arraycopy(criteria.mask, 0, buffer, 9,
                        criteria.mask.length);

                buffer = commonFun
                        .MakePacket(
                                buffer,
                                commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_MASK_ENABLE_COMMAND);
            } else {
                buffer = commonFun
                        .MakePacket(
                                buffer,
                                commonFun.HostPacketTypes.HOST_PACKET_TYPE_SET_MASK_DISABLE_COMMAND);
            }

            SerialPortManager.getInstance().write(buffer);

            int retryCount = 0;
            do {
                byte[] packet = recvBuffer.getFullPacket();
                if (packet != null
                        && ByteUtil.getReverseBytesShort(packet, 5) == commonFun.RfidPacketTypes.RFID_PACKET_TYPE_SET_MASK) {
                    if (packet[7] == 0x01) {
                        isSuccess = ctrlOperateResult.OK;
                    } else {
                        isSuccess = ctrlOperateResult.ERROR;
                    }

                    break;
                } else {
                    retryCount++;
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (retryCount < 300);
        } catch (SecurityException e) {
            isBusy = false;
            isSuccess = ctrlOperateResult.SERIALPORTERROR;
        }

        isBusy = false;
        return isSuccess;
    }
}
