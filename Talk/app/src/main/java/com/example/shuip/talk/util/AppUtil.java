/*
*AppUtil.java
*Created on 2014-10-15 下午3:53 by Ivan
*Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
*http://www.cniao5.com
*/
package com.example.shuip.talk.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.shuip.talk.model.AppInfo;


/**
 * Created by Ivan on 14-10-15.
 * Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
 * http://www.cniao5.com
 */
public class AppUtil {


    public static AppInfo getAppInfo(Context context){

        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String packageName = info.packageName;  //包名
            int versionCode = info.versionCode;  //版本号
            String versionName = info.versionName;   //版本名


           AppInfo  appInfo = new AppInfo();

            appInfo.setPkgName(packageName);
            appInfo.setVersionCode(versionCode);
            appInfo.setVersionName(versionName);

            return  appInfo;

        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return  null;
    }
}
