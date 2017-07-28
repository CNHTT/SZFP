package com.szfp.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * author：ct on 2017/7/28 17:11
 * email：cnhttt@163.com
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
