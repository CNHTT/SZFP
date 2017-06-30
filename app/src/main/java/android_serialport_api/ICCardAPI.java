package android_serialport_api;


import android.os.SystemClock;
import android.util.Log;

import com.szfp.szfplib.utils.DataUtils;

public class ICCardAPI {

	private String TAG ="xuws";
	private static final byte[] CMD_OPEN_SWITCH = "D&C0004010I".getBytes();
	private static final byte[] CMD_CLOSE_SWITCH = "D&C0004010J".getBytes();
	private static final byte PORTER_PROTOCOL_SD0 = (byte) 0xCA;//通信开始字节标识1
	private static final byte PORTER_PROTOCOL_SD1 = (byte) 0xDF;//通信开始字节标识2
	private static final byte MODULE_ID = (byte) 0x0;//指示目的模块
	private static final byte DATA_TYPE =(byte)0x35;// 可变标识,指示发送数据的类型,0x35:常规数据，0x36：回调指令
	private static final byte PORTER_PROTOCOL_ED =  (byte)0xE3;//表示一次数据传输结束

	public static final byte[] CMD_DELETE_FILE = { (byte) 0x80, 0x0E ,0x00 ,0x00 ,0x08 ,(byte) 0xFF ,(byte) 0xFF ,
			(byte) 0xFF ,(byte) 0xFF ,(byte) 0xFF ,(byte) 0xFF ,(byte) 0xFF ,(byte) 0xFF};
	public static final byte[] CMD_CREATE_MF = {(byte) 0x80,(byte) 0xE0,0x00,0x00,0x18,(byte) 0xFF,(byte) 0xFF ,
			(byte) 0xFF ,(byte) 0xFF ,(byte) 0xFF ,(byte) 0xFF ,(byte) 0xFF,(byte) 0xFF ,0x0F ,0x01 ,0x31 ,0x50 ,0x41 ,
			0x59 ,0x2E ,0x53 ,0x59 ,0x53 ,0x2E ,0x44 ,0x44 ,0x46 ,0x30 ,0x31 };
	public static final byte[] CMD_END_CREATE_MF = {(byte) 0x80,(byte) 0xE0,0x00,0x01,0x02,0x3F,0x00};
	public static final byte[] CMD_END_CREATE_DF = {(byte) 0x80,(byte) 0xE0,0x01,0x01,0x02,0x2F,0x01};
	public static final byte[] CMD_CREATE_DF = {(byte) 0x80 ,(byte) 0xE0 ,0x01 ,0x00 ,0x0D ,0x2F ,0x01 ,0x0F ,0x00 ,
			(byte) 0xA0 ,0x00 ,0x00 ,0x00 ,0x03 ,(byte) 0x86 ,(byte) 0x98 ,0x07 ,0x01  };
	public static final byte[] CMD_CREATE_BINARY = {(byte) 0x80 ,(byte) 0xE0 ,0x02 ,0x00 ,0x07 ,0x00 ,0x16 ,0x00 ,0x0F ,0x0F ,0x00 ,0x27};
	public static final byte[] CMD_CHOOSE_BINARY = { 0x00 ,(byte) 0xA4,0x02 ,0x00 ,0x02 ,0x00 ,0x16};
	public static final byte[] CMD_READ_BINARY = {0x00 ,(byte) 0xB0 ,(byte) 0x96 ,0x00 ,0x10};
	public static final byte[] CMD_WRITE_BINARY = {0x00 ,(byte) 0xD6 ,(byte) 0x96 ,0x00 ,0x10 ,0x31 ,0x31 ,0x30 ,0x31 ,
			0x30 ,0x38 ,0x37 ,0x30 ,0x30 ,0x33,0x31 ,0x37 ,0x31 ,0x38 ,0x39 ,0x00};
	public static final byte[] CMD_PROC_DIR = {0x00,(byte) 0xA4 ,0x04 ,0x00 ,0x0E ,0x31 ,0x50 ,0x41 ,0x59 ,0x2E ,
			0x53 ,0x59 ,0x53 ,0x2E ,0x44 ,0x44 ,0x46 ,0x30 ,0x31};
	public static final byte[] CMD_Get_Challenge = {0x00,(byte) 0x84 ,0x00 ,0x00 ,0x04 };
	private byte[] mBuffer = new byte[1024];
	public boolean switchStatus() {
		SerialPortManager.getInstance().write(CMD_OPEN_SWITCH);
		Log.i(TAG, "SWITCH_COMMAND hex=" + new String(CMD_OPEN_SWITCH));
		//MainActivity.mHandler.obtainMessage(Constant.IC_SEND, DataUtils.toHexString(CMD_OPEN_SWITCH)).sendToTarget();
		SystemClock.sleep(200);
		SerialPortManager.switchRFID = true;
		return true;
	}
	public boolean CloseSwitchStatus() {
		SerialPortManager.getInstance().write(CMD_CLOSE_SWITCH);
		Log.i(TAG, "CloseSwitchStatus hex=" + new String(CMD_CLOSE_SWITCH));
		SystemClock.sleep(200);
		SerialPortManager.switchRFID = true;
		return true;
	}
	/**
	 * 函数说明：删除文件
	 * @return
	 */
	public String ICDeleteFile() {
		int length = receive(CMD_DELETE_FILE, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICDeleteFile  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICCreateMF() {
		int length = receive(CMD_CREATE_MF, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICCreateMF  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICCreateDF() {
		int length = receive(CMD_CREATE_DF, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICCreateDF  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICCreateBinary() {
		int length = receive(CMD_CREATE_BINARY, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICCreateBinary  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICChooseBinary() {
		int length = receive(CMD_CHOOSE_BINARY, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICChooseBinary  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICReadBinary() {
		int length = receive(CMD_READ_BINARY, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICReadBinary  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICWriteBinary() {
		int length = receive(CMD_WRITE_BINARY, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICWriteBinary  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICProcDir() {
		int length = receive(CMD_PROC_DIR, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i(TAG, "ICProcDir  str=" + receiveData);
		if (length > 0)
			return receiveData;
		return "";
	}
	public String ICGetChallenge() {
		int length = receive(CMD_Get_Challenge, mBuffer);
		if (length > 0)
		{
			byte[] tmp = new byte[length];
			System.arraycopy(mBuffer, 0, tmp, 0,length);
			if((DataUtils.byte2Hexstr(tmp[length - 3]).equals("90")
					&& DataUtils.byte2Hexstr(tmp[length - 2]).equals("00")))
				return DataUtils.toHexString(tmp);
			else
				return "";
		}
		return "";
	}
	/**
	 * 函数说明：组装数据
	 * @param data
	 * @return
	 */
	public byte[] makeProcData(byte[] data)
	{
		byte[] tmp = new byte[data.length + 4 + 1 + 1];
		tmp[0] = PORTER_PROTOCOL_SD0;
		tmp[1] = PORTER_PROTOCOL_SD1;
		tmp[2] = MODULE_ID;
		tmp[3] = DATA_TYPE;
		tmp[4] = (byte) data.length;
		System.arraycopy(data, 0, tmp, 5, data.length);
		tmp[data.length + 4 + 1] = PORTER_PROTOCOL_ED;
		return tmp;
	}
	public String sendCommand(byte[] command)
	{
		int length = receive(command, mBuffer);
		if (length > 0)
		{
			byte[] tmp = new byte[length];
			System.arraycopy(mBuffer, 0, tmp, 0,length);
			return DataUtils.toHexString(tmp);
		}
		return "";
	}
	private int receive(byte[] command, byte[] buffer) {
		int length = -1;
		if (!SerialPortManager.switchRFID) {
			switchStatus();
		}
		//SerialPortManager.getInstance().write(makeProcHeaderData(CMD_PACKET_HEADER,command.length));
		//MainActivity.mHandler.obtainMessage(Constant.IC_SEND, DataUtils.toHexString(makeProcHeaderData(CMD_PACKET_HEADER,command.length))).sendToTarget();
		//SystemClock.sleep(10);
		SerialPortManager.getInstance().write(makeProcData(command));
		//MainActivity.mHandler.obtainMessage(Constant.IC_SEND, DataUtils.toHexString(command)).sendToTarget();
		//SystemClock.sleep(10);
		//SerialPortManager.getInstance().write(makeProcData(CMD_PACKET_END));
		//MainActivity.mHandler.obtainMessage(Constant.IC_SEND, DataUtils.toHexString(makeProcData(CMD_PACKET_END))).sendToTarget();
		length = SerialPortManager.getInstance().read(buffer, 150, 10);
		return length;
	}
}

