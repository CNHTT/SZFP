/*
 * BarCodeReader.java
 *
 *	Class used to access a Motorola Solutions imaging bar code reader.
 *
 *	Copyright (C) 2011 Motorola Solutions, Inc.
 */

package com.hiklife.rfidapi;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.io.IOException;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.ImageFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * The BarCodeReader class is used to set bar code reader settings, start/stop preview,
 * snap pictures, and capture frames for encoding for video.  This class is a
 * client for the Camera service, which manages the actual Camera hardware.
 *
 * <p>To decode bar codes with this class, use the following steps:</p>
 *
 * <ol>
 * <li>Obtain an instance of BarCodeReader with {@link #open(int)}.
 *
 * <li>Get the current settings with {@link #getParameters()}.
 *
 * <li>If necessary, modify the returned {@link Parameters} object and call
 * {@link #setParameters(Parameters)}.
 *
 * <li>Call {@link #setDecodeCallback(DecodeCallback)} to register a
 * bar code decode event handler.
 *
 * <li>If a view finder is desired, pass a fully initialized {@link SurfaceHolder} to
 * {@link #setPreviewDisplay(SurfaceHolder)}.
 *
 * <li>To begin a decode session, call {@link #startDecode()} or {@link #startHandsFreeDecode(int)}.
 * Your registered DecodeCallback will be called when a successful decode occurs or if
 * the configured timeout expires.
 *
 * <li>Call {@link #stopDecode()} to end the decode session.
 *
 * <li><b>Important:</b> Call {@link #release()} to release the BarCodeReader for
 * use by other applications.  Applications should release the BarCodeReader
 * immediately in {@link android.app.Activity#onPause()} (and re-{@link #open()}
 * it in {@link android.app.Activity#onResume()}).
 * </ol>
 *
 * <p>This class is not thread-safe, and is meant for use from one event thread.
 * Callbacks will be invoked on the event thread {@link #open(int)} was called from.
 * This class's methods must never be called from multiple threads at once.</p>
 */

public class BarCodeReader
{
	private static final String TAG = "BarCodeReader";

	// These match the enums in frameworks/base/include/bcreader/BCReader.h
	private static final int BCRDR_MSG_ERROR			= 0x000001;
	private static final int BCRDR_MSG_SHUTTER			= 0x000002;
	private static final int BCRDR_MSG_FOCUS			= 0x000004;
	private static final int BCRDR_MSG_ZOOM				= 0x000008;
	private static final int BCRDR_MSG_PREVIEW_FRAME	= 0x000010;
	private static final int BCRDR_MSG_VIDEO_FRAME		= 0x000020;
	private static final int BCRDR_MSG_POSTVIEW_FRAME	= 0x000040;
	private static final int BCRDR_MSG_RAW_IMAGE		= 0x000080;
	private static final int BCRDR_MSG_COMPRESSED_IMAGE	= 0x000100;
	private static final int BCRDR_MSG_LAST_DEC_IMAGE	= 0x000200;
	private static final int BCRDR_MSG_DEC_COUNT		= 0x000400;
	// Add bar code reader specific values here
	private static final int BCRDR_MSG_DECODE_COMPLETE	= 0x010000;
	private static final int BCRDR_MSG_DECODE_TIMEOUT	= 0x020000;
	private static final int BCRDR_MSG_DECODE_CANCELED	= 0x040000;
	private static final int BCRDR_MSG_DECODE_ERROR		= 0x080000;
	private static final int BCRDR_MSG_DECODE_EVENT		= 0x100000;
	private static final int BCRDR_MSG_FRAME_ERROR		= 0x200000;
	private static final int BCRDR_MSG_ALL_MSGS			= 0x3F03FF;

	private static final int DECODE_MODE_PREVIEW		= 1;
	private static final int DECODE_MODE_VIEWFINDER		= 2;
	private static final int DECODE_MODE_VIDEO			= 3;

	private int						mNativeContext;		// accessed by native methods
	private EventHandler			mEventHandler;
	private AutoFocusCallback		mAutoFocusCallback;
	private DecodeCallback			mDecodeCallback;
	private ErrorCallback			mErrorCallback;
	private VideoCallback			mVideoCallback;
	private PictureCallback			mSnapshotCallback;
	private PreviewCallback			mPreviewCallback;
	private OnZoomChangeListener	mZoomListener;
	private boolean					mOneShot;
	private boolean					mWithBuffer;

	/////////////////////////////////////////////////////////////////
	// Private native functions
	/////////////////////////////////////////////////////////////////

	private native final void native_autoFocus();
	private native final void native_cancelAutoFocus();
	private native final String native_getParameters();
	private native final void native_release();
	private native final int setNumParameter(int paramNum, int paramVal);
	private native final int setStrParameter(int paramNum, String paramVal);
	private native final void native_setParameters(String params);
	private native final void native_setup(Object reader_this, int readerId);
	private native final void native_setup(Object reader_this, int readerId, Object currentContext);
	private native final void native_startPreview(int mode);
	private native final void native_takePicture();
	private native final void setHasPreviewCallback(boolean installed, boolean manualBuffer);
	private native final void setPreviewDisplay(Surface surface);

	/////////////////////////////////////////////////////////////////
	// Public native functions
	/////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of physical readers available on this device.
	 */
	public native static int getNumberOfReaders();

	/**
	 * Returns the information about a particular reader.
	 * If {@link #getNumberOfReaders()} returns N, the valid id is 0 to N-1.
	 */
	public native static void getReaderInfo(int readerId, ReaderInfo readerInfo);

	/**
	 * Re-locks the reader to prevent other processes from accessing it.
	 * BarCodeReader objects are locked by default unless {@link #unlock()} is
	 * called.  Normally {@link #reconnect()} is used instead.
	 *
	 * <p>If you are not recording video, you probably do not need this method.
	 *
	 * @throws RuntimeException if the reader cannot be re-locked (for
	 *	 example, if the reader is still in use by another process).
	 */
	public native final void lock();

	/**
	 * Unlocks the reader to allow another process to access it.
	 * Normally, the reader is locked to the process with an active BarCodeReader
	 * object until {@link #release()} is called.  To allow rapid handoff
	 * between processes, you can call this method to release the reader
	 * temporarily for another process to use; once the other process is done
	 * you can call {@link #reconnect()} to reclaim the reader.
	 *
	 * <p>This must be done before calling
	 * {@link android.media.MediaRecorder#setCamera(BarCodeReader)}.
	 *
	 * <p>If you are not recording video, you probably do not need this method.
	 *
	 * @throws RuntimeException if the reader cannot be unlocked.
	 */
	public native final void unlock();

	/**
	 * Reconnects to the reader service after another process used it.
	 * After {@link #unlock()} is called, another process may use the
	 * reader; when the process is done, you must reconnect to the reader,
	 * which will re-acquire the lock and allow you to continue using the
	 * reader.
	 *
	 * <p>This must be done after {@link android.media.MediaRecorder} is
	 * done recording if {@link android.media.MediaRecorder#setReader(BarCodeReader)}
	 * was used.
	 *
	 * <p>If you are not recording video, you probably do not need this method.
	 *
	 * @throws IOException if a connection cannot be re-established (for
	 *	 example, if the reader is still in use by another process).
	 */
	public native final void reconnect() throws IOException;

	/**
	 * Returns the value of a specified bar code reader numeric property or BCR_ERROR
	 * if the specified property number is invalid.
	 */
	public native final int getNumProperty(int propNum);

	/**
	 * Returns the value of a specified bar code reader string property or null
	 * if the specified property number is invalid.
	 */
	public native final String getStrProperty(int propNum);

	/**
	 * Returns the value of a specified bar code reader numeric parameter or BCR_ERROR
	 * if the specified parameter number is invalid.
	 */
	public native final int getNumParameter(int paramNum);

	/**
	 * Returns the value of a specified bar code reader string parameter or BCR_ERROR
	 * if the specified parameter number is invalid.
	 */
	public native final String getStrParameter(int paramNum);

	/**
	 * Sets the value of a specified bar code reader numeric parameter.
	 * Returns BCR_SUCCESS if successful or BCR_ERROR if the specified parameter number
	 * or value is invalid.
	 */
	public final int setParameter(int paramNum, int paramVal)
	{
		return(setNumParameter(paramNum, paramVal));
	}

	/**
	 * Sets the value of a specified bar code reader string parameter.
	 *
	 * @param paramNum	The parameter number to set
	 * @param paramVal	The new value for the parameter
	 *
	 * @return BCR_SUCCESS if successful or BCR_ERROR if the specified parameter number
	 * or value is invalid.
	 */
	public final int setParameter(int paramNum, String paramVal)
	{
		return(setStrParameter(paramNum, paramVal));
	}

	/**
	 * Sets all bar code reader parameters to their default values.
	 */
	public native final void setDefaultParameters();

	/**
	 * Adds a pre-allocated buffer to the preview callback buffer queue.
	 * Applications can add one or more buffers to the queue. When a preview
	 * frame arrives and there is still at least one available buffer, the
	 * buffer will be used and removed from the queue. Then preview callback is
	 * invoked with the buffer. If a frame arrives and there is no buffer left,
	 * the frame is discarded. Applications should add buffers back when they
	 * finish processing the data in them.
	 *
	 * <p>The size of the buffer is determined by multiplying the preview
	 * image width, height, and bytes per pixel.  The width and height can be
	 * read from {@link Parameters#getPreviewSize()}.  Bytes per pixel
	 * can be computed from
	 * {@link ImageFormat#getBitsPerPixel(int)} / 8,
	 * using the image format from {@link Parameters#getPreviewFormat()}.
	 *
	 * <p>This method is only necessary when
	 * {@link #setPreviewCallbackWithBuffer(PreviewCallback)} is used.  When
	 * {@link #setOneShotPreviewCallback(PreviewCallback)} is used, buffers
	 * are automatically allocated.
	 *
	 * @param callbackBuffer the buffer to add to the queue.
	 *	 The size should be width * height * bits_per_pixel / 8.
	 * @see #setPreviewCallbackWithBuffer(PreviewCallback)
	 */
	public native final void addCallbackBuffer(byte[] callbackBuffer);

	/**
	 * Update the scanner Firmware. 
	 * @param FilePath - Full path with the filename
	 * @param forceDownload - whether to force download or not - recommended true   
	 * @param IgnoreSignature - whether to ignore signature - recommended false
	 */
	public native final int FWUpdate(String FilePath, boolean forceDownload, boolean IgnoreSignature);
	
	/**
	 * Starts capturing frames in video mode. If a surface has been supplied
	 * with {@link #setPreviewDisplay(SurfaceHolder)}, the frames will
	 * be drawn to the surface.
	 *
	 * <p>{@link VideoCallback#onVideoFrame(format, width, height, byte[], BarCodeReader)}
	 * will be called when preview data becomes available. The data passed will be
	 * in the format and resolution specified by ParamNum.IMG_FILE_FORMAT and
	 * ParamNum.IMG_VIDEOSUB.
	 */
	public final void startVideoCapture(VideoCallback cb)
	{
		mVideoCallback = cb;
		native_startPreview(DECODE_MODE_VIDEO);
	}

	/**
	 * Starts capturing frames in view finder mode in preparation of taking a snapshot.
	 * If a surface has been supplied with {@link #setPreviewDisplay(SurfaceHolder)},
	 * the frames will be drawn to the surface.
	 */
	public final void startViewFinder()
	{
		native_startPreview(DECODE_MODE_VIEWFINDER);
	}

	/**
	 * Starts capturing frames in preview mode. If a surface has been supplied
	 * with {@link #setPreviewDisplay(SurfaceHolder)}, the frames will
	 * be drawn to the surface.
	 *
	 * <p>If {@link #setOneShotPreviewCallback(PreviewCallback)} or
	 * {@link #setPreviewCallbackWithBuffer(PreviewCallback)} was
	 * called, {@link PreviewCallback#onPreviewFrame(byte[], BarCodeReader)}
	 * will be called when preview data becomes available. 
	 *
	 * <p>If {@link #setImageCallback(BarCodeReader.ImageCallback)} was
	 * called, {@link PreviewCallback#onVideoFrame(format, width, height, byte[], BarCodeReader)}
	 * will be called when preview data becomes available. The data passed will be
	 * in the format and resolution specified by ParamNum.IMG_FILE_FORMAT and
	 * ParamNum.IMG_VIDEOSUB.
	 */
	public final void startPreview()
	{
		native_startPreview(DECODE_MODE_PREVIEW);
	}

	/**
	 * Stops capturing and drawing preview frames to the surface, and
	 * resets the reader for a future call to {@link #startPreview()}.
	 */
	public native final void stopPreview();

	/**
	 * Starts capturing frames and passes the captured frames to the decoder.
	 * If a surface has been supplied with {@link #setPreviewDisplay(SurfaceHolder)},
	 * the frames will be drawn to the surface. When a decode occurs or timeout
	 * expires and {@link #setDecodeCallback(DecodeCallback)} was called,
	 * {@link #BarCodeReader.DecodeCallback.onDecodeComplete(int, int, byte[], BarCodeReader)}
	 * will be called with the decode results.
	 */
	public native final void startDecode();

	/**
	 * Starts capturing frames and passes the captured frames to the decoder.
	 * If a surface has been supplied with {@link #setPreviewDisplay(SurfaceHolder)},
	 * the frames will be drawn to the surface. If motion is detected, a motion event
	 * is generated. If a decode occurs a decode event is generated. Decoding
	 * continues until {@link #stopDecode()} is called.
	 *
	 * @param mode	Indicates the trigger mode to use. It must be either
	 *	{@link #ParamVal.HANDSFREE} or {@link #ParamVal.AUTO_AIM}.
	 *
	 * @return BCR_SUCCESS if hands-free mode is successfully started or BCR_ERROR
	 * if an invalid mode is specified or if a decode session is already in progress.
	 */
	public native final int startHandsFreeDecode(int mode);

	/**
	 * Stops capturing and decoding frames.
	 */
	public native final void stopDecode();

	/** Return current preview state.
	 *
	 * FIXME: Unhide before release
	 * @hide
	 */
	public native final boolean previewEnabled();

	/**
	 * Zooms to the requested value smoothly. The driver will notify {@link
	 * OnZoomChangeListener} of the zoom value and whether zoom is stopped at
	 * the time. For example, suppose the current zoom is 0 and startSmoothZoom
	 * is called with value 3. The
	 * {@link OnZoomChangeListener#onZoomChange(int, boolean, BarCodeReader)}
	 * method will be called three times with zoom values 1, 2, and 3.
	 * Applications can call {@link #stopSmoothZoom} to stop the zoom earlier.
	 * Applications should not call startSmoothZoom again or change the zoom
	 * value before zoom stops. If the supplied zoom value equals to the current
	 * zoom value, no zoom callback will be generated. This method is supported
	 * if {@link com.motorolasolutions.adc.decoder.BarCodeReader.Parameters#isSmoothZoomSupported}
	 * returns true.
	 *
	 * @param value	zoom value. The valid range is 0 to {@link
	 *				com.motorolasolutions.adc.decoder.BarCodeReader.Parameters#getMaxZoom}.
	 * @throws IllegalArgumentException if the zoom value is invalid.
	 * @throws RuntimeException if the method fails.
	 * @see #setZoomChangeListener(OnZoomChangeListener)
	 */
	public native final void startSmoothZoom(int value);

	/**
	 * Stops the smooth zoom. Applications should wait for the {@link
	 * OnZoomChangeListener} to know when the zoom is actually stopped. This
	 * method is supported if {@link
	 * com.motorolasolutions.adc.decoder.BarCodeReader.Parameters#isSmoothZoomSupported} is true.
	 *
	 * @throws RuntimeException if the method fails.
	 */
	public native final void stopSmoothZoom();

	/**
	 * Set the clockwise rotation of preview display in degrees. This affects
	 * the preview frames and the picture displayed after snapshot. This method
	 * is useful for portrait mode applications. Note that preview display of
	 * front-facing readers is flipped horizontally before the rotation, that
	 * is, the image is reflected along the central vertical axis of the reader
	 * sensor. So the users can see themselves as looking into a mirror.
	 *
	 * <p>This does not affect the order of byte array passed in {@link
	 * PreviewCallback#onPreviewFrame}, JPEG pictures, or recorded videos. This
	 * method is not allowed to be called during preview.
	 *
	 * <p>If you want to make the reader image show in the same orientation as
	 * the display, you can use the following code.
	 * <pre>
	 *	#import com.motorolasolutions.adc.decoder;
	 *
	 *	public static void setReaderDisplayOrientation(Activity activity, int readerId, BarCodeReader reader)
	 *	{
	 *		int		result;
	 *		int		degrees = 0;
	 *		int		rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	 *	 	BarCodeReader.ReaderInfo	info = new BarCodeReader.ReaderInfo();
	 *		BarCodeReader.getReaderInfo(readerId, info);
	 *		switch (rotation)
	 *		{
	 *		case Surface.ROTATION_0:
	 *			degrees = 0;
	 *			break;
	 *		case Surface.ROTATION_90:
	 *			degrees = 90;
	 *			break;
	 *		case Surface.ROTATION_180:
	 *			degrees = 180;
	 *			break;
	 *		case Surface.ROTATION_270:
	 *			degrees = 270;
	 *			break;
	 *		default:
	 *			break;
	 *		}
	 *
	 *		if ( info.facing == BarCodeReader.ReaderInfo.BCRDR_FACING_FRONT )
	 *		{
	 *			result = (info.orientation + degrees) % 360;
	 *			result = (360 - result) % 360;	// compensate the mirror
	 *		}
	 *		else
	 *		{
	 *			// back-facing
	 *			result = (info.orientation - degrees + 360) % 360;
	 *		}
	 *		reader.setDisplayOrientation(result);
	 *	}
	 * </pre>
	 * @param degrees the angle that the picture will be rotated clockwise.
	 *				Valid values are 0, 90, 180, and 270. The starting
	 *				position is 0 (landscape).
	 * @see #setPreviewDisplay(SurfaceHolder)
	 */
	public native final void setDisplayOrientation(int degrees);
	
	/**
	 * Returns the number of barcodes decoded during a multiple barcode
	 * decode session. For a regular decode session, it will return 1
	 */
	public native final int getDecodeCount();

	/**
	 * Enables all code types
	 */
	public native final void enableAllCodeTypes();
	
	/**
	 * Disables all code types
	 */
	public native final void disableAllCodeTypes();
	
	/**
	 * Get the last decoded image. When the image is available, BCRDR_MSG_LAST_DEC_IMAGE
	 * is triggered
	 */
	public native final byte[] getLastDecImage();
	
	
	// Result codes for functions that return and integer status

	/**
	 * Function completed successfully
	 */
	public static final int BCR_SUCCESS						= 0;

	/**
	 * Function failed
	 */
	public static final int BCR_ERROR						= -1;


	// onDecodeComplete status codes passed as the length value

	/**
	 * onDecodeComplete length value indicating that the decode timed out
	 */
	public static final int DECODE_STATUS_TIMEOUT			= 0;

	/**
	 * onDecodeComplete length value indicating that the decode was canceled
	 */
	public static final int DECODE_STATUS_CANCELED			= -1;

	/**
	 * onDecodeComplete length value indicating that an error occurred
	 */
	public static final int DECODE_STATUS_ERROR				= -2;
	
	/**
	 * onDecodeComplete length value indicating a multi-decode event
	 */
	public static final int DECODE_STATUS_MULTI_DEC_COUNT	= -3;


	// Miscellaneous event ID's

	/**
	 * Scan mode changed event ID
	 */
	public static final int BCRDR_EVENT_SCAN_MODE_CHANGED	= 5;

	/**
	 * Motion detected event ID
	 */
	public static final int BCRDR_EVENT_MOTION_DETECTED		= 6;

	/**
	 * Scanner reset event ID
	 */
	public static final int BCRDR_EVENT_SCANNER_RESET		= 7;

	/**
	 * Unspecified reader error.
	 * @see ErrorCallback
	 */
	public static final int BCRDR_ERROR_UNKNOWN = 1;

	/**
	 * Media server died. In this case, the application must release the
	 * BarCodeReader object and instantiate a new one.
	 * @see ErrorCallback
	 */
	public static final int BCRDR_ERROR_SERVER_DIED = 100;

	/**
	 * Information about a bar code reader
	 */
	public static class ReaderInfo
	{
		/*
		 * The facing of the reader is opposite to that of the screen.
		 */
		public static final int BCRDR_FACING_BACK = 0;

		/**
		 * The facing of the reader is the same as that of the screen.
		 */
		public static final int BCRDR_FACING_FRONT = 1;

		/**
		 * The direction to which the reader faces. It must be
		 * BCRDR_FACING_BACK or BCRDR_FACING_FRONT.
		 */
		public int facing;

		/**
		 * The orientation of the reader image. The value is the angle that the
		 * reader image needs to be rotated clockwise so it shows correctly on
		 * the display in its natural orientation. It should be 0, 90, 180, or 270.
		 *
		 * For example, suppose a device has a naturally tall screen. The
		 * back-facing reader sensor is mounted in landscape. You are looking at
		 * the screen. If the top side of the reader sensor is aligned with the
		 * right edge of the screen in natural orientation, the value should be
		 * 90. If the top side of a front-facing reader sensor is aligned with
		 * the right of the screen, the value should be 270.
		 *
		 * @see #setDisplayOrientation(int)
		 * @see Parameters#setRotation(int)
		 * @see Parameters#setPreviewSize(int, int)
		 * @see Parameters#setPictureSize(int, int)
		 * @see Parameters#setJpegThumbnailSize(int, int)
		 */
		public int orientation;
	};

	/**
	 * Parameter numbers
	 */
	public static class ParamNum
	{
		/*						  Name						  Number	   Min	   Max	  Default	*/
		public static final short CODE39					=   0;	//		0		1		1
		public static final short UPCA						=   1;	//		0		1		1
		public static final short UPCE						=   2;	//		0		1		1
		public static final short EAN13						=   3;	//		0		1		1
		public static final short EAN8						=   4;	//		0		1		1
		public static final short D25						=   5;	//		0		1		0
		public static final short I25						=   6;	//		0		1		1
		public static final short CODABAR					=   7;	//		0		1		0
		public static final short CODE128					=   8;	//		0		1		1
		public static final short CODE93					=   9;	//		0		1		0
		public static final short CODE11					=  10;	//		0		1		0
		public static final short MSI						=  11;	//		0		1		0
		public static final short UPCE1						=  12;	//		0		1		0
		public static final short TRIOPTIC					=  13;	//		0		1		0
		public static final short EAN128					=  14;	//		0		1		1
		public static final short PDF						=  15;	//		0		1		1
		public static final short SUPPS						=  16;	//		0	   12	SUPP_NONE
		public static final short C39_FULL_ASCII			=  17;	//		0		1		0
		public static final short C39_LEN1					=  18;	//		0	   55		2
		public static final short C39_LEN2					=  19;	//		0	   55	   55
		public static final short D25_LEN1					=  20;	//		0	   55	   12
		public static final short D25_LEN2					=  21;	//		0	   55		0
		public static final short I25_LEN1					=  22;	//		0	   55	   14
		public static final short I25_LEN2					=  23;	//		0	   55		0
		public static final short CBR_LEN1					=  24;	//		0	   55		5
		public static final short CBR_LEN2					=  25;	//		0	   55	   55
		public static final short C93_LEN1					=  26;	//		0	   55		4
		public static final short C93_LEN2					=  27;	//		0	   55	   55
		public static final short C11_LEN1					=  28;	//		0	   55		4
		public static final short C11_LEN2					=  29;	//		0	   55	   55
		public static final short MSI_LEN1					=  30;	//		0	   55		4
		public static final short MSI_LEN2					=  31;	//		0	   55	   55
		public static final short UPCA_PREAM				=  34;	//		0		2		1
		public static final short UPCE_PREAM				=  35;	//		0		2		1
		public static final short UPCE1_PREAM				=  36;	//		0		2		1
		public static final short UPCE_TO_A					=  37;	//		0		1		0
		public static final short UPCE1_TO_A				=  38;	//		0		1		0
		public static final short EAN8_TO_13				=  39;	//		0		1		0
		public static final short UPCA_CHK					=  40;	//		0		1		1
		public static final short UPCE_CHK					=  41;	//		0		1		1
		public static final short UPCE1_CHK					=  42;	//		0		1		1
		public static final short XMIT_C39_CHK				=  43;	//		0		1		0
		public static final short XMIT_I25_CHK				=  44;	//		0		1		0
		public static final short XMIT_CODE_ID				=  45;	//		0		2		0
		public static final short XMIT_MSI_CHK				=  46;	//		0		1		0
		public static final short XMIT_C11_CHK				=  47;	//		0		1		0
		public static final short C39_CHK_EN				=  48;	//		0		1		0
		public static final short I25_CHK_TYPE				=  49;	//		0		2		0
		public static final short MSI_CHK_1_2				=  50;	//		0		1		0
		public static final short MSI_CHK_SCHEME			=  51;	//		0		1		1
		public static final short C11_CHK_TYPE				=  52;	//		0		2		0
		public static final short CLSI						=  54;	//		0		1		0
		public static final short NOTIS						=  55;	//		0		1		0
		public static final short UPC_SEC_LEV				=  77;	//		0		3		1
		public static final short LIN_SEC_LEV				=  78;	//		1		4		1
		public static final short SUPP_REDUN				=  80;	//		2		30	   10
		public static final short I25_TO_EAN13				=  82;	//		0		1		0
		public static final short BOOKLAND					=  83;	//		0		1		0
		public static final short ISBT_128					=  84;	//		0		1		1
		public static final short COUPON					=  85;	//		0		1		0
		public static final short CODE32					=  86;	//		0		1		0
		public static final short POST_US1					=  89;	//		0		1		1
		public static final short POST_US2					=  90;	//		0		1		1
		public static final short POST_UK					=  91;	//		0		1		1
		public static final short SIGNATURE					=  93;	//		0		1		0
		public static final short XMIT_NO_READ				=  94;	//		0		1		0
		public static final short POST_US_PARITY			=  95;	//		0		1		1
		public static final short POST_UK_PARITY			=  96;	//		0		1		1
		public static final short EMUL_EAN128				= 123;	//		0		1		0
		public static final short LASER_ON_PRIM				= 136;	//		5	   99	   99
		public static final short LASER_OFF_PRIM			= 137;	//		0	   99		6
		public static final short PRIM_TRIG_MODE			= 138;	//	   N/A	   N/A	  LEVEL
		public static final short C128_LEN1					= 209;	//		0	   55		0
		public static final short C128_LEN2					= 210;	//		0	   55		0
		public static final short ISBT_MAX_TRY				= 223;	//		0		0	   10
		public static final short UPDF						= 227;	//		0		1		0
		public static final short C32_PREFIX				= 231;	//		0		1		0
		public static final short POSTAL_JAP				= 290;	//		0		1		1
		public static final short POSTAL_AUS				= 291;	//		0		1		1
		public static final short DATAMATRIX				= 292;	//		0		1		1
		public static final short QRCODE					= 293;	//		0		1		1
		public static final short MAXICODE					= 294;	//		0		1		1
		public static final short IMG_ILLUM					= 298;	//		0		1		1
		public static final short IMG_AIM_SNAPSHOT			= 300;	//		0		1		1
		public static final short IMG_CROP					= 301;	//		0		1		0
		public static final short IMG_SUBSAMPLE				= 302;	//		0		3		0
		public static final short IMG_BPP					= 303;	//		0		2	IMG_BPP_8
		public static final short IMG_FILE_FORMAT			= 304;	//		1		4 IMG_FORMAT_JPEG
		public static final short IMG_JPEG_QUAL				= 305;	//		5	  100	   65
		public static final short IMG_AIM_MODE				= 306;	//		0		2	 AIM_ON
		public static final short IMG_SIG_FMT				= 313;	//		1		4		1
		public static final short IMG_SIG_BPP				= 314;	//		0		2	IMG_BPP_8
		public static final short IMG_CROP_TOP				= 315;	//		0	  479		0
		public static final short IMG_CROP_LEFT				= 316;	//		0	  751		0
		public static final short IMG_CROP_BOT				= 317;	//		0	  479	  479
		public static final short IMG_CROP_RIGHT			= 318;	//		0	  751	  751
		public static final short IMG_SNAPTIMEOUT			= 323;	//		0		9		0
		public static final short IMG_VIDEOVF				= 324;	//		0		1		0
		public static final short POSTAL_DUTCH				= 326;	//		0		1		1
		public static final short RSS_14					= 338;	//		0		1		1
		public static final short RSS_LIM					= 339;	//		0		1		0
		public static final short RSS_EXP					= 340;	//		0		1		0
		public static final short CCC_ENABLE				= 341;	//		0		1		0
		public static final short CCAB_ENABLE				= 342;	//		0		1		0
		public static final short UPC_COMPOSITE				= 344;	//		0		2	UPC_ALWAYS
		public static final short IMG_IMAGE_ILLUM			= 361;	//		0		1		1
		public static final short SIGCAP_WIDTH				= 366;	//	   16	  752	  400
		public static final short SIGCAP_HEIGHT				= 367;	//	   16	  480	  100
		public static final short TCIF						= 371;	//		0		1		0
		public static final short MARGIN_RATIO				= 381;	//	   N/A	   N/A		6
		public static final short DEMOTE_RSS				= 397;	//		0		1		0
		public static final short PICKLIST_MODE				= 402;	//		0		2 PICKLIST_NEVER
		public static final short C25						= 408;	//		0		1		0
		public static final short IMAGE_SIG_JPEG_QUALITY	= 421;	//		5	  100	   65
		public static final short EMUL_UCCEAN128			= 427;	//		0		1		0
		public static final short MIRROR_IMAGE				= 537;	//		0		2  MIRROR_NEVER
		public static final short IMG_ENHANCEMENT			= 564;	//		0		4 IMG_ENHANCE_OFF
		public static final short UQR_EN					= 573;	//		0		1		1
		public static final short AZTEC						= 574;	//		0		1		1
		public static final short BOOKLAND_FORMAT			= 576;	//		0		1		0
		public static final short ISBT_CONCAT_MODE			= 577;	//		0		0 ISBT_CONCAT_NONE
		public static final short CHECK_ISBT_TABLE			= 578;	//		0		0		1
    	public static final short SUPP_USER_1				= 579;	//	   N/A	   N/A	 0xFFFF
		public static final short SUPP_USER_2				= 580;	//	   N/A	   N/A	 0xFFFF
		public static final short K35						= 581;	//		0		1		0
		public static final short ONE_D_INVERSE				= 586;	//		0		2  REGULAR_ONLY
		public static final short QR_INVERSE				= 587;	//		0		2  REGULAR_ONLY
		public static final short DATAMATRIX_INVERSE		= 588;	//		0		2  REGULAR_ONLY
		public static final short AZTEC_INVERSE				= 589;	//		0		2  REGULAR_ONLY
		public static final short AIMMODEHANDSFREE			= 590;	//		0		1	 AIM_ON
		public static final short POST_US3					= 592;	//		0		1		0
		public static final short POST_US4					= 611;	//		0		1		0
		public static final short ISSN_EAN_EN				= 617;	//		0		1		0
		public static final short MATRIX_25_EN				= 618;	//		0		1		0
		public static final short MATRIX_25_LEN1			= 619;	//		0	   55	   14
		public static final short MATRIX_25_LEN2			= 620;	//		0	   55		0
		public static final short MATRIX_25_REDUN			= 621;	//		0		1		0
		public static final short MATRIX_25_CHK_EN			= 622;	//		0		1		0
		public static final short MATRIX_25_XMIT_CHK		= 623;	//		0		1		0
		public static final short AIMID_SUPP_FORMAT			= 672;	//		0		2		1
		public static final short CELL_DISPLAY_MODE			= 716;	//		0		1		1
		public static final short POST_AUS_FMT				= 718;	//		0		3		0
		public static final short DATABAR_LIM_SEC_LEV		= 728;	//		0		4		3
		public static final short COUPON_REPORT				= 730;	//		0		2		1
		public static final short VIDEO_SUBSMAPLE			= 761;	//		0		3		2
		public static final short IMG_MOTIONILLUM			= 762;	//		0		1		1
		public static final short MULTI_DECODE				= 900;	//		0		1		1
		public static final short FULL_READ_MODE			= 901;	//		0		1		1
		public static final short NUM_BR_TO_READ			= 902;	//		1		10		1
		public static final short RETRIEVE_LAST_DECODE		= 905;	//		0		1		0
		public static final short SECURITY_LEVEL			= 1121;	//		0		3		1
		public static final short ENABLE_HANXIN				= 1167;	//		0		1		0
		public static final short HANXIN_INVERSE			= 1168;	//		0		2		0
	};

	public static class ParamVal
	{
		/**
		 * Valid values for ParamNum.SUPPS
		 */
		public static final byte SUPP_NONE				=  0;
		public static final byte SUPP_ONLY				=  1;
		public static final byte SUPP_AUTOD				=  2;
		public static final byte SUPP_SMART				=  3;
		public static final byte SUPP_378_379			=  4;
		public static final byte SUPP_978_979			=  5;
		public static final byte SUPP_414_419_434_439	=  6;
		public static final byte SUPP_977				=  7;
		public static final byte SUPP_491				=  8;
		public static final byte SUPP_PROG_1			=  9;
		public static final byte SUPP_PROG_1_AND_2		= 10;
		public static final byte SUPP_SMART_PLUS_1		= 11;
		public static final byte SUPP_SMART_PLUS_1_2	= 12;

		/**
		 * Valid values for ParamNum.PRIM_TRIG_MODE
		 */
		public static final byte LEVEL					= 0;	// Normal soft-trigger mode
		public static final byte HANDSFREE				= 7;	// Presentation/hands-free trigger mode
		public static final byte AUTO_AIM				= 9;	// Motion detection turns AIM reticle on

		/**
		 * Valid values for ParamNum.IMG_BPP and ParamNum. IMG_SIG_BPP
		 */
		public static final byte IMG_BPP_1				= 0;
		public static final byte IMG_BPP_4				= 1;
		public static final byte IMG_BPP_8				= 2;

		/**
		 * Valid values for ParamNum.IMG_FILE_FORMAT
		 */
		public static final byte IMG_FORMAT_JPEG		= 1;
		public static final byte IMG_FORMAT_BMP			= 3;
		public static final byte IMG_FORMAT_TIFF		= 4;

		/**
		 * Valid values for ParamNum.IMG_SUBSAMPLE and ParamNum.IMG_VIDEOSUB
		 */
		public static final byte IMG_SUBSAMPLE_FACTOR_1	= 0;	// Full size image
		public static final byte IMG_SUBSAMPLE_FACTOR_2	= 1;	// Width and height divided by 2 (1/4 size) 
		public static final byte IMG_SUBSAMPLE_FACTOR_3	= 2;	// Width and height divided by 3 (1/9 size) 
		public static final byte IMG_SUBSAMPLE_FACTOR_4	= 3;	// Width and height divided by 4 (1/16 size)

		/**
		 * Valid values for ParamNum.IMG_AIM_MODE and ParamNum.AIMMODEHANDSFREE
		 */
		public static final byte AIM_OFF				= 0;
		public static final byte AIM_ON					= 1;
		public static final byte AIM_ON_ALWAYS			= 2;

		/**
		 * Valid values for ParamNum.UPC_COMPOSITE
		 */
		public static final byte UPC_NEVER				= 0;
		public static final byte UPC_ALWAYS				= 1;
		public static final byte UPC_AUTOD				= 2;

		/**
		 * Valid values for ParamNum.PICKLIST_MODE
		 */
		public static final byte PICKLIST_NEVER				= 0;
		public static final byte PICKLIST_OUT_OF_SCANSTAND	= 1;
		public static final byte PICKLIST_ALWAYS			= 1;

		/**
		 * Valid values for ParamNum.PICKLIST_MODE
		 */
		public static final byte MIRROR_NEVER			= 0;
		public static final byte MIRROR_ALWAYS			= 1;
		public static final byte MIRROR_AUTO			= 2;

		/**
		 * Valid values for ParamNum.IMG_ENHANCEMENT
		 */
		public static final byte IMG_ENHANCE_OFF		= 0;
		public static final byte IMG_ENHANCE_LOW		= 1;
		public static final byte IMG_ENHANCE_MED		= 2;
		public static final byte IMG_ENHANCE_HIGH		= 3;
		public static final byte IMG_ENHANCE_CUSTOM		= 4;

		/**
		 * Valid values for ParamNum.ISBT_CONCAT_MODE
		 */
		public static final byte ISBT_CONCAT_NONE		= 0;
		public static final byte ISBT_CONCAT_ONLY		= 1;
		public static final byte ISBT_CONCAT_AUTOD 		= 2;

		/**
		 * Valid values for ParamNum.*_INVERSE
		 */
		public static final byte REGULAR_ONLY			= 0;
		public static final byte INVERSE_ONLY			= 1;
		public static final byte INVERSE_AUTOD			= 2;

		/**
		 * Valid values for ParamNum.PDF_SECURITY_LEVEL
		 */
		public static final byte PDF_SECURITY_STRICT	= 0;
		public static final byte PDF_CWLEN_ZERO_OK		= 1;
	};

	/**
	 * Property numbers used to get information from the scanner hardware.
	 */
	public static class PropertyNum
	{
		/**
		 * Property number used to get the scanner model number string
		 */
		public static final int MODEL_NUMBER			= 1;
		/**
		 * Property number used to get the scanner serial number string
		 */
		public static final int SERIAL_NUM				= 2;
		/**
		 * Property number used to get the maximum buffer size required for a frame
		 */
		public static final int MAX_FRAME_BUFFER_SIZE	= 3;
		/**
		 * Property number used to get the scanner's horizontal resolution
		 */
		public static final int HORIZONTAL_RES			= 4;
		/**
		 * Property number used to get the scanner's vertical resolution
		 */
		public static final int VERTICAL_RES			= 5;
		/**
		 * Property number used to get the Image Kit version string
		 */
		public static final int IMGKIT_VER				= 6;
		/**
		 * Property number used to get the Scan Engine version string
		 */
		public static final int ENGINE_VER				= 7;
		/**
		 * Property number used to get the firmware Bootloader version string
		 */
		public static final int BTLD_FW_VER				= 11;
		/**
		 * Property number used to get the SDL version string
		 */
		public static final int SDL_VER				= 99;
	}

	/**
	 * Creates a new BarCodeReader object to access a particular hardware reader.
	 *
	 * <p>You must call {@link #release()} when you are done using the reader,
	 * otherwise it will remain locked and be unavailable to other applications.
	 *
	 * <p>Your application should only have one BarCodeReader object active at a time
	 * for a particular hardware reader.
	 *
	 * <p>Callbacks from other methods are delivered to the event loop of the
	 * thread which called open().  If this thread has no event loop, then
	 * callbacks are delivered to the main application event loop.  If there
	 * is no main application event loop, callbacks are not delivered.
	 *
	 * <p class="caution"><b>Caution:</b> On some devices, this method may
	 * take a long time to complete.  It is best to call this method from a
	 * worker thread (possibly using {@link android.os.AsyncTask}) to avoid
	 * blocking the main application UI thread.
	 *
	 * @param readerId the hardware reader to access, between 0 and {@link #getNumberOfReaders()}-1.
	 *
	 * @return a new BarCodeReader object, connected, locked and ready for use.
	 *
	 * @throws RuntimeException if connection to the reader service fails (for
	 *	 example, if the reader is in use by another process).
	 */
	public static BarCodeReader open(int readerId)
	{
		return(new BarCodeReader(readerId));
	}

	/**
	 * Creates a new BarCodeReader object to access the first back-facing reader on
	 * the device. If the device does not have a back-facing reader, this returns null
	 *
	 * @see #open(int)
	 */
	public static BarCodeReader open()
	{
		ReaderInfo	readerInfo;

		int		iIdx;
		int		iNumReaders;

		iNumReaders = getNumberOfReaders();
		readerInfo = new ReaderInfo();
		Log.e("BarCodeReader","open:iNumReaders"+iNumReaders);
		for ( iIdx = 0; iIdx < iNumReaders; ++iIdx )
		{
			BarCodeReader.getReaderInfo(iIdx, readerInfo);
			if ( readerInfo.facing == ReaderInfo.BCRDR_FACING_FRONT )
			{
				Log.e("BarCodeReader","open:iIdx:"+iIdx);
				return(new BarCodeReader(iIdx));
			}
		}
		return(null);
	}
	
	/**
	 * Creates a new BarCodeReader object to access a particular hardware reader.
	 *
	 * <p>You must call {@link #release()} when you are done using the reader,
	 * otherwise it will remain locked and be unavailable to other applications.
	 *
	 * <p>Your application should only have one BarCodeReader object active at a time
	 * for a particular hardware reader.
	 *
	 * <p>Callbacks from other methods are delivered to the event loop of the
	 * thread which called open().  If this thread has no event loop, then
	 * callbacks are delivered to the main application event loop.  If there
	 * is no main application event loop, callbacks are not delivered.
	 *
	 * <p class="caution"><b>Caution:</b> On some devices, this method may
	 * take a long time to complete.  It is best to call this method from a
	 * worker thread (possibly using {@link android.os.AsyncTask}) to avoid
	 * blocking the main application UI thread.
	 *
	 * @param readerId the hardware reader to access, between 0 and {@link #getNumberOfReaders()}-1.
	 * @param context is the application context 
	 * 
	 * @return a new BarCodeReader object, connected, locked and ready for use.
	 *
	 * @throws RuntimeException if connection to the reader service fails (for
	 *	 example, if the reader is in use by another process).
	 */
	public static BarCodeReader open(int readerId, Context context)
	{
		return(new BarCodeReader(readerId, context));
	}

	/**
	 * Creates a new BarCodeReader object to access the first back-facing reader on
	 * the device. If the device does not have a back-facing reader, this returns null.
	 * Use this for Android version 4.3 and above
	 * @see #open(int)
	 */
	public static BarCodeReader open(Context context)
	{
		ReaderInfo	readerInfo;

		int		iIdx;
		int		iNumReaders;

		iNumReaders = getNumberOfReaders();
		readerInfo = new ReaderInfo();
		for ( iIdx = 0; iIdx < iNumReaders; ++iIdx )
		{
			BarCodeReader.getReaderInfo(iIdx, readerInfo);
			if ( readerInfo.facing == ReaderInfo.BCRDR_FACING_BACK )
			{
				return(new BarCodeReader(iIdx, context));
			}
		}
		return(null);
	}

	BarCodeReader(int readerId)
	{
		Looper	aLooper;

		mEventHandler		= null;
		mAutoFocusCallback	= null;
		mDecodeCallback		= null;
		mErrorCallback		= null;
		mPreviewCallback	= null;
		mSnapshotCallback	= null;
		mVideoCallback		= null;
		mZoomListener		= null;

		aLooper = Looper.myLooper();
		if ( null == aLooper )
			aLooper = Looper.getMainLooper();
		if ( aLooper != null )
		{
			mEventHandler = new EventHandler(this, aLooper);
		}

		native_setup(new WeakReference<BarCodeReader>(this), readerId);
	}
	
	BarCodeReader(int readerId, Context context)
	{
		Looper	aLooper;

		mEventHandler		= null;
		mAutoFocusCallback	= null;
		mDecodeCallback		= null;
		mErrorCallback		= null;
		mPreviewCallback	= null;
		mSnapshotCallback	= null;
		mVideoCallback		= null;
		mZoomListener		= null;

		aLooper = Looper.myLooper();
		if ( null == aLooper )
			aLooper = Looper.getMainLooper();
		if ( aLooper != null )
		{
			mEventHandler = new EventHandler(this, aLooper);
		}

		native_setup(new WeakReference<BarCodeReader>(this), readerId, context);
	}

	protected void finalize()
	{
		native_release();
	}

	/**
	 * Disconnects and releases the BarCodeReader object resources.
	 *
	 * <p>You must call this as soon as you're done with the BarCodeReader object.</p>
	 */
	public final void release()
	{
		native_release();
	}

	/**
	 * Sets the {@link Surface} to be used for live preview.
	 * A surface is necessary for preview, and preview is necessary to take
	 * pictures.  The same surface can be re-set without harm.
	 *
	 * <p>The {@link SurfaceHolder} must already contain a surface when this
	 * method is called.  If you are using {@link android.view.SurfaceView},
	 * you will need to register a {@link SurfaceHolder.Callback} with
	 * {@link SurfaceHolder#addCallback(SurfaceHolder.Callback)} and wait for
	 * {@link SurfaceHolder.Callback#surfaceCreated(SurfaceHolder)} before
	 * calling setPreviewDisplay() or starting preview.
	 *
	 * <p>This method must be called before {@link #startPreview()}.  The
	 * one exception is that if the preview surface is not set (or set to null)
	 * before startPreview() is called, then this method may be called once
	 * with a non-null parameter to set the preview surface.  (This allows
	 * reader setup and surface creation to happen in parallel, saving time.)
	 * The preview surface may not otherwise change while preview is running.
	 *
	 * @param holder containing the Surface on which to place the preview,
	 *	 or null to remove the preview surface
	 * @throws IOException if the method fails (for example, if the surface
	 *	 is unavailable or unsuitable).
	 */
	public final void setPreviewDisplay(SurfaceHolder holder) throws IOException
	{
		if ( holder != null )
		{
			setPreviewDisplay(holder.getSurface());
		}
		else
		{
			setPreviewDisplay((Surface) null);
		}
	}

	/**
	 * Callback interface used to notify on completion of reader auto focus.
	 *
	 * <p>Devices that do not support auto-focus will receive a "fake"
	 * callback to this interface. If your application needs auto-focus and
	 * should not be installed on devices <em>without</em> auto-focus, you must
	 * declare that your app uses the
	 * {@code android.hardware.camera.autofocus} feature, in the
	 * <a href="{@docRoot}guide/topics/manifest/uses-feature-element.html">&lt;uses-feature></a>
	 * manifest element.</p>
	 *
	 * @see #autoFocus(AutoFocusCallback)
	 */
	public interface AutoFocusCallback
	{
		/**
		 * Called when the reader auto focus completes.  If the reader
		 * does not support auto-focus and autoFocus is called,
		 * onAutoFocus will be called immediately with a fake value of
		 * <code>success</code> set to <code>true</code>.
		 *
		 * @param success	true if focus was successful, false if otherwise
		 * @param reader	the BarCodeReader service object
		 */
		void onAutoFocus(boolean success, BarCodeReader reader);
	};

	/**
	 * Starts reader auto-focus and registers a callback function to run when the
	 * reader is focused.  This method is only valid when frame acquisition is active.
	 *
	 * <p>Callers should check
	 * {@link com.motorolasolutions.adc.decoder.BarCodeReader.Parameters#getFocusMode()} to determine if
	 * this method should be called. If the reader does not support auto-focus,
	 * it is a no-op and {@link AutoFocusCallback#onAutoFocus(boolean, BarCodeReader)}
	 * callback will be called immediately.
	 *
	 * <p>If your application should not be installed on devices without
	 * auto-focus, you must declare that your application uses auto-focus with the
	 * <a href="{@docRoot}guide/topics/manifest/uses-feature-element.html">&lt;uses-feature></a>
	 * manifest element.</p>
	 *
	 * <p>If the current flash mode is not
	 * {@link com.motorolasolutions.adc.decoder.BarCodeReader.Parameters#FLASH_MODE_OFF}, flash may be
	 * fired during auto-focus, depending on the driver and reader hardware.<p>
	 *
	 * @param cb the callback to run
	 * @see #cancelAutoFocus()
	 */
	public final void autoFocus(AutoFocusCallback cb)
	{
		mAutoFocusCallback = cb;
		native_autoFocus();
	}

	/**
	 * Cancels any auto-focus function in progress.
	 * Whether or not auto-focus is currently in progress,
	 * this function will return the focus position to the default.
	 * If the reader does not support auto-focus, this is a no-op.
	 *
	 * @see #autoFocus(AutoFocusCallback)
	 */
	public final void cancelAutoFocus()
	{
		mAutoFocusCallback = null;
		native_cancelAutoFocus();
	}

	/**
	 * Callback interface used to deliver decode results.
	 *
	 * @see #setDecodeCallback(DecodeCallback)
	 * @see #startDecode()
	 */
	public interface DecodeCallback
	{
		/**
		 * Called when a decode operation has completed, either due to a timeout,
		 * a successful decode or canceled by the user.  This callback is invoked
		 * on the event thread {@link #open(int)} was called from.
		 *
		 * @param symbology the symbology of decoded bar code if any
		 * @param status if positive, indicates the length of the bar code data,
		 *	otherwise, DECODE_STATUS_TIMEOUT if the request timed out or
		 *	DECODE_STATUS_CANCELED if stopDecode() is called before a successful
		 *	decode or timeout.
		 * @param data the contents of the decoded bar code
		 * @param reader the BarCodeReader service object.
		 */
		void onDecodeComplete(int symbology, int length, byte[] data, BarCodeReader reader);

		/**
		 * Called to indicate that the decoder detected an event such as MOTION DECTECTED.
		 * This callback is invoked on the event thread {@link #open(int)} was called from.
		 *
		 * @param event the type of event that has occurred
		 * @param info additional event information, if any, else zero
		 * @param data data associated with the event, if any, else null
		 * @param reader the BarCodeReader service object.
		 */
		void onEvent(int event, int info, byte[] data, BarCodeReader reader);
	};

	/**
	 * Specifies whether or not automatic auto-focus should be performed
	 * during decode operations and if so, how many frames to initially
	 * wait before issuing the the first auto-focus request and how many
	 * frames to wait after receiving an auto-focus complete notification
	 * before issuing another request. An application should call
	 * {@link #Parameters.setFocusMode(String)} and {@link #setParameters(Parameters)}
	 * to set the focus mode to {@link #BarCodeReader.Parameters.FOCUS_MODE_AUTO}.
	 *
	 * When this function is used to enable automatic auto-focus requests,
	 * auto-focus callbacks are disabled. If an application needs to receive
	 * auto-focus callbacks, it should issue its own {@link #autoFocus(AutoFocusCallback)}
	 * requests and should not call this function.
	 *
	 * @param initialDelay	the number of frames to process when a decode
	 *	session is started before issuing the first auto-focus request.
	 *	If this parameter is less than one and secondaryDelay is greater
	 *	than zero, an auto-focus request will be issued as soon as the
	 *	decode session is started. If both initialDelay and secondaryDelay
	 *	are both less than one, no auto-focus requests will be issued.
	 * @param secondaryDelay	the number of frames to process after
	 *	receiving an auto-focus complete notification before issuing
	 *	another auto-focus request. If this parameter is less than one,
	 *	only the initial auto-focus request, if any, will be performed.
	 */
	public native final void setAutoFocusDelay(int initialDelay, int secondaryDelay);

	/**
	 * Installs callbacks to be invoked when a decode request completes
	 * or a decoder event occurs. This method can be called at any time,
	 * even while a decode request is active.  Any other decode callbacks
	 * are overridden.
	 *
	 * @param cb a callback object that receives a notification of a completed,
	 *	decode request or null to stop receiving decode callbacks.
	 */
	public final void setDecodeCallback(DecodeCallback cb)
	{
		mDecodeCallback = cb;
	}

	/**
	 * Callback interface used to supply image data from a photo capture.
	 *
	 * @see #takePicture(PictureCallback)
	 */
	public interface PictureCallback
	{
		/**
		 * Called when image data is available after a picture is taken.
		 * The format of the data depends on the current value of the
		 * IMG_FILE_FORMAT and IMG_VIDEOSUB parameters.
		 *
		 * @param format	format of the image (IMG_FORMAT_JPEG, IMG_FORMAT_BMP, or IMG_FORMAT_TIFF)
		 * @param with		horizontal resolution of the image
		 * @param height	vertical resolution of the image
		 * @param data		a byte array of the picture data
		 * @param reader	the BarCodeReader service object
		 */
		void onPictureTaken(int format, int width, int height, byte[] data, BarCodeReader reader);
	};

	/**
	 * Triggers an asynchronous image capture. The picture taken callback
	 * occurs when a scaled, fully processed image is available
	 *
	 * <p>This method is only valid when the decoder is idle or view finder
	 * mode is active (after calling {@link #startViewFinder()}).  Image capture
	 * will be stopped after the picture taken callback is called.  Callers must
	 * call {@link #startViewFiner()} and/or takePicture() again if they want to
	 * re-start the view finder or take more pictures.
	 *
	 * <p>After calling this method, you must not call {@link #startPreview()},
	 * {@link #startViewFinder()} or take another picture until the picture taken
	 * callback has returned.
	 *
	 * @param cb		the callback for processed image data
	 */
	public final void takePicture(PictureCallback cb)
	{
		mSnapshotCallback = cb;
		try
		{
			native_takePicture();
		}
		catch ( Throwable thrw )
		{
			// TODO: Call error callback?
		}
	}

	/**
	 * Callback interface used to supply image data in video capture mode.
	 *
	 * @see #startVideoCapture(VideoCallback)
	 */
	public interface VideoCallback
	{
		/**
		 * Called when image data is available during video capture mode.
		 * The format of the data depends on the current value of the
		 * IMG_FILE_FORMAT and IMG_VIDEOSUB parameters.
		 *
		 * @param format	format of the image (IMG_FORMAT_JPEG, IMG_FORMAT_BMP, or IMG_FORMAT_TIFF)
		 * @param with		horizontal resolution of the image
		 * @param height	vertical resolution of the image
		 * @param data		a byte array of the video frame
		 * @param reader	the BarCodeReader service object
		 */
		void onVideoFrame(int format, int width, int height, byte[] data, BarCodeReader reader);
	};

	/**
	 * Callback interface used to deliver copies of preview frames as
	 * they are displayed.
	 *
	 * @see #setOneShotPreviewCallback(PreviewCallback)
	 * @see #setPreviewCallbackWithBuffer(PreviewCallback)
	 * @see #startPreview()
	 */
	public interface PreviewCallback
	{
		/**
		 * Called as preview frames are displayed.  This callback is invoked
		 * on the event thread {@link #open(int)} was called from.
		 *
		 * @param data the contents of the preview frame in the format defined
		 *	by {@link ImageFormat}, which can be queried
		 *	with {@link com.motorolasolutions.adc.decoder.BarCodeReader.Parameters#getPreviewFormat()}.
		 *	If {@link com.motorolasolutions.adc.decoder.BarCodeReader.Parameters#setPreviewFormat(int)}
		 *	is never called, the default will be the YCbCr_420_SP (NV21) format.
		 * @param reader the BarCodeReader service object.
		 */
		void onPreviewFrame(byte[] data, BarCodeReader reader);
	};

	/**
	 * Installs a callback to be invoked for the next preview frame in addition
	 * to displaying it on the screen.  After one invocation, the callback is
	 * cleared. This method can be called any time, even when preview is live.
	 * Any other preview callbacks are overridden.
	 *
	 * @param cb a callback object that receives a copy of the next preview frame,
	 *	 or null to stop receiving callbacks.
	 */
	public final void setOneShotPreviewCallback(PreviewCallback cb)
	{
		mPreviewCallback = cb;
		mOneShot		 = true;
		mWithBuffer		 = false;
		setHasPreviewCallback(cb != null, false);
	}

	/**
	 * Installs a callback to be invoked for every preview frame, using buffers
	 * supplied with {@link #addCallbackBuffer(byte[])}, in addition to
	 * displaying them on the screen.  The callback will be repeatedly called
	 * for as long as preview is active and buffers are available.
	 * Any other preview callbacks are overridden.
	 *
	 * <p>The purpose of this method is to improve preview efficiency and frame
	 * rate by allowing preview frame memory reuse.  You must call
	 * {@link #addCallbackBuffer(byte[])} at some point -- before or after
	 * calling this method -- or no callbacks will received.
	 *
	 * The buffer queue will be cleared if this method is called with a null callback
	 * or if {@link #setOneShotPreviewCallback(PreviewCallback)} is called.
	 *
	 * @param cb a callback object that receives a copy of the preview frame,
	 *	 or null to stop receiving callbacks and clear the buffer queue.
	 * @see #addCallbackBuffer(byte[])
	 */
	public final void setPreviewCallbackWithBuffer(PreviewCallback cb)
	{
		mPreviewCallback = cb;
		mOneShot		 = false;
		mWithBuffer		 = true;
		setHasPreviewCallback(cb != null, true);
	}

	private class EventHandler extends Handler
	{
		private BarCodeReader mReader;

		public EventHandler(BarCodeReader rdr, Looper looper)
		{
			super(looper);
			mReader = rdr;
		}

		@Override
		public void handleMessage(Message msg)
		{
			Log.v(TAG, String.format("Event message: %X, arg1=%d, arg2=%d", msg.what, msg.arg1, msg.arg2));
			switch ( msg.what )
			{
			case BCRDR_MSG_DECODE_COMPLETE:
				if ( mDecodeCallback != null )
				{
					mDecodeCallback.onDecodeComplete(msg.arg1, msg.arg2, (byte[]) msg.obj, mReader);
				}
				return;

			case BCRDR_MSG_DECODE_TIMEOUT:
				if ( mDecodeCallback != null )
				{
					mDecodeCallback.onDecodeComplete(0, 0, (byte[]) msg.obj, mReader);
				}
				return;

			case BCRDR_MSG_DECODE_CANCELED:
				if ( mDecodeCallback != null )
				{
					mDecodeCallback.onDecodeComplete(0, DECODE_STATUS_CANCELED, (byte[]) msg.obj, mReader);
				}
				return;

			case BCRDR_MSG_FRAME_ERROR:
				// TODO:
			case BCRDR_MSG_DECODE_ERROR:
				if ( mDecodeCallback != null )
				{
					mDecodeCallback.onDecodeComplete(0, DECODE_STATUS_ERROR, (byte[]) msg.obj, mReader);
				}
				return;

			case BCRDR_MSG_DECODE_EVENT:
				if ( mDecodeCallback != null )
				{
					mDecodeCallback.onEvent(msg.arg1, msg.arg2, (byte[]) msg.obj, mReader);
				}
				return;

			case BCRDR_MSG_SHUTTER:
				// We do not support the shutter callback
				return;

			case BCRDR_MSG_COMPRESSED_IMAGE:
				if ( mSnapshotCallback != null )
				{
					int		iCX;
					int		iCY;

					iCX = (msg.arg1 >>  0) & 0xFFFF;
					iCY = (msg.arg1 >> 16) & 0xFFFF;
					mSnapshotCallback.onPictureTaken(msg.arg2, iCX, iCY, (byte[]) msg.obj, mReader);
				}
				else
				{
					Log.e(TAG, "BCRDR_MSG_COMPRESSED_IMAGE event with no snapshot callback");
				}
				return;

			case BCRDR_MSG_VIDEO_FRAME:
				if ( mVideoCallback != null )
				{
					int		iCX;
					int		iCY;

					iCX = (msg.arg1 >>  0) & 0xFFFF;
					iCY = (msg.arg1 >> 16) & 0xFFFF;
					mVideoCallback.onVideoFrame(msg.arg2, iCX, iCY, (byte[]) msg.obj, mReader);
				}
				else
				{
					Log.e(TAG, "BCRDR_MSG_VIDEO_FRAME event with no video callback");
				}
				return;

			case BCRDR_MSG_PREVIEW_FRAME:
				if ( mPreviewCallback != null )
				{
					PreviewCallback cb = mPreviewCallback;
					if ( mOneShot )
					{
						// Clear the callback variable before the callback
						// in case the app calls setOneShotPreviewCallback from
						// the callback function
						mPreviewCallback = null;
					}
					else if ( !mWithBuffer )
					{
						// We're faking the reader preview mode to prevent
						// the app from being flooded with preview frames.
						// Set to one-shot mode again.
						setHasPreviewCallback(true, false);
					}
					cb.onPreviewFrame((byte[]) msg.obj, mReader);
				}
				return;

			case BCRDR_MSG_FOCUS:
				if ( mAutoFocusCallback != null )
				{
					mAutoFocusCallback.onAutoFocus(msg.arg1 == 0 ? false : true, mReader);
				}
				return;

			case BCRDR_MSG_ZOOM:
				if ( mZoomListener != null )
				{
					mZoomListener.onZoomChange(msg.arg1, msg.arg2 != 0, mReader);
				}
				return;

			case BCRDR_MSG_ERROR:
				Log.e(TAG, "Error " + msg.arg1);
				if ( mErrorCallback != null )
				{
					mErrorCallback.onError(msg.arg1, mReader);
				}
				return;
				
			case BCRDR_MSG_DEC_COUNT:
				if ( mDecodeCallback != null )
				{
					mDecodeCallback.onDecodeComplete( msg.arg1, DECODE_STATUS_MULTI_DEC_COUNT,(byte[]) msg.obj, mReader);
				}
				return;	

			default:
				Log.e(TAG, "Unknown message type " + msg.what);
				return;
			}
		}
	};

	private static void postEventFromNative(Object reader_ref, int what, int arg1, int arg2, Object obj)
	{
		@SuppressWarnings("unchecked")
		BarCodeReader c = (BarCodeReader) ((WeakReference<BarCodeReader>) reader_ref).get();
		if ( (c != null) && (c.mEventHandler != null) )
		{
			Message m = c.mEventHandler.obtainMessage(what, arg1, arg2, obj);
			c.mEventHandler.sendMessage(m);
		}
	}

	/**
	 * Callback interface for zoom changes during a smooth zoom operation.
	 *
	 * @see #setZoomChangeListener(OnZoomChangeListener)
	 * @see #startSmoothZoom(int)
	 */
	public interface OnZoomChangeListener
	{
		/**
		 * Called when the zoom value has changed during a smooth zoom.
		 *
		 * @param zoomValue	the current zoom value. In smooth zoom mode, reader
		 *					calls this for every new zoom value.
		 * @param stopped	whether smooth zoom is stopped. If the value is true,
		 *					this is the last zoom update for the application.
		 * @param reader	the BarCodeReader service object
		 */
		void onZoomChange(int zoomValue, boolean stopped, BarCodeReader reader);
	};

	/**
	 * Registers a listener to be notified when the zoom value is updated by the
	 * reader driver during smooth zoom.
	 *
	 * @param listener the listener to notify
	 * @see #startSmoothZoom(int)
	 */
	public final void setZoomChangeListener(OnZoomChangeListener listener)
	{
		mZoomListener = listener;
	}

	/**
	 * Callback interface for reader error notification.
	 *
	 * @see #setErrorCallback(ErrorCallback)
	 */
	public interface ErrorCallback
	{
		/**
		 * Callback for reader errors.
		 * @param error	error code:
		 * <ul>
		 * <li>{@link #BCRDR_ERROR_UNKNOWN}
		 * <li>{@link #BCRDR_ERROR_SERVER_DIED}
		 * </ul>
		 * @param reader	the BarCodeReader service object
		 */
		void onError(int error, BarCodeReader reader);
	};

	/**
	 * Registers a callback to be invoked when an error occurs.
	 * @param cb The callback to run
	 */
	public final void setErrorCallback(ErrorCallback cb)
	{
		mErrorCallback = cb;
	}

	/**
	 * Changes the settings for this BarCodeReader service.
	 *
	 * @param params the Parameters to use for this BarCodeReader service
	 * @throws RuntimeException if any parameter is invalid or not supported.
	 * @see #getParameters()
	 */
	public void setParameters(Parameters params)
	{
		native_setParameters(params.flatten());
	}

	/**
	 * Returns the current settings for this BarCodeReader service.
	 * If modifications are made to the returned Parameters, they must be passed
	 * to {@link #setParameters(Parameters)} to take effect.
	 *
	 * @see #setParameters(Parameters)
	 */
	public Parameters getParameters()
	{
		Parameters p = new Parameters();
		String s = native_getParameters();
		p.unflatten(s);
		return(p);
	}

	/**
	 * Image size (width and height dimensions).
	 */
	public class Size
	{
		/**
		 * Sets the dimensions for snapshots.
		 *
		 * @param w the image width (pixels)
		 * @param h the image height (pixels)
		 */
		public Size(int w, int h)
		{
			width = w;
			height = h;
		}
		/**
		 * Compares {@code obj} to this size.
		 *
		 * @param obj the object to compare this size with.
		 * @return {@code true} if the width and height of {@code obj} is the
		 *		 same as those of this size. {@code false} otherwise.
		 */
		@Override
		public boolean equals(Object obj)
		{
			if ( !(obj instanceof Size) )
			{
				return(false);
			}
			Size s = (Size) obj;
			return((width == s.width) && (height == s.height));
		}
		@Override
		public int hashCode()
		{
			return((width * 32713) + height);
		}
		// width of the image
		public int width;
		// height of the image
		public int height;
	};

	/**
	 * BarCodeReader service settings.
	 *
	 * <p>To make reader parameters take effect, applications have to call
	 * {@link BarCodeReader#setParameters(Parameters)}. For example, after
	 * {@link Parameters#setWhiteBalance} is called, white balance is not
	 * actually changed until {@link BarCodeReader#setParameters(Parameters)}
	 * is called with the changed parameters object.
	 *
	 * <p>Different devices may have different reader capabilities, such as
	 * picture size or flash modes. The application should query the reader
	 * capabilities before setting parameters. For example, the application
	 * should call {@link Parameters#getSupportedColorEffects()} before
	 * calling {@link Parameters#setColorEffect(String)}. If the
	 * reader does not support color effects,
	 * {@link Parameters#getSupportedColorEffects()} will return null.
	 */
	public class Parameters
	{
		// Parameter keys to communicate with the reader driver.
		private static final String KEY_PREVIEW_SIZE				= "preview-size";
		private static final String KEY_PREVIEW_FORMAT				= "preview-format";
		private static final String KEY_PREVIEW_FRAME_RATE			= "preview-frame-rate";
		private static final String KEY_PREVIEW_FPS_RANGE			= "preview-fps-range";
		private static final String KEY_PICTURE_SIZE				= "picture-size";
		private static final String KEY_PICTURE_FORMAT				= "picture-format";
		private static final String KEY_JPEG_THUMBNAIL_SIZE			= "jpeg-thumbnail-size";
		private static final String KEY_JPEG_THUMBNAIL_WIDTH		= "jpeg-thumbnail-width";
		private static final String KEY_JPEG_THUMBNAIL_HEIGHT		= "jpeg-thumbnail-height";
		private static final String KEY_JPEG_THUMBNAIL_QUALITY		= "jpeg-thumbnail-quality";
		private static final String KEY_JPEG_QUALITY				= "jpeg-quality";
		private static final String KEY_ROTATION					= "rotation";
		private static final String KEY_GPS_LATITUDE				= "gps-latitude";
		private static final String KEY_GPS_LONGITUDE				= "gps-longitude";
		private static final String KEY_GPS_ALTITUDE				= "gps-altitude";
		private static final String KEY_GPS_TIMESTAMP				= "gps-timestamp";
		private static final String KEY_GPS_PROCESSING_METHOD		= "gps-processing-method";
		private static final String KEY_WHITE_BALANCE				= "whitebalance";
		private static final String KEY_EFFECT						= "effect";
		private static final String KEY_ANTIBANDING					= "antibanding";
		private static final String KEY_SCENE_MODE					= "scene-mode";
		private static final String KEY_FLASH_MODE					= "flash-mode";
		private static final String KEY_FOCUS_MODE					= "focus-mode";
		private static final String KEY_FOCAL_LENGTH				= "focal-length";
		private static final String KEY_HORIZONTAL_VIEW_ANGLE		= "horizontal-view-angle";
		private static final String KEY_VERTICAL_VIEW_ANGLE			= "vertical-view-angle";
		private static final String KEY_EXPOSURE_COMPENSATION		= "exposure-compensation";
		private static final String KEY_MAX_EXPOSURE_COMPENSATION	= "max-exposure-compensation";
		private static final String KEY_MIN_EXPOSURE_COMPENSATION	= "min-exposure-compensation";
		private static final String KEY_EXPOSURE_COMPENSATION_STEP	= "exposure-compensation-step";
		private static final String KEY_ZOOM						= "zoom";
		private static final String KEY_MAX_ZOOM					= "max-zoom";
		private static final String KEY_ZOOM_RATIOS					= "zoom-ratios";
		private static final String KEY_ZOOM_SUPPORTED				= "zoom-supported";
		private static final String KEY_SMOOTH_ZOOM_SUPPORTED		= "smooth-zoom-supported";
		private static final String KEY_FOCUS_DISTANCES				= "focus-distances";

		// Parameter key suffix for supported values.
		private static final String SUPPORTED_VALUES_SUFFIX			= "-values";

		private static final String TRUE							= "true";

		// Values for white balance settings.
		public static final String WHITE_BALANCE_AUTO				= "auto";
		public static final String WHITE_BALANCE_INCANDESCENT		= "incandescent";
		public static final String WHITE_BALANCE_FLUORESCENT		= "fluorescent";
		public static final String WHITE_BALANCE_WARM_FLUORESCENT	= "warm-fluorescent";
		public static final String WHITE_BALANCE_DAYLIGHT			= "daylight";
		public static final String WHITE_BALANCE_CLOUDY_DAYLIGHT	= "cloudy-daylight";
		public static final String WHITE_BALANCE_TWILIGHT			= "twilight";
		public static final String WHITE_BALANCE_SHADE				= "shade";

		// Values for color effect settings.
		public static final String EFFECT_NONE						= "none";
		public static final String EFFECT_MONO						= "mono";
		public static final String EFFECT_NEGATIVE					= "negative";
		public static final String EFFECT_SOLARIZE					= "solarize";
		public static final String EFFECT_SEPIA						= "sepia";
		public static final String EFFECT_POSTERIZE					= "posterize";
		public static final String EFFECT_WHITEBOARD				= "whiteboard";
		public static final String EFFECT_BLACKBOARD				= "blackboard";
		public static final String EFFECT_AQUA						= "aqua";

		// Values for antibanding settings.
		public static final String ANTIBANDING_AUTO					= "auto";
		public static final String ANTIBANDING_50HZ					= "50hz";
		public static final String ANTIBANDING_60HZ					= "60hz";
		public static final String ANTIBANDING_OFF					= "off";

		// Values for flash mode settings.
		/**
		 * Flash will not be fired.
		 */
		public static final String FLASH_MODE_OFF					= "off";

		/**
		 * Flash will be fired automatically when required. The flash may be fired
		 * during preview, auto-focus, or snapshot depending on the driver.
		 */
		public static final String FLASH_MODE_AUTO					= "auto";

		/**
		 * Flash will always be fired during snapshot. The flash may also be
		 * fired during preview or auto-focus depending on the driver.
		 */
		public static final String FLASH_MODE_ON					= "on";

		/**
		 * Flash will be fired in red-eye reduction mode.
		 */
		public static final String FLASH_MODE_RED_EYE				= "red-eye";

		/**
		 * Constant emission of light during preview, auto-focus and snapshot.
		 * This can also be used for video recording.
		 */
		public static final String FLASH_MODE_TORCH					= "torch";

		/**
		 * Scene mode is off.
		 */
		public static final String SCENE_MODE_AUTO					= "auto";

		/**
		 * Take photos of fast moving objects. Same as {@link
		 * #SCENE_MODE_SPORTS}.
		 */
		public static final String SCENE_MODE_ACTION				= "action";

		/**
		 * Take people pictures.
		 */
		public static final String SCENE_MODE_PORTRAIT				= "portrait";

		/**
		 * Take pictures on distant objects.
		 */
		public static final String SCENE_MODE_LANDSCAPE				= "landscape";

		/**
		 * Take photos at night.
		 */
		public static final String SCENE_MODE_NIGHT					= "night";

		/**
		 * Take people pictures at night.
		 */
		public static final String SCENE_MODE_NIGHT_PORTRAIT		= "night-portrait";

		/**
		 * Take photos in a theater. Flash light is off.
		 */
		public static final String SCENE_MODE_THEATRE				= "theatre";

		/**
		 * Take pictures on the beach.
		 */
		public static final String SCENE_MODE_BEACH					= "beach";

		/**
		 * Take pictures on the snow.
		 */
		public static final String SCENE_MODE_SNOW					= "snow";

		/**
		 * Take sunset photos.
		 */
		public static final String SCENE_MODE_SUNSET				= "sunset";

		/**
		 * Avoid blurry pictures (for example, due to hand shake).
		 */
		public static final String SCENE_MODE_STEADYPHOTO			= "steadyphoto";

		/**
		 * For shooting firework displays.
		 */
		public static final String SCENE_MODE_FIREWORKS				= "fireworks";

		/**
		 * Take photos of fast moving objects. Same as {@link
		 * #SCENE_MODE_ACTION}.
		 */
		public static final String SCENE_MODE_SPORTS				= "sports";

		/**
		 * Take indoor low-light shot.
		 */
		public static final String SCENE_MODE_PARTY					= "party";

		/**
		 * Capture the naturally warm color of scenes lit by candles.
		 */
		public static final String SCENE_MODE_CANDLELIGHT			= "candlelight";

		/**
		 * Applications are looking for a barcode. BarCodeReader driver will be
		 * optimized for barcode reading.
		 */
		public static final String SCENE_MODE_BARCODE				= "barcode";

		/**
		 * Auto-focus mode. Applications should call {@link
		 * #autoFocus(AutoFocusCallback)} to start the focus in this mode.
		 */
		public static final String FOCUS_MODE_AUTO					= "auto";

		/**
		 * Focus is set at infinity. Applications should not call
		 * {@link #autoFocus(AutoFocusCallback)} in this mode.
		 */
		public static final String FOCUS_MODE_INFINITY				= "infinity";

		/**
		 * Macro (close-up) focus mode. Applications should call
		 * {@link #autoFocus(AutoFocusCallback)} to start the focus in this
		 * mode.
		 */
		public static final String FOCUS_MODE_MACRO					= "macro";

		/**
		 * Focus is fixed. The reader is always in this mode if the focus is not
		 * adjustable. If the reader has auto-focus, this mode can fix the
		 * focus, which is usually at hyperfocal distance. Applications should
		 * not call {@link #autoFocus(AutoFocusCallback)} in this mode.
		 */
		public static final String FOCUS_MODE_FIXED					= "fixed";

		/**
		 * Extended depth of field (EDOF). Focusing is done digitally and
		 * continuously. Applications should not call {@link
		 * #autoFocus(AutoFocusCallback)} in this mode.
		 */
		public static final String FOCUS_MODE_EDOF					= "edof";

		/**
		 * Continuous auto focus mode intended for video recording. The reader
		 * continuously tries to focus. This is ideal for shooting video.
		 * Auto focus starts when the parameter is set. Applications
		 * should not call {@link #autoFocus(AutoFocusCallback)} in this mode.
		 * To stop continuous focus, applications should change the focus mode
		 * to other modes.
		 */
		public static final String FOCUS_MODE_CONTINUOUS_VIDEO		= "continuous-video";

		// Indices for focus distance array.
		/**
		 * The array index of near focus distance for use with
		 * {@link #getFocusDistances(float[])}.
		 */
		public static final int FOCUS_DISTANCE_NEAR_INDEX			= 0;

		/**
		 * The array index of optimal focus distance for use with
		 * {@link #getFocusDistances(float[])}.
		 */
		public static final int FOCUS_DISTANCE_OPTIMAL_INDEX		= 1;

		/**
		 * The array index of far focus distance for use with
		 * {@link #getFocusDistances(float[])}.
		 */
		public static final int FOCUS_DISTANCE_FAR_INDEX			= 2;

		/**
		 * The array index of minimum preview fps for use with {@link
		 * #getPreviewFpsRange(int[])} or {@link
		 * #getSupportedPreviewFpsRange()}.
		 */
		public static final int PREVIEW_FPS_MIN_INDEX				= 0;

		/**
		 * The array index of maximum preview fps for use with {@link
		 * #getPreviewFpsRange(int[])} or {@link
		 * #getSupportedPreviewFpsRange()}.
		 */
		public static final int PREVIEW_FPS_MAX_INDEX				= 1;

		// Formats for setPreviewFormat and setPictureFormat.
		private static final String PIXEL_FORMAT_YUV422SP			= "yuv422sp";
		private static final String PIXEL_FORMAT_YUV420SP			= "yuv420sp";
		private static final String PIXEL_FORMAT_YUV422I			= "yuv422i-yuyv";
		private static final String PIXEL_FORMAT_RGB565				= "rgb565";
		private static final String PIXEL_FORMAT_JPEG				= "jpeg";

		private HashMap<String, String> mMap;

		private Parameters()
		{
			mMap = new HashMap<String, String>();
		}

		/**
		 * Writes the current Parameters to the log.
		 * @hide
		 * @deprecated
		 */
		public void dump()
		{
			Log.e(TAG, "dump: size=" + mMap.size());
			for ( String k : mMap.keySet() )
			{
				Log.e(TAG, "dump: " + k + "=" + mMap.get(k));
			}
		}

		/**
		 * Creates a single string with all the parameters set in
		 * this Parameters object.
		 * <p>The {@link #unflatten(String)} method does the reverse.</p>
		 *
		 * @return a String with all values from this Parameters object, in
		 *		 semi-colon delimited key-value pairs
		 */
		public String flatten()
		{
			StringBuilder flattened = new StringBuilder();
			for ( String k : mMap.keySet() )
			{
				flattened.append(k);
				flattened.append("=");
				flattened.append(mMap.get(k));
				flattened.append(";");
			}
			// chop off the extra semicolon at the end
			flattened.deleteCharAt(flattened.length()-1);
			return(flattened.toString());
		}

		/**
		 * Takes a flattened string of parameters and adds each one to
		 * this Parameters object.
		 * <p>The {@link #flatten()} method does the reverse.</p>
		 *
		 * @param flattened	a String of parameters (key-value paired) that
		 *					are semi-colon delimited
		 */
		public void unflatten(String flattened)
		{
			StringTokenizer	tokenizer;

			int		iPos;
			String	strKV;

			mMap.clear();

			tokenizer = new StringTokenizer(flattened, ";");
			while ( tokenizer.hasMoreElements() )
			{
				strKV = tokenizer.nextToken();
				iPos = strKV.indexOf('=');
				if ( iPos == -1 )
				{
					continue;
				}
				mMap.put(strKV.substring(0, iPos), strKV.substring(iPos + 1));
			}
		}

		public void remove(String key)
		{
			mMap.remove(key);
		}

		/**
		 * Sets a String parameter.
		 *
		 * @param key	the key name for the parameter
		 * @param value the String value of the parameter
		 */
		public void set(String key, String value)
		{
			if ( (key.indexOf('=') != -1) || (key.indexOf(';') != -1) )
			{
				Log.e(TAG, "Key \"" + key + "\" contains invalid character (= or ;)");
				return;
			}
			if ( (value.indexOf('=') != -1) || (value.indexOf(';') != -1) )
			{
				Log.e(TAG, "Value \"" + value + "\" contains invalid character (= or ;)");
				return;
			}

			mMap.put(key, value);
		}

		/**
		 * Sets an integer parameter.
		 *
		 * @param key	the key name for the parameter
		 * @param value the int value of the parameter
		 */
		public void set(String key, int value)
		{
			if ( (key.indexOf('=') != -1) || (key.indexOf(';') != -1) )
			{
				Log.e(TAG, "Key \"" + key + "\" contains invalid character (= or ;)");
				return;
			}
			mMap.put(key, Integer.toString(value));
		}

		/**
		 * Returns the value of a String parameter.
		 *
		 * @param key the key name for the parameter
		 * @return the String value of the parameter
		 */
		public String get(String key)
		{
			return(mMap.get(key));
		}

		/**
		 * Returns the value of an integer parameter.
		 *
		 * @param key the key name for the parameter
		 * @return the int value of the parameter
		 */
		public int getInt(String key)
		{
			return(getInt(key, -1));
		}


		/**
		 * Sets the dimensions for preview pictures.
		 *
		 * The sides of width and height are based on reader orientation. That
		 * is, the preview size is the size before it is rotated by display
		 * orientation. So applications need to consider the display orientation
		 * while setting preview size. For example, suppose the reader supports
		 * both 480x320 and 320x480 preview sizes. The application wants a 3:2
		 * preview ratio. If the display orientation is set to 0 or 180, preview
		 * size should be set to 480x320. If the display orientation is set to
		 * 90 or 270, preview size should be set to 320x480. The display
		 * orientation should also be considered while setting picture size and
		 * thumbnail size.
		 *
		 * @param width		the width of the pictures, in pixels
		 * @param height	the height of the pictures, in pixels
		 * @see #setDisplayOrientation(int)
		 * @see #getReaderInfo(int, ReaderInfo)
		 * @see #setPictureSize(int, int)
		 * @see #setJpegThumbnailSize(int, int)
		 */
		public void setPreviewSize(int width, int height)
		{
			String v = Integer.toString(width) + "x" + Integer.toString(height);
			set(KEY_PREVIEW_SIZE, v);
		}

		/**
		 * Returns the dimensions setting for preview pictures.
		 *
		 * @return	a Size object with the height and width setting
		 *			for the preview picture
		 */
		public Size getPreviewSize()
		{
			String pair = get(KEY_PREVIEW_SIZE);
			return(strToSize(pair));
		}

		/**
		 * Gets the supported preview sizes.
		 *
		 * @return a list of Size object. This method will always return a list
		 *		 with at least one element.
		 */
		public List<Size> getSupportedPreviewSizes()
		{
			String str = get(KEY_PREVIEW_SIZE + SUPPORTED_VALUES_SUFFIX);
			return(splitSize(str));
		}
		
		/**
         * <p>Enables and disables video stabilization. Use
         * {@link #isVideoStabilizationSupported} to determine if calling this
         * method is valid.</p>
         *
         * <p>Video stabilization reduces the shaking due to the motion of the
         * camera in both the preview stream and in recorded videos, including
         * data received from the preview callback. It does not reduce motion
         * blur in images captured with
         * {@link Camera#takePicture takePicture}.</p>
         *
         * <p>Video stabilization can be enabled and disabled while preview or
         * recording is active, but toggling it may cause a jump in the video
         * stream that may be undesirable in a recorded video.</p>
         *
         * @param toggle Set to true to enable video stabilization, and false to
         * disable video stabilization.
         * @see #isVideoStabilizationSupported()
         * @see #getVideoStabilization()
         */
        //public void setVideoStabilization(boolean toggle) {
       //     set(KEY_VIDEO_STABILIZATION, toggle ? TRUE : FALSE);
        //}

        /**
         * Get the current state of video stabilization. See
         * {@link #setVideoStabilization} for details of video stabilization.
         *
         * @return true if video stabilization is enabled
         * @see #isVideoStabilizationSupported()
         * @see #setVideoStabilization(boolean)
         */
       // public boolean getVideoStabilization() {
       //     String str = get(KEY_VIDEO_STABILIZATION);
       //     return TRUE.equals(str);
       // }

        /**
         * Returns true if video stabilization is supported. See
         * {@link #setVideoStabilization} for details of video stabilization.
         *
         * @return true if video stabilization is supported
         * @see #setVideoStabilization(boolean)
         * @see #getVideoStabilization()
         */
     //   public boolean isVideoStabilizationSupported() {
      //      String str = get(KEY_VIDEO_STABILIZATION_SUPPORTED);
      //      return TRUE.equals(str);
      //  }

		/**
		 * Sets the dimensions for EXIF thumbnail in Jpeg picture. If
		 * applications set both width and height to 0, EXIF will not contain
		 * thumbnail.
		 *
		 * Applications need to consider the display orientation. See {@link
		 * #setPreviewSize(int,int)} for reference.
		 *
		 * @param width		the width of the thumbnail, in pixels
		 * @param height	the height of the thumbnail, in pixels
		 * @see #setPreviewSize(int,int)
		 */
		public void setJpegThumbnailSize(int width, int height)
		{
			set(KEY_JPEG_THUMBNAIL_WIDTH, width);
			set(KEY_JPEG_THUMBNAIL_HEIGHT, height);
		}

		/**
		 * Returns the dimensions for EXIF thumbnail in Jpeg picture.
		 *
		 * @return a Size object with the height and width setting for the EXIF
		 *		 thumbnails
		 */
		public Size getJpegThumbnailSize()
		{
			return(new Size(getInt(KEY_JPEG_THUMBNAIL_WIDTH), getInt(KEY_JPEG_THUMBNAIL_HEIGHT)));
		}

		/**
		 * Gets the supported jpeg thumbnail sizes.
		 *
		 * @return a list of Size object. This method will always return a list
		 *		 with at least two elements. Size 0,0 (no thumbnail) is always
		 *		 supported.
		 */
		public List<Size> getSupportedJpegThumbnailSizes()
		{
			String str = get(KEY_JPEG_THUMBNAIL_SIZE + SUPPORTED_VALUES_SUFFIX);
			return(splitSize(str));
		}

		/**
		 * Sets the quality of the EXIF thumbnail in Jpeg picture.
		 *
		 * @param quality the JPEG quality of the EXIF thumbnail. The range is 1
		 *				to 100, with 100 being the best.
		 */
		public void setJpegThumbnailQuality(int quality)
		{
			set(KEY_JPEG_THUMBNAIL_QUALITY, quality);
		}

		/**
		 * Returns the quality setting for the EXIF thumbnail in Jpeg picture.
		 *
		 * @return the JPEG quality setting of the EXIF thumbnail.
		 */
		public int getJpegThumbnailQuality()
		{
			return(getInt(KEY_JPEG_THUMBNAIL_QUALITY));
		}

		/**
		 * Sets Jpeg quality of captured picture.
		 *
		 * @param quality the JPEG quality of captured picture. The range is 1
		 *				to 100, with 100 being the best.
		 */
		public void setJpegQuality(int quality)
		{
			set(KEY_JPEG_QUALITY, quality);
		}

		/**
		 * Returns the quality setting for the JPEG picture.
		 *
		 * @return the JPEG picture quality setting.
		 */
		public int getJpegQuality()
		{
			return(getInt(KEY_JPEG_QUALITY));
		}

		/**
		 * Sets the rate at which preview frames are received. This is the
		 * target frame rate. The actual frame rate depends on the driver.
		 *
		 * @param fps the frame rate (frames per second)
		 * @deprecated replaced by {@link #setPreviewFpsRange(int,int)}
		 */
		@Deprecated
		public void setPreviewFrameRate(int fps)
		{
			set(KEY_PREVIEW_FRAME_RATE, fps);
		}

		/**
		 * Returns the setting for the rate at which preview frames are
		 * received. This is the target frame rate. The actual frame rate
		 * depends on the driver.
		 *
		 * @return the frame rate setting (frames per second)
		 * @deprecated replaced by {@link #getPreviewFpsRange(int[])}
		 */
		@Deprecated
		public int getPreviewFrameRate()
		{
			return(getInt(KEY_PREVIEW_FRAME_RATE));
		}

		/**
		 * Gets the supported preview frame rates.
		 *
		 * @return a list of supported preview frame rates. null if preview
		 *		 frame rate setting is not supported.
		 * @deprecated replaced by {@link #getSupportedPreviewFpsRange()}
		 */
		@Deprecated
		public List<Integer> getSupportedPreviewFrameRates()
		{
			String str = get(KEY_PREVIEW_FRAME_RATE + SUPPORTED_VALUES_SUFFIX);
			return(splitInt(str));
		}

		/**
		 * Sets the maximum and maximum preview fps. This controls the rate of
		 * preview frames received in {@link PreviewCallback}. The minimum and
		 * maximum preview fps must be one of the elements from {@link
		 * #getSupportedPreviewFpsRange}.
		 *
		 * @param min the minimum preview fps (scaled by 1000).
		 * @param max the maximum preview fps (scaled by 1000).
		 * @throws RuntimeException if fps range is invalid.
		 * @see #setPreviewCallbackWithBuffer(PreviewCallback)
		 * @see #getSupportedPreviewFpsRange()
		 */
		public void setPreviewFpsRange(int min, int max)
		{
			set(KEY_PREVIEW_FPS_RANGE, "" + min + "," + max);
		}

		/**
		 * Returns the current minimum and maximum preview fps. The values are
		 * one of the elements returned by {@link #getSupportedPreviewFpsRange}.
		 *
		 * @return range the minimum and maximum preview fps (scaled by 1000).
		 * @see #PREVIEW_FPS_MIN_INDEX
		 * @see #PREVIEW_FPS_MAX_INDEX
		 * @see #getSupportedPreviewFpsRange()
		 */
		public void getPreviewFpsRange(int[] range)
		{
			if ( (range == null) || (range.length != 2) )
			{
				throw new IllegalArgumentException("range must be an array with two elements.");
			}
			splitInt(get(KEY_PREVIEW_FPS_RANGE), range);
		}

		/**
		 * Gets the supported preview fps (frame-per-second) ranges. Each range
		 * contains a minimum fps and maximum fps. If minimum fps equals to
		 * maximum fps, the reader outputs frames in fixed frame rate. If not,
		 * the reader outputs frames in auto frame rate. The actual frame rate
		 * fluctuates between the minimum and the maximum. The values are
		 * multiplied by 1000 and represented in integers. For example, if frame
		 * rate is 26.623 frames per second, the value is 26623.
		 *
		 * @return a list of supported preview fps ranges. This method returns a
		 *		 list with at least one element. Every element is an int array
		 *		 of two values - minimum fps and maximum fps. The list is
		 *		 sorted from small to large (first by maximum fps and then
		 *		 minimum fps).
		 * @see #PREVIEW_FPS_MIN_INDEX
		 * @see #PREVIEW_FPS_MAX_INDEX
		 */
		public List<int[]> getSupportedPreviewFpsRange()
		{
			String str = get(KEY_PREVIEW_FPS_RANGE + SUPPORTED_VALUES_SUFFIX);
			return(splitRange(str));
		}

		/**
		 * Sets the image format for preview pictures.
		 * <p>If this is never called, the default format will be
		 * {@link ImageFormat#NV21}, which
		 * uses the NV21 encoding format.</p>
		 *
		 * @param pixel_format the desired preview picture format, defined
		 *	by one of the {@link ImageFormat} constants.
		 *	(E.g., <var>ImageFormat.NV21</var> (default),
		 *						<var>ImageFormat.RGB_565</var>, or
		 *						<var>ImageFormat.JPEG</var>)
		 * @see ImageFormat
		 */
		public void setPreviewFormat(int pixel_format)
		{
			String s = readerFormatForPixelFormat(pixel_format);
			if ( s == null )
			{
				throw new IllegalArgumentException("Invalid pixel_format=" + pixel_format);
			}

			set(KEY_PREVIEW_FORMAT, s);
		}

		/**
		 * Returns the image format for preview frames got from
		 * {@link PreviewCallback}.
		 *
		 * @return the preview format.
		 * @see ImageFormat
		 */
		public int getPreviewFormat()
		{
			return(pixelFormatForReaderFormat(get(KEY_PREVIEW_FORMAT)));
		}

		/**
		 * Gets the supported preview formats.
		 *
		 * @return a list of supported preview formats. This method will always
		 *		 return a list with at least one element.
		 * @see ImageFormat
		 */
		public List<Integer> getSupportedPreviewFormats()
		{
			String str = get(KEY_PREVIEW_FORMAT + SUPPORTED_VALUES_SUFFIX);
			ArrayList<Integer> formats = new ArrayList<Integer>();
			for (String s : split(str))
			{
				int f = pixelFormatForReaderFormat(s);
				if ( f == ImageFormat.UNKNOWN )
					continue;
				formats.add(f);
			}
			return(formats);
		}

		/**
		 * Sets the dimensions for pictures.
		 *
		 * Applications need to consider the display orientation. See {@link
		 * #setPreviewSize(int,int)} for reference.
		 *
		 * @param width		the width for pictures, in pixels
		 * @param height	the height for pictures, in pixels
		 * @see #setPreviewSize(int,int)
		 *
		 */
		public void setPictureSize(int width, int height)
		{
			String v = Integer.toString(width) + "x" + Integer.toString(height);
			set(KEY_PICTURE_SIZE, v);
		}

		/**
		 * Returns the dimension setting for pictures.
		 *
		 * @return	a Size object with the height and width setting
		 *			for pictures
		 */
		public Size getPictureSize()
		{
			String pair = get(KEY_PICTURE_SIZE);
			return(strToSize(pair));
		}

		/**
		 * Gets the supported picture sizes.
		 *
		 * @return a list of supported picture sizes. This method will always
		 *		 return a list with at least one element.
		 */
		public List<Size> getSupportedPictureSizes()
		{
			String str = get(KEY_PICTURE_SIZE + SUPPORTED_VALUES_SUFFIX);
			return(splitSize(str));
		}

		/**
		 * Sets the image format for pictures.
		 *
		 * @param pixel_format the desired picture format
		 *					 (<var>ImageFormat.NV21</var>,
		 *						<var>ImageFormat.RGB_565</var>, or
		 *						<var>ImageFormat.JPEG</var>)
		 * @see ImageFormat
		 */
		public void setPictureFormat(int pixel_format)
		{
			String s = readerFormatForPixelFormat(pixel_format);
			if ( s == null )
			{
				throw new IllegalArgumentException("Invalid pixel_format=" + pixel_format);
			}

			set(KEY_PICTURE_FORMAT, s);
		}

		/**
		 * Returns the image format for pictures.
		 *
		 * @return the picture format
		 * @see ImageFormat
		 */
		public int getPictureFormat()
		{
			return(pixelFormatForReaderFormat(get(KEY_PICTURE_FORMAT)));
		}

		/**
		 * Gets the supported picture formats.
		 *
		 * @return supported picture formats. This method will always return a
		 *		 list with at least one element.
		 * @see ImageFormat
		 */
		public List<Integer> getSupportedPictureFormats()
		{
			String str = get(KEY_PICTURE_FORMAT + SUPPORTED_VALUES_SUFFIX);
			ArrayList<Integer> formats = new ArrayList<Integer>();
			for (String s : split(str))
			{
				int f = pixelFormatForReaderFormat(s);
				if ( f == ImageFormat.UNKNOWN )
					continue;
				formats.add(f);
			}
			return(formats);
		}

		private String readerFormatForPixelFormat(int pixel_format)
		{
			switch(pixel_format)
			{
			case ImageFormat.NV16:
				return(PIXEL_FORMAT_YUV422SP);
			case ImageFormat.NV21:
				return(PIXEL_FORMAT_YUV420SP);
			case ImageFormat.YUY2:
				return(PIXEL_FORMAT_YUV422I);
			case ImageFormat.RGB_565:
				return(PIXEL_FORMAT_RGB565);
			case ImageFormat.JPEG:
				return(PIXEL_FORMAT_JPEG);
			default:
				break;
			}
			return(null);
		}

		private int pixelFormatForReaderFormat(String format)
		{
			if ( format == null )
				return(ImageFormat.UNKNOWN);

			if ( format.equals(PIXEL_FORMAT_YUV422SP) )
				return(ImageFormat.NV16);

			if ( format.equals(PIXEL_FORMAT_YUV420SP) )
				return(ImageFormat.NV21);

			if ( format.equals(PIXEL_FORMAT_YUV422I) )
				return(ImageFormat.YUY2);

			if ( format.equals(PIXEL_FORMAT_RGB565) )
				return(ImageFormat.RGB_565);

			if ( format.equals(PIXEL_FORMAT_JPEG) )
				return(ImageFormat.JPEG);

			return(ImageFormat.UNKNOWN);
		}

		/**
		 * Sets the rotation angle in degrees relative to the orientation of
		 * the reader. This affects the pictures returned from JPEG {@link
		 * PictureCallback}. The reader driver may set orientation in the
		 * EXIF header without rotating the picture. Or the driver may rotate
		 * the picture and the EXIF thumbnail. If the Jpeg picture is rotated,
		 * the orientation in the EXIF header will be missing or 1 (row #0 is
		 * top and column #0 is left side).
		 *
		 * <p>If applications want to rotate the picture to match the orientation
		 * of what users see, apps should use {@link
		 * android.view.OrientationEventListener} and {@link ReaderInfo}.
		 * The value from OrientationEventListener is relative to the natural
		 * orientation of the device. ReaderInfo.orientation is the angle
		 * between reader orientation and natural device orientation. The sum
		 * of the two is the rotation angle for back-facing reader. The
		 * difference of the two is the rotation angle for front-facing reader.
		 * Note that the JPEG pictures of front-facing readers are not mirrored
		 * as in preview display.
		 *
		 * <p>For example, suppose the natural orientation of the device is
		 * portrait. The device is rotated 270 degrees clockwise, so the device
		 * orientation is 270. Suppose a back-facing reader sensor is mounted in
		 * landscape and the top side of the reader sensor is aligned with the
		 * right edge of the display in natural orientation. So the reader
		 * orientation is 90. The rotation should be set to 0 (270 + 90).
		 *
		 * <p>The reference code is as follows.
		 *
		 * <pre>
		 *	#import com.motorolasolutions.adc.decoder;
		 *
		 *	public void public void onOrientationChanged(int orientation)
		 *	{
		 *		int rotation = 0;
		 *
		 *		if ( orientation == ORIENTATION_UNKNOWN )
		 *			return;
		 *
		 *		BarCodeReader.ReaderInfo info = new BarCodeReader.ReaderInfo();
		 *		BarCodeReader.getReaderInfo(readerId, info);
		 *		orientation = (orientation + 45) / 90 * 90;
		 *		if ( info.facing == BarCodeReader.ReaderInfo.BCRDR_FACING_FRONT )
		 *		{
		 *			rotation = (info.orientation - orientation + 360) % 360;
		 *		}
		 *		else
		 *		{
		 *			// back-facing reader
		 *			rotation = (info.orientation + orientation) % 360;
		 *		}
		 *		mParameters.setRotation(rotation);
		 *	}
		 * </pre>
		 *
		 * @param rotation The rotation angle in degrees relative to the
		 *				 orientation of the reader. Rotation can only be 0,
		 *				 90, 180 or 270.
		 * @throws IllegalArgumentException if rotation value is invalid.
		 * @see android.view.OrientationEventListener
		 * @see #getReaderInfo(int, ReaderInfo)
		 */
		public void setRotation(int rotation)
		{
			if ( (rotation == 0) || (rotation == 90) || (rotation == 180) || (rotation == 270) )
			{
				set(KEY_ROTATION, Integer.toString(rotation));
			}
			else
			{
				throw new IllegalArgumentException("Invalid rotation=" + rotation);
			}
		}

		/**
		 * Sets GPS latitude coordinate. This will be stored in JPEG EXIF
		 * header.
		 *
		 * @param latitude GPS latitude coordinate.
		 */
		public void setGpsLatitude(double latitude)
		{
			set(KEY_GPS_LATITUDE, Double.toString(latitude));
		}

		/**
		 * Sets GPS longitude coordinate. This will be stored in JPEG EXIF
		 * header.
		 *
		 * @param longitude GPS longitude coordinate.
		 */
		public void setGpsLongitude(double longitude)
		{
			set(KEY_GPS_LONGITUDE, Double.toString(longitude));
		}

		/**
		 * Sets GPS altitude. This will be stored in JPEG EXIF header.
		 *
		 * @param altitude GPS altitude in meters.
		 */
		public void setGpsAltitude(double altitude)
		{
			set(KEY_GPS_ALTITUDE, Double.toString(altitude));
		}

		/**
		 * Sets GPS timestamp. This will be stored in JPEG EXIF header.
		 *
		 * @param timestamp	GPS timestamp (UTC in seconds since January 1,
		 *					1970).
		 */
		public void setGpsTimestamp(long timestamp)
		{
			set(KEY_GPS_TIMESTAMP, Long.toString(timestamp));
		}

		/**
		 * Sets GPS processing method. It will store up to 32 characters
		 * in JPEG EXIF header.
		 *
		 * @param processing_method The processing method to get this location.
		 */
		public void setGpsProcessingMethod(String processing_method)
		{
			set(KEY_GPS_PROCESSING_METHOD, processing_method);
		}

		/**
		 * Removes GPS latitude, longitude, altitude, and timestamp from the
		 * parameters.
		 */
		public void removeGpsData()
		{
			remove(KEY_GPS_LATITUDE);
			remove(KEY_GPS_LONGITUDE);
			remove(KEY_GPS_ALTITUDE);
			remove(KEY_GPS_TIMESTAMP);
			remove(KEY_GPS_PROCESSING_METHOD);
		}

		/**
		 * Gets the current white balance setting.
		 *
		 * @return current white balance. null if white balance setting is not
		 *		 supported.
		 * @see #WHITE_BALANCE_AUTO
		 * @see #WHITE_BALANCE_INCANDESCENT
		 * @see #WHITE_BALANCE_FLUORESCENT
		 * @see #WHITE_BALANCE_WARM_FLUORESCENT
		 * @see #WHITE_BALANCE_DAYLIGHT
		 * @see #WHITE_BALANCE_CLOUDY_DAYLIGHT
		 * @see #WHITE_BALANCE_TWILIGHT
		 * @see #WHITE_BALANCE_SHADE
		 *
		 */
		public String getWhiteBalance()
		{
			return(get(KEY_WHITE_BALANCE));
		}

		/**
		 * Sets the white balance.
		 *
		 * @param value new white balance.
		 * @see #getWhiteBalance()
		 */
		public void setWhiteBalance(String value)
		{
			set(KEY_WHITE_BALANCE, value);
		}

		/**
		 * Gets the supported white balance.
		 *
		 * @return a list of supported white balance. null if white balance
		 *		 setting is not supported.
		 * @see #getWhiteBalance()
		 */
		public List<String> getSupportedWhiteBalance()
		{
			String str = get(KEY_WHITE_BALANCE + SUPPORTED_VALUES_SUFFIX);
			return(split(str));
		}

		/**
		 * Gets the current color effect setting.
		 *
		 * @return current color effect. null if color effect
		 *		 setting is not supported.
		 * @see #EFFECT_NONE
		 * @see #EFFECT_MONO
		 * @see #EFFECT_NEGATIVE
		 * @see #EFFECT_SOLARIZE
		 * @see #EFFECT_SEPIA
		 * @see #EFFECT_POSTERIZE
		 * @see #EFFECT_WHITEBOARD
		 * @see #EFFECT_BLACKBOARD
		 * @see #EFFECT_AQUA
		 */
		public String getColorEffect()
		{
			return(get(KEY_EFFECT));
		}

		/**
		 * Sets the current color effect setting.
		 *
		 * @param value new color effect.
		 * @see #getColorEffect()
		 */
		public void setColorEffect(String value)
		{
			set(KEY_EFFECT, value);
		}

		/**
		 * Gets the supported color effects.
		 *
		 * @return a list of supported color effects. null if color effect
		 *		 setting is not supported.
		 * @see #getColorEffect()
		 */
		public List<String> getSupportedColorEffects()
		{
			String str = get(KEY_EFFECT + SUPPORTED_VALUES_SUFFIX);
			return(split(str));
		}


		/**
		 * Gets the current antibanding setting.
		 *
		 * @return current antibanding. null if antibanding setting is not
		 *		 supported.
		 * @see #ANTIBANDING_AUTO
		 * @see #ANTIBANDING_50HZ
		 * @see #ANTIBANDING_60HZ
		 * @see #ANTIBANDING_OFF
		 */
		public String getAntibanding()
		{
			return(get(KEY_ANTIBANDING));
		}

		/**
		 * Sets the antibanding.
		 *
		 * @param antibanding new antibanding value.
		 * @see #getAntibanding()
		 */
		public void setAntibanding(String antibanding)
		{
			set(KEY_ANTIBANDING, antibanding);
		}

		/**
		 * Gets the supported antibanding values.
		 *
		 * @return a list of supported antibanding values. null if antibanding
		 *		 setting is not supported.
		 * @see #getAntibanding()
		 */
		public List<String> getSupportedAntibanding()
		{
			String str = get(KEY_ANTIBANDING + SUPPORTED_VALUES_SUFFIX);
			return(split(str));
		}

		/**
		 * Gets the current scene mode setting.
		 *
		 * @return one of SCENE_MODE_XXX string constant. null if scene mode
		 *		 setting is not supported.
		 * @see #SCENE_MODE_AUTO
		 * @see #SCENE_MODE_ACTION
		 * @see #SCENE_MODE_PORTRAIT
		 * @see #SCENE_MODE_LANDSCAPE
		 * @see #SCENE_MODE_NIGHT
		 * @see #SCENE_MODE_NIGHT_PORTRAIT
		 * @see #SCENE_MODE_THEATRE
		 * @see #SCENE_MODE_BEACH
		 * @see #SCENE_MODE_SNOW
		 * @see #SCENE_MODE_SUNSET
		 * @see #SCENE_MODE_STEADYPHOTO
		 * @see #SCENE_MODE_FIREWORKS
		 * @see #SCENE_MODE_SPORTS
		 * @see #SCENE_MODE_PARTY
		 * @see #SCENE_MODE_CANDLELIGHT
		 */
		public String getSceneMode()
		{
			return(get(KEY_SCENE_MODE));
		}

		/**
		 * Sets the scene mode. Changing scene mode may override other
		 * parameters (such as flash mode, focus mode, white balance). For
		 * example, suppose originally flash mode is on and supported flash
		 * modes are on/off. In night scene mode, both flash mode and supported
		 * flash mode may be changed to off. After setting scene mode,
		 * applications should call getParameters to know if some parameters are
		 * changed.
		 *
		 * @param value scene mode.
		 * @see #getSceneMode()
		 */
		public void setSceneMode(String value)
		{
			set(KEY_SCENE_MODE, value);
		}

		/**
		 * Gets the supported scene modes.
		 *
		 * @return a list of supported scene modes. null if scene mode setting
		 *		 is not supported.
		 * @see #getSceneMode()
		 */
		public List<String> getSupportedSceneModes()
		{
			String str = get(KEY_SCENE_MODE + SUPPORTED_VALUES_SUFFIX);
			return(split(str));
		}

		/**
		 * Gets the current flash mode setting.
		 *
		 * @return current flash mode. null if flash mode setting is not
		 *		 supported.
		 * @see #FLASH_MODE_OFF
		 * @see #FLASH_MODE_AUTO
		 * @see #FLASH_MODE_ON
		 * @see #FLASH_MODE_RED_EYE
		 * @see #FLASH_MODE_TORCH
		 */
		public String getFlashMode()
		{
			return(get(KEY_FLASH_MODE));
		}

		/**
		 * Sets the flash mode.
		 *
		 * @param value flash mode.
		 * @see #getFlashMode()
		 */
		public void setFlashMode(String value)
		{
			set(KEY_FLASH_MODE, value);
		}

		/**
		 * Gets the supported flash modes.
		 *
		 * @return a list of supported flash modes. null if flash mode setting
		 *		 is not supported.
		 * @see #getFlashMode()
		 */
		public List<String> getSupportedFlashModes()
		{
			String str = get(KEY_FLASH_MODE + SUPPORTED_VALUES_SUFFIX);
			return(split(str));
		}

		/**
		 * Gets the current focus mode setting.
		 *
		 * @return current focus mode. This method will always return a non-null
		 *		 value. Applications should call {@link
		 *		 #autoFocus(AutoFocusCallback)} to start the focus if focus
		 *		 mode is FOCUS_MODE_AUTO or FOCUS_MODE_MACRO.
		 * @see #FOCUS_MODE_AUTO
		 * @see #FOCUS_MODE_INFINITY
		 * @see #FOCUS_MODE_MACRO
		 * @see #FOCUS_MODE_FIXED
		 * @see #FOCUS_MODE_EDOF
		 * @see #FOCUS_MODE_CONTINUOUS_VIDEO
		 */
		public String getFocusMode()
		{
			return(get(KEY_FOCUS_MODE));
		}

		/**
		 * Sets the focus mode.
		 *
		 * @param value focus mode.
		 * @see #getFocusMode()
		 */
		public void setFocusMode(String value)
		{
			set(KEY_FOCUS_MODE, value);
		}

		/**
		 * Gets the supported focus modes.
		 *
		 * @return a list of supported focus modes. This method will always
		 *		 return a list with at least one element.
		 * @see #getFocusMode()
		 */
		public List<String> getSupportedFocusModes()
		{
			String str = get(KEY_FOCUS_MODE + SUPPORTED_VALUES_SUFFIX);
			return(split(str));
		}

		/**
		 * Gets the focal length (in millimeter) of the reader.
		 *
		 * @return the focal length. This method will always return a valid
		 *		 value.
		 */
		public float getFocalLength()
		{
			return(Float.parseFloat(get(KEY_FOCAL_LENGTH)));
		}

		/**
		 * Gets the horizontal angle of view in degrees.
		 *
		 * @return horizontal angle of view. This method will always return a
		 *		 valid value.
		 */
		public float getHorizontalViewAngle()
		{
			return(Float.parseFloat(get(KEY_HORIZONTAL_VIEW_ANGLE)));
		}

		/**
		 * Gets the vertical angle of view in degrees.
		 *
		 * @return vertical angle of view. This method will always return a
		 *		 valid value.
		 */
		public float getVerticalViewAngle()
		{
			return(Float.parseFloat(get(KEY_VERTICAL_VIEW_ANGLE)));
		}

		/**
		 * Gets the current exposure compensation index.
		 *
		 * @return current exposure compensation index. The range is {@link
		 *		 #getMinExposureCompensation} to {@link
		 *		 #getMaxExposureCompensation}. 0 means exposure is not
		 *		 adjusted.
		 */
		public int getExposureCompensation()
		{
			return(getInt(KEY_EXPOSURE_COMPENSATION, 0));
		}

		/**
		 * Sets the exposure compensation index.
		 *
		 * @param value exposure compensation index. The valid value range is
		 *		from {@link #getMinExposureCompensation} (inclusive) to {@link
		 *		#getMaxExposureCompensation} (inclusive). 0 means exposure is
		 *		not adjusted. Application should call
		 *		getMinExposureCompensation and getMaxExposureCompensation to
		 *		know if exposure compensation is supported.
		 */
		public void setExposureCompensation(int value)
		{
			set(KEY_EXPOSURE_COMPENSATION, value);
		}

		/**
		 * Gets the maximum exposure compensation index.
		 *
		 * @return maximum exposure compensation index (>=0). If both this
		 *		 method and {@link #getMinExposureCompensation} return 0,
		 *		 exposure compensation is not supported.
		 */
		public int getMaxExposureCompensation()
		{
			return(getInt(KEY_MAX_EXPOSURE_COMPENSATION, 0));
		}

		/**
		 * Gets the minimum exposure compensation index.
		 *
		 * @return minimum exposure compensation index (<=0). If both this
		 *		 method and {@link #getMaxExposureCompensation} return 0,
		 *		 exposure compensation is not supported.
		 */
		public int getMinExposureCompensation()
		{
			return(getInt(KEY_MIN_EXPOSURE_COMPENSATION, 0));
		}

		/**
		 * Gets the exposure compensation step.
		 *
		 * @return exposure compensation step. Applications can get EV by
		 *		 multiplying the exposure compensation index and step. Ex: if
		 *		 exposure compensation index is -6 and step is 0.333333333, EV
		 *		 is -2.
		 */
		public float getExposureCompensationStep()
		{
			return(getFloat(KEY_EXPOSURE_COMPENSATION_STEP, 0));
		}

		/**
		 * Gets current zoom value. This also works when smooth zoom is in
		 * progress. Applications should check {@link #isZoomSupported} before
		 * using this method.
		 *
		 * @return the current zoom value. The range is 0 to {@link
		 *		 #getMaxZoom}. 0 means the reader is not zoomed.
		 */
		public int getZoom()
		{
			return(getInt(KEY_ZOOM, 0));
		}

		/**
		 * Sets current zoom value. If the reader is zoomed (value > 0), the
		 * actual picture size may be smaller than picture size setting.
		 * Applications can check the actual picture size after picture is
		 * returned from {@link PictureCallback}. The preview size remains the
		 * same in zoom. Applications should check {@link #isZoomSupported}
		 * before using this method.
		 *
		 * @param value zoom value. The valid range is 0 to {@link #getMaxZoom}.
		 */
		public void setZoom(int value)
		{
			set(KEY_ZOOM, value);
		}

		/**
		 * Returns true if zoom is supported. Applications should call this
		 * before using other zoom methods.
		 *
		 * @return true if zoom is supported.
		 */
		public boolean isZoomSupported()
		{
			String str = get(KEY_ZOOM_SUPPORTED);
			return(TRUE.equals(str));
		}

		/**
		 * Gets the maximum zoom value allowed for snapshot. This is the maximum
		 * value that applications can set to {@link #setZoom(int)}.
		 * Applications should call {@link #isZoomSupported} before using this
		 * method. This value may change in different preview size. Applications
		 * should call this again after setting preview size.
		 *
		 * @return the maximum zoom value supported by the reader.
		 */
		public int getMaxZoom()
		{
			return(getInt(KEY_MAX_ZOOM, 0));
		}

		/**
		 * Gets the zoom ratios of all zoom values. Applications should check
		 * {@link #isZoomSupported} before using this method.
		 *
		 * @return the zoom ratios in 1/100 increments. Ex: a zoom of 3.2x is
		 *		 returned as 320. The number of elements is {@link
		 *		 #getMaxZoom} + 1. The list is sorted from small to large. The
		 *		 first element is always 100. The last element is the zoom
		 *		 ratio of the maximum zoom value.
		 */
		public List<Integer> getZoomRatios()
		{
			return(splitInt(get(KEY_ZOOM_RATIOS)));
		}

		/**
		 * Returns true if smooth zoom is supported. Applications should call
		 * this before using other smooth zoom methods.
		 *
		 * @return true if smooth zoom is supported.
		 */
		public boolean isSmoothZoomSupported()
		{
			String str = get(KEY_SMOOTH_ZOOM_SUPPORTED);
			return(TRUE.equals(str));
		}

		/**
		 * Gets the distances from the reader to where an object appears to be
		 * in focus. The object is sharpest at the optimal focus distance. The
		 * depth of field is the far focus distance minus near focus distance.
		 *
		 * Focus distances may change after calling {@link
		 * #autoFocus(AutoFocusCallback)}, {@link #cancelAutoFocus}, or {@link
		 * #startPreview()}. Applications can call {@link #getParameters()}
		 * and this method anytime to get the latest focus distances. If the
		 * focus mode is FOCUS_MODE_CONTINUOUS_VIDEO, focus distances may change
		 * from time to time.
		 *
		 * This method is intended to estimate the distance between the reader
		 * and the subject. After autofocus, the subject distance may be within
		 * near and far focus distance. However, the precision depends on the
		 * reader hardware, autofocus algorithm, the focus area, and the scene.
		 * The error can be large and it should be only used as a reference.
		 *
		 * Far focus distance >= optimal focus distance >= near focus distance.
		 * If the focus distance is infinity, the value will be
		 * Float.POSITIVE_INFINITY.
		 *
		 * @param output focus distances in meters. output must be a float
		 *		array with three elements. Near focus distance, optimal focus
		 *		distance, and far focus distance will be filled in the array.
		 * @see #FOCUS_DISTANCE_NEAR_INDEX
		 * @see #FOCUS_DISTANCE_OPTIMAL_INDEX
		 * @see #FOCUS_DISTANCE_FAR_INDEX
		 */
		public void getFocusDistances(float[] output)
		{
			if ( (output == null) || (output.length != 3) )
			{
				throw new IllegalArgumentException("output must be an float array with three elements.");
			}
			splitFloat(get(KEY_FOCUS_DISTANCES), output);
		}

		// Splits a comma delimited string to an ArrayList of String.
		// Return null if the passing string is null or the size is 0.
		private ArrayList<String> split(String str)
		{
			if ( str == null )
				return(null);

			// Use StringTokenizer because it is faster than split.
			StringTokenizer tokenizer = new StringTokenizer(str, ",");
			ArrayList<String> substrings = new ArrayList<String>();
			while (tokenizer.hasMoreElements())
			{
				substrings.add(tokenizer.nextToken());
			}
			return(substrings);
		}

		// Splits a comma delimited string to an ArrayList of Integer.
		// Return null if the passing string is null or the size is 0.
		private ArrayList<Integer> splitInt(String str)
		{
			if ( str == null )
				return(null);

			StringTokenizer tokenizer = new StringTokenizer(str, ",");
			ArrayList<Integer> substrings = new ArrayList<Integer>();
			while (tokenizer.hasMoreElements())
			{
				String token = tokenizer.nextToken();
				substrings.add(Integer.parseInt(token));
			}
			if ( substrings.size() == 0 )
				return(null);

			return(substrings);
		}

		private void splitInt(String str, int[] output)
		{
			if ( str == null )
				return;

			StringTokenizer tokenizer = new StringTokenizer(str, ",");
			int index = 0;
			while (tokenizer.hasMoreElements())
			{
				String token = tokenizer.nextToken();
				output[index++] = Integer.parseInt(token);
			}
		}

		// Splits a comma delimited string to an ArrayList of Float.
		private void splitFloat(String str, float[] output)
		{
			if ( str == null )
				return;

			StringTokenizer tokenizer = new StringTokenizer(str, ",");
			int index = 0;
			while (tokenizer.hasMoreElements())
			{
				String token = tokenizer.nextToken();
				output[index++] = Float.parseFloat(token);
			}
		}

		// Returns the value of a float parameter.
		private float getFloat(String key, float defaultValue)
		{
			float	flRetVal;

			try
			{
				flRetVal = Float.parseFloat(mMap.get(key));
				return(flRetVal);
			}
			catch ( Throwable thrw )
			{
			}
			return(defaultValue);
		}

		// Returns the value of a integer parameter.
		private int getInt(String key, int defaultValue)
		{
			int	iRetVal;

			try
			{
				iRetVal = Integer.parseInt(mMap.get(key));
				return(iRetVal);
			}
			catch ( Throwable thrw )
			{
			}
			return(defaultValue);
		}

		// Splits a comma delimited string to an ArrayList of Size.
		// Return null if the passing string is null or the size is 0.
		private ArrayList<Size> splitSize(String str)
		{
			if ( str == null )
				return(null);

			StringTokenizer tokenizer = new StringTokenizer(str, ",");
			ArrayList<Size> sizeList = new ArrayList<Size>();
			while (tokenizer.hasMoreElements())
			{
				Size size = strToSize(tokenizer.nextToken());
				if ( size != null )
					sizeList.add(size);
			}
			if ( sizeList.size() == 0 )
				return(null);

			return(sizeList);
		}

		// Parses a string (ex: "480x320") to Size object.
		// Return null if the passing string is null.
		private Size strToSize(String str)
		{
			if ( str == null )
				return(null);

			int pos = str.indexOf('x');
			if ( pos != -1 )
			{
				String width = str.substring(0, pos);
				String height = str.substring(pos + 1);
				return(new Size(Integer.parseInt(width), Integer.parseInt(height)));
			}
			Log.e(TAG, "Invalid size parameter string=" + str);
			return(null);
		}

		// Splits a comma delimited string to an ArrayList of int array.
		// Example string: "(10000,26623),(10000,30000)". Return null if the
		// passing string is null or the size is 0.
		private ArrayList<int[]> splitRange(String str)
		{
			if ( (str == null) || (str.charAt(0) != '(') || (str.charAt(str.length() - 1) != ')') )
			{
				Log.e(TAG, "Invalid range list string=" + str);
				return(null);
			}

			ArrayList<int[]> rangeList = new ArrayList<int[]>();
			int endIndex, fromIndex = 1;
			do
			{
				int[] range = new int[2];
				endIndex = str.indexOf("),(", fromIndex);
				if ( endIndex == -1 )
					endIndex = str.length() - 1;
				splitInt(str.substring(fromIndex, endIndex), range);
				rangeList.add(range);
				fromIndex = endIndex + 3;
			}
			while ( endIndex != (str.length() - 1) );

			if ( rangeList.size() == 0 )
				return(null);

			return(rangeList);
		}
	};
}
