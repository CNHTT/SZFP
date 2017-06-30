package android_serialport_api;

/**
 * cpu卡测试执行步骤如下：
 * 第1步 GetChallenge 获取4字节随机数
 * 第2步 authentication 4字节随机数+4字节0 密钥是8个字节0xFF，进行外部认证
 * 第3步 DeleteFile 删除MF目录下所有文件
 * 第4步 InitMF 建立MF目录   //跳过 目前无法创建
 * 第5步 InitADF 建立DF目录 //跳过 暂时未测试
 * 第6步 InitKEF 建立密钥文件 //建立MF目录下密钥文件
 * 第7步 appendKEY 添加密钥 //目前只需要添加外部认证密钥 为了方便记忆，暂时取8个字节0xFF
 * 第8步 InitBEF 建立二进制文件 //
 * 第9步 Select_BinaryFile 选择二进制文件 //选择文件暂时跳过 不影响读写
 * 第10步 UpdateBinary 写二进制文件
 * 第11步 ReadBinary 读取二进制文件  长度设为0 即表示读取整条记录，目前不支持任意长度读取
 * 第12步 InitREF 建立记录文件 目前只做了建立循环定长记录文件
 * 第13步 AppendRecord 增加循环定长记录文件  长度必须跟已建立的文件中所规定的长度一致
 * 第14步 ReadRecord 读取记录文件   长度设为0 即表示读取整条记录，目前不支持任意长度读取
 */
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.Lock;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

// Wrapper for native library

public class RFIDAPI {

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

    public final int PROCLAIMED = 0;//明文
    public final int CIPHERTEXT = 1;//密文
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
    private RFID rfid = null;

    //private static BluetoothChatService mChatService = null;
    //private Lock Mutex = null;

    private static final byte[] SWITCH_COMMAND = "D&C00040104".getBytes();

    public RFIDAPI() {
        //String className,String MethodSend,String MethodRecv
        rfid = new RFID();
        String className = new String("android_serialport_api/RFIDAPI");
        String MethodSend = new String("BluetoothSend");
        String MethodRecv = new String("BluetoothRecv");
        UninitRFID();
        InitRFID(className,MethodSend,MethodRecv);
    }

    public static void switchStatus() {
        SerialPortManager.getInstance().write(SWITCH_COMMAND);
        SystemClock.sleep(100);
    }

    public void InitRFID(String className,String MethodSend,String MethodRecv)
    {
        rfid.InitClassName(className.getBytes(),MethodSend.getBytes(),MethodRecv.getBytes());
    }

    public void UninitRFID()
    {
        rfid.DeInitClassName();
    }

    //创建MF文件
    public int InitMF()
    {
        byte TransCode = (byte) 0xFF;
        char Authority = 0xF0F0;
        byte FileId = 0x01;

        return rfid.InitMF(TransCode, Authority, FileId);
    }
    //创建DF文件
    public int InitADF()
    {
        byte FileId = (byte) 0x95;
        char Authority = 0xF0F0;
        int NameLen = 9;
        byte[] ADFName = {(byte) 0xA0,0x00,0x00,0x00,0x03,(byte) 0x86,(byte) 0x98,0x07,0x01};

        return  rfid.InitADF(FileId,Authority,NameLen,ADFName);
    }
    //创建MF文件下密钥文件
    public int InitMKEF()
    {
        char FileId = (0x00<<8)|0x00;
        byte FileType = (byte) 0x3F;
        char Authority = 0x01F0;
        char FileLen = 0x00B0;

        return rfid.InitBEF(FileId, FileType, Authority, FileLen);
    }
    //创建二进制文件
    public int InitBEF()
    {
        char FileId = (0x00<<8)|0x16;
        byte FileType = (byte) 0xA8;
        char Authority = 0xF0F0;
        char FileLen = 0x0027;

        return rfid.InitBEF(FileId, FileType, Authority, FileLen);
    }
    //创建循环记录文件
    public int InitREF()
    {
        char FileId = (0x00<<8)|0x18;
        byte FileType = 0x2E;
        char Authority = 0xF0F0;
        char FileLen = 0x0A17;//表示创建0x0A条记录，每一条记录的长度是0x17

        return rfid.InitREF(FileId,FileType,Authority,FileLen);
    }
//读二进制文件
    /**
     *
     * @param RecBuf
     * @param Count 长度不要大于已建立的二进制文件的长度， 暂时定为0x27
     * @return 返回0x04表示正确
     */
    public int ReadBinary(byte[] RecBuf,int Count)
    {
        byte FileId = (0x00<<8)|0x16;
        byte Offset = 0x00;
        return rfid.ReadBinary(FileId, Offset, Count, RecBuf);
    }
    //更新二进制文件
    public int UpdateBinary(byte[] SendBuf,int Count)
    {
        byte FileId = (0x00<<8)|0x16;
        byte Offset = 0x00;
        int level = 0x00;
        return rfid.UpdateBinary(FileId,Offset,level,SendBuf,Count);
    }
    //读循环记录文件
    public int ReadRecord(byte[] RecBuf,int Count)
    {
        byte FileId = (0x00<<8)|0x18;
        int Index = 0x01;
        int level = 0x00;

        return rfid.ReadRecord(FileId, level, Index, RecBuf, 0);
    }
//增加循环定长记录文件
    /**
     *
     * @param SendBuf
     * @param Count  长度最大不能超过已建立文件规定的单条记录的长度 暂时为0x17
     * @return 返回0x04表示正确
     */
    public int AppendRecord(
            byte[] SendBuf,
            int Count
    )
    {
        byte FileId = (0x00<<8)|0x18;
        int level = 0x00;
        byte[] tempBuf = new byte[0x17];

        if(Count>tempBuf.length)
            Count = tempBuf.length;

        System.arraycopy(SendBuf, 0, tempBuf, 0, Count);

        return rfid.AppendRecord(FileId,level,tempBuf,tempBuf.length);
    }
//更新循环记录文件
    /**
     *
     * @param SendBuf
     * @param Count 长度最大不能超过已建立文件规定的单条记录的长度 暂时为0x17
     * @return 返回0x04表示正确
     */
    public int UpdateRecord(byte[] SendBuf,int Count)
    {
        byte FileId = (0x00<<8)|0x18;
        int level = 0x00;
        int Index = 0x00;//index 为记录号，若该文件有N条记录，则记录号可以是1-N。

        byte[] tempBuf = new byte[0x17];

        if(Count>tempBuf.length)
            Count = tempBuf.length;

        System.arraycopy(SendBuf, 0, tempBuf, 0, Count);

        return rfid.UpdateRecord(FileId, level, Index, tempBuf, tempBuf.length);
    }
//选择二进制文件
    /**
     * 当目录下存在多个二进制文件时，需要选择对应的二进制文件，然后才能进行读写
     * @return 返回0x04表示正确
     */
    public int Select_BinaryFile() {
        int FileType = 0x02;
        //int FileIndex = 0x00;
        int Idlen = 0x02;
        byte[] FileId = {0x00,0x16};

        return rfid.SelectFile(FileType, Idlen, FileId);
    }
//选择循环记录文件
    /**
     * 当目录下存在多个循环记录文件时，需要选择对应的记录文件，然后才能进行读写
     * @return 返回0x04表示正确
     */
    public int Select_RecordFile() {
        int FileType = 0x02;
        //int FileIndex = 0x00;
        int Idlen = 0x02;
        byte[] FileId = {0x00,0x18};

        return rfid.SelectFile(FileType,Idlen,FileId);
    }

    //选择MF目录
    /**
     * 选择主目录，由于只有一个主目录，可不用选择
     * @return 返回0x04表示正确
     */
    public int Select_MasterFile() {
        int FileType = 0x00;
        //int FileIndex = 0x00;
        int Idlen = 0x00;
        byte[] FileId = {0x00,0x00};

        return rfid.SelectFile(FileType,Idlen,FileId);
    }

    //选择DF文件
    /**
     * 当需要在已创建的DF目录下操作时需要先选择对应DF目录
     * @return 返回0x04表示正确
     */
    public int Select_DedicatedFile() {
        int FileType = 0x04;
        //int FileIndex = 0x00;
        int Idlen = 0x09;
        //char FileId = (0x3F<<8)|0x00;
        byte[] FileId = {(byte) 0xA0,0x00,0x00,0x00,0x03,(byte) 0x86,(byte) 0x98,0x07,0x01};
        return rfid.SelectFile(FileType,Idlen,FileId);
    }

    //获取随机数
    public int GetChallenge(int Count,byte[] RecBuf)
    {
        return rfid.GetChallenge(Count, RecBuf);
    }
    //外部认证
    /**
     * 4字节随机数加上4字节0x00与8字节密钥对应，或者8字节随机数与16字节密钥对应
     * @param random 随机数
     * @param Keycode  密钥
     * @param KeyLen  密钥长度
     * @return 返回0x04表示正确
     */
    public int authentication(byte[] random , byte[] Keycode,int KeyLen)
    {
        byte KeyID = 0x00;
        //byte[] Keycode = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
        //int KeyLen = 16;
        //byte[] Keycode = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
        //int KeyLen = 8;
        return rfid.authentication(KeyID, random,Keycode,KeyLen);
    }
//获取返回值
    /**
     * Count 长度
     * RecBuf 获取CPU卡的数据
     */
    public int GetResponse(
            int Count,
            byte[] RecBuf
    )
    {
        return rfid.GetResponse(Count, RecBuf);
    }
//删除文件
    /**
     * 删除MF主文件文件
     * @return 返回0x04表示正确
     */
    public int DeleteFile()
    {
        return rfid.DeleteFile();
    }
    //1．	配置读卡器模式
    public int setReaderMode(byte[] RecBuf)
    {
        return rfid.setReaderMode(RecBuf);
    }
    //根据卡片类型配置读卡协议模式
    public int setProtocolMode(byte[] RecBuf)
    {
        return rfid.setProtocolMode(RecBuf);
    }
    //设置读卡器的校验码方式 自动接收超时判别
    public int setCheckMode(byte[] RecBuf)
    {
        return rfid.setCheckMode(RecBuf);
    }
    //寻卡
    public int SearchCard(byte[] RecBuf)
    {
        return rfid.SearchCard(RecBuf);
    }
    //碰撞选卡
    public int Anticoll(byte[] RecBuf)
    {
        return rfid.Anticoll(RecBuf);
    }
//选卡
    /**
     *
     * @param Count 卡号默认4位，加1位校验位
     * @param SendBuf 输入的卡号
     * @return 返回0x04表示正确
     */
    public int SelectCard(int Count,byte[] SendBuf)
    {
        return rfid.SelectCard(Count,SendBuf);
    }
//复位
    /**
     *
     * @param RecBuf 复位返回的内容
     * @return 返回0x04表示正确
     */
    public int ResetCard(byte[] RecBuf)
    {
        return rfid.ResetCard(RecBuf);
    }
    //添加密钥
    /**
     *
     * @param Keycode 8字节或10字节密钥
     * @param KeyLen 密钥长度
     * @return 返回0x04表示正确
     */
    public int appendKEY(byte[] Keycode,int KeyLen)
    {
        byte KEY_InMode = PROCLAIMED;
        byte KEY_Opt = 0x01;
        byte KEY_ID = 0x00;
        int Key_Msglen = 5+KeyLen;
        byte[] Key_MsgData = new byte[Key_Msglen] ;
        Key_MsgData[0] = 0x39;
        Key_MsgData[1] = (byte)0xF0;
        Key_MsgData[2] = (byte)0xF0;
        Key_MsgData[3] = (byte)0xAA;
        Key_MsgData[4] = 0x55;
        System.arraycopy(Keycode, 0, Key_MsgData, 5, KeyLen);

        return rfid.WriteKEY(KEY_InMode,KEY_Opt,KEY_ID,Key_MsgData,Key_Msglen);
    }

    public static int BluetoothSend(byte[] send) {
        if (!SerialPortManager.switchRFID) {
            switchStatus();
        }
        SerialPortManager.getInstance().write(send);
        return 1;

    }

    public static int BluetoothRecv(byte[] Recv, int bytes) {
        bytes = SerialPortManager.getInstance().read(Recv, 3000, 1000);
        if(bytes <=0)
        {
            String rec = "No respose from A602";
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
        return bytes;
    }
}