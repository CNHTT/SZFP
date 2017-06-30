package android_serialport_api;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.google.code.microlog4android.Logger;

import java.util.ArrayList;
import java.util.List;


public class BeidouAPI {

	private static final byte[] OPEN_COMMAND = "D&C00040106".getBytes();
	private static final byte[] CLOSE_COMMAND = "D&C00040105".getBytes();

	/* 启动控制命令 */

	/**
	 * 模块冷启动命令
	 */
	public static final byte[] COLD_START_COMMAND = "$PMTK103*30\r\n"
			.getBytes();

	/**
	 * 模块温启动命令
	 */
	public static final byte[] WARM_START_COMMAND = "$PMTK102*31\r\n"
			.getBytes();

	/**
	 * 模块热启动命令
	 */
	public static final byte[] HOT_START_COMMAND = "$PMTK101*32\r\n".getBytes();

	/* 模式切换命令 */

	/**
	 * 使用纯GPS 模式操作
	 */
	public static final byte[] GPS_MODE_OPERATION_COMMAND = "$PMTK353,1,0,0,0,0*2A\r\n"
			.getBytes();

	/**
	 * 使用BDS+GPS 混星模式操作
	 */
	public static final byte[] GPS_BDS_MODE_OPERATION_COMMAND = "$PMTK353,1,0,0,0,1*2B\r\n"
			.getBytes();

	/**
	 * 使用纯BDS 模式操作
	 */
	public static final byte[] BDS_MODE_OPERATION_COMMAND = "$PMTK353,0,0,0,0,1*2A\r\n"
			.getBytes();

	/* 语句及其频度控制命令 */

	/**
	 * 模块只输出GLL 语句，频度为1HZ
	 */
	public static final byte[] GLL_STATEMENT_MODEL_COMMAND = "$PMTK314,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();

	/**
	 * 模块只输出RMC 语句，频度为1HZ
	 */
	public static final byte[] RMC_STATEMENT_MODEL_COMMAND = "$PMTK314,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();

	/**
	 * 模块只输出VTG 语句，频度为1HZ
	 */
	public static final byte[] VTG_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();

	/**
	 * 模块只输出GGA 语句，频度为1HZ
	 */
	public static final byte[] GGA_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();

	/**
	 * 模块只输出GSA 语句，频度为1HZ
	 */
	public static final byte[] GSA_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();

	/**
	 * 模块只输出GSV 语句，频度为1HZ
	 */
	public static final byte[] GSV_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0*29\r\n"
			.getBytes();

	/**
	 * 模块只输出ZDA 语句，频度为1HZ
	 */
	public static final byte[] ZDA_STATEMENT_MODEL_COMMAND = "$PMTK314,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0*29\r\n"
			.getBytes();

	/**
	 * 模块恢复为输出RMC，GGA，GSA，GSV（5 秒输出一次），GLL，VTG，ZDA
	 */
	public static final byte[] RMC_GGA_GSA_GSV_STATEMENT_MODEL_COMMAND = "$PMTK314,1,1,1,1,1,5,0,0,0,0,0,0,0,0,0,0,0,1,0*2D\r\n"
			.getBytes();

	public void receive(byte[] command) {
		SerialPortManager.getInstance().write(command);
	}

	private boolean isOpen;

	public boolean isOpen() {
		return isOpen;
	}

	OnOpenListener onOpenListener;
	OnCloseListener onCloseListener;

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		this.onOpenListener = onOpenListener;
	}

	public void setOnCloseListener(OnCloseListener onCloseListener) {
		this.onCloseListener = onCloseListener;
	}

	public interface OnOpenListener {
		void openSuccess();

		void openFail();
	}

	public interface OnCloseListener {
		void closeSuccess();

		void closeFail();
	}

	/**
	 * 函数说明：打开北斗模块
	 */
	public void open() {
		SerialPortManager.getInstance().write(OPEN_COMMAND);
		SystemClock.sleep(10);
		isOpen = true;
		SerialPortManager.getInstance().setLoopBuffer(looperBuffer);
		looperBuffer.start();
	}

	/**
	 * 函数说明：关闭北斗模块
	 */
	public void close() {
		SerialPortManager.getInstance().write(CLOSE_COMMAND);
		SystemClock.sleep(10);
		isOpen = true;
		SerialPortManager.getInstance().setLoopBuffer(null);
		looperBuffer.isStop = true;
	}

	public static final String GPS_Mode_Header = "$GP";
	public static final String BeiDou_GPS_Mode_Header = "$GN";
	public static final String BeiDou_Mode_Header = "$BD";

	/**
	 * 纯GPS 模式
	 */
	public static final int MODE_GPS = 1;
	/**
	 * GPS+BDS 混星模式
	 */
	public static final int MODE_GPS_BEIDOU = 2;
	/**
	 * 纯BDS 模式
	 */
	public static final int MODE_BEIDOU = 3;

	public static final int GGA = 1;
	public static final int GLL = 2;
	public static final int GSA = 3;
	public static final int GSV = 4;
	public static final int RMC = 5;
	public static final int VTG = 6;
	public static final int OPEN = 7;
	public static final int CLOSE = 8;

	private OnUpdateDataListener listerer;

	public void setOnUpdateDataListener(OnUpdateDataListener listerer) {
		this.listerer = listerer;
	}

	public void removeOnUpdateDataListener() {
		this.listerer = null;
	}

	public interface OnUpdateDataListener {
		/**
		 * 表明该语句为Global Positioning System Fix Data（GGA）GPS 定位信息
		 * @param mode GPS+BDS混星模式   或  纯GPS模式 或  模式 纯BDS模式
		 * @param UTC  时间
		 * @param Latitude 纬度ddmm.mmmm，度分格式（前导位数不足则补0）
		 * @param Indicator_N_S 纬度N（北纬）或S（南纬）
		 * @param Longitude 经度dddmm.mmmm，度分格式（前导位数不足则补0）
		 * @param Indicator_E_W 经度E（东经）或W（西经）
		 * @param PositionFixIndicator GPS 状态，0=未定位，1=非差分定位，2=差分定位，3=无效PPS，6=正在估算
		 * @param satellitesUsed 正在使用的卫星数量（00 - 12）（前导位数不足则补0）
		 * @param HDOP HDOP 水平精度因子（0.5 - 99.9）
		 * @param MSLAltitude 海拔高度（-9999.9 - 99999.9）
		 * @param units1   地球椭球面相对大地水准面的高度
		 * @param GeoidSeparation   差分时间（从最近一次接收到差分信号开始的秒数，如果不是差分定位将为空）
		 * @param units2     差分站ID 号0000 - 1023（前导位数不足则补0，如果不是差分定位将为空）
		 * @param AgeOfDiffCorr  Null fields when DGPS is not used
		 * @param DiffRefStationID
		 */
		public void GGA(int mode, String UTC, String Latitude,
						String Indicator_N_S, String Longitude, String Indicator_E_W,
						String PositionFixIndicator, String satellitesUsed,
						String HDOP, String MSLAltitude, String units1,
						String GeoidSeparation, String units2, String AgeOfDiffCorr,
						String DiffRefStationID);

		/**
		 * 表明该语句为Geographic Position（GLL）地理定位信息
		 * @param mode GPS+BDS混星模式   或  纯GPS模式 或  模式 纯BDS模式
		 * @param Latitude 纬度ddmm.mmmm，度分格式（前导位数不足则补0）
		 * @param Indicator_N_S 纬度N（北纬）或S（南纬）
		 * @param Longitude 经度dddmm.mmmm，度分格式（前导位数不足则补0）
		 * @param Indicator_E_W 经度E（东经）或W（西经）
		 * @param UTC UTC 时间，hhmmss.sss 格式
		 * @param location 状态，A=定位，V=未定位
		 */
		public void GLL(int mode, String Latitude, String Indicator_N_S,
						String Longitude, String Indicator_E_W, String UTC,
						String location);

		/**
		 * 表明该语句为GPS DOP and Active Satellites（GSA）当前卫星信息
		 * @param mode GPS+BDS混星模式   或  纯GPS模式 或  模式 纯BDS模式
		 * @param mode1 定位模式，A=自动手动2D/3D，M=手动2D/3D
		 * @param mode2 定位类型，1=未定位，2=2D 定位，3=3D 定位
		 * @param IDOfSatelliteUsedList PRN 码（伪随机噪声码），第1-12信道正在使用的卫星PRN 码编号（00）（前导位数不足则补0）
		 * @param PDOP 综合位置精度因子（0.5 - 99.9）
		 * @param HDOP 水平精度因子（0.5 - 99.9）
		 * @param VDOP 垂直精度因子（0.5 - 99.9）
		 */
		public void GSA(int mode, String mode1, String mode2,
						List<String> IDOfSatelliteUsedList, String PDOP, String HDOP,
						String VDOP);

		/**
		 * 表明该语句为GPS Satellites in View（GSV）可见卫星信息
		 * @param mode GPS+BDS混星模式   或  纯GPS模式 或  模式 纯BDS模式
		 * @param TotalNumberOfMessage 本次GSV 语句的总数目（1 - 3）
		 * @param MessageNumber 本条GSV 语句是本次GSV 语句的第几条（1 - 3）
		 * @param SatelliteInView 当前可见卫星总数（00 - 12）（前导位数不足则补0）
		 * @param satelliteList
		 */
		public void GSV(int mode, String TotalNumberOfMessage,
						String MessageNumber, String SatelliteInView,
						List<Satellite> satelliteList);

		/**
		 * 表明该语句为Recommended Minimum SpecificGPS/TRANSIT Data（RMC）推荐最小定位信息
		 * @param mode GPS+BDS混星模式   或  纯GPS模式 或  模式 纯BDS模式
		 * @param UTC UTC 时间，hhmmss.sss 格式
		 * @param Status 状态，A=定位，V=未定位
		 * @param Latitude 纬度ddmm.mmmm，度分格式（前导位数不足则补0）
		 * @param Indicator_N_S 纬度N（北纬）或S（南纬）
		 * @param Longitude 经度dddmm.mmmm，度分格式（前导位数不足则补0）
		 * @param Indicator_E_W 经度E（东经）或W（西经）
		 * @param SpeedOverGround 速度，节，Knots
		 * @param CourseOverGround 方位角，度
		 * @param Date UTC 日期，DDMMYY 格式
		 * @param MagneticVariation 磁偏角，（000 - 180）度（前导位数不足则补0）
		 * @param VariationSense 磁偏角方向，E=东W=西
		 * @param Mode
		 * A:autonomous
		 * D:DPGS
		 * E:DR
		 * N:data not valid
		 * R:coarse position
		 * S:simulator
		 */
		public void RMC(int mode, String UTC, String Status, String Latitude,
						String Indicator_N_S, String Longitude, String Indicator_E_W,
						String SpeedOverGround, String CourseOverGround, String Date,
						String MagneticVariation, String VariationSense, String Mode);

		/**
		 * 表明该语句为Track Made Good and Ground Speed（VTG）地面速度信息
		 * @param mode GPS+BDS混星模式   或  纯GPS模式 或  模式 纯BDS模式
		 * @param CourseOverGround1 运动角度，000 - 359，（前导位数不足则补0）
		 * @param Reference1 T=真北参照系
		 * @param CourseOverGround2 运动角度，000 - 359，（前导位数不足则补0）
		 * @param Reference2 M=磁北参照系
		 * @param SpeedOverGround1 水平运动速度（0.00）（前导位数不足则补0）
		 * @param Units1 N=节，Knots
		 * @param SpeedOverGround2 水平运动速度（0.00）（前导位数不足则补0）
		 * @param Units2 K=公里/时，km/h
		 * @param Mode
		 * A:autonomous
		 * D:DGPS
		 * E:DR
		 * N:data not valid
		 * R:coarse position
		 * S:simulator
		 */
		public void VTG(int mode, String CourseOverGround1, String Reference1,
						String CourseOverGround2, String Reference2,
						String SpeedOverGround1, String Units1,
						String SpeedOverGround2, String Units2,String Mode);
	}

	public class Satellite {
		/**
		 * PRN 码（伪随机噪声码）（01 - 32）（前导位数不足则补0）
		 */
		String SatelliteID;
		/**
		 * 卫星仰角（00 - 90）度（前导位数不足则补0）
		 */
		String Elevation;
		/**
		 * 卫星方位角（00 - 359）度（前导位数不足则补0）
		 */
		String Azimuth;
		/**
		 * 信噪比（00－99）dbHz
		 */
		String SNR;

		public String getSatelliteID() {
			return SatelliteID;
		}

		public void setSatelliteID(String satelliteID) {
			SatelliteID = satelliteID;
		}

		public String getElevation() {
			return Elevation;
		}

		public void setElevation(String elevation) {
			Elevation = elevation;
		}

		public String getAzimuth() {
			return Azimuth;
		}

		public void setAzimuth(String azimuth) {
			Azimuth = azimuth;
		}

		public String getSNR() {
			return SNR;
		}

		public void setSNR(String sNR) {
			SNR = sNR;
		}

	}

	private LoopBufferBeidou looperBuffer = new LoopBufferBeidou();

	public class LoopBufferBeidou extends Thread implements LooperBuffer {
		private byte[] mBuffer = new byte[1024 * 50];
		private int packetStartIndex = 0;
		private int dataLength = 0;
		private boolean isStop = false;

		@Override
		public synchronized void add(byte[] buf) {
			System.arraycopy(buf, 0, mBuffer, dataLength, buf.length);
			dataLength += buf.length;
		}

		@Override
		public synchronized byte[] getFullPacket() {
			byte[] temp = null;
			if (dataLength > 0 && mBuffer[0] == '$'
					&& mBuffer[dataLength - 5] == '*') {
				temp = new byte[dataLength];
				System.arraycopy(mBuffer, 0, temp, 0, dataLength);
				dataLength = 0;
			}
			return temp;
		}

		@Override
		public void run() {
			while (!isStop) {
				byte[] data = getFullPacket();
				if (data != null) {
					String dataStr = new String(data);
					String[] strs = dataStr.split("\r\n");
					for (int i = 0; i < strs.length; i++) {
						Log.i("whw", "strs=" + strs[i]);
						if (strs[i].startsWith(GPS_Mode_Header)
								|| strs[i].startsWith(BeiDou_GPS_Mode_Header)
								|| strs[i].startsWith(BeiDou_Mode_Header)) {
							String[] items = strs[i].split(",");
							if (items[0].endsWith("GGA")) {
								mWorkerThreadHandler.obtainMessage(GGA, items)
										.sendToTarget();
							} else if (items[0].endsWith("GLL")) {
								mWorkerThreadHandler.obtainMessage(GLL, items)
										.sendToTarget();
							} else if (items[0].endsWith("GSA")) {
								mWorkerThreadHandler.obtainMessage(GSA, items)
										.sendToTarget();
							} else if (items[0].endsWith("GSV")) {
								logger.debug(strs[i]);
								mWorkerThreadHandler.obtainMessage(GSV, items)
										.sendToTarget();
							} else if (items[0].endsWith("RMC")) {
								mWorkerThreadHandler.obtainMessage(RMC, items)
										.sendToTarget();
							} else if (items[0].endsWith("VTG")) {
								mWorkerThreadHandler.obtainMessage(VTG, items)
										.sendToTarget();
							}
						}
					}
				}
				SystemClock.sleep(10);
			}
		}
	}

	private Handler mWorkerThreadHandler;

	Logger logger;

	public BeidouAPI(Logger logger) {
		this.logger = logger;
		mWorkerThreadHandler = new WorkerHandler();
	}

	protected class WorkerHandler extends Handler {

		private String getString(String srcStr) {
			int index = srcStr.indexOf("*");
			if (index >0) {
				return srcStr.substring(0, index);
			} else {
				return "";
			}
		}

		@Override
		public void handleMessage(Message msg) {
			if (listerer == null) {
				return;
			}
			switch (msg.what) {
				case GGA:
					String[] ggaStr = (String[]) msg.obj;
					listerer.GGA(
							ggaStr[0].startsWith(GPS_Mode_Header) ? MODE_GPS
									: (ggaStr[0].startsWith(BeiDou_GPS_Mode_Header) ? MODE_GPS_BEIDOU
									: MODE_BEIDOU), ggaStr[1], ggaStr[2],
							ggaStr[3], ggaStr[4], ggaStr[5], ggaStr[6], ggaStr[7],
							ggaStr[8], ggaStr[9], ggaStr[10], ggaStr[11],
							ggaStr[12], ggaStr[13], getString(ggaStr[14]));
					break;
				case GLL:
					String[] gllStr = (String[]) msg.obj;
					// Log.i("whw", "gllStr="+gllStr);
					listerer.GLL(
							gllStr[0].startsWith(GPS_Mode_Header) ? MODE_GPS
									: (gllStr[0].startsWith(BeiDou_GPS_Mode_Header) ? MODE_GPS_BEIDOU
									: MODE_BEIDOU), gllStr[1], gllStr[2],
							gllStr[3], gllStr[4], gllStr[5], gllStr[6]);
					break;
				case GSA:
					String[] gsaStr = (String[]) msg.obj;
					List<String> list = new ArrayList<String>();
					for (int i = 3; i < gsaStr.length - 3; i++) {
						list.add(gsaStr[i]);
					}
					listerer.GSA(
							gsaStr[0].startsWith(GPS_Mode_Header) ? MODE_GPS
									: (gsaStr[0].startsWith(BeiDou_GPS_Mode_Header) ? MODE_GPS_BEIDOU
									: MODE_BEIDOU), gsaStr[1], gsaStr[2],
							list, gsaStr[gsaStr.length - 3],
							gsaStr[gsaStr.length - 2],
							getString(gsaStr[gsaStr.length - 1]));
					break;
				case GSV:
					String[] gsvStr = (String[]) msg.obj;
					List<Satellite> satelliteList = new ArrayList<Satellite>();
					Satellite satellite = null;
					for (int i = 0; i < (gsvStr.length - 5) / 4; i++) {
						satellite = new Satellite();
						satellite.setSatelliteID(gsvStr[3 + i * 4 + 1]);
						satellite.setElevation(gsvStr[3 + i * 4 + 2]);
						satellite.setAzimuth(gsvStr[3 + i * 4 + 3]);
						satellite.setSNR(gsvStr[3 + i * 4 + 4]);
						satelliteList.add(satellite);
					}
					listerer.GSV(
							gsvStr[0].startsWith(GPS_Mode_Header) ? MODE_GPS
									: (gsvStr[0].startsWith(BeiDou_GPS_Mode_Header) ? MODE_GPS_BEIDOU
									: MODE_BEIDOU), gsvStr[1], gsvStr[2],
							satelliteList.size() == 0 ? getString(gsvStr[3])
									: gsvStr[3], satelliteList);
					break;
				case RMC:
					String[] rmcStr = (String[]) msg.obj;
					listerer.RMC(
							rmcStr[0].startsWith(GPS_Mode_Header) ? MODE_GPS
									: (rmcStr[0].startsWith(BeiDou_GPS_Mode_Header) ? MODE_GPS_BEIDOU
									: MODE_BEIDOU), rmcStr[1], rmcStr[2],
							rmcStr[3], rmcStr[4], rmcStr[5], rmcStr[6], rmcStr[7],
							rmcStr[8], rmcStr[9], rmcStr[10], rmcStr[11],
							getString(rmcStr[12]));
					break;
				case VTG:
					String[] vtgStr = (String[]) msg.obj;
					listerer.VTG(
							vtgStr[0].startsWith(GPS_Mode_Header) ? MODE_GPS
									: (vtgStr[0].startsWith(BeiDou_GPS_Mode_Header) ? MODE_GPS_BEIDOU
									: MODE_BEIDOU), vtgStr[1], vtgStr[2],
							vtgStr[3], vtgStr[4], vtgStr[5], vtgStr[6], vtgStr[7],
							vtgStr[8], getString(vtgStr[9]));
					break;
				case OPEN:
					if (onOpenListener != null) {
						if ((Boolean) msg.obj) {
							onOpenListener.openSuccess();
						} else {
							onOpenListener.openFail();
						}
					}
					break;
				case CLOSE:
					if (onCloseListener != null) {
						if ((Boolean) msg.obj) {
							onCloseListener.closeSuccess();
						} else {
							onCloseListener.closeFail();
						}
					}
					break;
				default:
					break;
			}
		}
	}

}
