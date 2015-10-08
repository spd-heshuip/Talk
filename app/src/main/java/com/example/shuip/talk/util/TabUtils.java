/*
*TabUtils.java
*Created on 2014-8-18 下午6:58 by Ivan
*Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
*http://www.cniao5.com
*/
package com.example.shuip.talk.util;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.example.shuip.talk.R;

/**
 * Created by Ivan on 14-8-18.
 * Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
 * http://www.cniao5.com
 */
public class TabUtils {


    public static View renderTabView(Context context, int titleResource, int badgeNumber) {
        FrameLayout view = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.tab_badge, null);

        // We need to manually set the LayoutParams here because we don't have a view root
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ((TextView) view.findViewById(R.id.tabText)).setText(titleResource);
        updateTabBadge((TextView) view.findViewById(R.id.tabBadge), badgeNumber);

        return view;
    }



    public static void updateTabBadge(ActionBar.Tab tab, int badgeNumber) {
        updateTabBadge((TextView) tab.getCustomView().findViewById(R.id.tabBadge), badgeNumber);
    }



    private static void updateTabBadge(TextView view, int badgeNumber) {
        if (badgeNumber > 0) {
            view.setVisibility(View.VISIBLE);
            view.setText(Integer.toString(badgeNumber));
        }
        else {
            view.setVisibility(View.GONE);
        }
    }
}
