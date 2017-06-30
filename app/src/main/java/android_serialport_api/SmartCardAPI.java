package android_serialport_api;

import android.util.Log;

import com.szfp.szfplib.utils.DataUtils;

import java.io.UnsupportedEncodingException;


// Wrapper for native library

public class SmartCardAPI {

	public final  int SCARD_LOAN		=	0x0001;	/*!< 贷款应用 */
	public final  int SCARD_DEBIT		=	0x0002;	/*!< 借记应用*/
	public final  int SCARD_QUASI_CREDIT		=	0x0003;	/*!< 准贷应用 */
	public final  int SCARD_ELECTRONIC_CASH		=	0x0004;	/*!< 电子现金应用 */

	public final  int SCARD_UNKNOWN		=	0x0001;	/*!< Unknown state */
	public final int SCARD_UART_NOT_CONNECTED	=		0x0002;	/*!< UART Not connected   */
	public final int SCARD_ABSENT	=		0x0003;	/*!< Card is absent */
	//smartCOS Create_File
	public final int SCARD_COMMAND_EXECUTED_CORRECTLY	=		0x0004;	/*!< Command executed correctly  */
	public final int SCARD_WRITE_EEPROM_FAIL	=		0x0005;	/*!< Write EEPROM failure   */
	public final int SCARD_DATA_LENGTH_ERROR	=		0x0006;	/*!< Data length error    */
	public final int SCARD_ALLOW_CODE_TRANSFER_ERROR_COUNT	=		0x0007;	/*!< Allow the code transfer error count    */
	public final int SCARD_CREATE_CONDITION_NOT_SATISFIED	=		0x0008;	/*!< Create condition not satisfied     */

	public final int SCARD_SECURITY_CONDITION_NOT_SATISFIED		=	0x0007;	/*!< Security condition is not satisfied     */
	public final int SCARD_IDENTIFIER_ALREADY_EXISTS	=		0x0008;	/*!< Identifier already exists      */
	public final int SCARD_FUNCTION_NOT_SUPPORTED	=		0x0009;	/*!< Function not supported       */
	public final int SCARD_FILE_NOT_FOUND	=		0x0011;	/*!< File not found        */
	public final int SCARD_NOT_ENOUGH_SPACE		=	0x0012;	/*!< Not enough space */
	public final int SCARD_PAREMETER_IS_INCORRECT	=		0x0013;	/*!< The parameter is incorrect         */
	public final int SCARD_INS_IS_INCORRECT		=	0x0014;	/*!< The INS is incorrect         */
	public final int SCARD_CLA_IS_INCORRECT		=	0x0015;	/*!< The CLA is incorrect         */

	//Write_KEY
	public final int SCARD_CMD_NOT_MATCH_TYPES	=		0x0016;	/*!< Command file types do not match        */
	public final int SCARD_KEY_LOCK		=	0x0017;	/*!< Key lock         */
	public final int SCARD_GET_RANDOM_INVALID	=		0x0018;	/*!< From a random number is invalid         */
	public final int SCARD_CONDITION_OF_USE_NOT_SATISFIED	=		0x0019;	/*!< Conditions of use does not satisfied          */
	public final int SCARD_MAC_INCORRECT	=		0x0020;	/*!< MAC is incorrect          */
	public final int SCARD_DATA_NOT_CORRECT		=	0x0021;	/*!< Data domain is not correct           */
	public final int SCARD_CARD_LOCK	=		0x0022;	/*!< Card lock            */
	public final int SCARD_FILE_SPACE_INSUFFICIENT		=	0x0023;	/*!< File space is insufficient              */
	public final int SCARD_P1_AND_P2_NOT_CORRECT	=		0x0024;	/*!< P1 and P2 not correct            */
	public final int SCARD_APP_PERMANENT_LOCK		=	0x0025;	/*!< Application  permanent lock             */
	public final int SCARD_KEY_NOT_FOUND	=		0x0026;	/*!< KEY is not found             */

	public final int SCARD_NOT_BINARY_FILE		=	0x0027;	/*!< not binary file           */
	public final int SCARD_CONDITION_OF_READ_NOT_SATISFIED	=		0x0028;	/*!< the conditions of read does not satisfied           */
	public final int SCARD_CONDITION_OF_CMD_NOT_SATISFIED	=		0x0029;	/*!< the condition of command execution does not satisfied           */
	public final int SCARD_RECORD_NOT_FOUND		=	0x0030;	/*!< record not found        */
	public final int SCARD_NO_DATA_RETURN		=	0x0031;	/*!< Card no data can be returned       */

	public final int SCARD_SECURITY_DATA_NOT_CORRECT	=		0x0032;	/*!< Security message data item is not correct        */
	public final int SCARD_P1_AND_P2_OUT_OF_GAUGE		=	0x0033;	/*!< P1 and P2 are out of gauge            */
	public final int SCARD_FILE_NOT_LINEAR_FIXED_FILE	=		0x0034;	/*!< The file is not a linear fixed length file             */
	public final int SCARD_APP_TEMPORARY_LOCED	=		0x0035;	/*!< APP Temporary locked           */
	public final int SCARD_FILE_STORAGE_SPACE_NOT_ENOUGH	=		0x0036;	/*!< File storage space is not enough          */
	public final int SCARD_NOT_EXTERNAL_AUTHENTICATION_KEY	=	0x0037;	/*!< Instead of external authentication key           */
	public final int SCARD_Key_CONDITION_NOT_SATISFIED 		=	0x0038;	/*!< Key using the condition is not satisfied          */
	public final int SCARD_AUTHENTICATION_METHOD_LOCKED 	=		0x0039;	/*!< Authentication method locked         */
	public final int SCARD_KEY_FILE_NOT_FOUND 	=		0x0040;	/*!< Key File not found           */
	public final int SCARD_SAFETY_INFORMATION_NOT_CORRECT 	=		0x0041;	/*!< Safety information is not correct         */
	public final int  SCARD_MALLOC_FAILURE 	=		0x0042;	/*!< Malloc Failure         */
	//mFd = open(path, baudrate);//device.getAbsolutePath()
	// JNI
	//public native int open(String path, int baudrate);
	//public native int write(int fd, byte[] buf, int count);
	//public native int read(int fd, byte[] buf, int count);
	//public native int close(int fd);

	//public Serialport(Context context) {
	//	final int mFd = open(path, baudrate);//device.getAbsolutePath()
	// }
	//mFd = open(path, baudrate);//device.getAbsolutePath()
	private SmartCard smartCard = null;
	private static final byte[] OPEN_COMMAND = "D&C0004010I".getBytes();
	private static final byte[] CLOSE_COMMAND = "D&C0004010J".getBytes();
	//private static BluetoothChatService mChatService = null;
	private static SerialPortManager communicateManager = null;
	//private Lock Mutex = null;

	public SmartCardAPI() {
		//String className,String MethodSend,String MethodRecv
		this.communicateManager = SerialPortManager.getInstance();
		smartCard = new SmartCard();
		//smartCard = new SmartCard(className,MethodSend,MethodRecv);
		//smartSerialport.Set_Bluetooth_Channel();
		//this.Mutex = Mutex;
		String className = new String("android_serialport_api/SmartCardAPI");
		String MethodSend = new String("SerialportSend");
		String MethodRecv = new String("SerialportRecv");
		open();
		UninitSmartCard();
		InitSmartCard(className,MethodSend,MethodRecv);
	}

	public boolean open(){
		communicateManager.write(OPEN_COMMAND);
		byte[] buffer = new byte[1];
		int length = communicateManager.read(buffer, 3000, 100);
		if(length==1){
			return true;
		}
		return false;
	}

	public boolean close(){
		communicateManager.write(CLOSE_COMMAND);
		byte[] buffer = new byte[1];
		int length = communicateManager.read(buffer, 3000, 100);
		Log.i("whw", "close buffer[0]="+buffer[0]);
		if(length==1){
			return true;
		}
		return false;
	}

	public void InitSmartCard(String className,String MethodSend,String MethodRecv)
	{
		smartCard.InitClassName(className.getBytes(),MethodSend.getBytes(),MethodRecv.getBytes());
	}

	public void UninitSmartCard()
	{
		smartCard.DeInitClassName();
	}

	public int InitMF() {
		byte TransCode = (byte) 0xFF;
		byte Authority = 0x0F;
		byte FileId = 0x01;
		int NameLen = 14;
		int ret = 0;
		byte[] MFName = {0x31,0x50,0x41,0x59,0x2E,0x53,0x59,0x53,0x2E,0x44,0x44,0x46,0x30,0x31};
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret = smartCard.InitMF(TransCode, Authority, FileId, NameLen, MFName);
//		Mutex.unlock();
		return ret;
	}

	public int InitADF() {
		char FileId = (0x2F<<8)|0x01;
		//	char FileLen = (0x02<<8)|0xF4;
		byte Authority = 0x0F;
		int NameLen = 9;
		int ret = 0;
		byte[] ADFName = {(byte) 0xA0,0x00,0x00,0x00,0x03,(byte) 0x86,(byte) 0x98,0x07,0x01};
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.InitADF(FileId,Authority,NameLen,ADFName);
//		Mutex.unlock();
		return ret;
	}
	/*
        public int ExternAuth(byte[] encrypt,byte[] keycode) {
            byte KeyID = 0x00;
            int ret = 0;
            //byte[] encrypt = new byte[8];
            //int Count = 8;
            //byte[] keycode = {0x57,0x41,0x54,0x43,0x48,0x44,0x41,0x54,0x41,0x54,0x69,0x6D,0x65,0x43,0x4F,0x53};

            //if(smartSerialport.GetChallenge(Count,encrypt)==SCARD_COMMAND_EXECUTED_CORRECTLY)
            if(Mutex == null)
                return -1;
            Mutex.lock();
            ret =  smartCard.ExternAuth(KeyID,encrypt,keycode);
            Mutex.unlock();
            return ret;

        }
    */
	public int InitBEF(char Len) {
		char FileId = (0x00<<8)|0x16;
		byte FileType = 0x00;
		char Authority = (0x0F<<8)|0x0F;
		//char Len = (0x00<<8)|0x3E;
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.InitBEF(FileId,FileType,Authority,Len);
//		Mutex.unlock();
		return ret;
	}

	public int InitREF(byte item,byte Count) {
		char FileId = (0x00<<8)|0x18;
		byte FileType = 0x03;
		char Authority = (0x0F<<8)|0x0F;
		//char Len = (0x0A<<8)|0x0B;
		char Len = (char) ((item<<8)|Count);
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.InitREF(FileId, FileType, Authority, Len);
//		Mutex.unlock();
		return ret;
	}

	//public native int Write_KEY();
	public int Read_Binary(byte[] RecBuf,int Count) {
		byte FileId = 0x16;
		byte Offset = 0x00;
		int ret = 0;
		System.out.println("Read_Binary="+Count);
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.ReadBinary(FileId,Offset,Count,RecBuf);//FileId,Offset,Count,
//		Mutex.unlock();
		return ret;
	}

	public int Update_Binary(byte[] RecBuf,int Count) {
		byte FileId = 0x16;
		byte Offset = 0x00;
		int level = 0x00;
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.UpdateBinary(FileId,Offset,level,RecBuf,Count);
//		Mutex.unlock();
		return ret;
	}

	public int Read_Record(byte[] RecBuf,int Count) {
		byte FileId = 0x18;
		int Index = 0x00;
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.ReadRecord(FileId,Index,RecBuf,Count);
//		Mutex.unlock();
		return ret;
	}

	public int Append_Record(byte[] RecBuf,int Count) {
		byte FileId = 0x18;
		int level = 0;
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.AppendRecord(FileId,level,RecBuf,Count);
//		Mutex.unlock();
		return ret;
	}

	public int Update_Record(int Index,byte[] RecBuf,int Count) {
		byte FileId = 0x18;
		int level = 0;
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.UpdateRecord(FileId,level,Index,RecBuf,Count);
//		Mutex.unlock();
		return ret;
	}

	public int Select_BinaryFile() {
		int FileType = 0x02;
		int FileIndex = 0x00;
		int SFDatalen = 0x02;
		byte[] FileId = {0x00,0x16};
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.SelectFile(FileType,FileIndex,SFDatalen,FileId) ;
//		Mutex.unlock();
		return ret;
	}

	public int Select_RecordFile() {
		int FileType = 0x02;
		int FileIndex = 0x00;
		int SFDatalen = 0x02;
		byte[] FileId = {0x00,0x18};
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.SelectFile(FileType,FileIndex,SFDatalen,FileId)  ;
//		Mutex.unlock();
		return ret;
	}

	public int Get_Challenge(int Count,byte[] RecBuf) {
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.GetChallenge(Count,RecBuf);
//		Mutex.unlock();
		return ret;
	}

	public int Get_Response(byte[] RecBuf,int Count) {
		int ret = 0;
//		if(Mutex == null)
//			return -1;
//		Mutex.lock();
		ret =  smartCard.GetResponse(Count,RecBuf);
//		Mutex.unlock();
		return ret;
	}

	public int IC_System_Reset(byte[] pRecBuf) {
		int ret = 0;
		return ret;
	}

	public int IC_ResetCard(byte[] pRecBuf) {
		int ret = 0;
		return ret;
	}

	public int IC_DeleteFile() {
		int ret = 0;
		ret =  smartCard.DeleteFile();
		//System.out.println("IC_DeleteFile=end ");
		return ret;
	}

	public int IC_Select_IBANFile(int PBOC_AID) {
		int FileType = 0x04;
		int FileIndex = 0x00;
		int SFDatalen = 0x08;

		byte[] LoanId = {(byte) 0xA0,0x00,0x00,0x03,0x33,0x01,0x01,0x01};
		byte[] DebitId = {(byte) 0xA0,0x00,0x00,0x03,0x33,0x01,0x01,0x02};
		byte[] QuasicreditId = {(byte) 0xA0,0x00,0x00,0x03,0x33,0x01,0x01,0x03};
		byte[] ElectroniccashId = {(byte) 0xA0,0x00,0x00,0x03,0x33,0x01,0x01,0x06};

		if(SCARD_LOAN==PBOC_AID)
		{
			return smartCard.SelectFile(FileType,FileIndex,SFDatalen,LoanId) ;
		}
		else if(SCARD_DEBIT==PBOC_AID)
		{
			return smartCard.SelectFile(FileType,FileIndex,SFDatalen,DebitId) ;
		}
		else if(SCARD_QUASI_CREDIT==PBOC_AID)
		{
			return smartCard.SelectFile(FileType,FileIndex,SFDatalen,QuasicreditId) ;
		}
		else if(SCARD_ELECTRONIC_CASH==PBOC_AID)
		{
			return smartCard.SelectFile(FileType,FileIndex,SFDatalen,ElectroniccashId) ;
		}
		return 0;

	}

	public int IC_ReadIBAN(byte[] IBANCode,int Count)
	{
		int ret = 0;

		ret =  smartCard.ReadIBAN(Count,IBANCode);
		return ret;
	}

	private static byte[] mSendBuffer = new byte[128];
	// 协议指令
	private static final int PORTER_PROTOCOL_SD0 = 0xCA; // Start delimiters
	private static final int PORTER_PROTOCOL_SD1 = 0xDF;
	private static final int PORTER_PROTOCOL_DATA = 0x35; // data type indicator
	// 常规数据
	private static final int PORTER_PROTOCOL_CMD = 0x36; // cmd type indicator
	// 回调指令
	private static final int PORTER_PROTOCOL_MTU = 0x80; // 一次最大发送数据流128个字节
	private static final byte[] PORTER_PROTOCOL_ED = new byte[]{(byte) 0xE3}; // End delimiter
	private static final byte[] PRIFIX = new byte[]{(byte) PORTER_PROTOCOL_SD0,(byte) PORTER_PROTOCOL_SD1,0x00,PORTER_PROTOCOL_DATA};
	public static int SerialportSend(byte[] send) {
		int len = send.length;
		int segment = 0, remain = 0;
		segment = len / PORTER_PROTOCOL_MTU;
		remain = len % PORTER_PROTOCOL_MTU;

		if (len == 0) {
			return 0;
		}
		int i = 0;
		// 分段处理
		for (i = 0; i < segment; i++) {
			communicateManager.write(PRIFIX);
			communicateManager.write(new byte[]{(byte) PORTER_PROTOCOL_MTU});
			System.arraycopy(send, i * PORTER_PROTOCOL_MTU, mSendBuffer, 0,
					PORTER_PROTOCOL_MTU);
			communicateManager.write(mSendBuffer);
			communicateManager.write(PORTER_PROTOCOL_ED);
			Log.i("whw", "send commnad start =" +
					DataUtils.toHexString(mSendBuffer));
		}
		if (remain > 0) {
			communicateManager.write(PRIFIX);
			communicateManager.write(new byte[]{(byte) remain});
			byte[] tmpBytes = new byte[remain];
			System.arraycopy(send, segment * PORTER_PROTOCOL_MTU, tmpBytes,
					0, remain);
			communicateManager.write(tmpBytes);
			communicateManager.write(PORTER_PROTOCOL_ED);
			Log.i("whw", "send commnad end xxxx=" +
					DataUtils.toHexString(send));
		}
		return 1;
	}

	public static int SerialportRecv(byte[] Recv,int len) {
		System.out.println("SerialportRecv start=");
		int bytes = communicateManager.read(Recv,3000, 1000);
		System.out.println("SerialportRecv@@@@@@@@@@@@@@@@@@");
		if(bytes > len)
			bytes = len;
		System.out.println("SerialportRecv bytes1="+DataUtils.toHexString(Recv));
		if(bytes <=0)
		{
			String rec = "......No respose from STM32";
			byte[] tempRecv = null;
			try {
				tempRecv = rec.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("from A602 bytes="+bytes);
			System.arraycopy(tempRecv, 0, Recv, 0, tempRecv.length);
			bytes = tempRecv.length;
		}
		System.out.println("SerialportRecv bytes2="+bytes);
		return bytes;
		//}
	}


}
