package android_serialport_api;

import android.os.SystemClock;
import android.util.Log;

import com.szfp.szfplib.utils.DataUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class FingerprintAPI {

	/**
	 * 缓冲区:下位机返回数据存放地 Buffer: The next crew to return data storage places
	 */
	private byte[] data = new byte[1024 * 50];

	private byte[] buffer = new byte[1024 * 50];

	private byte[] bufferImage = new byte[1024 * 50];

	public static final int BIG_FINGERPRINT_SIZE = 50052;
	public static final int SMALL_FINGERPRINT_SIZE = 40044;

	private static int CURRENT_FINGERPRINT_SIZE;

	/**
	 * 确认码：0x20—0xef为保留的确认码 表示指令执行完毕或OK； 0x20—0xefThe for the Reserved Indicates
	 * instruction completes or OK
	 */
	public static final int EXEC_COMMAND_SUCCESS = 0x00;
	/**
	 * 表示数据包接收错误； Indicates that the data packet reception error;
	 */
	public static final int RECEIVE_PACKAGE_ERROR = 0x01;
	/**
	 * 表示传感器上没有手指； Indicates that the sensor does not have fingers;
	 */
	public static final int NO_FINGER = 0x02;
	/**
	 * 表示录入指纹图像失败； Input fingerprint image indicates failure;
	 */
	public static final int GET_IMAGE_FAIL = 0x03;
	/**
	 * 表示指纹图像太干、太淡而生不成特征； Represents the fingerprint image is too dry, not too
	 * light and health characteristics;
	 */
	public static final int FINGERPRINT_SMALL = 0x04;
	/**
	 * 表示指纹图像太湿、太糊而生不成特征； Represents the fingerprint image is too wet, not too
	 * paste feature is born;
	 */
	public static final int FINGERPRINT_BLURRING = 0x05;
	/**
	 * 表示指纹图像太乱而生不成特征； Indicates born fingerprint image is too chaotic
	 * fragmentation characteristics;
	 */
	public static final int FINGERPRINT_NOT_GENCHAR = 0x06;
	/**
	 * 表示指纹图像正常，但特征点太少（或面积太小）而生不成特征； Represents the fingerprint image is normal,
	 * but too few feature points (or too small) was born not characteristic;
	 */
	public static final int FINGERPRINT_CHAR_LESS = 0x07;
	/**
	 * 表示指纹不匹配； Means that fingerprints do not match;
	 */
	public static final int FINGERPRINT_NO_MATCH = 0x08;
	/**
	 * 表示没搜索到指纹； Said they were not searching to fingerprint;
	 */
	public static final int FINGERPRINT_NO_SEARCH = 0x09;
	/**
	 * 表示特征合并失败； Indicates characteristics merge failed;
	 */
	public static final int FINGERPRINT_REG_MODEL_FAIL = 0x0a;
	/**
	 * 表示访问指纹库时地址序号超出指纹库范围； Represents the address number when accessing
	 * fingerprint database fingerprint database beyond the scope;
	 */
	public static final int FLASH_OUT_OF_INDEX = 0x0b;
	/**
	 * 表示从指纹库读模板出错或无效； Represents a template from a fingerprint database read
	 * error or invalid;
	 */
	public static final int READ_MODEL_FROM_FLASH = 0x0c;
	/**
	 * 表示上传特征失败； Uploads a feature to fail;
	 */
	public static final int UP_CHAR_FAIL = 0x0d;
	/**
	 * 表示模块不能接受后续数据包； Indicates that the module can not accept subsequent
	 * packets;
	 */
	public static final int NOT_RECEIVE_PACKAGE = 0x0e;
	/**
	 * 表示上传图像失败； Upload image indicates failure;
	 */
	public static final int UP_IMAGE_FAIL = 0x0f;
	/**
	 * 表示删除模板失败； Means to delete the template failed;
	 */
	public static final int DELETE_MODEL_FAIL = 0x10;
	/**
	 * 表示清空指纹库失败； Empty fingerprint database indicates failure;
	 */
	public static final int EMPTY_FLASH = 0x11;
	/**
	 * 表示不能进入低功耗状态； That they can not enter a low power state;
	 */
	public static final int NOT_ENTRY_LESS_STATUS = 0x12;
	/**
	 * 表示口令不正确； Means that the password is incorrect;
	 */
	public static final int COMMAND_FAIL = 0x13;
	public static final int ERROR1 = 0x14;// 表示系统复位失败；
	public static final int ERROR2 = 0x15;// 表示缓冲区内没有有效原始图而生不成图像；
	public static final int ERROR3 = 0x16;// 表示在线升级失败；
	public static final int ERROR4 = 0x17;// 表示残留指纹或两次采集之间手指没有移动过；
	public static final int ERROR5 = 0x18;// 表示读写FLASH 出错；
	public static final int ERROR6 = 0xf0;// 有后续数据包的指令，正确接收后用0xf0 应答；
	public static final int ERROR7 = 0xf1;// 有后续数据包的指令，命令包用0xf1 应答；
	public static final int ERROR8 = 0xf2;// 表示烧写内部FLASH 时，校验和错误；
	public static final int ERROR9 = 0xf3;// 表示烧写内部FLASH 时，包标识错误；
	public static final int ERROR10 = 0xf4;// 表示烧写内部FLASH 时，包长度错误；
	public static final int ERROR11 = 0xf5;// 表示烧写内部FLASH 时，代码长度太长；
	public static final int ERROR12 = 0xf6;// 表示烧写内部FLASH 时，烧写FLASH 失败；
	public static final int ERROR13 = 0x19;// 未定义错误；
	public static final int ERROR14 = 0x1a;// 无效寄存器号；
	public static final int ERROR15 = 0x1b;// 寄存器设定内容错误号；
	public static final int ERROR16 = 0x1c;// 记事本页码指定错误；
	public static final int ERROR17 = 0x1d;// 端口操作失败；
	/**
	 * 自动注册（enroll）失败； Automatically register (enroll) failed;
	 */
	public static final int ENROLL_FAIL = 0x1e;
	/**
	 * 指纹库满 Fingerprint database is full
	 */
	public static final int FLASH_FULL = 0x1f;
	/**
	 * 无响应 No response
	 */
	public static final int NO_RESPONSE = 0xff;

	public FingerprintAPI() {
	}

	/**
	 * set fingerprint type: big or small
	 *
	 * @param type
	 */
	public void setFingerprintType(int type) {
		CURRENT_FINGERPRINT_SIZE = type;
	}

	/**
	 * 录入指纹图像 功能说明： 探测手指，探测到后录入指纹图像存于ImageBuffer。 返回确认码表示：录入成功、无手指等。
	 *
	 * @return 返回值为确认码 确认码=00H表示录入成功 确认码=01H表示收包有错 确认码=02H表示传感器上无手指
	 *         确认码=03H表示录入不成功 确认码=ffH 表示无响应 Input fingerprint image Function:
	 *         probing fingers, detected after the entry stored in the
	 *         fingerprint image ImageBuffer. Return Confirmation code said:
	 *         Entry Success, no fingers.
	 * @return Confirmation code Confirmation code=00H Indicates successful
	 *         entry Confirmation code=01H An indication that the package is
	 *         wrong Confirmation code=02H Indicates that the sensor is no
	 *         finger Confirmation code=03H Indicates unsuccessful entry
	 *         Confirmation code=ffH Indicates no response
	 */
	public synchronized int PSGetImage() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x01, 0x00, 0x05 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSGetImage", length);
		if (length == 12) {
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	/**
	 * 功能说明： 将ImageBuffer 中的原始图像生成指纹特征文件存于CharBuffer1 或CharBuffer2 输入参数：
	 * BufferID(特征缓冲区号)
	 *
	 * @param bufferId
	 *            （CharBuffer1:1h,CharBuffer2:2h）
	 * @return 返回值为确认码 确认码=00H表示生成特征成功 确认码=01H表示收包有错 确认码=06H表示指纹图像太乱而生不成特征
	 *         确认码=07H表示指纹图像正常，但特征点太少而生不成特征 确认码=15H表示图像缓冲区内没有有效原始图而生不成图像
	 *         确认码=ffH表示无响应 Function Description: ImageBuffer the original image
	 *         to generate the fingerprint profiles stored in CharBuffer1 or
	 *         CharBuffer2 Input parameters: BufferID (characteristic buffer
	 *         number)
	 * @param bufferId
	 *            （CharBuffer1:1h,CharBuffer2:2h）
	 * @return Confirmation code Confirmation code=00H Indicates successful
	 *         generation features Confirmation code=01H An indication that the
	 *         package is wrong Confirmation code=06H Indicates born fingerprint
	 *         image is too chaotic fragmentation characteristics Confirmation
	 *         code=07H Represents the fingerprint image is normal, but too few
	 *         feature points fragmentation characteristics and health
	 *         Confirmation code=15H Means that there is no valid image buffer
	 *         born fragmentation original image image Confirmation code=ffH
	 *         Indicates no response
	 */
	public synchronized int PSGenChar(int bufferId) {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 01, (byte) 0x00, (byte) 0x04,
				(byte) 0x02, (byte) bufferId, (byte) 0x00,
				(byte) (0x7 + bufferId) };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSGenChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	/**
	 * 合并特征生成模板，将CharBuffer1和CharBuffer2中的特征文件合并生成模板，
	 * 结果存于CharBuffer1和CharBuffer2中。
	 *
	 * @return 返回值为确认码 确认码=00H表示合并成功 确认码=01H表示收包有错 确认码=0aH表示合并失败（两枚指纹不属于同一手指）
	 *         确认码=ffH 表示无响应 Merge features generate a template that will
	 *         CharBuffer1 and CharBuffer2 features in the file merge generation
	 *         templates The result is stored in CharBuffer1 and CharBuffer2 in.
	 *
	 * @return Confirmation code Confirmation code=00H Means that the merge was
	 *         successful Confirmation code=01H An indication that the package
	 *         is wrong Confirmation code=0aH Expressed merge failure (two
	 *         fingerprints do not belong to the same finger) Confirmation
	 *         code=ffH Indicates no response
	 */
	public synchronized int PSRegModel() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x05, 0x00, 0x09 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSRegModel", length);
		if (length == 12) {
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	/**
	 * 将CharBuffer中的模板储存到指定的pageId号的flash数据库位置 bufferId:只能为1h或2h
	 * pageId：范围为0~1009 输入参数： BufferID(缓冲区号)，PageID（指纹库位置号）
	 *
	 * @return 返回值为确认码 确认码=00H表示储存成功 确认码=01H表示收包有错 确认码=0bH表示PageID超出指纹库范围
	 *         确认码=18H表示写FLASH出错 确认码=ffH 表示无响应 The CharBuffer templates stored
	 *         to the specified number of flash pageId database location
	 *         bufferId: can only 1h or 2h pageId：0~1009 Input parameters:
	 *         BufferID (buffer number), PageID (fingerprint location number)
	 * @return Confirmation code Confirmation code=00H Expressed successfully
	 *         saved Confirmation code=01H An indication that the package is
	 *         wrong Confirmation code=0bH PageID beyond the scope of
	 *         representation fingerprint database Confirmation code=18H FLASH
	 *         write error indicates Confirmation code=ffH Indicates no response
	 */
	public synchronized int PSStoreChar(int bufferId, int pageId) {
		byte[] pageIDArray = short2byte((short) pageId);
		// Log.i("whw", "pageid hex=" + DataUtils.toHexString(pageIDArray));
		int checkSum = 0x01 + 0x00 + 0x06 + 0x06 + bufferId
				+ (pageIDArray[0] & 0xff) + (pageIDArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		// Log.i("whw",
		// "checkSumArray hex=" + DataUtils.toHexString(checkSumArray)
		// + "    checkSum=" + checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x06, (byte) 0x06, (byte) bufferId,
				(byte) pageIDArray[0], (byte) pageIDArray[1],
				(byte) checkSumArray[0], (byte) checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSStoreChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	/**
	 * 将flash 数据库中指定pageId号的指纹模板读入到模板缓冲区CharBuffer1或CharBuffer2
	 * bufferId:只能为1h或2h pageId：范围为0~1023 输入参数： BufferID(缓冲区号)，PageID(指纹库模板号)
	 *
	 *            pageId号
	 * @return 返回值为确认码 确认码=00H表示读出成功 确认码=01H表示收包有错 确认码=0cH表示读出有错或模板有错
	 *         确认码=0BH表示PageID超出指纹库范围 确认码=ffH 表示无响应 Specified in the database
	 *         will flash fingerprint template pageId number read into the
	 *         stencil buffer CharBuffer1 or CharBuffer2 bufferId:1h or 2h
	 *         pageId：0~1010 Input parameters: BufferID (buffer number), PageID
	 *         (fingerprint database template number)
	 * @return Confirmation code Confirmation code=00H Readout indicates success
	 *         Confirmation code=01H An indication that the package is wrong
	 *         Confirmation code=0cH Is the readout wrong or wrong template
	 *         Confirmation code=0BH PageID beyond the scope of representation
	 *         fingerprint database Confirmation code=ffH Indicates no response
	 */
	public synchronized int PSLoadChar(int bufferId, int pageId) {
		byte[] pageIDArray = short2byte((short) pageId);
		int checkSum = 0x01 + 0x00 + 0x06 + 0x07 + bufferId
				+ (pageIDArray[0] & 0xff) + (pageIDArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x06, (byte) 0x07, (byte) bufferId,
				(byte) pageIDArray[0], (byte) pageIDArray[1],
				(byte) checkSumArray[0], (byte) checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSLoadChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	/**
	 * 以CharBuffer1 或CharBuffer2 中的特征文件搜索整个或部分指纹库。若搜索到，则返回页码。 输入参数： BufferID，
	 * StartPage(起始页)，PageNum（页数） 返回参数： 确认字，页码（相配指纹模板）
	 *
	 * @param bufferId
	 *            缓冲区1h，2h
	 * @param startPageId
	 *            起始页
	 * @param pageNum
	 *            页数
	 * @return 确认码=00H 表示搜索到； 确认码=01H 表示收包有错； 确认码=09H 表示没搜索到；此时页码与得分为0 确认码=ffH
	 *         表示无响应 In CharBuffer1 or CharBuffer2 the feature to search the
	 *         entire or part of the file fingerprint database. If found, it
	 *         returns the page number. Input parameters: BufferID, StartPage
	 *         (Home), PageNum (Pages) Return parameter: confirm the word, page
	 *         (match fingerprint templates)
	 * @param bufferId
	 *            buffer:1h，2h
	 * @param startPageId
	 * @param pageNum
	 * @return Confirmation code=00H Indicates that the search to; Confirmation
	 *         code=01H An indication that the package is wrong； Confirmation
	 *         code=09H Said they were not searched; this time with a score of 0
	 *         page Confirmation code=ffH Indicates no response
	 */
	public synchronized Result PSSearch(int bufferId, int startPageId,
										int pageNum) {
		byte[] startPageIDArray = short2byte((short) startPageId);
		byte[] pageNumArray = short2byte((short) pageNum);
		int checkSum = 0x01 + 0x00 + 0x08 + 0x04 + bufferId
				+ (startPageIDArray[0] & 0xff) + (startPageIDArray[1] & 0xff)
				+ (pageNumArray[0] & 0xff) + (pageNumArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x08, (byte) 0x04, (byte) bufferId,
				(byte) startPageIDArray[0], (byte) startPageIDArray[1],
				(byte) pageNumArray[0], (byte) pageNumArray[1],
				(byte) checkSumArray[0], (byte) checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSSearch", length);
		Result result = new Result();
		if (length == 16) {
			result.code = buffer[9];
			result.pageId = getShort(buffer[10], buffer[11]);
			result.matchScore = getShort(buffer[12], buffer[13]);
		} else {
			result.code = NO_RESPONSE;
		}
		return result;
	}

	/**
	 * 精确比对CharBuffer1与CharBuffer2中的特征文件 注意点:下位机返回的数据里面还有一个得分，当得分大于等于50时，指纹匹配
	 *
	 * @return true：指纹匹配成功 false：比对失败 Exact Match CharBuffer1 the signature file
	 *         with CharBuffer2 attention point: the next crew returned data
	 *         there is also a score when the score is greater than or equal to
	 *         50, the fingerprint matching
	 *
	 * @return true：Fingerprint matching is successful false：Match fails
	 */
	public synchronized boolean PSMatch() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x03, (byte) 0x00, (byte) 0x07 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSMatch", length);
		if (length == 14) {
			if (buffer[9] == 0x00) {
				return score(buffer[10], buffer[11]);
			}
		}
		return false;
	}

	/**
	 * 采集一次指纹注册模板，在指纹库中搜索空位并存储，返回存储pageId 返回参数： 确认字，页码（相配指纹模板）
	 *
	 * @return 确认码=00H 表示注册成功； 确认码=01H 表示收包有错； 确认码=1eH 表示注册失败。 确认码=ffH 表示无响应
	 *         Collect a fingerprint registration template, fingerprint database
	 *         search space and storage, Storeback pageId Return parameter:
	 *         confirm the word, page (match fingerprint templates)
	 * @return Confirmation code=00H Indicates successful registration;
	 *         Confirmation code=01H An indication that the package is wrong；
	 *         Confirmation code=1eH Means that the registration failed.
	 *         Confirmation code=ffH Indicates no response
	 */
	public synchronized Result PSEnroll() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x14 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSEnroll", length);
		Result result = new Result();
		if (length == 14) {
			result.code = buffer[9];
			result.pageId = getShort(buffer[10], buffer[11]);
		} else {
			result.code = NO_RESPONSE;
		}
		return result;
	}

	/**
	 * 自动采集指纹，在指纹库中搜索目标模板并返回搜索结果。 如果目标模板同当前采集的指纹比对得分大于最高阀值，
	 * 并且目标模板为不完整特征则以采集的特征更新目标模板的空白区域。 返回参数： 确认码，页码（相配指纹模板）
	 *
	 * @return 确认码=00H 表示搜索到； 确认码=01H 表示收包有错； 确认码=09H 表示没搜索到；此时页码与得分为0 确认码=ffH
	 *         表示无响应 Automatic fingerprint in the fingerprint database search
	 *         target template and return to search results. If the target
	 *         template with the current collection of fingerprint matching
	 *         score greater than the highest threshold, And the target template
	 *         is incomplete collection characteristic feature places a blank
	 *         area to update the target template. Return parameters:
	 *         Confirmation code, page number (matching fingerprint template)
	 * @return Confirmation code=00H Indicates that the search to; Confirmation
	 *         code=01H An indication that the package is wrong； Confirmation
	 *         code=09H Said they were not searched; this time with a score of 0
	 *         page Confirmation code=ffH Indicates no response
	 */
	public synchronized Result PSIdentify() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x11, (byte) 0x00, (byte) 0x15 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSIdentify", length);
		Result result = new Result();
		if (length == 16) {
			result.code = buffer[9];
			result.pageId = getShort(buffer[10], buffer[11]);
			result.matchScore = getShort(buffer[12], buffer[13]);
		} else {
			result.code = NO_RESPONSE;
		}
		return result;
	}

	/**
	 * 删除模板 删除flash 数据库中指定ID 号开始的N 个指纹模板 输入参数：PageID(指纹库模板号)，N 删除的模板个数。
	 *
	 * @param pageIDStart
	 * @param delNum
	 * @return 确认码=00H 表示删除模板成功； 确认码=01H 表示收包有错； 确认码=10H 表示删除模板失败； 确认码=ffH 表示无响应
	 *         Delete Template Delete a flash ID number specified in the
	 *         database Start the N fingerprint templates Input parameters:
	 *         PageID (fingerprint database template number), N the number of
	 *         the deleted template.
	 * @param pageIDStart
	 * @param delNum
	 * @return Confirmation code=00H Success means to delete the template;
	 *         Confirmation code=01H An indication that the package is wrong；
	 *         Confirmation code=10H Means to delete the template failed;
	 *         Confirmation code=ffH Indicates no response
	 */
	public synchronized int PSDeleteChar(short pageIDStart, short delNum) {
		byte[] pageIDArray = short2byte(pageIDStart);
		byte[] delNumArray = short2byte(delNum);
		int checkSum = 0x01 + 0x07 + 0x0c + (pageIDArray[0] & 0xff)
				+ (pageIDArray[1] & 0xff) + (delNumArray[0] & 0xff)
				+ (delNumArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x07, (byte) 0x0c, pageIDArray[0], pageIDArray[1],
				delNumArray[0], delNumArray[1], checkSumArray[0],
				checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSDeleteChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	/**
	 * 功能说明： 删除flash 数据库中所有指纹模板
	 *
	 * @return 确认码=00H 表示清空成功； 确认码=01H 表示收包有错； 确认码=11H 表示清空失败； 确认码=ffH 表示无响应
	 *         Function: Delete all fingerprint template flash database
	 * @return Confirmation code=00H Empty successful representation;
	 *         Confirmation code=01H An indication that the package is wrong；
	 *         Confirmation code=11H Represents clear failure; Confirmation
	 *         code=ffH Indicates no response
	 */
	public synchronized int PSEmpty() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x0d, (byte) 0x00, (byte) 0x11 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSEmpty", length);
		if (length == 12) {
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	/**
	 * 将特征缓冲区中的特征文件上传给上位机
	 *
	 * @return byte[]：长度为512字节成功 否则失败 null:上传特征文件失败 The characteristic features
	 *         of the buffer file uploads to the host computer (the default
	 *         feature buffer charbuffer1)
	 *
	 * @return byte[]：Length success is 512 bytes null:Characteristics file
	 *         failed upload
	 */
	public synchronized byte[] PSUpChar(int bufferId) {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x04, (byte) 0x08, (byte) bufferId, (byte) 0x00,
				(byte) (0x0d + bufferId) };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 300);
		printlog("PSUpChar", 12);
		Log.i("whw", "upchar length=" + length);
		// 响应为12字节，共4个数据包，每个包为139字节，所以返回的总字节数为568字节
		if (length == 568) {
			index = 12;// 数据包的起始下标
			packetNum = 0;
			byte[] packets = new byte[568];
			System.arraycopy(buffer, 0, packets, 0, 568);
			return parsePacketData(packets);
		}
		return null;

	}

	/**
	 * 上位机下载特征文件到模块的特征缓冲区(默认的缓冲区为CharBuffer2)
	 *
	 * @param model
	 *            :指纹的特征文件
	 * @return 返回值为确认码 确认码=00H 表示可以接收后续数据包； 确认码=01H 表示收包有错； 确认码=0eH 表示不能接收后续数据包；
	 *         确认码=ffH 表示无响应 download the signature file to the module
	 *         characteristics buffer (the default buffer CharBuffer2)
	 *
	 * @param model
	 *            :Fingerprint template
	 * @return Confirmation code Confirmation code=00H Means that subsequent
	 *         packets can be received; Confirmation code=01H An indication that
	 *         the package is wrong； Confirmation code=0eH That they can not
	 *         receive subsequent data packets; Confirmation code=ffH Indicates
	 *         no response
	 */
	public synchronized int PSDownChar(int bufferId, byte[] model) {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x04, (byte) 0x09, (byte) bufferId, (byte) 0x00,
				(byte) (0x0e + bufferId) };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSDownChar", length);
		if (length == 12 && buffer[9] == 0x00) {
			sendData(model);
			return 0x00;
		}
		return NO_RESPONSE;
	}

	/**
	 * 功能说明：读取录入模版的索引表。 输入参数： 索引表页码, 页码0,1,2,3 分别对应模版从0-256，256-512，
	 * 512-768，768-1024 的索引，每1 位代表一个模版，1 表示对应存储区域 的模版已经录入，0 表示没录入。
	 *
	 * @param pageId
	 * @return
	 */
	private synchronized int PS_ReadIndexTable(byte[] data, int pageId) {
		int checkSum = 0x01 + 0x04 + 0x1f + pageId;
		byte[] checkSumArray = short2byte((short) checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x04, (byte) 0x1f, (byte) pageId, checkSumArray[0],
				checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PS_ReadIndexTable", length);
		if (length == 44) {
			System.arraycopy(buffer, 10, data, 0, 32);
			Log.i("whw", "PS_ReadIndexTable=" + DataUtils.toHexString(data));
			return buffer[9];
		}
		return NO_RESPONSE;
	}

	private final byte[] compareData = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
			0x40, (byte) 0x80 };

	/**
	 * Function Description: Get a can store fingerprint model ID(0~1009)
	 *
	 * @return If it returns -1, which means that the fingerprint database is
	 *         full.
	 */
	public synchronized int getValidId() {
		byte[] data = new byte[32];
		int response = -1;
		for (int i = 0; i < 4; i++) {
			response = PS_ReadIndexTable(data, i);
			if (response == 0x00) {
				for (int j = 0; j < data.length; j++) {
					for (int k = 0; k < compareData.length; k++) {
						if ((compareData[k] & data[j]) == 0x00) {
							Log.i("whw", "j=" + j + "   k=" + k);
							int id = i * 256 + j * 8 + k;
							if (id <= 1009) {
								return id;
							} else {
								return -1;
							}
						}
					}

				}
			}
		}
		return -1;
	}

	public static int tempLength;

	/**
	 * 将图像缓冲区的数据上传给上位机
	 *
	 * @return 返回值为bmp格式的指纹图像，如果为null上传失败 Upload the image buffer data to the
	 *         host computer;
	 *
	 * @return Return value bmp format image of the fingerprint, if null upload
	 *         failed
	 */
	public synchronized byte[] PSUpImage() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x0a, (byte) 0x00, (byte) 0x0e };
		byte[] nextCommand = { 'O' };
		byte[] repeatCommand = { 'R' };
		int startLength = 3348;
		int dataLength = 3336;
		int size = 0;
		int length = 0;
		sendCommand(command);
		boolean isFirst = true;
		do {
			size = SerialPortManager.getInstance().readFixedLength(buffer,
					3000, isFirst ? startLength : dataLength, 100);
			if (size == 0) {
				break;
			}
			if (isFirst) {
				isFirst = false;
				if (size < startLength) {
					System.arraycopy(buffer, 0, bufferImage, 0, 12);
					length += 12;
					SystemClock.sleep(5);
					sendCommand(repeatCommand);
				} else {
					System.arraycopy(buffer, 0, bufferImage, 0, size);
					length += size;
					SystemClock.sleep(5);
					sendCommand(nextCommand);
				}
				continue;
			}
			Log.i("whw", "size=" + size);
			if (size == dataLength) {
				Log.i("whw", "size == dataLength");
				System.arraycopy(buffer, 0, bufferImage, length, size);
				length += size;
				SystemClock.sleep(5);
				sendCommand(nextCommand);
			} else {
				Log.i("whw", "size != dataLength");
				SystemClock.sleep(5);
				sendCommand(repeatCommand);
			}
		} while (length < CURRENT_FINGERPRINT_SIZE);

		Log.i("whw", "PSUpImage length=" + length);
		if (length == CURRENT_FINGERPRINT_SIZE) {
			byte[] packets = new byte[length];
			System.arraycopy(bufferImage, 0, packets, 0, length);
			index = 12;
			packetNum = 0;
			byte[] data = parsePacketData(packets);
			return getFingerprintImage(data);
		}
		return null;

	}

	public synchronized int PSDownImage(byte[] image) {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x0b, (byte) 0x00, (byte) 0x0f };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 3000, 100);
		printlog("PSDownImage", length);
		if (length == 12 && buffer[9] == 0x00) {
			sendData(extractImageData(image));
			return 0x00;
		}
		return NO_RESPONSE;
	}

	private byte[] extractImageData(byte[] image) {
		int headerSize = 1078;// bmp文件头大小为1078字节
		byte[] data = new byte[image.length - 1078];
		System.arraycopy(image, headerSize, data, 0, data.length);

		byte[] compressData = new byte[data.length / 2];
		for (int i = 0; i < compressData.length; i++) {
			byte a = (byte) (data[i * 2] & 0xf0);
			byte b = (byte) ((data[i * 2 + 1] >>> 4) & 0x0f);
			compressData[i] = (byte) ((a | b) & 0xff);
		}
		return compressData;
	}

	/**
	 * 发送指纹模板数据包512字节,分为4次发送，3次数据包，一次结束包
	 *
	 * @param data
	 */
	private void sendData(byte[] data) {
		// 数据包指令头
		byte[] dataPrefix = { (byte) 0xef, (byte) 0x01, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x02,
				(byte) 0x00, (byte) 0x82 };
		// 结束包指令头
		byte[] endPrefix = { (byte) 0xef, (byte) 0x01, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x08,
				(byte) 0x00, (byte) 0x82 };
		byte[] command = new byte[dataPrefix.length + 128 + 2];
		int length = data.length / 128;
		Log.i("whw", "data packet length=" + length);
		for (int i = 0; i < length; i++) {
			if (i == length - 1) {
				System.arraycopy(endPrefix, 0, command, 0, endPrefix.length);
			} else {
				System.arraycopy(dataPrefix, 0, command, 0, dataPrefix.length);
			}
			System.arraycopy(data, i * 128, command, dataPrefix.length, 128);
			short sum = 0;
			for (int j = 6; j < command.length - 2; j++) {
				sum += (command[j] & 0xff);
			}
			byte[] size = short2byte(sum);
			command[command.length - 2] = size[0];
			command[command.length - 1] = size[1];
			sendCommand(command);
			SystemClock.sleep(10);
		}

	}

	private int index;// 数据包的起始下标
	private int packetNum;// 数据包的个数

	private byte[] parsePacketData(byte[] packet) {

		int dstPos = 0;
		int packageLength = 0;
		int size = 0;
		do {
			Log.i("pac"+packetNum, "**************"+packageLength+" ____ "+ index);
			packageLength = getShort(packet[index + 7], packet[index + 8]);
			Log.i("whw", "packageLength=" + packageLength);
			if (packageLength != 130) {
				// Log.i("whw", "**************packetNum=" + packetNum);
				// FileUtil.write(packet);
				// FileUtil.write("\n\n\n");
				return null;
			}
			System.arraycopy(packet, index + 9, data, dstPos, packageLength - 2);
			dstPos += packageLength - 2;// 2是校验和
			packetNum++;
			size += packageLength - 2;

		} while (moveToNext(index + 6, packageLength, packet));
		if (size != 0) {
			byte[] dataPackage = new byte[size];
			Log.i("whw", "**************packetNum=" + packetNum);
			System.arraycopy(data, 0, dataPackage, 0, size);
			return dataPackage;
		}
		return null;
	}

	private boolean moveToNext(int position, int packageLength, byte[] packet) {
		if (packet[position] == 0x02) {
			index += packageLength + 9;
			return true;
		}
		return false;
	}

	private byte[] getFingerprintImage(byte[] data) {
		if (data == null) {
			return null;
		}
		byte[] imageData = new byte[data.length * 2];
		// Log.i("whw", "*****************data.length="+data.length);
		for (int i = 0; i < data.length; i++) {
			imageData[i * 2] = (byte) (data[i] & 0xf0);
			imageData[i * 2 + 1] = (byte) (data[i] << 4 & 0xf0);
		}

		// byte[] temp = new byte[imageData.length];
		// for (int i = packetNum-1,j=0; i >0; i--,j++) {
		// System.arraycopy(imageData, j*256, temp, i*256, 256);
		// }
		// imageData = temp;

		Log.i("whw", "*****************imageData.length=" + imageData.length);
		byte[] bmpData = toBmpByte(256, packetNum, imageData);
		return bmpData;
	}

	/**
	 * 将数据传入内存
	 */
	private byte[] toBmpByte(int width, int height, byte[] data) {
		byte[] buffer = null;
		try {
			// // 创建输出流文件对象
			// java.io.FileOutputStream fos = new
			// java.io.FileOutputStream(path);
			// // 创建原始数据输出流对象
			// java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			// 给文件头的变量赋值
			int bfType = 0x424d; // 位图文件类型（0—1字节）
			int bfSize = 54 + 1024 + width * height;// bmp文件的大小（2—5字节）
			int bfReserved1 = 0;// 位图文件保留字，必须为0（6-7字节）
			int bfReserved2 = 0;// 位图文件保留字，必须为0（8-9字节）
			int bfOffBits = 54 + 1024;// 文件头开始到位图实际数据之间的字节的偏移量（10-13字节）

			// 输入数据的时候要注意输入的数据在内存中要占几个字节，
			// 然后再选择相应的写入方法，而不是它自己本身的数据类型
			// 输入文件头数据
			dos.writeShort(bfType); // 输入位图文件类型'BM'
			dos.write(changeByte(bfSize), 0, 4); // 输入位图文件大小
			dos.write(changeByte(bfReserved1), 0, 2);// 输入位图文件保留字
			dos.write(changeByte(bfReserved2), 0, 2);// 输入位图文件保留字
			dos.write(changeByte(bfOffBits), 0, 4);// 输入位图文件偏移量

			// 给信息头的变量赋值
			int biSize = 40;// 信息头所需的字节数（14-17字节）
			int biWidth = width;// 位图的宽（18-21字节）
			int biHeight = height;// 位图的高（22-25字节）
			int biPlanes = 1; // 目标设备的级别，必须是1（26-27字节）
			int biBitcount = 8;// 每个像素所需的位数（28-29字节），必须是1位（双色）、4位（16色）、8位（256色）或者24位（真彩色）之一。
			int biCompression = 0;// 位图压缩类型，必须是0（不压缩）（30-33字节）、1（BI_RLEB压缩类型）或2（BI_RLE4压缩类型）之一。
			int biSizeImage = width * height;// 实际位图图像的大小，即整个实际绘制的图像大小（34-37字节）
			int biXPelsPerMeter = 0;// 位图水平分辨率，每米像素数（38-41字节）这个数是系统默认值
			int biYPelsPerMeter = 0;// 位图垂直分辨率，每米像素数（42-45字节）这个数是系统默认值
			int biClrUsed = 256;// 位图实际使用的颜色表中的颜色数（46-49字节），如果为0的话，说明全部使用了
			int biClrImportant = 0;// 位图显示过程中重要的颜色数(50-53字节)，如果为0的话，说明全部重要

			// 因为java是大端存储，那么也就是说同样会大端输出。
			// 但计算机是按小端读取，如果我们不改变多字节数据的顺序的话，那么机器就不能正常读取。
			// 所以首先调用方法将int数据转变为多个byte数据，并且按小端存储的顺序。

			// 输入信息头数据
			dos.write(changeByte(biSize), 0, 4);// 输入信息头数据的总字节数
			dos.write(changeByte(biWidth), 0, 4);// 输入位图的宽
			dos.write(changeByte(biHeight), 0, 4);// 输入位图的高
			dos.write(changeByte(biPlanes), 0, 2);// 输入位图的目标设备级别
			dos.write(changeByte(biBitcount), 0, 2);// 输入每个像素占据的字节数
			dos.write(changeByte(biCompression), 0, 4);// 输入位图的压缩类型
			dos.write(changeByte(biSizeImage), 0, 4);// 输入位图的实际大小
			dos.write(changeByte(biXPelsPerMeter), 0, 4);// 输入位图的水平分辨率
			dos.write(changeByte(biYPelsPerMeter), 0, 4);// 输入位图的垂直分辨率
			dos.write(changeByte(biClrUsed), 0, 4);// 输入位图使用的总颜色数
			dos.write(changeByte(biClrImportant), 0, 4);// 输入位图使用过程中重要的颜色数

			// 构造调色板数据
			byte[] palatte = new byte[1024];
			for (int i = 0; i < 256; i++) {
				palatte[i * 4] = (byte) i;
				palatte[i * 4 + 1] = (byte) i;
				palatte[i * 4 + 2] = (byte) i;
				palatte[i * 4 + 3] = 0;
			}
			dos.write(palatte);

			dos.write(data);
			// 关闭数据的传输
			dos.flush();
			buffer = baos.toByteArray();
			dos.close();
			// fos.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 将一个int数据转为按小端顺序排列的字节数组
	 *
	 * @param data
	 *            int数据
	 * @return 按小端顺序排列的字节数组
	 */
	private byte[] changeByte(int data) {
		byte b4 = (byte) ((data) >> 24);
		byte b3 = (byte) (((data) << 8) >> 24);
		byte b2 = (byte) (((data) << 16) >> 24);
		byte b1 = (byte) (((data) << 24) >> 24);
		byte[] bytes = { b1, b2, b3, b4 };
		return bytes;
	}

	private short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}

	private byte[] short2byte(short s) {
		byte[] size = new byte[2];
		size[1] = (byte) (s & 0xff);
		size[0] = (byte) ((s >> 8) & 0xff);
		return size;
	}

	/**
	 * 指纹得分比对,>=50分返回true：比对成功
	 *
	 * @return
	 */
	private boolean score(byte b1, byte b2) {
		byte[] temp = { b1, b2 };
		short score = 0;
		score |= (temp[0] & 0xff);
		score <<= 8;
		score |= (temp[1] & 0xff);
		return score >= 50;
	}

	private void sendCommand(byte[] command) {
		SerialPortManager.switchRFID = false;
		SerialPortManager.getInstance().write(command);
	}

	private void printlog(String tag, int length) {
		byte[] temp = new byte[length];
		System.arraycopy(buffer, 0, temp, 0, length);
		Log.i("whw", tag + "=" + DataUtils.toHexString(temp));
	}

	public class Result {
		/**
		 * 确认码
		 */
		public int code;
		/**
		 * 页码
		 */
		public int pageId;
		/**
		 * 得分
		 */
		public int matchScore;
	}

	/**
	 * 校准
	 */
	public synchronized int PSCalibration() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x2e, (byte) 0x00, (byte) 0x32 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 15000, 100);
		printlog("calibration", length);
		if (length == 12 && buffer[9] == 0x00) {
			return 0x00;
		}
		return NO_RESPONSE;
	}

}
