package com.szfp.szfp.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android_serialport_api.BarCodeAPI;


public class AsyncBarCode extends Handler {

	private final int PREPARE = 1;
	private final int START_DECODE = 2;
	private final int OPEN = 0;
	private final int CLOSE = 3;
	private final int PARAM = 4;
	private final int SCAN_CONFIGURATION_PARAMETER = 5;
	private final int RESTORE = 6;
	private final int SET_HOST = 7;
	private final int DISABLE_ALL_SYMBOLOGIES = 8;

	private final byte[] buffer = new byte[1024];

	private Handler mWorkerThreadHandler;

	private BarCodeAPI api;

	public AsyncBarCode(Looper looper) {
		createHandler(looper);
		api = new BarCodeAPI();
	}
	
	public BarCodeAPI getApi(){
		return api;
	}

	private Handler createHandler(Looper looper) {
		return mWorkerThreadHandler = new WorkerHandler(looper);
	}

	private OnOpenListener onOpenListener;
	private OnCloseListener onCloseListener;
	private OnPrepareListener onPrepareListener;
	private OnStartDecodeListener onStartDecodeListener;
	private OnConfigurationParameterListener onConfigurationParameterListener;
	private OnSetParameterListener onSetParameterListener;
	private OnRestoreListener onRestoreListener;

	public void setOnOpenListener(
			OnOpenListener onOpenListener) {
		this.onOpenListener = onOpenListener;
	}

	public void setOnCloseListener(
			OnCloseListener onCloseListener) {
		this.onCloseListener = onCloseListener;
	}

	public void setOnPrepareListener(OnPrepareListener onPrepareListener) {
		this.onPrepareListener = onPrepareListener;
	}

	public void setOnStartDecodeListener(
			OnStartDecodeListener onStartDecodeListener) {
		this.onStartDecodeListener = onStartDecodeListener;
	}

	public void setOnConfigurationParameterListener(
			OnConfigurationParameterListener onConfigurationParameterListener) {
		this.onConfigurationParameterListener = onConfigurationParameterListener;
	}

	public void setOnSetParameterListener(
			OnSetParameterListener onSetParameterListener) {
		this.onSetParameterListener = onSetParameterListener;
	}
	
	public void setOnRestoreListener(OnRestoreListener onRestoreListener) {
		this.onRestoreListener = onRestoreListener;
	}

	public interface OnPrepareListener {
		void OnPrepare(boolean initSuccess);
	};

	public interface OnOpenListener {
		void onOpenSuccess();
		void onOpenFail();
	}

	public interface OnCloseListener {
		void onCloseSuccess();
		void onCloseFail();
	}

	public interface OnStartDecodeListener {
		void OnDecodeSuccess(byte[] data);

		void OnDecodeFail();
	};

	public interface OnConfigurationParameterListener {
		void OnConfigurationParameterSuccess();

		void OnConfigurationParameterFail();
	};

	public interface OnSetParameterListener {
		void OnSetParameterSuccess();

		void OnSetParameterFail();
	};
	
	public interface OnRestoreListener{
		void onRestoreSuccess();
		void onRestoreFail();
	}

	public void prepareDecode() {
		mWorkerThreadHandler.sendEmptyMessage(PREPARE);
	}

	public void startDecode() {
		mWorkerThreadHandler.sendEmptyMessage(START_DECODE);
	}

	public void scanConfigurationParameter() {
		mWorkerThreadHandler.sendEmptyMessage(SCAN_CONFIGURATION_PARAMETER);
	}

	public void setParameter(byte[] param) {
		mWorkerThreadHandler.obtainMessage(PARAM, param).sendToTarget();
	}

	public void restore() {
		mWorkerThreadHandler.sendEmptyMessage(RESTORE);
	}

	public void setHost() {
		mWorkerThreadHandler.sendEmptyMessage(SET_HOST);
	}

	public void disableAllSymbologies() {
		mWorkerThreadHandler.sendEmptyMessage(DISABLE_ALL_SYMBOLOGIES);
	}

	public void open() {
		mWorkerThreadHandler.sendEmptyMessage(OPEN);
	}

	public void close() {
		mWorkerThreadHandler.sendEmptyMessage(CLOSE);
	}

	/**
	 * ����˵������ʼ��BarCode����ʼ������Ϣ�ᱻд��BarCode���ڲ��洢�ռ䣬����ֻ��Ҫ����һ��
	 * 
	 */
	private void prepare() {
		if(api.decodeDataPacketFormat()){
			if(api.setMultipacketOptionContinuously()){
				if(api.lowPowerTimeout()){
					obtainMessage(PREPARE, true).sendToTarget();
					return;
				}
			}
		}
		obtainMessage(PREPARE, false).sendToTarget();
		
		
	}

	private void start() {
		int length = api.startDecode(buffer);
		api.stopDecode();
		obtainMessage(START_DECODE, length, -1).sendToTarget();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case PREPARE:
			if (onPrepareListener != null) {
				onPrepareListener.OnPrepare((Boolean)msg.obj);
			}
			break;
		case START_DECODE:
			if (onStartDecodeListener != null) {
				int length = msg.arg1;
				if (length > 0) {
					byte[] data = new byte[length];
					System.arraycopy(buffer, 0, data, 0, data.length);
					onStartDecodeListener.OnDecodeSuccess(data);
				} else {
					onStartDecodeListener.OnDecodeFail();
				}
			}
			break;
		case SCAN_CONFIGURATION_PARAMETER:
			if (onConfigurationParameterListener != null) {
				if ((Boolean) msg.obj) {
					onConfigurationParameterListener
							.OnConfigurationParameterSuccess();
				} else {
					onConfigurationParameterListener
							.OnConfigurationParameterFail();
				}
			}
			break;
		case PARAM:
			if (onSetParameterListener != null) {
				if ((Boolean) msg.obj) {
					onSetParameterListener.OnSetParameterSuccess();
				} else {
					onSetParameterListener.OnSetParameterFail();
				}
			}
			break;
		case RESTORE:
			if (onRestoreListener != null) {
				if ((Boolean) msg.obj) {
					onRestoreListener.onRestoreSuccess();
				} else {
					onRestoreListener.onRestoreFail();
				}
			}
			break;
		case OPEN:
			if (onOpenListener != null) {
				if ((Boolean) msg.obj) {
					onOpenListener.onOpenSuccess();
				} else {
					onOpenListener.onOpenFail();
				}
			}
			break;
		case CLOSE:
			if (onCloseListener != null) {
				if ((Boolean) msg.obj) {
					onCloseListener.onCloseSuccess();
				} else {
					onCloseListener.onCloseFail();
				}
			}
			break;

		default:
			break;
		}
	}

	protected class WorkerHandler extends Handler {
		public WorkerHandler(Looper looper) {
			super(looper);

		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PREPARE:
				prepare();
				break;
			case START_DECODE:
				start();
				break;
			case SCAN_CONFIGURATION_PARAMETER:
				AsyncBarCode.this.obtainMessage(SCAN_CONFIGURATION_PARAMETER,
						api.scanConfigurationParameter()).sendToTarget();
				break;
			case OPEN:
				AsyncBarCode.this.obtainMessage(OPEN, api.open())
						.sendToTarget();
				break;
			case CLOSE:
				AsyncBarCode.this.obtainMessage(CLOSE, api.close())
						.sendToTarget();
				break;
			case PARAM:
				AsyncBarCode.this.obtainMessage(PARAM,
						api.setParameter((byte[]) msg.obj)).sendToTarget();
				break;
			case RESTORE:
				AsyncBarCode.this.obtainMessage(RESTORE, api.restore())
						.sendToTarget();
				break;
			case SET_HOST:
				AsyncBarCode.this.obtainMessage(PARAM, api.setHost())
						.sendToTarget();
				break;
			case DISABLE_ALL_SYMBOLOGIES:
				AsyncBarCode.this.obtainMessage(PARAM,
						api.disableAllSymbologies()).sendToTarget();
				break;
			default:
				break;
			}
		}
	}
}
