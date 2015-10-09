/*
*AppInfo.java
*Created on 2014-10-15 下午3:55 by Ivan
*Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
*http://www.cniao5.com
*/
package com.example.shuip.talk.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by Ivan on 14-10-15.
 * Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
 * http://www.cniao5.com
 */
public class AppInfo {

     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity
    private String pkgName ;    //应用程序所对应的包名
    private int versionCode;
    private String versionName;

    public AppInfo(){}


    public String getPkgName(){
        return pkgName ;
    }
    public void setPkgName(String pkgName){
        this.pkgName=pkgName ;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
