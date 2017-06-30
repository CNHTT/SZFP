package com.szfp.szfp.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android_serialport_api.RFID15693API;

public class AsyncRFID15693Card extends Handler {
	private static final int INIT = 0;
	private static final int FIND_CARD = 1;
	private static final int READ = 2;
	private static final int WRITE = 3;
	private static final int READ_MORE = 4;
	private static final int WRITE_MORE = 5;

	private Handler mWorkerThreadHandler;

	private RFID15693API reader;

	public AsyncRFID15693Card(Looper looper) {
		mWorkerThreadHandler = createHandler(looper);
		reader = new RFID15693API();
	}

	protected Handler createHandler(Looper looper) {
		return new WorkerHandler(looper);
	}

	private OnInitListener onInitListener;
	private OnFindCardListener onFindCardListener;
	private OnWriteListener onWriteListener;
	private OnReadListener onReadListener;
	
	private OnWriteMoreListener onWriteMoreListener;
	private OnReadMoreListener onReadMoreListener;

	public void setOnInitListener(OnInitListener onInitListener) {
		this.onInitListener = onInitListener;
	}

	public void setOnFindCardListener(OnFindCardListener onFindCardListener) {
		this.onFindCardListener = onFindCardListener;
	}

	public void setOnWriteListener(OnWriteListener onWriteListener) {
		this.onWriteListener = onWriteListener;
	}

	public void setOnReadListener(OnReadListener onReadListener) {
		this.onReadListener = onReadListener;
	}

	public interface OnInitListener {
		void initSuccess();

		void initFail();
	}

	public interface OnFindCardListener {
		void findSuccess(byte[] data);

		void findFail();
	}

	public interface OnWriteListener {
		void writeSuccess();

		void writeFail();
	}
	
	public interface OnWriteMoreListener {
		void writeMoreSuccess();

		void writeMoreFail();
	}

	public interface OnReadListener {
		void readSuccess(byte[] data);

		void readFail();
	}
	
	public interface OnReadMoreListener {
		void readMoreSuccess(byte[] data);

		void readMoreFail();
	}
	
	

	public void setOnWriteMoreListener(OnWriteMoreListener onWriteMoreListener) {
		this.onWriteMoreListener = onWriteMoreListener;
	}

	public void setOnReadMoreListener(OnReadMoreListener onReadMoreListener) {
		this.onReadMoreListener = onReadMoreListener;
	}

	public void init() {
		mWorkerThreadHandler.sendEmptyMessage(INIT);
	}

	public void findCard() {
		mWorkerThreadHandler.sendEmptyMessage(FIND_CARD);
	}

	public void write(int position, byte[] data) {
		mWorkerThreadHandler.obtainMessage(WRITE, position, -1, data)
				.sendToTarget();
	}
	
	public void writeMore(int position, byte[] data) {
		mWorkerThreadHandler.obtainMessage(WRITE_MORE, position, -1, data)
				.sendToTarget();
	}

	public void read(int position) {
		mWorkerThreadHandler.obtainMessage(READ, position, -1).sendToTarget();
	}
	
	public void readMore(int position,int count) {
		mWorkerThreadHandler.obtainMessage(READ_MORE, position, count).sendToTarget();
	}

	protected class WorkerHandler extends Handler {
		public WorkerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INIT:
				AsyncRFID15693Card.this.obtainMessage(INIT, initReader())
						.sendToTarget();
				break;
			case FIND_CARD:
				AsyncRFID15693Card.this.obtainMessage(FIND_CARD,
						reader.findCard()).sendToTarget();
				break;
			case READ:
				AsyncRFID15693Card.this.obtainMessage(READ, readData(msg.arg1))
						.sendToTarget();
				break;
			case WRITE:
				AsyncRFID15693Card.this.obtainMessage(WRITE,
						writeData(msg.arg1, (byte[]) msg.obj)).sendToTarget();
				break;
			case WRITE_MORE:
				AsyncRFID15693Card.this.obtainMessage(WRITE_MORE,
						writeMoreData(msg.arg1, (byte[]) msg.obj)).sendToTarget();
				break;
			case READ_MORE:
				AsyncRFID15693Card.this.obtainMessage(READ_MORE,
						readMoreData(msg.arg1, msg.arg2)).sendToTarget();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case INIT:
			if ((Boolean) msg.obj) {
				if (onInitListener != null) {
					onInitListener.initSuccess();
				}
			} else {
				if (onInitListener != null) {
					onInitListener.initFail();
				}
			}
			break;
		case FIND_CARD:
			byte[] findData = (byte[]) msg.obj;
			if (findData != null) {
				if (onFindCardListener != null) {
					onFindCardListener.findSuccess(findData);
				}
			} else {
				if (onFindCardListener != null) {
					onFindCardListener.findFail();
				}
			}
			break;
		case READ:
			byte[] readData = (byte[]) msg.obj;
			if (readData != null) {
				if (onReadListener != null) {
					onReadListener.readSuccess(readData);
				}
			} else {
				if (onReadListener != null) {
					onReadListener.readFail();
				}
			}
		case READ_MORE:
			byte[] readMoreData = (byte[]) msg.obj;
			if (readMoreData != null) {
				if (onReadMoreListener != null) {
					onReadMoreListener.readMoreSuccess(readMoreData);
				}
			} else {
				if (onReadMoreListener != null) {
					onReadMoreListener.readMoreFail();
				}
			}
			break;
		case WRITE:
			if ((Boolean) msg.obj) {
				if (onWriteListener != null) {
					onWriteListener.writeSuccess();
				}
			} else {
				if (onWriteListener != null) {
					onWriteListener.writeFail();
				}
			}
			break;
		case WRITE_MORE:
			if ((Boolean) msg.obj) {
				if (onWriteMoreListener != null) {
					onWriteMoreListener.writeMoreSuccess();
				}
			} else {
				if (onWriteMoreListener != null) {
					onWriteMoreListener.writeMoreFail();
				}
			}
			break;
		default:
			break;
		}
	}

	private boolean initReader() {
		if (reader.configurationReaderMode()) {
			if (reader.configurationProtocolMode()) {
				if (reader.setCheckCode()) {
					return true;
				}
			}
		}
		return false;
	}

	private byte[] readData(int position) {
		byte[] findData = reader.findCard();
		if (findData != null) {
			byte[] uid = new byte[8];
			System.arraycopy(findData, 1, uid, 0, uid.length);
			if (reader.selectCard(uid)) {
				return reader.readOne(position);
			}
		}
		return null;
	}

	private boolean writeData(int position, byte[] data) {
		byte[] findData = reader.findCard();
		if (findData != null) {
			byte[] uid = new byte[8];
			System.arraycopy(findData, 1, uid, 0, uid.length);
			if (reader.selectCard(uid)) {
				return reader.writeOne(position, data);
			}
		}
		return false;
	}
	
	private byte[] readMoreData(int position,int count) {
		byte[] findData = reader.findCard();
		if (findData != null) {
			byte[] uid = new byte[8];
			System.arraycopy(findData, 1, uid, 0, uid.length);
			if (reader.selectCard(uid)) {
				return reader.readMore(position, count);
			}
		}
		return null;
	}

	private boolean writeMoreData(int position, byte[] data) {
		byte[] findData = reader.findCard();
		if (findData != null) {
			byte[] uid = new byte[8];
			System.arraycopy(findData, 1, uid, 0, uid.length);
			if (reader.selectCard(uid)) {
				int length = data.length % 4 == 0 ? data.length / 4
						: data.length / 4 + 1;
				for (int i = 0; i < length; i++) {
					byte[] temp = new byte[4];
					if (i == length - 1) {
						System.arraycopy(data, i * 4, temp, 0, data.length%4);
					} else {
						System.arraycopy(data, i * 4, temp, 0, temp.length);
					}
					Log.i("whw", "writeMoreData="+i);
					if (!reader.writeOne(position++, temp)) {
						return false;
					}
				}
				return true;

			}
		}
		return false;
	}
}
