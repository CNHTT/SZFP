package android_serialport_api;

import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.szfp.szfplib.utils.DataUtils;

public class TypeBCardAPI {
	/**
	 * 超时
	 */
	public static final int TIME_OUT = 1;
	/**
	 * 无响应
	 */
	public static final int NO_RESPONSE = 2;
	/**
	 * 通过认证
	 */
	public static final int CERTIFICATION_SUCCESS = 3;
	/**
	 * 认证失败
	 */
	public static final int CERTIFICATION_FAIL = 4;

	/**
	 * 循环冗余校验码错误
	 */
	public static final int CRC_ERROR = 5;

	/**
	 * 读成功
	 */
	public static final int READ_SUCCESS = 6;

	/**
	 * 读失败
	 */
	public static final int READ_FAIL = 7;

	/**
	 * 写成功
	 */
	public static final int WRITE_SUCCESS = 8;

	/**
	 * 写失败
	 */
	public static final int WRITE_FAIL = 9;

	/**
	 * 去选成功
	 */
	public static final int DESELECT_SUCCESS = 10;

	/**
	 * 激活成功
	 */
	public static final int ACTIVE_SUCCESS = 11;

	/**
	 * 邀请成功
	 */
	public static final int REQUEST_SUCCESS = 12;

	private byte[] buffer = new byte[100];

	private static final byte[] SWITCH_COMMAND = "D&C00040104".getBytes();

	private static final String END_IDENTIFY = "\r\n";

	/**
	 * 应用类别标识符（Application Family Identifier），卡片预选应用标准 AFI
	 * 代表读卡机具指定的使用类型，用于预选卡片，只有卡片的应用为指定的AFI 时，卡片才会应答这条指令。 卡片的AFI 可定制。当AFI
	 * 为‘0x00’时，所有卡片都会应答（不预选）。
	 */
	private byte AFI;

	/**
	 * 伪唯一卡片标识符（Pseudo-Unique PICC Identifier）
	 */
	private String PUPI;

	/**
	 * 应用数据（Application Data）（4 个字节）： 发行商可定义的卡片专用标识，位于存储区第0 页的高4 个字
	 * 卡片的这个数据信息用于告诉读卡机具卡片的具体应用，使读卡机具在发现多张卡片时可以选择需要的卡片
	 */
	private String application_data;

	/**
	 * 卡片标识符（Card IDentifier）
	 * 每个处于激活状态下的卡片拥有唯一的通道号，它的取值范围为‘0~14’，‘15’保留,用4bit位表示（0000~1110）
	 */
	private String CID;

	private static final byte PAGE0 = 0;
	private static final byte PAGE1 = 4;
	private static final byte PAGE2 = 8;
	private static final byte PAGE3 = 12;

	private static final byte READ = 2;
	private static final byte WRITE = 3;

	private final Message msg = new Message();

	private void clearMessage() {
		msg.what = -1;
		msg.obj = null;
	}

	/**
	 * 卡片序列号
	 */
	private String serialNum;

	private boolean isSwitch = false;

	private boolean switchStatus() {
		SerialPortManager.getInstance().write(SWITCH_COMMAND);
		Log.i("whw", "SWITCH_COMMAND hex=" + new String(SWITCH_COMMAND));
		SystemClock.sleep(500);
		return true;
	}

	/**
	 * 应用类别标识符（Application Family Identifier），卡片预选应用标准 AFI
	 * 代表读卡机具指定的使用类型，用于预选卡片，只有卡片的应用为指定的AFI 时，卡片才会应答这条指令。 卡片的AFI 可定制。当AFI
	 * 为‘0x00’时，所有卡片都会应答（不预选）。
	 * 当邀请成功，返回8字节的数据
	 * 标识符（PUPI）（4 个字节）：卡片的唯一序列号的低4 个字节。
	 * 应用数据（Application Data）（4 个字节）：发行商可定义的卡片专用标识，位于存储区第0 页的高4 个字节。
	 */
	public Message request(byte afi) {
		if (!isSwitch) {
			switchStatus();
			isSwitch = true;
		}

		StringBuffer commandStr = new StringBuffer("f05");
		commandStr.append(DataUtils.toHexString1(afi));
		commandStr.append("00");
		commandStr.append(END_IDENTIFY);
		Log.i("whw", "request=" + commandStr.toString());
		SerialPortManager.getInstance().write(commandStr.toString().getBytes());
		int length = SerialPortManager.getInstance().read(buffer, 3000, 30);
		Log.i("whw", "length=" + length);
		String result = null;
		if (length > 0) {
			byte[] data = new byte[length];
			System.arraycopy(buffer, 0, data, 0, length);
			result = new String(data);
			Log.i("whw", "temp str=" + result);
			PUPI = result.substring(2, 10);
			application_data = result.substring(10, 18);
			Log.i("whw", "PUPI=" + PUPI);
			Log.i("whw", "application_data=" + application_data);
		}

		clearMessage();
		if (result == null) {
			msg.what = TIME_OUT;
		} else {
			if (length == 26) {
				String dataStr = result.substring(2, 18);
				msg.obj = DataUtils.hexStringTobyte(dataStr);
				msg.what = REQUEST_SUCCESS;
			} else {
				msg.what = NO_RESPONSE;
			}
		}
		return msg;
	}

	/**
	 * 卡片标识符（Card IDentifier）
	 * 每个处于激活状态下的卡片拥有唯一的通道号，它的取值范围为‘0~14’，‘15’保留,用4bit位表示（0000~1110）
	 * cid可以用十六进制字符'0'~'e'表示 ，字符'f'保留
	 * 如果激活成功   ACTIVE_SUCCESS  ，返回8字节的序列号（serialNum）
	 */
	public Message active(byte[] pupi, char cid) {
		StringBuffer commandStr = new StringBuffer("f1d");
		commandStr.append(DataUtils.toHexString(pupi));
		commandStr.append("0000000" + cid);
		commandStr.append("00");
		commandStr.append(END_IDENTIFY);

		Log.i("whw", "active=" + commandStr.toString());
		SerialPortManager.getInstance().write(commandStr.toString().getBytes());
		int activelength = SerialPortManager.getInstance().read(buffer, 3000,
				30);
		Log.i("whw", "activelength=" + activelength);
		String result = null;
		if (activelength > 0) {
			byte[] data = new byte[activelength];
			System.arraycopy(buffer, 0, data, 0, activelength);
			result = new String(data);
			Log.i("whw", "active str=" + result);

		}

		clearMessage();
		if (result == null) {
			msg.what = TIME_OUT;
		} else {
			if (activelength == 22) {
				serialNum = result.substring(4, 20);
				Log.i("whw", "SerialNum=" + serialNum);
				msg.obj = DataUtils.hexStringTobyte(serialNum);
				msg.what = ACTIVE_SUCCESS;
			} else {
				msg.what = NO_RESPONSE;
			}
		}
		return msg;
	}

	private Message read(char cid, byte pageId, String address) {
		StringBuffer commandStr = new StringBuffer("f");
		commandStr.append(cid);
		commandStr.append(DataUtils.toHexString1((byte) (pageId + READ))
				.substring(1));
		commandStr.append(address);
		commandStr.append(END_IDENTIFY);
		Log.i("whw", "read=" + commandStr);
		SerialPortManager.getInstance().write(commandStr.toString().getBytes());
		int readlength = SerialPortManager.getInstance().read(buffer, 3000, 30);
		String result = null;
		if (readlength > 0) {
			byte[] data = new byte[readlength];
			System.arraycopy(buffer, 0, data, 0, readlength);
			result = new String(data);
			Log.i("whw", "read result str=" + result + "   length="
					+ readlength);

		}

		clearMessage();
		byte[] data = null;
		if (result == null) {
			msg.what = TIME_OUT;
		} else {
			if (result.length() == 20) {
				data = DataUtils.hexStringTobyte(result.substring(2, 18));
				msg.what = READ_SUCCESS;
				msg.obj = data;
				Log.i("whw", "obj length=" + data.length);
			} else if (result.length() == 4) {
				if (result.charAt(1) == '1') {
					msg.what = READ_FAIL;
				} else if (result.charAt(1) == '2') {
					msg.what = CRC_ERROR;
				}
			} else {
				msg.what = NO_RESPONSE;
			}
		}
		Log.i("whw", "msg.what=" + msg.what + "  obj" + msg.obj);
		return msg;
	}

	private Message write(char cid, byte pageId, String address, String dataStr) {
		StringBuffer commandStr = new StringBuffer("f");
		commandStr.append(cid);
		commandStr.append(DataUtils.toHexString1((byte) (pageId + WRITE))
				.substring(1));
		commandStr.append(address);
		commandStr.append(dataStr);
		commandStr.append(END_IDENTIFY);
		Log.i("whw", "write=" + commandStr);
		SerialPortManager.getInstance().write(commandStr.toString().getBytes());
		int writelength = SerialPortManager.getInstance()
				.read(buffer, 3000, 30);
		String result = null;
		if (writelength > 0) {
			byte[] data = new byte[writelength];
			System.arraycopy(buffer, 0, data, 0, writelength);
			result = new String(data);
			Log.i("whw", "write result str=" + result);
		}

		clearMessage();
		if (result == null) {
			msg.what = TIME_OUT;
		} else {
			if (result.charAt(1) == '0') {
				msg.what = WRITE_SUCCESS;
			} else if (result.charAt(1) == '1') {
				msg.what = WRITE_FAIL;
			} else if (result.charAt(1) == '2') {
				msg.what = CRC_ERROR;
			} else {
				msg.what = NO_RESPONSE;
			}
		}
		return msg;

	}

	public Message authentication(char cid, byte[] key) {
		return write(cid, PAGE2, "00", DataUtils.toHexString(key));

	}

	public Message deselect(char cid) {
		StringBuffer commandStr = new StringBuffer("f");
		commandStr.append(cid);
		commandStr.append("8");// 1000
		commandStr.append(END_IDENTIFY);
		Log.i("whw", "deselect=" + commandStr);
		SerialPortManager.getInstance().write(commandStr.toString().getBytes());
		int disSelectlength = SerialPortManager.getInstance().read(buffer,
				3000, 30);
		String result = null;
		if (disSelectlength > 0) {
			byte[] data = new byte[disSelectlength];
			System.arraycopy(buffer, 0, data, 0, disSelectlength);
			result = new String(data);
			Log.i("whw", "disSelect str=" + result);
		}

		clearMessage();
		if (result == null) {
			msg.what = TIME_OUT;
		} else {
			if (result.length() == 4) {
				msg.what = DESELECT_SUCCESS;
			} else {
				msg.what = NO_RESPONSE;
			}
		}
		return msg;
	}

	/**
	 * 写用户标识符
	 *
	 * @param identify
	 * @return
	 */
	public Message writeIdentify(char cid, byte[] identify, String address) {
		// return write(PAGE1, "00", identifyCommand);
		Log.i("whw", "writeIdentify----------" + address);
		return write(cid, PAGE1, address, DataUtils.toHexString(identify));
	}

	/**
	 * 读用户标识符
	 *
	 * @return
	 */
	public Message readIdentify(char cid, String address) {
		Log.i("whw", "readIdentify----------" + address);
		return read(cid, PAGE1, address);
	}

	/**
	 * 写密钥
	 *
	 * @param key
	 *            为8个字节的密钥
	 * @return
	 */
	public Message writeKey(char cid, byte[] key) {
		// return write(PAGE2, "00", "0807060504030201");
		Log.i("whw", "writeKey----------");
		return write(cid, PAGE2, "00", DataUtils.toHexString(key));

	}

	/**
	 * 读密钥
	 */
	public Message readKey(char cid) {
		Log.i("whw", "readKey----------");
		return read(cid, PAGE2, "00");
	}

	public Message writePermission(char cid, byte[] applicationData, byte afi,
								   byte[] permission) {
		StringBuffer commandStr = new StringBuffer();
		commandStr.append(DataUtils.toHexString(applicationData));
		commandStr.append(DataUtils.toHexString1(afi));
		commandStr.append(DataUtils.toHexString(permission));
		Log.i("whw", "writePermission=" + commandStr);
		return write(cid, PAGE0, "00", commandStr.toString());
	}

	public Message readPermission(char cid) {
		return read(cid, PAGE0, "00");

	}

	public Message readData(char cid) {
		return read(cid, PAGE3, "00");
	}

	public Message writeData(char cid, byte[] data) {
		return write(cid, PAGE3, "00", DataUtils.toHexString(data));
	}

	public void release(char cid) {
		request((byte) 0x00);
		active(DataUtils.hexStringTobyte(PUPI), cid);

		writeIdentify(cid, new byte[] { (byte) 0xaa, 0x22, 0x33, 0x44, 0x55,
				0x66, 0x77, (byte) 0x88 }, "00");

		readIdentify(cid, "00");

		writeKey(cid, new byte[] { 0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02,
				0x01 });
		readKey(cid);

		writeData(cid, new byte[] { (byte) 0xbb, 0x01, 0x0f, 0x0f, 0x04, 0x05,
				0x06, 0x07 });
		readData(cid);

		// writePermission(new byte[] { 0x01, 0x02, 0x03, 0x04 }, (byte) 0x21,
		// new byte[] { 0x1B, (byte) 0xE4, 0x1B });
		// readPermission();

		deselect(cid);

	}

	public void comsume(char cid) {
		request((byte) 0x21);
		active(DataUtils.hexStringTobyte(PUPI), cid);
		read(cid, PAGE1, "00");
		int aut = authentication(cid, new byte[] { 0x08, 0x07, 0x06, 0x05,
				0x04, 0x03, 0x02, 0x01 }).what;
		Log.i("whw", "aut=" + aut);
		readData(cid);
		writeData(cid, new byte[] { (byte) 0xbb, 0x01, 0x0f, 0x0f, 0x04, 0x05,
				0x06, 0x07 });
		readData(cid);
		deselect(cid);
	}

}
