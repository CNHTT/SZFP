package android_serialport_api;


import android.os.SystemClock;
import android.util.Log;

import com.szfp.szfplib.utils.DataUtils;

public class BarCodeAPI {
	private static final byte[] OPEN_COMMAND = "D&C0004010B".getBytes();
	private static final byte[] CLOSE_COMMAND = "D&C0004010C".getBytes();

	private static final byte[] WAKEUP = { 0x00 };

	private static final byte[] START_DECODE = { 0x04, (byte) 0xe4, 0x04, 0x01,
			(byte) 0xff, 0x13 };

	private static final byte[] STOP_DECODE = { 0x04, (byte) 0xe5, 0x04, 0x01,
			(byte) 0xff, 0x12 };

	// private static final byte[] DECODE_DATA_PACKET_FORMAT = { 0x07,
	// (byte) 0xc6, 0x04, 0x01, 0x0d, (byte) 0xee, 0x01, (byte) 0xfe, 0x32 };
	//
	// private static final byte[] MALTIPACKET_OPTION_CONTINUOUSLY = { 0x08,
	// (byte) 0xc6, 0x04, 0x01, 0x0d, (byte) 0xF0, 0x4E, 0x01,
	// (byte) 0xfd, (byte) 0xe1 };
	//
	// private static final byte[] LOW_POWER_TIMEOUT = { 0x07, (byte) 0xC6,
	// 0x04,
	// 0x08, (byte) 0xFF, (byte) 0x92, 0x15, (byte) 0xFD, (byte) 0x81 };

	private static final byte[] DECODE_DATA_PACKET_FORMAT = { 0x07,
			(byte) 0xc6, 0x04, 0x08, 0x0d, (byte) 0xee, 0x01, (byte) 0xfe, 0x2b };

	private static final byte[] MALTIPACKET_OPTION_CONTINUOUSLY = { 0x08,
			(byte) 0xc6, 0x04, 0x08, 0x0d, (byte) 0xF0, 0x4E, 0x01,
			(byte) 0xfd, (byte) 0xda };

	private static final byte[] LOW_POWER_TIMEOUT = { 0x07, (byte) 0xC6, 0x04,
			0x08, (byte) 0xFF, (byte) 0x92, 0x15, (byte) 0xFD, (byte) 0x81 };

	private static byte[] PARAM_COMMON = { (byte) 0xc6, 0x04, 0x08, (byte) 0xff };

	private static final byte[] CMD_ACK = { 0x04, (byte) 0xd0, 0x04, 0x00,
			(byte) 0xff, 0x28 };

	private static final byte[] RESPONSE_ACK = { 0x04, (byte) 0xd0, 0x00, 0x00,
			(byte) 0xff, 0x2c };

	private static final byte[] CMD_NAK = { 0x05, (byte) 0xd1, 0x04, 0x00,
			0x00, 0x06, (byte) 0xff, 0x20 };

	// public static final byte[] SET_HOST_COMMAND = { 0x07, (byte) 0xc6, 0x04,
	// 0x01, 0x0d, (byte) 0x8a, 0x08, (byte) 0xfe, (byte) 0x8f };
	//
	// public static final byte[] RESTORE_COMMAND = { 0x04, (byte) 0xC8, 0x04,
	// 0x01, (byte) 0xFF, 0x2F };
	//
	// public static final byte[] DISABLE_ALL_SYMBOLOGIES_COMMAND = { 0x05,
	// (byte) 0xC9, 0x04, 0x00, 0x00, (byte) 0xFF, 0x2E };

	public static final byte[] SET_HOST_COMMAND = { 0x07, (byte) 0xc6, 0x04,
			0x01, 0x0d, (byte) 0x8a, 0x08, (byte) 0xfe, (byte) 0x8f };

	public static final byte[] RESTORE_COMMAND = { 0x04, (byte) 0xC8, 0x04,
			0x01, (byte) 0xFF, 0x2F };

	public static final byte[] DISABLE_ALL_SYMBOLOGIES_COMMAND = { 0x05,
			(byte) 0xC9, 0x04, 0x08, 0x00, (byte) 0xFF, 0x26 };

	/******************************* 1D Symbologies ******************************************/

	/**
	 * To enable or disable UPC-A.
	 */
	public static final byte[] UPC_A_ENABLE = { 0x01, 0x01 };
	public static final byte[] UPC_A_DISABLE = { 0x01, 0x00 };

	/**
	 * To enable or disable UPC-E.
	 */
	public static final byte[] UPC_E_ENABLE = { 0x02, 0x01 };
	public static final byte[] UPC_E_DISABLE = { 0x02, 0x00 };

	/**
	 * To enable or disable UPC-E1,UPC-E1 is disabled by default.
	 */
	public static final byte[] UPC_E1_ENABLE = { 0x0C, 0x01 };
	public static final byte[] UPC_E1_DISABLE = { 0x0C, 0x00 };

	/**
	 * To enable or disable EAN-8/JAN-8.
	 */
	public static final byte[] EAN8_JAN8_ENABLE = { 0x04, 0x01 };
	public static final byte[] EAN8_JAN8_DISABLE = { 0x04, 0x00 };

	/**
	 * To enable or disable EAN-13/JAN-13.
	 */
	public static final byte[] EAN13_JAN13_ENABLE = { 0x03, 0x01 };
	public static final byte[] EAN13_JAN13_DISABLE = { 0x03, 0x00 };

	/**
	 * To enable or disable Bookland EAN.
	 */
	public static final byte[] BOOKLAND_EAN_ENABLE = { 0x53, 0x01 };
	public static final byte[] BOOKLAND_EAN_DISABLE = { 0x53, 0x00 };

	/**
	 * Bookland ISBN Format,If Bookland EAN is enabled. Bookland ISBN-10 - The
	 * decoder reports Bookland data starting with 978 in traditional 10-digit
	 * format with the special Bookland check digit for backward-compatibility.
	 * Data starting with 979 is not considered Bookland in this mode.
	 */
	public static final byte[] BOOKLAND_ISBN_10 = { (byte) 0xF1, 0x40, 0x00 };

	/**
	 * Bookland ISBN Format,If Bookland EAN is enabled. Bookland ISBN-13 - The
	 * decoder reports Bookland data (starting with either 978 or 979) as EAN-13
	 * in 13-digit format to meet the 2007 ISBN-13 protocol.
	 */
	public static final byte[] BOOKLAND_ISBN_13 = { (byte) 0xF1, 0x40, 0x01 };

	/**
	 * Enable this parameter to decode UPC-A bar codes starting with digit ‘5’,
	 * EAN-13 bar codes starting with digit ‘99’, and UPC-A/GS1-128 Coupon
	 * Codes. UPCA, EAN-13, and GS1-128 must be enabled to scan all types of
	 * Coupon Codes.
	 */
	public static final byte[] UCC_COUPON_EXTENDED_CODE_ENABLE = { 0x55, 0x01 };
	public static final byte[] UCC_COUPON_EXTENDED_CODE_DISABLE = { 0x55, 0x00 };

	/**
	 * To enable or disable ISSN EAN.
	 */
	public static final byte[] ISSN_EAN_ENABLE = { (byte) 0xF1, 0x69, 0x01 };
	public static final byte[] ISSN_EAN_DISABLE = { (byte) 0xF1, 0x69, 0x00 };

	/**
	 * To enable or disable Code 128.
	 */
	public static final byte[] CODE_128_ENABLE = { 0x08, 0x01 };
	public static final byte[] CODE_128_DISABLE = { 0x08, 0x00 };

	/**
	 * Enable/Disable GS1-128 (formerly UCC/EAN-128)
	 */
	public static final byte[] GS1_128_ENABLE = { 0x0E, 0x01 };
	public static final byte[] GS1_128_DISABLE = { 0x0E, 0x00 };

	/**
	 * Enable/Disable ISBT 128,ISBT 128 is a variant of Code 128 used in the
	 * blood bank industry. If necessary, the host must perform concatenation of
	 * the ISBT data.
	 */
	public static final byte[] ISBT_128_ENABLE = { 0x54, 0x01 };
	public static final byte[] ISBT_128_DISABLE = { 0x54, 0x00 };

	/**
	 * Enable/Disable Code 39.
	 */
	public static final byte[] CODE_39_ENABLE = { 0x00, 0x01 };
	public static final byte[] CODE_39_DISABLE = { 0x00, 0x00 };

	/**
	 * Enable/Disable Trioptic Code 39 Trioptic Code 39 is a variant of Code 39
	 * used in the marking of computer tape cartridges. Trioptic Code 39 symbols
	 * always contain six characters.
	 */
	public static final byte[] TRIOPTIC_CODE_39_ENABLE = { 0x0D, 0x01 };
	public static final byte[] TRIOPTIC_CODE_39_DISABLE = { 0x0D, 0x00 };

	/**
	 * Enable/Disable Code 93.
	 */
	public static final byte[] CODE_93_ENABLE = { 0x09, 0x01 };
	public static final byte[] CODE_93_DISABLE = { 0x09, 0x00 };

	/**
	 * To enable or disable Code 11.
	 */
	public static final byte[] CODE_11_ENABLE = { 0x0A, 0x01 };
	public static final byte[] CODE_11_DISABLE = { 0x0A, 0x00 };

	/**
	 * Enable/Disable Interleaved 2 of 5(ITF).
	 */
	public static final byte[] ITF_ENABLE = { 0x06, 0x01 };
	public static final byte[] ITF_DISABLE = { 0x06, 0x00 };

	/**
	 * Enable/Disable Discrete 2 of 5(DTF).
	 */
	public static final byte[] DTF_ENABLE = { 0x05, 0x01 };
	public static final byte[] DTF_DISABLE = { 0x05, 0x00 };

	/**
	 * Enable/Disable Codabar.
	 */
	public static final byte[] CODABAR_ENABLE = { 0x07, 0x01 };
	public static final byte[] CODABAR_DISABLE = { 0x07, 0x00 };

	/**
	 * Enable/Disable MSI.
	 */
	public static final byte[] MSI_ENABLE = { 0x0B, 0x01 };
	public static final byte[] MSI_DISABLE = { 0x0B, 0x00 };

	/**
	 * Enable/Disable Chinese 2 of 5.
	 */
	public static final byte[] CTF_ENABLE = { (byte) 0xF0, (byte) 0x98, 0x01 };
	public static final byte[] CTF_DISABLE = { (byte) 0xF0, (byte) 0x98, 0x00 };

	/**
	 * Enable/Disable Matrix 2 of 5.
	 */
	public static final byte[] MTF_ENABLE = { (byte) 0xF1, (byte) 0x6A, 0x01 };
	public static final byte[] MTF_DISABLE = { (byte) 0xF1, (byte) 0x6A, 0x00 };

	/**
	 * Enable/Disable Korean 3 of 5.
	 */
	public static final byte[] KTF_ENABLE = { (byte) 0xF1, (byte) 0x45, 0x01 };
	public static final byte[] KTF_DISABLE = { (byte) 0xF1, (byte) 0x45, 0x00 };

	/**
	 * Regular Only - the decoder decodes regular 1D bar codes only.
	 */
	public static final byte[] INVERSE_1D_REGULAR_ONLY = { (byte) 0xF1,
			(byte) 0x4A, 0x00 };

	/**
	 * Inverse Only - the decoder decodes inverse 1D bar codes only.
	 */
	public static final byte[] INVERSE_1D_INVERSE_ONLY = { (byte) 0xF1,
			(byte) 0x4A, 0x01 };

	/**
	 * Inverse Autodetect - the decoder decodes both regular and inverse 1D bar
	 * codes.
	 */
	public static final byte[] INVERSE_1D_INVERSE_AUTODETECT = { (byte) 0xF1,
			(byte) 0x4A, 0x02 };

	/******************************* 2D Symbologies ******************************************/
	/**
	 * Enable/Disable PDF417
	 */
	public static final byte[] PDF417_ENABLE = { 0x0F, 0x01 };
	public static final byte[] PDF417_DISABLE = { 0x0F, 0x00 };

	/**
	 * Enable/Disable MicroPDF417
	 */
	public static final byte[] MICRO_PDF417_ENABLE = { (byte) 0xE3, 0x01 };
	public static final byte[] MICRO_PDF417_DISABLE = { (byte) 0xE3, 0x00 };

	/**
	 * Enable Code 128 Emulation to transmit these MicroPDF417 symbols with one
	 * of the following prefixes: ]C1 if the first codeword is 903-905 ]C2 if
	 * the first codeword is 908 or 909 ]C0 if the first codeword is 910 or 911
	 */
	public static final byte[] CODE_128_EMULATION_ENABLE = { (byte) 0x7B, 0x01 };

	/**
	 * Disable Code 128 Emulation to transmit these MicroPDF417 symbols with one
	 * of the following prefixes: ]L3 if the first codeword is 903-905 ]L4 if
	 * the first codeword is 908 or 909 ]L5 if the first codeword is 910 or 911
	 */
	public static final byte[] CODE_128_EMULATION_DISABLE = { (byte) 0x7B, 0x00 };

	/**
	 * To enable or disable Data Matrix
	 */
	public static final byte[] DATA_MATRIX_ENABLE = { (byte) 0xF0, 0x24, 0x01 };
	public static final byte[] DATA_MATRIX_DISABLE = { (byte) 0xF0, 0x24, 0x00 };

	/**
	 * Regular Only - the decoder decodes regular Data Matrix bar codes only.
	 */
	public static final byte[] DATA_MATRIX_REGULAR_ONLY = { (byte) 0xF1, 0x4C,
			0x00 };

	/**
	 * Inverse Only - the decoder decodes inverse Data Matrix bar codes only.
	 */
	public static final byte[] DATA_MATRIX_INVERSE_ONLY = { (byte) 0xF1, 0x4C,
			0x01 };

	/**
	 * Inverse Autodetect - the decoder decodes both regular and inverse Data
	 * Matrix bar codes.
	 */
	public static final byte[] DATA_MATRIX_INVERSE_AUTODETECT = { (byte) 0xF1,
			0x4C, 0x02 };

	/**
	 * Decode Mirror Images (Data Matrix Only) Never - do not decode Data Matrix
	 * bar codes that are mirror images
	 */
	public static final byte[] DECODE_MIRROR_IMAGES_NEVER = { (byte) 0xF1,
			0x19, 0x00 };

	/**
	 * Always - decode only Data Matrix bar codes that are mirror images
	 */
	public static final byte[] DECODE_MIRROR_IMAGES_ALWAYS = { (byte) 0xF1,
			0x19, 0x01 };

	/**
	 * Auto - decode both mirrored and unmirrored Data Matrix bar codes.
	 */
	public static final byte[] DECODE_MIRROR_IMAGES_AUTO = { (byte) 0xF1, 0x19,
			0x02 };

	/**
	 * To enable or disable Maxicode
	 */
	public static final byte[] MAXICODE_ENABLE = { (byte) 0xF0, 0x26, 0x01 };
	public static final byte[] MAXICODE_DISABLE = { (byte) 0xF0, 0x26, 0x00 };

	/**
	 * To enable or disable QR Code
	 */
	public static final byte[] QR_CODE_ENABLE = { (byte) 0xF0, 0x25, 0x01 };
	public static final byte[] QR_CODE_DISABLE = { (byte) 0xF0, 0x25, 0x00 };

	/**
	 * Regular Only - the decoder decodes regular QR bar codes only.
	 */
	public static final byte[] QR_INVERSE_REGULAR = { (byte) 0xF1, 0x4B, 0x00 };

	/**
	 * Inverse Only - the decoder decodes inverse QR bar codes only.
	 */
	public static final byte[] QR_INVERSE_ONLY = { (byte) 0xF1, 0x4B, 0x01 };

	/**
	 * Inverse Autodetect - the decoder decodes both regular and inverse QR bar
	 * codes.
	 */
	public static final byte[] QR_INVERSE_AUTODETECT = { (byte) 0xF1, 0x4B,
			0x02 };

	/**
	 * To enable or disable MicroQR
	 */
	public static final byte[] MICRO_QR_ENABLE = { (byte) 0xF1, 0x3D, 0x01 };
	public static final byte[] MICRO_QR_DISABLE = { (byte) 0xF1, 0x3D, 0x00 };

	/**
	 * To enable or disable Aztec.
	 */
	public static final byte[] AZTEC_ENABLE = { (byte) 0xF1, 0x3E, 0x01 };
	public static final byte[] AZTEC_DISABLE = { (byte) 0xF1, 0x3E, 0x00 };

	/**
	 * Regular Only - the decoder decodes regular Aztec bar codes only.
	 */
	public static final byte[] AZTEC_REGULAR_ONLY = { (byte) 0xF1, 0x4D, 0x00 };

	/**
	 * Inverse Only - the decoder decodes inverse Aztec bar codes only.
	 */
	public static final byte[] AZTEC_INVERSE_ONLY = { (byte) 0xF1, 0x4D, 0x01 };

	/**
	 * Inverse Autodetect - the decoder decodes both regular and inverse Aztec
	 * bar codes.
	 */
	public static final byte[] AZTEC_INVERSE_AUTODETECT = { (byte) 0xF1, 0x4D,
			0x02 };

	/**
	 * To enable or disable Han Xin
	 */
	public static final byte[] Han_Xin_ENABLE = { (byte) 0xF3, (byte) 0x8F,
			0x01 };
	public static final byte[] Han_Xin_DISABLE = { (byte) 0xF3, (byte) 0x8F,
			0x00 };

	/******************************* Postal Codes ******************************************/
	/**
	 * To enable or disable US Postnet.
	 */
	public static final byte[] US_POSTNET_ENABLE = { (byte) 0x59, 0x01 };
	public static final byte[] US_POSTNET_DISABLE = { (byte) 0x59, 0x00 };

	/**
	 * To enable or disable US Planet.
	 */
	public static final byte[] US_PLANET_ENABLE = { (byte) 0x5A, 0x01 };
	public static final byte[] US_PLANET_DISABLE = { (byte) 0x5A, 0x00 };

	/**
	 * To enable or disable UK Postal.
	 */
	public static final byte[] UK_POSTAL_ENABLE = { (byte) 0x5B, 0x01 };
	public static final byte[] UK_POSTAL_DISABLE = { (byte) 0x5B, 0x00 };

	/**
	 * To enable or disable Japan Postal.
	 */
	public static final byte[] JAPAN_POSTAL_ENABLE = { (byte) 0xF0, 0X22, 0x01 };
	public static final byte[] JAPAN_POSTAL_DISABLE = { (byte) 0xF0, 0X22, 0x00 };

	/**
	 * To enable or disable Australia Post.
	 */
	public static final byte[] AUSTRALIA_POST_ENABLE = { (byte) 0xF0, 0X23,
			0x01 };
	public static final byte[] AUSTRALIA_POST_DISABLE = { (byte) 0xF0, 0X23,
			0x00 };

	/**
	 * To enable or disable Netherlands KIX Code.
	 */
	public static final byte[] NETHERLANDS_KIX_CODE_ENABLE = { (byte) 0xF0,
			0X46, 0x01 };
	public static final byte[] NETHERLANDS_KIX_CODE_DISABLE = { (byte) 0xF0,
			0X46, 0x00 };

	/**
	 * To enable or disable USPS 4CB/One Code/Intelligent Mail.
	 */
	public static final byte[] USPS_4CB_ONE_CODE_INTELLIGENT_MAIL_ENABLE = {
			(byte) 0xF1, 0X50, 0x01 };
	public static final byte[] USPS_4CB_ONE_CODE_INTELLIGENT_MAIL_DISABLE = {
			(byte) 0xF1, 0X50, 0x00 };

	/**
	 * To enable or disable UPU FICS Postal.
	 */
	public static final byte[] UPU_FICS_POSTAL_ENABLE = { (byte) 0xF1, 0X63,
			0x01 };
	public static final byte[] UPU_FICS_POSTAL_DISABLE = { (byte) 0xF1, 0X63,
			0x00 };

	private static final byte[] buffer = new byte[1024];

	private boolean isOpen = false;

	public BarCodeAPI() {
		SerialPortManager.switchRFID = false;
	}

	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * 函数说明：打开二维码模块，实际上给模块上电，需要等待1.5S才能解码
	 * @return
	 */
	public boolean open() {
		SerialPortManager.getInstance().write(OPEN_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 100, 100);
		Log.i("whw", "open length=" + length + "  buffer[0]=" + buffer[0]);
		if (length == 1) {
			// 二维码上电之后需要等待1.5S才能解码。
			SystemClock.sleep(3000);
			isOpen = true;
			return true;
		}
		return false;
	}

	/**
	 * 函数说明：关闭二维码模块，实际上给模块下电
	 * @return
	 */
	public boolean close() {
		SerialPortManager.getInstance().write(CLOSE_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 100, 100);
		Log.i("whw", "close length=" + length + "  buffer[0]=" + buffer[0]);
		if (length == 1) {
			isOpen = false;
			return true;
		}
		return false;
	}

	/**
	 * 函数说明：恢复出厂设置命令（第一次使用时使用该命令）
	 * @return
	 */
	public boolean restore() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(RESTORE_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 5000, 200);
		if (length == 0) {
			sendNAK();
		} else if (length == 6) {
			return true;
		}
		print("restore");
		SystemClock.sleep(50);
		return false;
	}
	/**
	 * 函数说明：设置为HOST模式（设置一次之后955掉电再上电仍然有效）
	 * @return
	 */
	public boolean setHost() {
		for (int i = 0; i < 3; i++) {
			SerialPortManager.getInstance().write(WAKEUP);
			SystemClock.sleep(50);
			SerialPortManager.getInstance().write(SET_HOST_COMMAND);
			int length = SerialPortManager.getInstance().read(buffer, 100, 100);
			Log.i("whw", "setHost length=" + length);
			if (length == 0) {
				sendNAK();
			} else if (length == 6) {
				return true;
			}
			print("setHost");
		}
		return false;
	}
	/**
	 * 函数说明：禁止解析所有的条码或二维码，具体使用场景，因为BarCode出厂时默认会开启大部分解码功能，
	 * 当用户仅需要解析一种或几种BarCode时，可以先调用该方法，
	 * 然后开启需要使用的一种或多种BarCode，来提高解码效率，减少解码时间。
	 * @return
	 */
	public boolean disableAllSymbologies() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(DISABLE_ALL_SYMBOLOGIES_COMMAND);
		int length = SerialPortManager.getInstance().read(buffer, 100, 100);
		if (length == 0) {
			sendNAK();
		} else if (length == 6) {
			return true;
		}
		return false;
	}

	/**
	 * 函数说明：时间延迟低功耗模式
	 *
	 * @return
	 */
	public boolean lowPowerTimeout() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(LOW_POWER_TIMEOUT);
		int length = SerialPortManager.getInstance().read(buffer, 5000, 100);
		if (length == 0) {
			sendNAK();
		} else if (length == 6) {
			return true;
		}
		return false;
	}
	/**
	 * 函数说明：条码参数配置；当扫描引擎处于休眠模式时需要发送0x00以唤醒扫描引擎；
	 * 唤醒之后再10~500ms之内发送相应的指令，否则又将进入休眠模式。
	 * @return
	 */
	public boolean scanConfigurationParameter() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(START_DECODE);
		int length = SerialPortManager.getInstance().read(buffer, 300, 500);
		Log.i("whw", "scanConfigurationParameter length=" + length);
		print("scanConfigurationParameter");
		if (length == 6) {
			return true;
		}
		return false;
	}
	/**
	 * 函数说明： 开始解析条码或二维码
	 * @return
	 */
	public int startDecode(byte[] inBuffer) {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(START_DECODE);
		int length = SerialPortManager.getInstance().readFixedLength(buffer,
				1500, 6);// 6:ack
		Log.i("whw", "startDecode length=" + length);
		if (length == 6 && buffer[0] == RESPONSE_ACK[0]) {
			SerialPortManager.getInstance().clearReceiveData();
			// sendACK();
			int packetsLength = SerialPortManager.getInstance()
					.readFixedLength(buffer, 5000, 0);
			if (packetsLength > 0) {
				byte[] data = new byte[packetsLength];
				System.arraycopy(buffer, 0, data, 0, data.length);
				sendACK();
				return parseMultiPacket(data, inBuffer);
			}

		} else if (length > 6 && buffer[0] == RESPONSE_ACK[0]) {
			Log.i("whw", "length > 6");
			byte[] data = new byte[length - RESPONSE_ACK.length];
			System.arraycopy(buffer, 6, data, 0, data.length);
			sendACK();
			return parseMultiPacket(data, inBuffer);
		}
		return 0;
	}

	/**
	 * 函数说明：关闭解码
	 *
	 * @return
	 */
	public boolean stopDecode() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(STOP_DECODE);
		int length = SerialPortManager.getInstance().read(buffer, 100, 100);
		Log.i("whw", "stopDecode length=" + length);
		print("stopDecode");
		// length==6 is ack response
		if (length == 6) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 函数说明：
	 * @return
	 */
	private int parseMultiPacket(byte[] data, byte[] inBuffer) {
		int index = 0;
		int startPosition = 5;
		int nextPacketPosition = 0;
		int parseDataLength = 0;
		if (data != null && data.length > 0) {
			boolean isContinue = true;
			while (isContinue) {
				int dataLength = data[nextPacketPosition] & 0xff;
				parseDataLength += dataLength - 5;
				System.arraycopy(data, startPosition, inBuffer, index,
						dataLength - 5);
				if (data.length > dataLength + 2 + nextPacketPosition) {
					nextPacketPosition += dataLength + 2;
					startPosition = nextPacketPosition + 5;
					index += dataLength - 5;
				} else {
					isContinue = false;
				}
			}

		}
		return parseDataLength;

	}

	/**
	 * 函数说明：生成指定的解码数据的格式
	 *
	 * @return
	 */
	public boolean decodeDataPacketFormat() {
		for (int i = 0; i < 3; i++) {
			SerialPortManager.getInstance().write(WAKEUP);
			SystemClock.sleep(100);
			SerialPortManager.getInstance().write(DECODE_DATA_PACKET_FORMAT);
			int length = SerialPortManager.getInstance().read(buffer, 100, 100);
			Log.i("whw", "DECODE_DATA_PACKET_FORMAT length=" + length);
			SystemClock.sleep(500);

			if (length == 0) {
				sendNAK();
			} else if (length == 6) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 函数说明：设置多个包选项
	 *
	 * @return
	 */
	public boolean setMultipacketOptionContinuously() {
		for (int i = 0; i < 3; i++) {
			SerialPortManager.getInstance().write(WAKEUP);
			SystemClock.sleep(100);
			SerialPortManager.getInstance().write(
					MALTIPACKET_OPTION_CONTINUOUSLY);
			int length = SerialPortManager.getInstance().read(buffer, 100, 100);
			Log.i("whw", "MALTIPACKET_OPTION_CONTINUOUSLY length=" + length);
			if (length == 0) {
				sendNAK();
			} else if (length == 6) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 函数说明：使能汉信码
	 * @return
	 */
	public boolean enbaleHanxin() {
		byte[] command = new byte[] { 0x09, (byte) 0xC6, 0x04, 0x00,
				(byte) 0xFF, (byte) 0xF8, 0x04, (byte) 0x8F, 0x01, (byte) 0xFC,
				(byte) 0xA2 };
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(command);
		int length = SerialPortManager.getInstance().read(buffer, 5000, 100);
		Log.i("whw", "setParameter length=" + length);
		if (length == 0) {
			sendNAK();
		} else if (length == 6) {
			// byte[] temp = new byte[6];
			// System.arraycopy(buffer, 0, temp, 0, 6);
			// Log.i("whw", "temp hex=" + DataUtils.toHexString(temp));
			return true;
		}
		return false;
	}
	/**
	 * 函数说明：禁用汉信码
	 * @return
	 */
	public boolean disableHanxin() {
		byte[] command = new byte[] { 0x09, (byte) 0xC6, 0x04, 0x00,
				(byte) 0xFF, (byte) 0xF8, 0x04, (byte) 0x8F, 0x00, (byte) 0xFC,
				(byte) 0xA3 };
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(command);
		int length = SerialPortManager.getInstance().read(buffer, 5000, 100);
		Log.i("whw", "setParameter length=" + length);
		if (length == 0) {
			sendNAK();
		} else if (length == 6) {
			return true;
		}
		return false;
	}
	/**
	 * 函数说明：配置BarCode参数，一般用来开启或禁用某一条码或二维码，设置的参数信息会被写入BarCode的内部存储空间，所以只需要调用一次
	 * @return
	 */
	public boolean setParameter(byte[] param) {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		byte[] command = packageCommandPacket(param);
		SerialPortManager.getInstance().write(command);
		int length = SerialPortManager.getInstance().read(buffer, 5000, 100);
		Log.i("whw", "setParameter length=" + length);
		if (length == 0) {
			sendNAK();
		} else if (length == 6) {
			return true;
		}
		return false;
	}
	/**
	 * 函数说明：
	 * @return
	 */
	private byte[] packageCommandPacket(byte[] params) {
		int length = 1 + PARAM_COMMON.length + params.length + 2;
		byte[] command = new byte[length];
		command[0] = (byte) (length - 2);
		System.arraycopy(PARAM_COMMON, 0, command, 1, PARAM_COMMON.length);
		System.arraycopy(params, 0, command, 1 + PARAM_COMMON.length,
				params.length);
		int sum = 0x10000;
		for (int i = 0; i < command.length - 2; i++) {
			sum -= (command[i] & 0xff);
		}
		byte[] shot2byte = DataUtils.short2byte((short) sum);
		Log.i("whw", "sum hex=" + Integer.toHexString(sum));
		command[length - 2] = shot2byte[0];
		command[length - 1] = shot2byte[1];
		return command;
	}
	/**
	 * 函数说明：发送ACK指令
	 * @return
	 */
	private void sendACK() {
		// SerialPortManager.getInstance().write(WAKEUP);
		// SystemClock.sleep(20);
		SerialPortManager.getInstance().write(CMD_ACK);
	}
	/**
	 * 函数说明：发送NAK指令
	 * @return
	 */
	private void sendNAK() {
		SerialPortManager.getInstance().write(WAKEUP);
		SystemClock.sleep(50);
		SerialPortManager.getInstance().write(CMD_NAK);
	}

	private void print(String str) {
		// byte[] temp = new byte[10];
		// System.arraycopy(buffer, 0, temp, 0, temp.length);
		// Log.i("whw", str + " hex=" + DataUtils.toHexString(temp));
	}
}
