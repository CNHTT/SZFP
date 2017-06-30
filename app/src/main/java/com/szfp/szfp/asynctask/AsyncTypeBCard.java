package com.szfp.szfp.asynctask;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android_serialport_api.TypeBCardAPI;

public class AsyncTypeBCard extends Handler {

	private static final int RELEASE_CARD = 1;

	private static final int USE_CARD = 2;

	private Handler mWorkerThreadHandler;

	private TypeBCardAPI api;

	public static final int WRITE = 1;
	public static final int READ_INFO = 3;

	public AsyncTypeBCard(Looper looper) {
		mWorkerThreadHandler = createHandler(looper);
		api = new TypeBCardAPI();
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
			case RELEASE_CARD:
				int code = release((DataObject) msg.obj);
				AsyncTypeBCard.this.obtainMessage(RELEASE_CARD,code,-1, serialNum).sendToTarget();
				break;
			default:
				break;
			}
		}
	}

	private OnReleaseCardListener onReleaseCardListener;

	public void setOnReleaseCardListener(
			OnReleaseCardListener onReleaseCardListener) {
		this.onReleaseCardListener = onReleaseCardListener;
	}

	public interface OnReleaseCardListener {
		public void onReleaseCardSuccess(byte[] serialNum);

		public void onReleaseCardFail(int comfirmationCode);
	}


	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case RELEASE_CARD:
			if(onReleaseCardListener != null){
				if(msg.arg1 != TypeBCardAPI.DESELECT_SUCCESS){
					onReleaseCardListener.onReleaseCardFail(msg.arg1);
				}else{
					onReleaseCardListener.onReleaseCardSuccess((byte[])msg.obj);
				}
			}
			break;

		default:
			break;
		}
	}



	byte[] serialNum = null;
	private int release(DataObject dataObject) {
		Message msg = null;
		msg = api.request((byte) 0x00);
		byte[] pupi = new byte[4];
		byte[] applicationData = new byte[4];
		if (msg.what == TypeBCardAPI.REQUEST_SUCCESS) {
			System.arraycopy((byte[]) msg.obj, 0, pupi, 0, pupi.length);
			System.arraycopy((byte[]) msg.obj, 4, applicationData, 0,
					applicationData.length);
		} else {
			return msg.what;
		}

		msg = api.active(pupi, dataObject.cid);
		if (msg.what == TypeBCardAPI.ACTIVE_SUCCESS) {
			serialNum = (byte[]) msg.obj;
		} else {
			return msg.what;
		}

		msg = api.writeIdentify(dataObject.cid, new byte[] { (byte) 0xaa, 0x22,
				0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88 }, "00");
		if (msg.what == TypeBCardAPI.WRITE_SUCCESS) {
		} else {
			return msg.what;
		}

		msg = api.readIdentify(dataObject.cid, "00");
		if (msg.what == TypeBCardAPI.READ_SUCCESS) {
		} else {
			return msg.what;
		}

		msg = api.writeKey(dataObject.cid, new byte[] { 0x08, 0x07, 0x06, 0x05,
				0x04, 0x03, 0x02, 0x01 });
		if (msg.what == TypeBCardAPI.WRITE_SUCCESS) {
		} else {
			return msg.what;
		}

		msg = api.readKey(dataObject.cid);
		if (msg.what == TypeBCardAPI.READ_SUCCESS) {
		} else {
			return msg.what;
		}

		msg = api.writeData(dataObject.cid, new byte[] { (byte) 0xbb, 0x01,
				0x0f, 0x0f, 0x04, 0x05, 0x06, 0x07 });
		if (msg.what == TypeBCardAPI.WRITE_SUCCESS) {
		} else {
			return msg.what;
		}

		msg = api.readData(dataObject.cid);
		if (msg.what == TypeBCardAPI.READ_SUCCESS) {
		} else {
			return msg.what;
		}

//		msg = api.writePermission(dataObject.cid, new byte[] { 0x01, 0x02,
//				0x03, 0x04 }, (byte) 0x21,
//				new byte[] { 0x1B, (byte) 0xE4, 0x1B });
//		if (msg.what == TypeBCardAPI.WRITE_SUCCESS) {
//		} else {
//			return msg.what;
//		}
//
//		msg = api.readPermission(dataObject.cid);
//		if (msg.what == TypeBCardAPI.READ_SUCCESS) {
//		} else {
//			return msg.what;
//		}

		msg = api.deselect(dataObject.cid);
		if (msg.what == TypeBCardAPI.DESELECT_SUCCESS) {
			return msg.what;
		} else {
			return msg.what;
		}
		
		

	}

	private void comsume(char cid) {
		api.request((byte) 0x21);
		// api.active(DataUtils.hexStringTobyte(PUPI), cid);
		api.readIdentify(cid, "00");
		int aut = api.authentication(cid, new byte[] { 0x08, 0x07, 0x06, 0x05,
				0x04, 0x03, 0x02, 0x01 }).what;
		Log.i("whw", "aut=" + aut);
		api.readData(cid);
		api.writeData(cid, new byte[] { (byte) 0xbb, 0x01, 0x0f, 0x0f, 0x04,
				0x05, 0x06, 0x07 });
		api.readData(cid);
		api.deselect(cid);
	}

	public void release(char cid, byte[] identify, byte[] key, byte[] data) {
		DataObject dataObject = new DataObject();
		dataObject.cid = cid;
		dataObject.identify = identify;
		dataObject.key = key;
		dataObject.data = data;
		mWorkerThreadHandler.obtainMessage(RELEASE_CARD, dataObject)
				.sendToTarget();
	}

	public void consume() {
		mWorkerThreadHandler.obtainMessage(USE_CARD).sendToTarget();
	}

	private class DataObject {
		char cid;
		byte[] identify;
		byte[] key;
		byte[] data;
	}

}
