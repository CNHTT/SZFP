package com.szfp.szfp;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.szfp.szfp.utils.GreenDaoManager;
import com.szfp.szfplib.utils.Utils;

import java.io.File;

/**
 * 作者：ct on 2017/6/22 10:19
 * 邮箱：cnhttt@163.com
 */

public class SzfpApplication extends Application {
    public static String diskCachePath; // 缓存目录
    private static String mAppName;
    public static String DISK_CACHE_PATH = "/" + mAppName + "/image/"; // 图片缓存地址
    public static String PHOTO       =      "photo/";
    public static String FINGERPRINT =      "fingerprint/";
    public static String PDF =      "pdf/";


    private String rootPath;
    private static final Logger logger = LoggerFactory.getLogger();

    private HandlerThread handlerThread;
    public String getRootPath() {
        return rootPath;
    }

    public HandlerThread getHandlerThread() {
        return handlerThread;
    }


    @Override
    public void onCreate() {
        mAppName ="SZFP";
        DISK_CACHE_PATH = "/" + mAppName + "/image/";
        checkCachePath();
        super.onCreate();
        Utils.init(getApplicationContext());
        GreenDaoManager.getInstance();
        PropertyConfigurator.getConfigurator(this).configure();
        final FileAppender fa = (FileAppender) logger.getAppender(1);
        fa.setAppend(true);

        logger.debug("**********Enter Myapplication********");
        handlerThread = new HandlerThread("handlerThread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        // handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        setRootPath();
    }
    private void setRootPath() {
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            rootPath = info.applicationInfo.dataDir;
            Log.i("rootPath", "################rootPath=" + rootPath);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void checkCachePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            diskCachePath = Environment.getExternalStorageDirectory().getPath() + DISK_CACHE_PATH;

            File outFilead = new File(diskCachePath + PHOTO);

            File outFilechu = new File(diskCachePath + FINGERPRINT);
            File outFilepdf = new File(diskCachePath + PDF);

            File outFile = new File(diskCachePath);
            outFile.mkdirs();

            outFilead.mkdirs();

            outFilechu.mkdirs();
            outFilepdf.mkdirs();

        } else {
            new CountDownTimer(2000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        diskCachePath = Environment.getExternalStorageDirectory().getPath() + DISK_CACHE_PATH;


                        File outFilead = new File(diskCachePath + PHOTO);

                        File outFilechu = new File(diskCachePath + FINGERPRINT);
                        File outFilepdf = new File(diskCachePath + PDF);

                        File outFile = new File(diskCachePath);
                        outFile.mkdirs();

                        outFilead.mkdirs();

                        outFilechu.mkdirs();
                        outFilepdf.mkdirs();
                        this.cancel();
                    } else {
                        this.start();
                    }
                }
            }.start();
        }

    }

}
