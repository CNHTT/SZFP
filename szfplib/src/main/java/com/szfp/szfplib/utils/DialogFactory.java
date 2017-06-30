package com.szfp.szfplib.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * 作者：ct on 2017/6/26 10:30
 * 邮箱：cnhttt@163.com
 */

public class DialogFactory {
    public static Dialog createDialog(Context context, int layout, int theme) {
        Dialog dialog = new Dialog(context,theme);
        dialog.setContentView(layout);
        return  dialog;
    }

    public static ProgressDialog createWaitProgressDialog(Context context, int message){
        ProgressDialog progress = new ProgressDialog(context);
//		progress.setIcon(R.drawable.write_success);
        progress.setMessage(context.getResources().getString(message));
        return progress;
    }
}
