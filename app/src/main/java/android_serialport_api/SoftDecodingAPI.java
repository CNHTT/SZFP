package android_serialport_api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;

/**
 * SoftDecodingApi
 */
public class SoftDecodingAPI {
	private static final String SCANNER_POWER_ON = "SCANNER_POWER_ON";
	private static final String SCANNER_OUTPUT_MODE = "SCANNER_OUTPUT_MODE";
	private static final String SCANNER_TERMINAL_CHAR = "SCANNER_TERMINAL_CHAR";
	private static final String SCANNER_PREFIX = "SCANNER_PREFIX";
	private static final String SCANNER_SUFFIX = "SCANNER_SUFFIX";
	private static final String SCANNER_VOLUME = "SCANNER_VOLUME";
	private static final String SCANNER_PLAYTONE_MODE = "SCANNER_PLAYTONE_MODE";
	private static final int ENABLE = 1;
	private static final int DISENABLE = 0;

	private IBarCodeData inter;

	private Context mcontext;
	private String Prefix = "";
	private String Suffix = "";
	private String barcode = "";

	private boolean isLoop;

	public SoftDecodingAPI(Context context, IBarCodeData inter) {
		this.mcontext = context;
		IntentFilter filter = new IntentFilter(
				"com.android.server.scannerservice.broadcast");
		mcontext.registerReceiver(receiver, filter);
		this.inter = inter;
	}

	/**
	 * Get system settings
	 */
	public void getSettings() {
		int mPowerOnOff = Settings.System.getInt(mcontext.getContentResolver(),
				SCANNER_POWER_ON, 1);
		int mOutputMode = Settings.System.getInt(mcontext.getContentResolver(),
				SCANNER_OUTPUT_MODE, -1);
		int mTerminalChar = Settings.System.getInt(
				mcontext.getContentResolver(), SCANNER_TERMINAL_CHAR, -1);
		String mPrefix = Settings.System.getString(
				mcontext.getContentResolver(), SCANNER_PREFIX);
		Prefix = mPrefix;
		String mSuffix = Settings.System.getString(
				mcontext.getContentResolver(), SCANNER_SUFFIX);
		Suffix = mSuffix;
		int mVolume = Settings.System.getInt(mcontext.getContentResolver(),
				SCANNER_VOLUME, 0);
		int mPlayoneMode = Settings.System.getInt(
				mcontext.getContentResolver(), SCANNER_PLAYTONE_MODE, 0);
		inter.getSettings(mPowerOnOff, mOutputMode, mTerminalChar, mPrefix,
				mSuffix, mVolume, mPlayoneMode);
	}

	/**
	 * set barcode
	 * 
	 * @param PowerOnOff
	 * @param OutputMode
	 * @param TerminalChar
	 * @param Prefix
	 * @param Suffix
	 * @param Volume
	 * @param PlayoneMode
	 */
	public void setSettings(int PowerOnOff, int OutputMode, int TerminalChar,
			String Prefix, String Suffix, int Volume, int PlayoneMode) {
		Settings.System.putInt(mcontext.getContentResolver(), SCANNER_POWER_ON,
				PowerOnOff);
		Settings.System.putInt(mcontext.getContentResolver(),
				SCANNER_OUTPUT_MODE, OutputMode);
		Settings.System.putInt(mcontext.getContentResolver(),
				SCANNER_TERMINAL_CHAR, TerminalChar);
		Settings.System.putString(mcontext.getContentResolver(),
				SCANNER_PREFIX, Prefix);
		Settings.System.putString(mcontext.getContentResolver(),
				SCANNER_SUFFIX, Suffix);
		Settings.System.putInt(mcontext.getContentResolver(), SCANNER_VOLUME,
				Volume);
		switch (PlayoneMode) {
		case 0:
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_SOUND", 1);
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_VIBRATION", 0);
			break;
		case 1:
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_SOUND", 0);
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_VIBRATION", 1);
			break;
		case 2:
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_SOUND", 1);
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_VIBRATION", 1);
			break;
		case 3:
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_SOUND", 0);
			Settings.System.putInt(mcontext.getContentResolver(),
					"SCANNER_VIBRATION", 0);
			break;
		default:
			break;
		}
		Settings.System.putInt(mcontext.getContentResolver(),
				SCANNER_PLAYTONE_MODE, PlayoneMode);

		Intent intent = new Intent(
				"com.android.server.scannerservice.settingchange", null);
		mcontext.sendOrderedBroadcast(intent, null);
		inter.setSettingsSuccess();
	}

	/**
	 * Setting scanner switch
	 * 
	 * @param flag
	 *            true:open;false:close
	 */
	public void setScannerStatus(boolean flag) {
		if (flag) {
			Intent intent = new Intent(
					"com.android.server.scannerservice.onoff");
			intent.putExtra("scanneronoff", ENABLE);
			mcontext.sendBroadcast(intent);
		} else {
			Intent intent = new Intent(
					"com.android.server.scannerservice.onoff");
			intent.putExtra("scanneronoff", DISENABLE);
			mcontext.sendBroadcast(intent);
		}
	}

	/**
	 * Start scanning
	 */
	public void scan() {
		inter.sendScan();
		Intent startIntent = new Intent(
				"android.intent.action.SCANNER_BUTTON_DOWN", null);
		mcontext.sendOrderedBroadcast(startIntent, null);
	}

	/**
	 * Close scanning
	 */
	public void closeScan() {
		Intent endIntent = new Intent(
				"android.intent.action.SCANNER_BUTTON_UP", null);
		mcontext.sendOrderedBroadcast(endIntent, null);
	}

	/**
	 * Continuous scanning
	 */
	public void ContinuousScanning() {
		isLoop = true;
		new Thread() {
			@Override
			public void run() {
				while (isLoop) {
					scan();
					try {
						Thread.sleep((int) (1 * 1500));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					closeScan();
				}
			}
		}.start();
	}

	/**
	 * close Continuous scanning
	 */
	public void CloseScanning() {
		isLoop = false;
	}

	/**
	 * Bar code value
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"com.android.server.scannerservice.broadcast")) {
				barcode = intent.getExtras().getString("scannerdata");
				inter.onBarCodeData(Prefix + barcode + Suffix);
			}
		}
	};

	public interface IBarCodeData {
		void sendScan();

		void onBarCodeData(String data);

		void getSettings(int PowerOnOff, int OutputMode, int TerminalChar,
                         String Prefix, String Suffix, int Volume, int PlayoneMode);

		void setSettingsSuccess();
	};
}