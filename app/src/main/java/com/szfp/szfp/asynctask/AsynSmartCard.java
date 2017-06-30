package com.szfp.szfp.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android_serialport_api.SmartCardAPI;

public class AsynSmartCard extends Handler {

	public static final  int SCARD_LOAN		=	0x0001;	/*!< 贷款应用 */
	public static final  int SCARD_DEBIT		=	0x0002;	/*!< 借记应用*/
	public static final  int SCARD_QUASI_CREDIT		=	0x0003;	/*!< 准贷应用 */
	public static final  int SCARD_ELECTRONIC_CASH		=	0x0004;	/*!< 电子现金应用 */
	
	public static final int ASYNSMARTCARD_INITCARD = 100;
	public static final int ASYNSMARTCARD_EXTERNALAUTH = 101;
	public static final int ASYNSMARTCARD_READBINARY = 102;
	public static final int ASYNSMARTCARD_READRECORD = 103;
	public static final int ASYNSMARTCARD_WRITEBINARY = 104;
	public static final int ASYNSMARTCARD_WRITERECORD = 105;
	public static final int ASYNSMARTCARD_CREATECARD = 106;
	public static final int ASYNSMARTCARD_DELETECARD = 107;
	public static final int ASYNSMARTCARD_READIBAN = 108;
	public static final int ASYNSMARTCARD_OPEN = 109;
	public static final int ASYNSMARTCARD_CLOSE = 110;


	private SmartCardAPI SmartCard = null;
	private Handler mWorkerThreadHandler;

	public AsynSmartCard(Looper looper) {
		mWorkerThreadHandler = createHandler(looper);
		SmartCard = new SmartCardAPI();
	}

	protected Handler createHandler(Looper looper) {
		return new WorkerHandler(looper);
	}

	protected class WorkerHandler extends Handler {
		public WorkerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ASYNSMARTCARD_INITCARD:
				Init_Card();
				break;
			case ASYNSMARTCARD_EXTERNALAUTH:
				External_Authentication();
				break;
			case ASYNSMARTCARD_READBINARY:
				Read_Basic_Information((int) msg.arg1);
				break;
			case ASYNSMARTCARD_READRECORD:
				Read_Violate_Record((int) msg.arg1);
				break;
			case ASYNSMARTCARD_WRITEBINARY:
				Update_Basic_Information((byte[])msg.obj, msg.arg1);
				break;
			case ASYNSMARTCARD_WRITERECORD:
				Update_Violate_Record((byte[])msg.obj, msg.arg1);
				break;
			case ASYNSMARTCARD_CREATECARD:
				Create_SmartFile();
				break;
			case ASYNSMARTCARD_DELETECARD:
				Delete_SmartFile();
				break;
			case ASYNSMARTCARD_READIBAN:
				ReadIBANCode((int) msg.arg1);
				break;
			case ASYNSMARTCARD_CLOSE:
				SmartCard.close();
				break;
			default:
				break;
			}
		}
	}
    
    public void Read_Violate_Record(int Count)
	{
    	int ret = 0;
    	byte[] readRecord = new byte[1024];
    	if(onReadRecordListener == null){
			return;
		}
    	ret = SmartCard.Read_Record(readRecord,Count);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			onReadRecordListener.OnReadRecordSuccess(readRecord);
		}
		else
		{
			onReadRecordListener.OnReadRecordFail(ret);
		}
		
	}
    
	public void Update_Violate_Record(byte[] ReqBuf, int Count)
	{
		int ret = 0;
		if(onWriteRecordListener == null){
			return;
		}
		if(Count>0x0B)
			Count = 0x0B;
		ret = SmartCard.Append_Record(ReqBuf, Count);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			onWriteRecordListener.OnWriteRecordSuccess();
		}
		else
		{
			onWriteRecordListener.OnWriteRecordFail(ret);
		}
	}
	
	public void Read_Basic_Information(int Count)
	{
		int ret = 0;
		byte[] readBinary = new byte[1024];
		if(onReadBinaryListener == null){
			return;
		}
		ret = SmartCard.Read_Binary(readBinary, Count);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			onReadBinaryListener.OnReadBinarySuccess(readBinary);
		}
		else
		{
			onReadBinaryListener.OnReadBinaryFail(ret);
		}
	
	}
	
	public void Update_Basic_Information(byte[] ReqBuf, int Count)
	{
		int ret = 0;
		if(onWriteBinaryListener == null){
			return;
		}

		if(Count>0x31)
			Count = 0x31;
		ret = SmartCard.Update_Binary(ReqBuf, Count);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			onWriteBinaryListener.OnWriteBinarySuccess();
		}
		else
		{
			onWriteBinaryListener.OnWriteBinaryFail(ret);
		}
	}
	
	public void Init_Card()
	{
		Log.i("whw","Init_Card");
		int ret = 0;
		byte[] pRecBuf = new byte[271];
		if(onInitCardListener == null){
			return;
		}

		ret = SmartCard.IC_System_Reset(pRecBuf);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			onInitCardListener.OnInitCardSuccess(pRecBuf);
			byte[] pResetBuf = new byte[271];
			ret = SmartCard.IC_ResetCard(pResetBuf);
			if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
			{
				onInitCardListener.OnInitCardSuccess(pResetBuf);
			}
			else
			{
				onInitCardListener.OnInitCardFail(ret);
			}
		}
		else
		{
			onInitCardListener.OnInitCardFail(ret);
		}
	}
	
	public void External_Authentication()
	{
		int ret = 0;
		int Count = 8;
		byte[] encrypt = new byte[8];
		byte[] keycode = {0x57,0x41,0x54,0x43,0x48,0x44,0x41,0x54,0x41,0x54,0x69,0x6D,0x65,0x43,0x4F,0x53};
		if(onExternalAuthListener == null){
			return;
		}
		ret = SmartCard.Get_Challenge(Count,encrypt);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			onExternalAuthListener.OnExternalAuthSuccess();
		}
		else
		{
			onExternalAuthListener.OnExternalAuthFail(ret);
		}
	}
	
	public void Create_SmartFile()
	{
		int ret = 0;
		char BEFLen = (0x00<<8)|0x3E;
		byte item = 0x0A;
		byte len = 0x0B;
		if(onCreateCardListener == null){
			return;
		}
		ret = SmartCard.InitMF();
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			ret = SmartCard.InitADF();
			if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
			{
				ret = SmartCard.InitBEF(BEFLen);
				if(ret != SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
				{
					onCreateCardListener.OnCreateCardFail(ret);
				}
				else
				{
					ret = SmartCard.InitREF(item,len);
					if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
					{
						onCreateCardListener.OnCreateCardSuccess();
					}
					else
					{
						onCreateCardListener.OnCreateCardFail(ret);
					}
				}
				
			}
			else
			{
				onCreateCardListener.OnCreateCardFail(ret);
			}
		}
		else
		{
			onCreateCardListener.OnCreateCardFail(ret);
		}
	}
	
	public void Delete_SmartFile()
	{
		int ret = 0;
		if(onDeleteCardListener == null){
			return;
		}
		ret = SmartCard.IC_DeleteFile();
		Log.i("whw", "Delete_SmartFile ret="+ret);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{
			onDeleteCardListener.OnDeleteCardSuccess();
		}
		else
		{
			onDeleteCardListener.OnDeleteCardFail(ret);
		}
	}
	
	public void ReadIBANCode(int PBOC_AID)
	{
		int ret = 0;
		byte[] IBANCode = new byte[8];

		if(onReadIBANListener == null){
			return;
		}
		
		ret = SmartCard.IC_Select_IBANFile(PBOC_AID);
		if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
		{

			ret = SmartCard.IC_ReadIBAN(IBANCode, IBANCode.length);
			if(ret == SmartCard.SCARD_COMMAND_EXECUTED_CORRECTLY)
			{
				onReadIBANListener.OnReadIBANSuccess(IBANCode);
			}
			else
			{
				onReadIBANListener.OnReadIBANFail(ret);
			}
		}
		else
		{
			onReadIBANListener.OnReadIBANFail(ret);
		}
	}
	
	private OnInitCardListener onInitCardListener;
	private OnExternalAuthListener onExternalAuthListener;
	private OnReadBinaryListener onReadBinaryListener;
	private OnReadRecordListener onReadRecordListener;
	private OnWriteBinaryListener onWriteBinaryListener;
	private OnWriteRecordListener onWriteRecordListener;
	private OnCreateCardListener onCreateCardListener;
	private OnDeleteCardListener onDeleteCardListener;
	private OnReadIBANListener onReadIBANListener;

	public void setOnInitCardListener(OnInitCardListener onInitCardListener) {
		this.onInitCardListener = onInitCardListener;
	}
 
	public void setOnExternalAuthListener(OnExternalAuthListener onExternalAuthListener) {
		this.onExternalAuthListener = onExternalAuthListener;
	}
	
	public void setOnReadBinaryListener(OnReadBinaryListener onReadBinaryListener) {
		this.onReadBinaryListener = onReadBinaryListener;
	}
	
	public void setOnReadRecordListener(OnReadRecordListener onReadRecordListener) {
		this.onReadRecordListener = onReadRecordListener;
	}
	
	public void setOnWriteBinaryListener(OnWriteBinaryListener onWriteBinaryListener) {
		this.onWriteBinaryListener = onWriteBinaryListener;
	}
	
	public void setOnWriteRecordListener(OnWriteRecordListener onWriteRecordListener) {
		this.onWriteRecordListener = onWriteRecordListener;
	}
	
	public void setOnCreateCardListener(OnCreateCardListener onCreateCardListener) {
		this.onCreateCardListener = onCreateCardListener;
	}
	
	public void setOnDeleteCardListener(OnDeleteCardListener onDeleteCardListener) {
		this.onDeleteCardListener = onDeleteCardListener;
	}
	
	public void setOnReadIBANListener(OnReadIBANListener onReadIBANListener) {
		this.onReadIBANListener = onReadIBANListener;
	}

	public interface OnInitCardListener {
		public void OnInitCardSuccess(byte[] data);

		public void OnInitCardFail(int code);
	}

	public interface OnExternalAuthListener {
		public void OnExternalAuthSuccess();

		public void OnExternalAuthFail(int code);
	}

	public interface OnReadBinaryListener {
		public void OnReadBinarySuccess(byte[] data);

		public void OnReadBinaryFail(int code);
	}

	public interface OnReadRecordListener {
		public void OnReadRecordSuccess(byte[] data);

		public void OnReadRecordFail(int code);
	}
	
	public interface OnWriteBinaryListener {
		public void OnWriteBinarySuccess();

		public void OnWriteBinaryFail(int code);
	}

	public interface OnWriteRecordListener {
		public void OnWriteRecordSuccess();

		public void OnWriteRecordFail(int code);
	}

	public interface OnCreateCardListener {
		public void OnCreateCardSuccess();

		public void OnCreateCardFail(int code);
	}

	public interface OnDeleteCardListener {
		public void OnDeleteCardSuccess();

		public void OnDeleteCardFail(int code);
	}
	
	public interface OnReadIBANListener {
		public void OnReadIBANSuccess(byte[] data);

		public void OnReadIBANFail(int code);
	}
	
	public void initcard(){
		System.out.println("initcard initcard 111");
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_INITCARD).sendToTarget();
	}
	
	public void externalauth(){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_EXTERNALAUTH).sendToTarget();
	}
	
	public void readbinary(int count){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_READBINARY,count,0,null).sendToTarget();
	}
	
	public void readrecord(int count){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_READRECORD,count,0,null).sendToTarget();
	}
	
	public void writebinary(byte[] data,int count){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_WRITEBINARY,count,0,data).sendToTarget();
	}
	
	public void writerecord(byte[] data,int count){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_WRITERECORD,count,0,data).sendToTarget();
	}
	
	public void createcard(){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_CREATECARD).sendToTarget();
	}
	
	public void deletecard(){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_DELETECARD).sendToTarget();
	}
	
	public void ReadIBAN(int PBOC_AID){
		mWorkerThreadHandler.obtainMessage(ASYNSMARTCARD_READIBAN,PBOC_AID,0,null).sendToTarget();
	}
	
	public void close(){
//		mWorkerThreadHandler.sendEmptyMessage(ASYNSMARTCARD_CLOSE);
		SmartCard.close();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
	}

}
