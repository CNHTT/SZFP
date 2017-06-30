package android_serialport_api;


import android.util.Log;

import com.szfp.szfplib.utils.DataUtils;

public class M1CardAPI {
	public static final int KEY_A = 1;
	public static final int KEY_B = 2;

	/**
	 * 为了兼容No response!\r\n 和 No responese!\r\n ，只判断前面部分"No respon"
	 */
	private static final String NO_RESPONSE = "No respon";

	// 发送数据包的前缀
	private static final String DATA_PREFIX = "c050605";
	private static final String FIND_CARD_ORDER = "01";// 寻卡指令
	private static final String PASSWORD_SEND_ORDER = "02";// 密码下发指令
	private static final String PASSWORD_VALIDATE_ORDER = "03";// 密码认证命令
	private static final String READ_DATA_ORDER = "04";// 读指令
	private static final String WRITE_DATA_ORDER = "05";// 写指令
	private static final String ENTER = "\r\n";// 换行符
	private static final String TURN_OFF = "c050602\r\n";// 关闭天线厂

	// 寻卡的指令包
	private static final String FIND_CARD = DATA_PREFIX + FIND_CARD_ORDER
			+ ENTER;

	// 下发密码指令包(A，B段密码各12个’f‘)
	private static final String SEND_PASSWORD = DATA_PREFIX
			+ PASSWORD_SEND_ORDER + "ffffffffffffffffffffffff" + ENTER;
	private static final String DEFAULT_PASSWORD = "ffffffffffff";
	private static final String FIND_SUCCESS = "0x00,";
	private static final String WRITE_SUCCESS = " Write Success!" + ENTER;
	public byte[] buffer = new byte[100];

	private int receive(byte[] command, byte[] buffer) {
		int length = -1;
		if (!SerialPortManager.switchRFID) {
			SerialPortManager.getInstance().switchStatus();
		}
		sendCommand(command);
		length = SerialPortManager.getInstance().read(buffer, 300, 5);
		return length;
	}

	private void sendCommand(byte[] command) {
		SerialPortManager.getInstance().write(command);
	}

	/**
	 * 函数说明：获取密码类型对应的字节数据，默认密码类型为KEYA
	 *
	 * @param keyType
	 * @return
	 */
	private String getKeyTypeStr(int keyType) {
		String keyTypeStr = null;
		switch (keyType) {
			case KEY_A:
				keyTypeStr = "60";
				break;
			case KEY_B:
				keyTypeStr = "61";
				break;
			default:
				keyTypeStr = "60";
				break;
		}
		return keyTypeStr;
	}

	/**
	 * 函数说明：转换扇区里块的地址为两位
	 *
	 * @param block
	 *            块号
	 * @return
	 */
	private String getZoneId(int block) {
		return DataUtils.byte2Hexstr((byte) block);
	}

	/**
	 * 函数说明：读取M1卡卡号 Read the M1 card number
	 *
	 * @return Result
	 */
	public Result readCardNum() {
		Log.i("whw", "!!!!!!!!!!!!readCard");
		Result result = new Result();
		byte[] command = FIND_CARD.getBytes();
		int length = receive(command, buffer);
		if (length == 0) {
			result.confirmationCode = Result.TIME_OUT;
			return result;
		}
		String msg = "";
		msg = new String(buffer, 0, length);
		Log.i("whw", "msg hex=" + msg);
		turnOff();
		if (msg.startsWith(FIND_SUCCESS)) {
			result.confirmationCode = Result.SUCCESS;
			result.num = msg.substring(FIND_SUCCESS.length());
		} else {
			result.confirmationCode = Result.FIND_FAIL;
		}
		return result;
	}

	/**
	 * 函数说明：验证密码
	 *
	 * @param block
	 * @param keyType
	 * @param keyA
	 * @param keyB
	 * @return
	 */
	public boolean validatePassword(int block, int keyType, String keyA,
									String keyB) {
		byte[] cmd = (DATA_PREFIX + PASSWORD_SEND_ORDER + keyA + keyB + ENTER)
				.getBytes();// 下发密码指令
		int tempLength = receive(cmd, buffer);// 下发验证指令
		String verifyStr = new String(buffer, 0, tempLength);
		Log.i("whw", "validatePassword verifyStr=" + verifyStr);
		byte[] command2 = (DATA_PREFIX + PASSWORD_VALIDATE_ORDER
				+ getKeyTypeStr(keyType) + getZoneId(block) + ENTER).getBytes();
		int length = receive(command2, buffer);// 验证密码
		String msg = new String(buffer, 0, length);
		Log.i("whw", "validatePassword msg=" + msg);
		String prefix = "0x00,\r\n";
		if (msg.startsWith(prefix)) {
			return true;
		}
		return false;
	}

	/**
	 * 读取指定块号存储的数据，长度一般为16字节 Reads the specified number stored data, length of
	 * 16 bytes
	 *
	 * @param position
	 *            block number
	 * @return
	 */
	public byte[][] read(int startPosition, int num) {
		byte[] command = { 'c', '0', '5', '0', '6', '0', '5', '0', '4', '0',
				'0', '\r', '\n' };
		byte[][] pieceDatas = new byte[num][];
		for (int i = 0; i < num; i++) {
			char[] c = getZoneId(startPosition + i).toCharArray();
			command[9] = (byte) c[0];
			command[10] = (byte) c[1];
			int readTime = 0;
			int length = 0;
			String data = "";
			while (readTime < 3) {
				readTime++;
				length = receive(command, buffer);
				data = new String(buffer, 0, length);
				if (data != null && data.startsWith(NO_RESPONSE)) {
					continue;
				} else {
					break;
				}
			}
			Log.i("whw", "read data=" + data + "   readTime=" + readTime);
			String[] split = data.split(";");
			String msg = "";
			if (split.length == 2) {
				int index = split[1].indexOf("\r\n");
				if (index != -1) {
					msg = split[1].substring(0, index);
				}

				Log.i("whw",
						"split msg=" + msg + "  msg length=" + msg.length());
			}
			pieceDatas[i] = DataUtils.hexStringTobyte(msg);
		}

		return pieceDatas;
	}

	/**
	 * 函数说明: 读取指定块号存储的数据，长度一般为16字节 Reads the specified number stored data,
	 * length of 16 bytes
	 *
	 * @param position
	 *            块号(S50 类型默认0-63)
	 *
	 * @return 返回指定块号数据
	 */
	public byte[] read(int startPosition) {
		byte[] command = { 'c', '0', '5', '0', '6', '0', '5', '0', '4', '0',
				'0', '\r', '\n' };
		byte[] pieceDatas = null;
		char[] c = getZoneId(startPosition).toCharArray();
		command[9] = (byte) c[0];
		command[10] = (byte) c[1];
		int readTime = 0;
		int length = 0;
		String data = "";
		while (readTime < 3) {
			readTime++;
			length = receive(command, buffer);
			data = new String(buffer, 0, length);
			if (data != null && data.startsWith(NO_RESPONSE)) {
				continue;
			} else {
				break;
			}
		}
		Log.i("xuws", "read data=" + data + "   readTime=" + readTime);
		String[] split = data.split(";");
		String msg = "";
		if (split.length == 2) {
			int index = split[1].indexOf("\r\n");
			if (index != -1) {
				msg = split[1].substring(0, index);
			}
			Log.i("xuws", "split msg=" + msg + "  msg length=" + msg.length());
		}
		pieceDatas = DataUtils.hexStringTobyte(msg);
		return pieceDatas;
	}

	/**
	 * 向指定的块号写入数据，长度为16字节 Write data to the specified block, length is 16 bytes
	 * argument should be data[i].length == num
	 *
	 * @param data
	 * @param startPosition
	 * @param num
	 *            the number of block
	 * @return
	 */
	public boolean write(int block, int num, String data) {
		if (data.length() == 0) {
			return false;
		}
		for (int i = 0; i < num; i++) {
			byte[] command = (DATA_PREFIX + WRITE_DATA_ORDER + getZoneId(block)
					+ data + ENTER).getBytes();
			Log.i("whw", "***write hexStr=" + DataUtils.toHexString(command));
			int length = receive(command, buffer);
			boolean isWrite = false;
			if (length > 0) {
				String writeResult = new String(buffer, 0, length);
				Log.i("whw", "write result=" + writeResult);
				isWrite = M1CardAPI.WRITE_SUCCESS.equals(writeResult);
			}
			if (!isWrite) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 函数说明：修改密码
	 *
	 * @param block
	 * @param num
	 * @param data
	 * @param keyType
	 * @return
	 */
	public boolean updatePwd(int block, int num, String data, int keyType) {
		if (data.length() == 0) {
			return false;
		}
		for (int i = 0; i < num; i++) {
			byte[] command = (DATA_PREFIX + WRITE_DATA_ORDER + getZoneId(block)
					+ makeCompletePassword(keyType, data) + ENTER).getBytes();
			Log.i("whw", "***write hexStr=" + DataUtils.toHexString(command));
			int length = receive(command, buffer);
			boolean isWrite = false;
			if (length > 0) {
				String writeResult = new String(buffer, 0, length);
				Log.i("whw", "write result=" + writeResult);
				isWrite = M1CardAPI.WRITE_SUCCESS.equals(writeResult);
			}
			if (!isWrite) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 函数说明：组装密码块
	 *
	 * @param keyType
	 * @param passwordHexStr
	 * @return
	 */
	private String makeCompletePassword(int keyType, String passwordHexStr) {
		String completePasswordHexStr = "";
		switch (keyType) {
			case KEY_A:
				completePasswordHexStr = passwordHexStr + "ff078069"
						+ DEFAULT_PASSWORD;
				break;
			case KEY_B:
				completePasswordHexStr = DEFAULT_PASSWORD + "ff078069"
						+ completePasswordHexStr;
				break;
			default:
				break;
		}
		Log.i("whw", "completePasswordHexStr == " + completePasswordHexStr);
		return completePasswordHexStr;
	}

	/**
	 * 向指定的块号写入数据，长度为16字节 Write data to the specified block, length is 16 bytes
	 *
	 * @param data
	 * @param position
	 * @return
	 */
	public boolean write(byte[] data, int position) {
		String hexStr = DataUtils.toHexString(data);
		byte[] command = (DATA_PREFIX + WRITE_DATA_ORDER + getZoneId(position)
				+ hexStr + ENTER).getBytes();
		Log.i("whw", "***write hexStr=" + hexStr);
		int length = receive(command, buffer);
		if (length > 0) {
			String writeResult = new String(buffer, 0, length);
			Log.i("whw", "write result=" + writeResult);
			return M1CardAPI.WRITE_SUCCESS.equals(writeResult);
		}
		return false;
	}

	/**
	 * 函数说明：关闭天线厂
	 *
	 * @return
	 */
	public String turnOff() {
		// byte[] command = TURN_OFF.getBytes();
		// int length = receive(command, buffer);
		// String str = "";
		// if (length > 0) {
		// str = new String(buffer, 0, length);
		// }
		// return str;
		return "";
	}

	public static class Result {
		/**
		 * 成功 successful
		 */
		public static final int SUCCESS = 1;
		/**
		 * 寻卡失败 Find card failure
		 */
		public static final int FIND_FAIL = 2;
		/**
		 * 验证失败 Validation fails
		 */
		public static final int VALIDATE_FAIL = 3;
		/**
		 * 读卡失败 Read card failure
		 */
		public static final int READ_FAIL = 4;
		/**
		 * 写卡失败 Write card failure
		 */
		public static final int WRITE_FAIL = 5;
		/**
		 * 超时 timeout
		 */
		public static final int TIME_OUT = 6;
		/**
		 * 其它异常 other exception
		 */
		public static final int OTHER_EXCEPTION = 7;

		/**
		 * 确认码 1: 成功 2：寻卡失败 3：验证失败 4:写卡失败 5：超时 6：其它异常
		 */
		public int confirmationCode;

		/**
		 * 结果集:当确认码为1时，再判断是否有结果 Results: when the code is 1, then determine
		 * whether to have the result
		 */
		public Object resultInfo;

		/**
		 * 卡号 The card number
		 */
		public String num;
	}

}
