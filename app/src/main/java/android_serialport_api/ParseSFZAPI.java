package android_serialport_api;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.ivsign.android.IDCReader.SfzFileManager;
import com.szfp.szfplib.R;
import com.szfp.szfplib.utils.DataUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ParseSFZAPI {
	private String src=Environment.getExternalStorageDirectory().getAbsolutePath()+"/sfzPic"; 
	private static final byte[] command1 = "D&C00040101".getBytes();
	private static final byte[] command2 = "D&C00040102".getBytes();
	private static final byte[] command3 = "D&C00040108".getBytes();
	private static final byte[] SFZ_ID_COMMAND1 = "f050000\r\n".getBytes();
	private static final byte[] SFZ_ID_COMMAND2 = "f1d0000000000080108\r\n"
			.getBytes();
	private static final byte[] SFZ_ID_COMMAND3 = "f0036000008\r\n".getBytes();
	private static final String TURN_ON = "c050601\r\n";// ???????
	private static final String TURN_OFF = "c050602\r\n";// ????????

	private static final String SFZ_ID_RESPONSE1 = "5000000000";
	private static final String SFZ_ID_RESPONSE2 = "08";
	private static final String TURN_OFF_RESPONSE = "RF carrier off!";

	private static final String CARD_SUCCESS = "AAAAAA96690508000090";
	private static final String CARD_SUCCESS2 = "AAAAAA9669090A000090";

	private static final String TIMEOUT_RETURN = "AAAAAA96690005050505";
	private static final String CMD_ERROR = "CMD_ERROR";

	private static final String MODULE_SUCCESS = "AAAAAA96690014000090";

	public static final int DATA_SIZE = 2321;

	private byte[] buffer = new byte[DATA_SIZE];

	private String path;

	public static final int SECOND_GENERATION_CARD = 1295;

	public static final int THIRD_GENERATION_CARD = 2321;

	private Context m_Context;

	public ParseSFZAPI(Looper looper, String rootPath, Context context) {
		this.path = rootPath + File.separator + "wltlib";
		this.m_Context = context;
	}

	private Result result;

	private final Logger logger = LoggerFactory.getLogger();

	/**
	 * ????????????????????????????????????????
	 * 
	 * @return true?????????????????false???????????????????????????????????????
	 */
	public Result read(int cardType) {
		People people = null;
		if (cardType == SECOND_GENERATION_CARD) {
			SerialPortManager.getInstance().write(command1);
		} else if (cardType == THIRD_GENERATION_CARD) {
			SerialPortManager.getInstance().write(command3);
		} else {
			return null;
		}
		result = new Result();
		SerialPortManager.switchRFID = false;
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		if (length == 0) {
			result.confirmationCode = Result.TIME_OUT;
			return result;
		}

		if (length == 1297 && cardType == THIRD_GENERATION_CARD) {
			result.confirmationCode = Result.NO_THREECARD;
			return result;
		}

		people = decode(buffer, length);
		if (people == null) {
			result.confirmationCode = Result.FIND_FAIL;
		} else {
			result.confirmationCode = Result.SUCCESS;
			result.resultInfo = people;
		}
		return result;
	}

	public Result readModule() {
		result = new Result();
		SerialPortManager.switchRFID = false;
		SerialPortManager.getInstance().write(command2);
		byte[] buffer = new byte[DATA_SIZE];
		int length = SerialPortManager.getInstance().read(buffer, 3000, 300);
		if (length == 0) {
			result.confirmationCode = Result.TIME_OUT;
			return result;
		}
		byte[] module = new byte[length];
		System.arraycopy(buffer, 0, module, 0, length);
		String data = DataUtils.toHexString(module);
		if (length > 10) {
			String prefix = data.substring(0, 20);
			if (prefix.equalsIgnoreCase(MODULE_SUCCESS)) {
				String temp1 = DataUtils.toHexString1(module[10]);
				String temp2 = DataUtils.toHexString1(module[12]);
				byte[] temp3 = new byte[4];
				System.arraycopy(module, 14, temp3, 0, temp3.length);
				reversal(temp3);
				byte[] temp4 = new byte[4];
				System.arraycopy(module, 18, temp4, 0, temp4.length);
				reversal(temp4);
				byte[] temp5 = new byte[4];
				System.arraycopy(module, 22, temp5, 0, temp5.length);
				reversal(temp5);
				StringBuffer sb = new StringBuffer();
				sb.append(temp1);
				sb.append(".");
				sb.append(temp2);
				sb.append("-");
				sb.append(byte2Int(temp3));
				sb.append("-");
				String str4 = Long.toString(byte2Int(temp4));
				for (int i = 0; i < 10 - str4.length(); i++) {
					sb.append("0");
				}
				sb.append(str4);
				sb.append("-");
				String str5 = Long.toString(byte2Int(temp5));
				for (int i = 0; i < 10 - str5.length(); i++) {
					sb.append("0");
				}
				sb.append(str5);
				result.confirmationCode = Result.SUCCESS;
				result.resultInfo = sb.toString();
				return result;
			}
		}
		result.confirmationCode = Result.FIND_FAIL;
		return result;
	}

	public String readCardID() {
		if (!SerialPortManager.switchRFID) {
			SerialPortManager.getInstance().switchStatus();
		}
		turnOff();
		Log.i("whw", "readCardID");
		if (sendReceive(SFZ_ID_COMMAND1, SFZ_ID_RESPONSE1)) {
			if (sendReceive(SFZ_ID_COMMAND2, SFZ_ID_RESPONSE2)) {
				return sendReceive(SFZ_ID_COMMAND3);
			}
		}
		return "";
	}

	private boolean sendReceive(byte[] command, String response) {
		SerialPortManager.getInstance().write(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 10);
		if (length > 0) {
			String dataStr = new String(buffer, 0, length).trim();
			Log.i("whw", "dataStr=" + dataStr);
			if (dataStr.startsWith(response)) {
				return true;
			}
		}
		return false;
	}

	// ????????
	public boolean turnOff() {
		byte[] command = TURN_OFF.getBytes();
		SerialPortManager.getInstance().write(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 10);
		String str = "";
		if (length > 0) {
			str = new String(buffer, 0, length).trim();
			if (str.equals(TURN_OFF_RESPONSE)) {
				return true;
			}
		}
		return false;
	}

	public boolean turnOn() {
		byte[] command = TURN_ON.getBytes();
		SerialPortManager.getInstance().write(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 10);
		String str = "";
		if (length > 0) {
			str = new String(buffer, 0, length).trim();
			if (str.equals(TURN_OFF_RESPONSE)) {
				return true;
			}
		}
		return false;
	}

	private String sendReceive(byte[] command) {
		SerialPortManager.getInstance().write(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 10);
		if (length > 0) {
			String dataStr = new String(buffer, 0, length).trim();
			Log.i("whw", "dataStr=" + dataStr);
			if (dataStr.endsWith("9000"))
				return dataStr.substring(0, 16);
		}
		return "";
	}

	private void reversal(byte[] data) {
		int length = data.length;
		for (int i = 0; i < length / 2; i++) {
			byte temp = data[i];
			data[i] = data[length - 1 - i];
			data[length - 1 - i] = temp;
		}
	}

	private long byte2Int(byte[] data) {
		int intValue = 0;
		for (int i = 0; i < data.length; i++) {
			intValue += (data[i] & 0xff) << (8 * (3 - i));
		}
		long temp = intValue;
		temp <<= 32;
		temp >>>= 32;
		return temp;
	}

	private People decode(byte[] buffer, int length) {
		if (buffer == null) {
			return null;
		}
		byte[] b = new byte[10];
		System.arraycopy(buffer, 0, b, 0, 10);
		String result = toHexString(b);
		Log.i("whw", "result sfz=" + result);
		People people = null;
		if (result.equalsIgnoreCase(CARD_SUCCESS)
				|| result.equalsIgnoreCase(CARD_SUCCESS2)) {
			byte[] data = new byte[buffer.length - 10];
			System.arraycopy(buffer, 10, data, 0, buffer.length - 10);
			people = decodeInfo(data, length);
		} else if (result.equalsIgnoreCase(TIMEOUT_RETURN)) {
			logger.debug(TIMEOUT_RETURN);
		} else if (result.startsWith(CMD_ERROR)) {
			logger.debug(CMD_ERROR);
		}
		return people;

	}

	private People decodeInfo(byte[] buffer, int length) {
		short textSize = getShort(buffer[0], buffer[1]);
		short imageSize = getShort(buffer[2], buffer[3]);
		short modelSize = 0;
		byte[] model = null;
		short skipLength = 0;
		if (length == THIRD_GENERATION_CARD) {
			modelSize = getShort(buffer[4], buffer[5]);
			skipLength = 2;
			model = new byte[modelSize];
			System.arraycopy(buffer, 4 + skipLength + textSize + imageSize,
					model, 0, modelSize);
		}
		byte[] text = new byte[textSize];
		System.arraycopy(buffer, 4 + skipLength, text, 0, textSize);
		byte[] image = new byte[imageSize];
		System.arraycopy(buffer, 4 + skipLength + textSize, image, 0, imageSize);

		People people = null;
		try {
			String temp = null;
			people = new People();
			people.setHeadImage(image);
			// ????
			temp = new String(text, 0, 30, "UTF-16LE").trim();
			people.setPeopleName(temp);

			// ???
			temp = new String(text, 30, 2, "UTF-16LE");
			if (temp.equals("1"))
				temp = "??";
			else
				temp = "?";
			people.setPeopleSex(temp);

			// ????
			temp = new String(text, 32, 4, "UTF-16LE");
			try {
				int code = Integer.parseInt(temp.toString());
				temp = decodeNation(code);
			} catch (Exception e) {
				temp = "";
			}
			people.setPeopleNation(temp);

			// ????
			temp = new String(text, 36, 16, "UTF-16LE").trim();
			people.setPeopleBirthday(temp);

			// ??
			temp = new String(text, 52, 70, "UTF-16LE").trim();
			people.setPeopleAddress(temp);

			// ??????
			temp = new String(text, 122, 36, "UTF-16LE").trim();
			people.setPeopleIDCode(temp);

			// ???????
			temp = new String(text, 158, 30, "UTF-16LE").trim();
			people.setDepartment(temp);

			// ???????????
			temp = new String(text, 188, 16, "UTF-16LE").trim();
			people.setStartDate(temp);

			// ???????????
			temp = new String(text, 204, 16, "UTF-16LE").trim();
			people.setEndDate(temp);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}

		people.setPhoto(parsePhoto(image));
		people.setModel(model);
		return people;
	}

	private String decodeNation(int code) {
		String nation;
		switch (code) {
		case 1:
			nation = "??";
			break;
		case 2:
			nation = "???";
			break;
		case 3:
			nation = "??";
			break;
		case 4:
			nation = "??";
			break;
		case 5:
			nation = "????";
			break;
		case 6:
			nation = "??";
			break;
		case 7:
			nation = "??";
			break;
		case 8:
			nation = "?";
			break;
		case 9:
			nation = "????";
			break;
		case 10:
			nation = "????";
			break;
		case 11:
			nation = "??";
			break;
		case 12:
			nation = "??";
			break;
		case 13:
			nation = "??";
			break;
		case 14:
			nation = "??";
			break;
		case 15:
			nation = "????";
			break;
		case 16:
			nation = "????";
			break;
		case 17:
			nation = "??????";
			break;
		case 18:
			nation = "??";
			break;
		case 19:
			nation = "??";
			break;
		case 20:
			nation = "????";
			break;
		case 21:
			nation = "??";
			break;
		case 22:
			nation = "?";
			break;
		case 23:
			nation = "???";
			break;
		case 24:
			nation = "????";
			break;
		case 25:
			nation = "?";
			break;
		case 26:
			nation = "????";
			break;
		case 27:
			nation = "????";
			break;
		case 28:
			nation = "????";
			break;
		case 29:
			nation = "???????";
			break;
		case 30:
			nation = "??";
			break;
		case 31:
			nation = "?????";
			break;
		case 32:
			nation = "????";
			break;
		case 33:
			nation = "?";
			break;
		case 34:
			nation = "????";
			break;
		case 35:
			nation = "????";
			break;
		case 36:
			nation = "???";
			break;
		case 37:
			nation = "????";
			break;
		case 38:
			nation = "????";
			break;
		case 39:
			nation = "????";
			break;
		case 40:
			nation = "????";
			break;
		case 41:
			nation = "??????";
			break;
		case 42:
			nation = "?";
			break;
		case 43:
			nation = "???????";
			break;
		case 44:
			nation = "?????";
			break;
		case 45:
			nation = "?????";
			break;
		case 46:
			nation = "????";
			break;
		case 47:
			nation = "????";
			break;
		case 48:
			nation = "???";
			break;
		case 49:
			nation = "??";
			break;
		case 50:
			nation = "??????";
			break;
		case 51:
			nation = "????";
			break;
		case 52:
			nation = "?????";
			break;
		case 53:
			nation = "????";
			break;
		case 54:
			nation = "???";
			break;
		case 55:
			nation = "???";
			break;
		case 56:
			nation = "???";
			break;
		case 97:
			nation = "????";
			break;
		case 98:
			nation = "??????????????";
			break;
		default:
			nation = "";
		}

		return nation;
	}

	/**
	 * ???????16?????????
	 * 
	 * @param b
	 * @return
	 */
	public static String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			buffer.append(toHexString1(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString1(byte b) {
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
	}

	private short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}

	private byte[] parsePhoto(byte[] wltdata) {
		SfzFileManager sfzFileManager = new SfzFileManager(src);
		if (sfzFileManager.initDB(this.m_Context, R.raw.base, R.raw.license)) {
			int ret = IDCReaderSDK.Init();
			if (0 == ret) {
				ret = IDCReaderSDK.unpack(buffer);
				if (1 == ret) {
					byte[] image = IDCReaderSDK.getPhoto();
					return image;
				}
			}
		}
		return null;
	}

	private boolean isExistsParsePath(String wltPath, byte[] wltdata) {
		File myFile = new File(path);
		boolean isMKDir = true;
		if (!myFile.exists()) {
			isMKDir = myFile.mkdir();
		}
		if (!isMKDir) {
			return false;
		}

		File wltFile = new File(wltPath);
		boolean isCreate = true;
		if (!wltFile.exists()) {
			try {
				isCreate = wltFile.createNewFile();
			} catch (IOException e) {
				isCreate = false;
				e.printStackTrace();
			}
		}
		if (!isCreate) {
			return false;
		}

		boolean isWriteData = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(wltFile);
			fos.write(wltdata);
			fos.flush();
			isWriteData = true;
		} catch (FileNotFoundException e) {
			isWriteData = false;
			e.printStackTrace();
		} catch (IOException e) {
			isWriteData = false;
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return isWriteData;
	}

	/**
	 * ???????????byte????
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public static class People {
		/**
		 * ????
		 */
		private String peopleName;

		/**
		 * ???
		 */
		private String peopleSex;

		/**
		 * ????
		 */
		private String peopleNation;

		/**
		 * ????????
		 */
		private String peopleBirthday;

		/**
		 * ??
		 */
		private String peopleAddress;

		/**
		 * ??????
		 */
		private String peopleIDCode;

		/**
		 * ???????
		 */
		private String department;

		/**
		 * ????????????
		 */
		private String startDate;

		/**
		 * ?????????????
		 */
		private String endDate;

		/**
		 * ???????
		 */
		private byte[] photo;

		/**
		 * ????????????????????????1024???
		 */
		private byte[] headImage;

		/**
		 * ??????????????????????1024??????null??????????????????????????
		 */
		private byte[] model;

		public String getPeopleName() {
			return peopleName;
		}

		public void setPeopleName(String peopleName) {
			this.peopleName = peopleName;
		}

		public String getPeopleSex() {
			return peopleSex;
		}

		public void setPeopleSex(String peopleSex) {
			this.peopleSex = peopleSex;
		}

		public String getPeopleNation() {
			return peopleNation;
		}

		public void setPeopleNation(String peopleNation) {
			this.peopleNation = peopleNation;
		}

		public String getPeopleBirthday() {
			return peopleBirthday;
		}

		public void setPeopleBirthday(String peopleBirthday) {
			this.peopleBirthday = peopleBirthday;
		}

		public String getPeopleAddress() {
			return peopleAddress;
		}

		public void setPeopleAddress(String peopleAddress) {
			this.peopleAddress = peopleAddress;
		}

		public String getPeopleIDCode() {
			return peopleIDCode;
		}

		public void setPeopleIDCode(String peopleIDCode) {
			this.peopleIDCode = peopleIDCode;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public byte[] getPhoto() {
			return photo;
		}

		public void setPhoto(byte[] photo) {
			this.photo = photo;
		}

		public byte[] getHeadImage() {
			return headImage;
		}

		public void setHeadImage(byte[] headImage) {
			this.headImage = headImage;
		}

		public byte[] getModel() {
			return model;
		}

		public void setModel(byte[] model) {
			this.model = model;
		}
	}

	public static class Result {
		public static final int SUCCESS = 1;
		public static final int FIND_FAIL = 2;
		public static final int TIME_OUT = 3;
		public static final int OTHER_EXCEPTION = 4;
		public static final int NO_THREECARD = 5;

		/**
		 * ????? 1: ??? 2????? 3: ??? 4????????5:?????????
		 */
		public int confirmationCode;

		/**
		 * ?????:????????1?????????????????
		 */
		public Object resultInfo;
	}
}