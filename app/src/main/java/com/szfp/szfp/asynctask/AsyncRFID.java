package com.szfp.szfp.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android_serialport_api.RFIDAPI;

public class AsyncRFID extends Handler {

	private static final int INIT = 0;
	private static final int RELEASE = 100;
	private static final int GET_RANDOM_NUM = 1;
	private static final int AUTHENTICATION = 2;
	private static final int DELETE_FILE = 3;
	private static final int INIT_KEY = 4;
	private static final int APPEND_KEY = 5;
	private static final int INIT_BEF = 6;
	private static final int READ_BINARY_FILE = 7;
	private static final int WRITE_BINARY_FILE = 8;
	private static final int INIT_REF = 9;
	private static final int READ_RECORD_FILE = 10;
	private static final int WRITE_RECORD_FILE = 11;

	private static final int SWITCH = -9;



	private byte[] mBuffer = new byte[1024];

	private RFIDAPI rfidAPI;
	private Handler mWorkerThreadHandler;

	public AsyncRFID(Looper looper) {
		mWorkerThreadHandler = createHandler(looper);
		rfidAPI = new RFIDAPI();
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

			case INIT:
				for (int i = 0; i < 16; i++) {
					// Log.i("whw", "resetCard array="+mBuffer[i]);
					mBuffer[i] = 0x00;
				}
				int r = rfidAPI.setReaderMode(mBuffer);
				Log.i("whw", "////////////////// setReaderMode=" + r);

				for (int i = 0; i < 16; i++) {
					// Log.i("whw", "resetCard array="+mBuffer[i]);
					mBuffer[i] = 0x00;
				}
				int pm = rfidAPI.setProtocolMode(mBuffer);
				Log.i("whw", "//////////////////// setProtocolMode=" + pm);
				for (int i = 0; i < 16; i++) {
					// Log.i("whw", "resetCard array="+mBuffer[i]);
					mBuffer[i] = 0x00;
				}
				int cc = rfidAPI.setCheckMode(mBuffer);
				Log.i("whw", "//////////////////// setCheckMode=" + cc);
				for (int i = 0; i < 16; i++) {
					// Log.i("whw", "resetCard array="+mBuffer[i]);
					mBuffer[i] = 0x00;
				}
				AsyncRFID.this.obtainMessage(INIT, cc, -1)
				.sendToTarget();
				break;
			case RELEASE:
				release();
				break;
			case GET_RANDOM_NUM:
				int getChallenge = rfidAPI.GetChallenge(4, mBuffer);
				Log.i("whw", "///////////////////getChallenge=" + getChallenge);
				byte[] random = new byte[4];
				AsyncRFID.this.obtainMessage(GET_RANDOM_NUM, getChallenge, -1,
						random).sendToTarget();

				break;
			case AUTHENTICATION:
				mBuffer[4] = 0;
				mBuffer[5] = 0;
				mBuffer[6] = 0;
				mBuffer[7] = 0;
				byte[] Keycode = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF };
				int auth = rfidAPI.authentication(mBuffer, Keycode, 8);
				Log.i("whw", "///////////////////auth=" + auth);
				AsyncRFID.this.obtainMessage(AUTHENTICATION, auth, -1)
						.sendToTarget();

				break;
			case DELETE_FILE:
				int delete = rfidAPI.DeleteFile();
				Log.i("whw", "DELETE_FILE=" + delete);
				AsyncRFID.this.obtainMessage(DELETE_FILE, delete, -1)
						.sendToTarget();

				break;
			case INIT_KEY:
				int initkey = rfidAPI.InitMKEF();
				Log.i("whw", "initkey=" + initkey);
				AsyncRFID.this.obtainMessage(INIT_KEY, initkey, -1)
						.sendToTarget();

				break;
			case APPEND_KEY:
				byte[] Keycode1 = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF };
				int appendkey = rfidAPI.appendKEY(Keycode1, 8);
				Log.i("whw", "appendkey=" + appendkey);
				AsyncRFID.this.obtainMessage(APPEND_KEY, appendkey, -1)
						.sendToTarget();

				break;
			case INIT_BEF:
				int BEF1 = rfidAPI.InitBEF();
				Log.i("whw", "///////////////////InitBEF=" + BEF1);
				AsyncRFID.this.obtainMessage(INIT_BEF, BEF1, -1).sendToTarget();

				break;
			case READ_BINARY_FILE:
				int RB = rfidAPI.ReadBinary(mBuffer, 27);
				Log.i("whw", "//ReadBinary=" + RB + "   buffer[0]" + mBuffer[0]
						+ "   buffer[1]" + mBuffer[1] + "   buffer[2]"
						+ mBuffer[2] + "   buffer[3]" + mBuffer[3]);
				byte[] tempBinary = new byte[27];
				System.arraycopy(mBuffer, 0, tempBinary, 0, tempBinary.length);
				AsyncRFID.this.obtainMessage(READ_BINARY_FILE, RB, -1,
						tempBinary).sendToTarget();

				break;
			case WRITE_BINARY_FILE:
				byte[] binaryData = (byte[]) msg.obj;
				System.arraycopy(binaryData, 0, mBuffer, 0, binaryData.length);
				int UB = rfidAPI.UpdateBinary(mBuffer, binaryData.length);
				Log.i("whw", "///////////////////UpdateBinary=" + UB);
				AsyncRFID.this.obtainMessage(WRITE_BINARY_FILE, UB, -1)
						.sendToTarget();
				for (int i = 0; i < 4; i++) {
					mBuffer[0] = 0;
					mBuffer[1] = 0;
					mBuffer[2] = 0;
					mBuffer[3] = 0;
				}

				break;
			case INIT_REF:
				int initRef = rfidAPI.InitREF();
				Log.i("whw", "///////////////////InitREF=" + initRef);
				AsyncRFID.this.obtainMessage(INIT_REF, initRef, -1)
						.sendToTarget();

				break;
			case READ_RECORD_FILE:

				int readRecord = rfidAPI.ReadRecord(mBuffer, 17);
				Log.i("whw", "///////////////////ReadRecord=" + readRecord);
				byte[] tempRecord = new byte[17];
				System.arraycopy(mBuffer, 0, tempRecord, 0, 17);
				AsyncRFID.this.obtainMessage(READ_RECORD_FILE, readRecord, -1, tempRecord)
						.sendToTarget();

				break;
			case WRITE_RECORD_FILE:
				byte[] tempRecv = (byte[]) msg.obj;
				System.arraycopy(tempRecv, 0, mBuffer, 0, tempRecv.length);
				int appendRecord = rfidAPI.AppendRecord(mBuffer, tempRecv.length);
				Log.i("whw", "///////////////////AppendRecord=" + appendRecord);
				AsyncRFID.this.obtainMessage(WRITE_RECORD_FILE, appendRecord, -1)
						.sendToTarget();

				break;
			default:
				break;
			}
		}
	}
	
	public void test(){
		int sc = rfidAPI.SearchCard(mBuffer);
		Log.i("whw", "//////////////////// SearchCard=" + sc);
		for (int i = 0; i < 16; i++) {
			// Log.i("whw", "resetCard array="+mBuffer[i]);
			mBuffer[i] = 0x00;
		}
		int anticoll = rfidAPI.Anticoll(mBuffer);
		Log.i("whw", "anticoll=" + anticoll + ";" + mBuffer[0] + ";"
				+ mBuffer[1] + ";" + mBuffer[2] + ";" + mBuffer[3]
				+ ";" + mBuffer[4]);
		Log.i("whw", "Anticoll /////////////// anticoll=" + anticoll);
		int SAK = rfidAPI.SelectCard(5, mBuffer);
		Log.i("whw", "SelectCard ////////////////SAK=" + SAK);
		for (int i = 0; i < 16; i++) {
			// Log.i("whw", "resetCard array="+mBuffer[i]);
			mBuffer[i] = 0x00;
		}
		int resetCard = rfidAPI.ResetCard(mBuffer);
		Log.i("whw", "//////////////////// resetCard=" + resetCard);
		for (int i = 0; i < 16; i++) {
			// Log.i("whw", "resetCard array="+mBuffer[i]);
			mBuffer[i] = 0x00;
		}

	}
	
	private void release(){

		
		int sc = rfidAPI.SearchCard(mBuffer);
		Log.i("whw", "//////////////////// SearchCard=" + sc);
		for (int i = 0; i < 16; i++) {
			// Log.i("whw", "resetCard array="+mBuffer[i]);
			mBuffer[i] = 0x00;
		}
		int anticoll = rfidAPI.Anticoll(mBuffer);
		Log.i("whw", "anticoll=" + anticoll + ";" + mBuffer[0] + ";"
				+ mBuffer[1] + ";" + mBuffer[2] + ";" + mBuffer[3]
				+ ";" + mBuffer[4]);
		Log.i("whw", "Anticoll /////////////// anticoll=" + anticoll);
		int SAK = rfidAPI.SelectCard(5, mBuffer);
		Log.i("whw", "SelectCard ////////////////SAK=" + SAK);
		for (int i = 0; i < 16; i++) {
			// Log.i("whw", "resetCard array="+mBuffer[i]);
			mBuffer[i] = 0x00;
		}
		
		int resetCard = rfidAPI.ResetCard(mBuffer);
		Log.i("whw", "//////////////////// resetCard=" + resetCard);
		for (int i = 0; i < 16; i++) {
			// Log.i("whw", "resetCard array="+mBuffer[i]);
			mBuffer[i] = 0x00;
		}
		
//		int getChallenge = rfidAPI.GetChallenge(4, mBuffer);
//		Log.i("whw", "///////////////////getChallenge=" + getChallenge);
//		
//		
//		mBuffer[4] = 0;
//		mBuffer[5] = 0;
//		mBuffer[6] = 0;
//		mBuffer[7] = 0;
//		byte[] Keycode = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
//				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
//				(byte) 0xFF };
//		int auth = rfidAPI.authentication(mBuffer, Keycode, 8);
//		Log.i("whw", "///////////////////auth=" + auth);
//		
//		int deleteFile = rfidAPI.DeleteFile();
//		Log.i("whw", "deleteFile ////////////////deleteFile=" + deleteFile);
//		
//		int initMF = rfidAPI.InitMF();
//		Log.i("whw", "initMF ////////////////initMF=" + initMF);
//
//		
//		
//		
//		
//		
//		int initkey = rfidAPI.InitMKEF();
//		Log.i("whw", "initkey=" + initkey);
//		
//		
//		byte[] Keycode1 = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
//				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
//				(byte) 0xFF };
//		int appendkey = rfidAPI.appendKEY(Keycode1, 8);
//		Log.i("whw", "appendkey=" + appendkey);
//		AsyncRFID.this.obtainMessage(RELEASE)
//				.sendToTarget();
	}


	private OnInitListener onInitListener;
	private OnReleaseListener onReleaseListener;
	private OnGetRandomNumListener onGetRandomNumListener;
	private OnAuthenticationListener onAuthenticationListener;
	private OnDeleteFileListener onDeleteFileListener;
	private OnInitKeyListener onInitKeyListener;
	private OnAppendKeyListener onAppendKeyListener;
	private OnInitBefListener onInitBefListener;
	private OnReadBinaryListener onReadBinaryListener;
	private OnWriteBinaryListener onWriteBinaryListener;
	private OnInitRefListener onInitRefListener;
	private OnReadRecordListener onReadRecordListener;
	private OnWriteRecordListener onWriteRecordListener;

	public void setOnInitListener(OnInitListener onInitListener) {
		this.onInitListener = onInitListener;
	}
	
	public void setOnReleaseListener(OnReleaseListener onReleaseListener) {
		this.onReleaseListener = onReleaseListener;
	}

	public void setOnGetRandomNumListener(
			OnGetRandomNumListener onGetRandomNumListener) {
		this.onGetRandomNumListener = onGetRandomNumListener;
	}

	public void setOnAuthenticationListener(
			OnAuthenticationListener onAuthenticationListener) {
		this.onAuthenticationListener = onAuthenticationListener;
	}

	public void setOnDeleteFileListener(
			OnDeleteFileListener onDeleteFileListener) {
		this.onDeleteFileListener = onDeleteFileListener;
	}

	public void setOnInitKeyListener(OnInitKeyListener onInitKeyListener) {
		this.onInitKeyListener = onInitKeyListener;
	}

	public void setOnAppendKeyListener(OnAppendKeyListener onAppendKeyListener) {
		this.onAppendKeyListener = onAppendKeyListener;
	}

	public void setOnInitBefListener(OnInitBefListener onInitBefListener) {
		this.onInitBefListener = onInitBefListener;
	}

	public void setOnReadBinaryListener(
			OnReadBinaryListener onReadBinaryListener) {
		this.onReadBinaryListener = onReadBinaryListener;
	}

	public void setOnWriteBinaryListener(
			OnWriteBinaryListener onWriteBinaryListener) {
		this.onWriteBinaryListener = onWriteBinaryListener;
	}

	public void setOnInitRefListener(OnInitRefListener onInitRefListener) {
		this.onInitRefListener = onInitRefListener;
	}

	public void setOnReadRecordListener(
			OnReadRecordListener onReadRecordListener) {
		this.onReadRecordListener = onReadRecordListener;
	}

	public void setOnWriteRecordListener(
			OnWriteRecordListener onWriteRecordListener) {
		this.onWriteRecordListener = onWriteRecordListener;
	}

	public interface OnInitListener {
		public void onInitSuccess();

		public void onInitFail(int code);
	}
	
	public interface OnReleaseListener {
		public void onReleaseSuccess();

		public void onReleaseFail(int code);
	}

	public interface OnGetRandomNumListener {
		public void onGetRandomSuccess(byte[] random);

		public void onGetRandomFail(int code);
	}

	public interface OnAuthenticationListener {
		public void OnAuthenticationSuccess();

		public void OnAuthenticationFail(int code);
	}

	public interface OnDeleteFileListener {
		public void OnDeleteFileSuccess();

		public void OnDeleteFileFail(int code);
	}

	public interface OnInitKeyListener {
		public void OnInitKeySuccess();

		public void OnInitKeyFail(int code);
	}

	public interface OnAppendKeyListener {
		public void OnAppendKeySuccess();

		public void OnAppendKeyFail(int code);
	}

	public interface OnInitBefListener {
		public void OnInitBefSuccess();

		public void OnInitBefFail(int code);
	}

	public interface OnReadBinaryListener {
		public void OnReadBinarySuccess(byte[] data);

		public void OnReadBinaryFail(int code);
	}

	public interface OnWriteBinaryListener {
		public void OnWriteBinarySuccess();

		public void OnWriteBinaryFail(int code);
	}

	public interface OnInitRefListener {
		public void OnInitRefSuccess();

		public void OnInitRefFail(int code);
	}

	public interface OnReadRecordListener {
		public void OnReadRecordSuccess(byte[] data);

		public void OnReadRecordFail(int code);
	}

	public interface OnWriteRecordListener {
		public void OnWriteRecordSuccess();

		public void OnWriteRecordFail(int code);
	}

	public void init() {
		mWorkerThreadHandler.obtainMessage(INIT).sendToTarget();
	}
	
	public void releaseCard() {
		mWorkerThreadHandler.obtainMessage(RELEASE).sendToTarget();
	}

	public void getRandomNum() {
		mWorkerThreadHandler.obtainMessage(GET_RANDOM_NUM).sendToTarget();
	}

	public void authentication() {
		mWorkerThreadHandler.obtainMessage(AUTHENTICATION).sendToTarget();
	}

	public void deleteFile() {
		mWorkerThreadHandler.obtainMessage(DELETE_FILE).sendToTarget();
	}

	public void initKey() {
		mWorkerThreadHandler.obtainMessage(INIT_KEY).sendToTarget();
	}

	public void appendKey() {
		mWorkerThreadHandler.obtainMessage(APPEND_KEY).sendToTarget();
	}

	public void initBef() {
		mWorkerThreadHandler.obtainMessage(INIT_BEF).sendToTarget();
	}

	public void readBinary() {
		mWorkerThreadHandler.obtainMessage(READ_BINARY_FILE).sendToTarget();
	}

	public void writeBinary(byte[] data) {
		mWorkerThreadHandler.obtainMessage(WRITE_BINARY_FILE, data)
				.sendToTarget();
	}

	public void initRef() {
		mWorkerThreadHandler.obtainMessage(INIT_REF).sendToTarget();
	}

	public void readRecord() {
		mWorkerThreadHandler.obtainMessage(READ_RECORD_FILE).sendToTarget();
	}

	public void writeRecord(byte[] data) {
		mWorkerThreadHandler.obtainMessage(WRITE_RECORD_FILE, data)
				.sendToTarget();
	}

	public void switchstatus() {
		mWorkerThreadHandler.obtainMessage(SWITCH).sendToTarget();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case INIT:
			if (onInitListener != null) {
				if (msg.arg1 == 4) {
					onInitListener.onInitSuccess();
				} else {
					onInitListener.onInitFail(msg.arg1);
				}
			}
			break;
		case GET_RANDOM_NUM:
			if (onGetRandomNumListener != null) {
				if (msg.arg1 == 4) {
					byte[] random = (byte[]) msg.obj;
					System.arraycopy(mBuffer, 0, random, 0, random.length);
					onGetRandomNumListener.onGetRandomSuccess(random);
				} else {
					onGetRandomNumListener.onGetRandomFail(msg.arg1);
				}
			}
			break;
		case AUTHENTICATION:
			if (onAuthenticationListener != null) {
				if (msg.arg1 == rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY) {
					onAuthenticationListener.OnAuthenticationSuccess();
				} else {
					onAuthenticationListener.OnAuthenticationFail(msg.arg1);
				}
			}
			break;
		case DELETE_FILE:
			if (onDeleteFileListener != null) {
				if (rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY == msg.arg1) {
					onDeleteFileListener.OnDeleteFileSuccess();
				} else {
					onDeleteFileListener.OnDeleteFileFail(msg.arg1);
				}
			}
			break;
		case INIT_KEY:
			if (onInitKeyListener != null) {
				if (rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY == msg.arg1) {
					onInitKeyListener.OnInitKeySuccess();
				} else {
					onInitKeyListener.OnInitKeyFail(msg.arg1);
				}
			}
			break;
		case APPEND_KEY:
			if (onAppendKeyListener != null) {
				if (rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY == msg.arg1) {
					onAppendKeyListener.OnAppendKeySuccess();
				} else {
					onAppendKeyListener.OnAppendKeyFail(msg.arg1);
				}
			}
			break;
		case INIT_BEF:
			if (onInitBefListener != null) {
				if (rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY == msg.arg1) {
					onInitBefListener.OnInitBefSuccess();
				} else {
					onInitBefListener.OnInitBefFail(msg.arg1);
				}
			}
			break;
		case READ_BINARY_FILE:
			if (onReadBinaryListener != null) {
				if (msg.arg1 == rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY) {

					onReadBinaryListener.OnReadBinarySuccess((byte[]) msg.obj);
				} else {
					onReadBinaryListener.OnReadBinaryFail(msg.arg1);
				}
			}
			break;
		case WRITE_BINARY_FILE:
			if (onWriteBinaryListener != null) {
				if (msg.arg1 == rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY) {
					onWriteBinaryListener.OnWriteBinarySuccess();
				} else {
					onWriteBinaryListener.OnWriteBinaryFail(msg.arg1);
				}
			}
			break;
		case INIT_REF:
			if (onInitRefListener != null) {
				if (rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY == msg.arg1) {
					onInitRefListener.OnInitRefSuccess();
				} else {
					onInitRefListener.OnInitRefFail(msg.arg1);
				}
			}
			break;
		case READ_RECORD_FILE:
			if (onReadRecordListener != null) {
				if (msg.arg1 == rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY) {

					onReadRecordListener.OnReadRecordSuccess((byte[]) msg.obj);
				} else {
					onReadRecordListener.OnReadRecordFail(msg.arg1);
				}
			}
			break;
		case WRITE_RECORD_FILE:
			if (onWriteRecordListener != null) {
				if (msg.arg1 == rfidAPI.SCARD_COMMAND_EXECUTED_CORRECTLY) {
					onWriteRecordListener.OnWriteRecordSuccess();
				} else {
					onWriteRecordListener.OnWriteRecordFail(msg.arg1);
				}
			}
			break;
		default:
			break;
		}
	}

}
