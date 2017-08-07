package com.szfp.home;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;


/**
 * author：ct on 2017/7/31 12:00
 * email：cnhttt@163.com
 */

public class ApkTools {
    public static List<AppInfo>  list = null;

    public static List<AppInfo> getApkList(PackageManager packageManager){
        List<AppInfo> appInfos = new ArrayList<>();


        List<PackageInfo> packageInfos = packageManager .getInstalledPackages(0);
        for (PackageInfo page:packageInfos) {
//            过滤掉系统app
            if((ApplicationInfo.FLAG_SYSTEM& page.applicationInfo.flags) !=0) {
                continue;
            }
            if(page.applicationInfo.loadIcon(packageManager) ==null) {
                continue;
            }

            if (page.packageName.equals("com.szfp.home")){
                continue;
            }
            if (page.packageName.equals("com.estrongs.android.pop.pro")){
                continue;
            }
            AppInfo  apk = new AppInfo();
            apk.setAppName(page.applicationInfo.loadLabel(packageManager).toString());
            apk.setImage(page.applicationInfo.loadIcon(packageManager));
            apk.setPageName(page.packageName);
            appInfos.add(apk);

        }

        return appInfos;
    }
}
