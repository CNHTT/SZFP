package com.szfp.szfp.asynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.szfp.szfplib.R;

import android_serialport_api.FingerprintAPI;


public class AsyncFingerprint extends Handler {

	private static final int PS_GetImage = 0x01;
	private static final int PS_GenChar = 0x02;
	private static final int PS_Match = 0x03;
	private static final int PS_Search = 0x04;
	private static final int PS_RegModel = 0x05;
	private static final int PS_StoreChar = 0x06;
	private static final int PS_LoadChar = 0x070;
	private static final int PS_UpChar = 0x08;

	private static final int PS_DownChar = 0x09;
	private static final int PS_UpImage = 0x0a;
	private static final int PS_DownImage = 0x0b;
	private static final int PS_DeleteChar = 0x0c;
	private static final int PS_Empty = 0x0d;
	private static final int PS_Enroll = 0x10;
	private static final int PS_Identify = 0x11;
	private Handler mWorkerThreadHandler;
	private static final int REGISTER = 0x12;
	private static final int VALIDATE = 0x13;
	private static final int REGISTER2 = 0x14;
	private static final int VALIDATE2 = 0x15;

	private static final int PS_CALIBRATION = 0x2e;

	public static final int FOREFINGER = 1;
	public static final int MIDFINGER = 2;

	public static final int SHOW_PROGRESSDIALOG = 1;
	public static final int SHOW_FINGER_IMAGE = 2;
	public static final int SHOW_FINGER_MODEL = 3;
	public static final int REGISTER_SUCCESS = 4;
	public static final int REGISTER_FAIL = 5;
	public static final int VALIDATE_RESULT1 = 6;
	public static final int VALIDATE_RESULT2 = 100;
	public static final int VALIDATE_RESULT_STORE = 7;
	public static final int UP_IMAGE_RESULT = 8;
	public static final int UP_IMAGE_FAIL = 9;

	private FingerprintAPI fingerprint;

	public boolean isCancle = false;

	public AsyncFingerprint(Looper looper,Handler mHandler) {
		this.mHandler = mHandler;
		createHandler(looper);
		fingerprint = new FingerprintAPI();
	}
	
	public void setFingerprintType(int type){
		fingerprint.setFingerprintType(type);
	}

	private Handler createHandler(Looper looper) {
		return mWorkerThreadHandler = new WorkerHandler(looper);
	}

	private Handler mHandler;

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case PS_GetImage:
			if (onGetImageListener == null) {
				return;
			}
			if (msg.arg1 == 0) {
				onGetImageListener.onGetImageSuccess();
			} else {
				onGetImageListener.onGetImageFail();
			}
			break;
		case PS_UpImage:
			if (onUpImageListener == null) {
				return;
			}
			if (msg.obj != null) {
				onUpImageListener.onUpImageSuccess((byte[]) msg.obj);
			} else {
				onUpImageListener.onUpImageFail();
			}
			break;
		case PS_DownImage:
			if (onDownImageListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onDownImageListener.onDownImageSuccess();
				} else {
					onDownImageListener.onDownImageFail();
				}
			}
			break;
		case PS_GenChar:
			if (onGenCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onGenCharListener.onGenCharSuccess(msg.arg2);
				} else {
					onGenCharListener.onGenCharFail();
				}
			}
			break;
		case PS_RegModel:
			if (onRegModelListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onRegModelListener.onRegModelSuccess();
				} else {
					onRegModelListener.onRegModelFail();
				}
			}
			break;
		case PS_UpChar:
			if (onUpCharListener == null) {
				return;
			} else {
				if (msg.obj != null) {
					onUpCharListener.onUpCharSuccess((byte[]) msg.obj);
				} else {
					onUpCharListener.onUpCharFail();
				}
			}
			break;
		case PS_DownChar:
			if (onDownCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onDownCharListener.onDownCharSuccess();
				} else {
					onDownCharListener.onDownCharFail();
				}
			}
			break;
		case PS_Match:
			if (onMatchListener == null) {
				return;
			} else {
				if ((Boolean) msg.obj) {
					onMatchListener.onMatchSuccess();
				} else {
					onMatchListener.onMatchFail();
				}
			}
			break;
		case PS_StoreChar:
			if (onStoreCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onStoreCharListener.onStoreCharSuccess();
				} else {
					onStoreCharListener.onStoreCharFail();
				}
			}
			break;
		case PS_LoadChar:
			if (onLoadCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onLoadCharListener.onLoadCharSuccess();
				} else {
					onLoadCharListener.onLoadCharFail();
				}
			}
			break;
		case PS_DeleteChar:
			if (onDeleteCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onDeleteCharListener.onDeleteCharSuccess();
				} else {
					onDeleteCharListener.onDeleteCharFail();
				}
			}
			break;
		case PS_Empty:
			if (onEmptyListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onEmptyListener.onEmptySuccess();
				} else {
					onEmptyListener.onEmptyFail();
				}
			}
			break;
		case PS_CALIBRATION:
			if (onCalibrationListener == null) {
				return;
			} else {
				if ((Integer) msg.obj == 0x00) {
					onCalibrationListener.onCalibrationSuccess();
				} else {
					onCalibrationListener.onCalibrationFail();
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
			if(isStop){
				return;
			}
			switch (msg.what) {
			case PS_GetImage:
				int valueGetImage = fingerprint.PSGetImage();
				AsyncFingerprint.this.obtainMessage(PS_GetImage, valueGetImage,
						-1).sendToTarget();
				break;
			case PS_UpImage:
				byte[] imageData = fingerprint.PSUpImage();
				AsyncFingerprint.this.obtainMessage(PS_UpImage, imageData)
						.sendToTarget();
				break;
			case PS_DownImage:
				int valueDownImage = fingerprint.PSDownImage((byte[]) msg.obj);
				AsyncFingerprint.this.obtainMessage(PS_DownImage,
						valueDownImage, -1).sendToTarget();
				break;
			case PS_GenChar:
				int valueGenChar = fingerprint.PSGenChar(msg.arg1);
				AsyncFingerprint.this.obtainMessage(PS_GenChar, valueGenChar,
						msg.arg1).sendToTarget();
				break;
			case PS_RegModel:
				int valueRegModel = fingerprint.PSRegModel();
				AsyncFingerprint.this.obtainMessage(PS_RegModel, valueRegModel,
						-1).sendToTarget();
				break;
			case PS_UpChar:
				byte[] charData = fingerprint.PSUpChar(2);
				AsyncFingerprint.this.obtainMessage(PS_UpChar, charData)
						.sendToTarget();
				break;
			case PS_DownChar:
				int valueDownChar = fingerprint.PSDownChar(msg.arg1,
						(byte[]) msg.obj);
				AsyncFingerprint.this.obtainMessage(PS_DownChar, valueDownChar,
						-1).sendToTarget();
				break;
			case PS_Match:
				boolean valueMatch = fingerprint.PSMatch();
				AsyncFingerprint.this.obtainMessage(PS_Match,
						Boolean.valueOf(valueMatch)).sendToTarget();
				break;
			case PS_StoreChar:
				int valueStoreChar = fingerprint
						.PSStoreChar(msg.arg1, msg.arg2);
				AsyncFingerprint.this.obtainMessage(PS_StoreChar,
						valueStoreChar, -1).sendToTarget();
				break;
			case PS_LoadChar:
				int valueLoadChar = fingerprint.PSLoadChar(msg.arg1, msg.arg2);
				AsyncFingerprint.this.obtainMessage(PS_LoadChar, valueLoadChar,
						-1).sendToTarget();
				break;
			case PS_DeleteChar:
				int valueDeleteChar = fingerprint.PSDeleteChar(
						(short) msg.arg1, (short) msg.arg2);
				AsyncFingerprint.this.obtainMessage(PS_DeleteChar,
						valueDeleteChar, -1).sendToTarget();
				break;
			case PS_Empty:
				int valueEmpty = fingerprint.PSEmpty();
				AsyncFingerprint.this.obtainMessage(PS_Empty, valueEmpty, -1)
						.sendToTarget();
				break;
			case REGISTER:
				boolean isSuccess = registerFinger();
				if (isSuccess) {
					mHandler.sendEmptyMessage(REGISTER_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(REGISTER_FAIL);
				}
				break;

			case REGISTER2:
				int isSuccess2 = registerFinger2();
				if (isSuccess2 != -1) {
					mHandler.obtainMessage(REGISTER_SUCCESS, isSuccess2).sendToTarget();
				} else {
					mHandler.sendEmptyMessage(REGISTER_FAIL);
				}
				break;
			case PS_CALIBRATION:
				int calibration = fingerprint.PSCalibration();
				AsyncFingerprint.this
						.obtainMessage(PS_CALIBRATION, calibration)
						.sendToTarget();
				break;
			case VALIDATE:
				byte[] data = (byte[]) msg.obj;
				boolean match = validateFinger(data);
				mHandler.obtainMessage(VALIDATE_RESULT1, match).sendToTarget();
				break;
			case VALIDATE2:
				mHandler.obtainMessage(VALIDATE_RESULT2, validateFinger2())
						.sendToTarget();
				break;
			default:
				break;
			}
		}
	}

	private OnGetImageListener onGetImageListener;

	private OnUpImageListener onUpImageListener;

	private OnDownImageListener onDownImageListener;

	private OnGenCharListener onGenCharListener;

	private OnRegModelListener onRegModelListener;

	private OnUpCharListener onUpCharListener;

	private OnDownCharListener onDownCharListener;

	private OnMatchListener onMatchListener;

	private OnStoreCharListener onStoreCharListener;

	private OnLoadCharListener onLoadCharListener;

	private OnSearchListener onSearchListener;

	private OnDeleteCharListener onDeleteCharListener;

	private OnEmptyListener onEmptyListener;

	private OnEnrollListener onEnrollListener;

	private OnIdentifyListener onIdentifyListener;

	private OnCalibrationListener onCalibrationListener;

	public void setOnGetImageListener(OnGetImageListener onGetImageListener) {
		this.onGetImageListener = onGetImageListener;
	}

	public void setOnUpImageListener(OnUpImageListener onUpImageListener) {
		this.onUpImageListener = onUpImageListener;
	}

	public void setOnGenCharListener(OnGenCharListener onGenCharListener) {
		this.onGenCharListener = onGenCharListener;
	}

	public void setOnRegModelListener(OnRegModelListener onRegModelListener) {
		this.onRegModelListener = onRegModelListener;
	}

	public void setOnUpCharListener(OnUpCharListener onUpCharListener) {
		this.onUpCharListener = onUpCharListener;
	}

	public void setOnDownCharListener(OnDownCharListener onDownCharListener) {
		this.onDownCharListener = onDownCharListener;
	}

	public void setOnMatchListener(OnMatchListener onMatchListener) {
		this.onMatchListener = onMatchListener;
	}

	public void setOnStoreCharListener(OnStoreCharListener onStoreCharListener) {
		this.onStoreCharListener = onStoreCharListener;
	}

	public void setOnLoadCharListener(OnLoadCharListener onLoadCharListener) {
		this.onLoadCharListener = onLoadCharListener;
	}

	public void setOnSearchListener(OnSearchListener onSearchListener) {
		this.onSearchListener = onSearchListener;
	}

	public void setOnDeleteCharListener(
			OnDeleteCharListener onDeleteCharListener) {
		this.onDeleteCharListener = onDeleteCharListener;
	}

	public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
		this.onEmptyListener = onEmptyListener;
	}

	public void setOnEnrollListener(OnEnrollListener onEnrollListener) {
		this.onEnrollListener = onEnrollListener;
	}

	public void setOnIdentifyListener(OnIdentifyListener onIdentifyListener) {
		this.onIdentifyListener = onIdentifyListener;
	}

	public void setOnCalibrationListener(
			OnCalibrationListener onCalibrationListener) {
		this.onCalibrationListener = onCalibrationListener;
	}

	public interface OnGetImageListener {
		void onGetImageSuccess();

		void onGetImageFail();
	}

	public interface OnUpImageListener {
		void onUpImageSuccess(byte[] data);

		void onUpImageFail();
	}

	public interface OnDownImageListener {
		void onDownImageSuccess();

		void onDownImageFail();
	}

	public interface OnGenCharListener {
		void onGenCharSuccess(int bufferId);

		void onGenCharFail();
	}

	public interface OnRegModelListener {
		void onRegModelSuccess();

		void onRegModelFail();
	}

	public interface OnUpCharListener {
		void onUpCharSuccess(byte[] model);

		void onUpCharFail();
	}

	public interface OnDownCharListener {
		void onDownCharSuccess();

		void onDownCharFail();
	}

	public interface OnMatchListener {
		void onMatchSuccess();

		void onMatchFail();
	}

	public interface OnStoreCharListener {
		void onStoreCharSuccess();

		void onStoreCharFail();
	}

	public interface OnLoadCharListener {
		void onLoadCharSuccess();

		void onLoadCharFail();
	}

	public interface OnSearchListener {
		void onSearchSuccess(int pageId, int matchScore);

		void onSearchFail();
	}

	public interface OnDeleteCharListener {
		void onDeleteCharSuccess();

		void onDeleteCharFail();
	}

	public interface OnEmptyListener {
		void onEmptySuccess();

		void onEmptyFail();
	}

	public interface OnEnrollListener {
		void onEnrollSuccess(int pageId);

		void onEnrollFail();
	}

	public interface OnCalibrationListener {
		void onCalibrationSuccess();

		void onCalibrationFail();
	}

	public interface OnIdentifyListener {
		void onIdentifySuccess(int pageId, int matchScore);

		void onIdentifyFail();
	}

	public void PS_GetImage() {
		mWorkerThreadHandler.sendEmptyMessage(PS_GetImage);
	}

	public void PS_UpImage() {
		mWorkerThreadHandler.sendEmptyMessage(PS_UpImage);
	}

	public void PS_DownImage(byte[] image) {
		mWorkerThreadHandler.obtainMessage(PS_DownImage, image).sendToTarget();
	}

	public void PS_GenChar(int bufferId) {
		mWorkerThreadHandler.obtainMessage(PS_GenChar, bufferId, -1)
				.sendToTarget();
	}

	public void PS_RegModel() {
		mWorkerThreadHandler.sendEmptyMessage(PS_RegModel);
	}

	public void PS_UpChar() {
		mWorkerThreadHandler.sendEmptyMessage(PS_UpChar);
	}

	public void PS_DownChar(int bufferId, byte[] model) {
		mWorkerThreadHandler.obtainMessage(PS_DownChar, bufferId, -1, model)
				.sendToTarget();
	}

	public void PS_Match() {
		mWorkerThreadHandler.sendEmptyMessage(PS_Match);
	}

	public void PS_StoreChar(int bufferId, int pageId) {
		mWorkerThreadHandler.obtainMessage(PS_StoreChar, bufferId, pageId)
				.sendToTarget();
	}

	public void PS_LoadChar(int bufferId, int pageId) {
		mWorkerThreadHandler.obtainMessage(PS_LoadChar, bufferId, pageId)
				.sendToTarget();
	}

	public void PS_Search(int bufferId, int startPageId, int pageNum) {
		mWorkerThreadHandler.obtainMessage(PS_Search, bufferId, startPageId,
				pageNum).sendToTarget();
	}

	public void PS_DeleteChar(int pageIDStart, int delNum) {
		mWorkerThreadHandler.obtainMessage(PS_DeleteChar, pageIDStart, delNum)
				.sendToTarget();
	}

	public void PS_Empty() {
		mWorkerThreadHandler.sendEmptyMessage(PS_Empty);
	}

	public void PS_Enroll() {
		mWorkerThreadHandler.sendEmptyMessage(PS_Enroll);
	}

	public void PS_Identify() {
		mWorkerThreadHandler.sendEmptyMessage(PS_Identify);
	}

	public void PS_Calibration() {
		mWorkerThreadHandler.sendEmptyMessage(PS_CALIBRATION);
	}

	public boolean isStop;

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
	
	public void register() {
		mWorkerThreadHandler.sendEmptyMessage(REGISTER);
	}

	public void validate() {
		mWorkerThreadHandler.sendEmptyMessage(VALIDATE);
	}

	public void register2() {
		mWorkerThreadHandler.sendEmptyMessage(REGISTER2);
	}

	public void validate2() {
		mWorkerThreadHandler.sendEmptyMessage(VALIDATE2);
	}

	private boolean registerFinger() {
		isStop = false;
		for (int i = 1; i < 3; i++) {
			if (i == 1) {
				mHandler.obtainMessage(SHOW_PROGRESSDIALOG,
						R.string.print_finger).sendToTarget();
			} else {
				mHandler.obtainMessage(SHOW_PROGRESSDIALOG,
						R.string.print_finger_again).sendToTarget();
			}
			int getImage = -1;
			do {
				if (!isStop) {
					getImage = fingerprint.PSGetImage();
				} else {
					return false;
				}
			} while (getImage != 0x00);
			mHandler.obtainMessage(SHOW_PROGRESSDIALOG, R.string.processing)
					.sendToTarget();
			byte[] image = fingerprint.PSUpImage();
			if (image == null) {
				mHandler.obtainMessage(UP_IMAGE_RESULT, R.string.up_image_fail)
						.sendToTarget();
				return false;
			}
			mHandler.obtainMessage(SHOW_FINGER_IMAGE, image).sendToTarget();
			int genChar = fingerprint.PSGenChar(i);
			if (genChar != 0x00) {
				return false;
			}
		}
		int regModel = fingerprint.PSRegModel();
		if (regModel != 0x00) {
			return false;
		}
		byte[] model = fingerprint.PSUpChar(2);
//		Log.i("whw", "model length=" + model.length);
		mHandler.obtainMessage(SHOW_FINGER_MODEL, model).sendToTarget();
		if (model == null) {
			return false;
		}
		return true;
	}

	private int registerFinger2() {
		isStop = false;
		for (int i = 1; i < 3; i++) {
			if (i == 1) {
				mHandler.obtainMessage(SHOW_PROGRESSDIALOG,
						R.string.print_finger).sendToTarget();
			} else {
				mHandler.obtainMessage(SHOW_PROGRESSDIALOG,
						R.string.print_finger_again).sendToTarget();
			}
			int getImage = -1;
			do {
				if (!isStop) {
					getImage = fingerprint.PSGetImage();
				} else {
					return -1;
				}
			} while (getImage != 0x00);
			mHandler.obtainMessage(SHOW_PROGRESSDIALOG, R.string.processing)
					.sendToTarget();
			byte[] image = fingerprint.PSUpImage();
			if (image == null) {
				mHandler.obtainMessage(UP_IMAGE_RESULT, R.string.up_image_fail)
						.sendToTarget();
				return -1;
			}
			mHandler.obtainMessage(SHOW_FINGER_IMAGE, image).sendToTarget();
			// mHandler.obtainMessage(UP_IMAGE_RESULT,
			// R.string.up_image_success)
			// .sendToTarget();
			int genChar = fingerprint.PSGenChar(i);
			if (genChar != 0x00) {
				return -1;
			}
		}
		int regModel = fingerprint.PSRegModel();
		if (regModel != 0x00) {
			return -1;
		}

		int pageId = fingerprint.getValidId();
		int storeResult = -1;
		if (pageId >= 0 && pageId <= 1009) {
			storeResult = fingerprint.PSStoreChar(1, pageId);
			return pageId;
		}
		Log.i("whw", "pageId=" + pageId + "  store=" + storeResult);

		return -1;
	}

	private int validateFinger2() {
		isStop = false;
		mHandler.obtainMessage(SHOW_PROGRESSDIALOG, R.string.print_finger)
				.sendToTarget();
		int getImage = -1;
		do {
			if (!isStop) {
				getImage = fingerprint.PSGetImage();
			} else {
				isStop = false;
				return -1;
			}
		} while (getImage != 0x00);
		mHandler.obtainMessage(SHOW_PROGRESSDIALOG, R.string.processing)
				.sendToTarget();
		byte[] image = fingerprint.PSUpImage();
		if (image == null) {
			return -1;
		}
		mHandler.obtainMessage(SHOW_FINGER_IMAGE, image).sendToTarget();
		int genChar = fingerprint.PSGenChar(1);
		if (genChar != 0x00) {
			return -1;
		}

		FingerprintAPI.Result result = fingerprint.PSSearch(1, 0, 1009);
		if (result.code == 0x00) {
			return result.pageId;
		}

		return -1;
	}

	public void validate(byte[] model) {
		mWorkerThreadHandler.obtainMessage(VALIDATE, model).sendToTarget();
	}

	private boolean validateFinger(byte[] model) {
		isStop = false;
		mHandler.obtainMessage(SHOW_PROGRESSDIALOG, R.string.print_finger)
				.sendToTarget();
		int getImage = -1;
		do {
			if (!isStop) {
				getImage = fingerprint.PSGetImage();
			} else {
				isStop = false;
				return false;
			}
		} while (getImage != 0x00);
		mHandler.obtainMessage(SHOW_PROGRESSDIALOG, R.string.processing)
				.sendToTarget();
		byte[] image = fingerprint.PSUpImage();
		if (image == null) {
			return false;
		}
		mHandler.obtainMessage(SHOW_FINGER_IMAGE, image).sendToTarget();
		int genChar = fingerprint.PSGenChar(1);
		if (genChar != 0x00) {
			return false;
		}

		int downChar = fingerprint.PSDownChar(2, model);
		if (downChar != 0x00) {
			return false;
		}

		boolean match = fingerprint.PSMatch();
		return match;
	}
}