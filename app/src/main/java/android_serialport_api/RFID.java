
package android_serialport_api;

//import android.content.Context;

// Wrapper for native library

public class RFID {
	int mFd = 0;

	//mFd = open(path, baudrate);//device.getAbsolutePath()
	// JNI
	//public native int open(String path, int baudrate);
	//public native int write(int fd, byte[] buf, int count);
	//public native int read(int fd, byte[] buf, int count);
	//public native int close(int fd);
	static 
	{
			System.loadLibrary("RFID");
	}
	
	public RFID()
	{
	
	}
	
	public static native int InitClassName(byte[] className,byte[] MethodSend,byte[] MethodRecv);

	public static native int DeInitClassName();

	public static native int UartOpen();

	public static native int UartClose();
	
	public static native int InitMF(byte TransCode,char Authority,byte FileId);

	public static native int InitADF(byte FileId, char Authority,int NameLen,byte[] ADFName);

	public static native int InitBEF(char FileId,byte FileType,char Authority,char FileLen);

	public static native int InitREF(char FileId,byte FileType,char Authority,char FileLen);

	public static native int WriteKEY();

	public static native int ReadBinary(byte FileId,byte Offset,int Count,byte[] RecBuf);

	public static native int UpdateBinary(
		byte FileId,
		byte Offset,
		int level,
		byte[] SendBuf,
		int Count
    );

	public static native int ReadRecord(
		byte FileId,
		int level,
        int Index,
        byte[] RecBuf,
        int Count
    );

	public static native int AppendRecord(
		byte FileId,
        int level,
        byte[] SendBuf,
        int Count
    );

	public static native int UpdateRecord(
		byte FileId,
        int level,
        int Index,
        byte[] SendBuf,
        int Count
    );

	public static native int SelectFile(
        int FileType,
        int SFDatalen,
        byte[] FileId
    );

	public static native int authentication(
		byte KeyID,
		byte[] jarray,byte[] Keycode,int KeyLen
	);
	
	public static native int GetChallenge(
        int Count,
        byte[] RecBuf
    );

	public static native int GetResponse(
        int Count,
        byte[] RecBuf
    );

	public static native int DeleteFile();

//	public static native int SystemReset(byte[] RecBuf);

//	public static native int ResetCard(byte[] RecBuf);

	public static native int setReaderMode(byte[] RecBuf);

	public static native int setProtocolMode(byte[] RecBuf);

	public static native int setCheckMode(byte[] RecBuf);

	public static native int SearchCard(byte[] RecBuf);

	public static native int Anticoll(byte[] RecBuf);

	public static native int SelectCard(int Count,byte[] SendBuf);

	public static native int ResetCard(byte[] RecBuf);

	public static native int WriteKEY(byte KEY_InMode,byte KEY_Opt,byte KEY_ID,byte[] Key_MsgData,int Key_Msglen);
}

