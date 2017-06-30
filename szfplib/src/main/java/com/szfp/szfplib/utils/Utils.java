package com.szfp.szfplib.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 项目名称：Avatar.
 * 创建人： CT.
 * 创建时间: 2017/6/20.
 * GitHub:https://github.com/CNHTT
 */

public class Utils {
    private static Context context;

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        Utils.context = context.getApplicationContext();
        CrashUtils.getInstance(context).init();
    }

    /**
     * 在某种获取不到 Context 的情况下，即可以使用才方法获取 Context
     * <p>
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("请先调用init()方法");
    }

    //----------------------------------------------------------------------------------------------延时任务封装 start
    public interface DelayListener {
        void doSomething();
    }

    public static void delayToDo(final DelayListener delayListener, long delayTime) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                delayListener.doSomething();
            }
        }, delayTime);
    }
    //==============================================================================================延时任务封装 end

    /**
     * 倒计时
     *
     * @param textView 控件
     * @param waitTime 倒计时总时长
     * @param interval 倒计时的间隔时间
     * @param hint     倒计时完毕时显示的文字
     */
    public static void countDown(final TextView textView, long waitTime, long interval, final String hint) {
        textView.setEnabled(false);
        android.os.CountDownTimer timer = new android.os.CountDownTimer(waitTime, interval) {

            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText("剩下 " + (millisUntilFinished / 1000) + " S");
            }

            @Override
            public void onFinish() {
                textView.setEnabled(true);
                textView.setText(hint);
            }
        };
        timer.start();
    }

    /***
     * 隐藏键盘
     * @param view
     * @param context
     */
    public static void hideKeyboard(View view , final Activity context) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (context.getCurrentFocus() != null && context.getCurrentFocus().getWindowToken() != null) {
                        manager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
    }


    /**
     * 手动计算出listView的高度，但是不再具有滚动效果
     *
     * @param listView
     */
    public static void fixListViewHeight(ListView listView) {
        // 如果没有设置数据适配器，则ListView没有子项，返回。
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        if (listAdapter == null) {
            return;
        }
        for (int index = 0, len = listAdapter.getCount(); index < len; index++) {
            View listViewItem = listAdapter.getView(index, null, listView);
            // 计算子项View 的宽高
            listViewItem.measure(0, 0);
            // 计算所有子项的高度
            totalHeight += listViewItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符的高度
        // params.height设置ListView完全显示需要的高度
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //---------------------------------------------MD5加密-------------------------------------------

    /**
     * 生成MD5加密32位字符串
     *
     * @param MStr :需要加密的字符串
     * @return
     */
    public static String Md5(String MStr) {
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(MStr.getBytes());
            return bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(MStr.hashCode());
        }
    }

    // MD5内部算法---------------不能修改!
    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    //============================================MD5加密============================================

    /**
     * 根据资源名称获取资源 id
     *
     *      不提倡使用这个方法获取资源,比其直接获取ID效率慢
     *
     * 例如
     *      getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
     *
     * @param context
     * @param name
     * @param defType
     * @return
     */
    public static final int getResIdByName(Context context,String name,String defType) {
        return context.getResources().getIdentifier("ic_launcher", "drawable", context.getPackageName());
    }
}
