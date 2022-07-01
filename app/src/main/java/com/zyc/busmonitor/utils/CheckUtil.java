package com.zyc.busmonitor.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


public class CheckUtil {

    public static boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName== null || pkgName.isEmpty()) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            // e.printStackTrace();
            // TODO 异常抛出 通过日志打印， 减少栈堆使用 提高性能
            Log.e("NameNotFoundException", String.format("未获取到包名: %s", pkgName), e);
        }
        // 返回包信息 not null 安装，null 未安装
        return packageInfo != null;
    }
}
