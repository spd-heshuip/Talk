package com.example.shuip.talk.util;

import android.content.Context;

import com.example.shuip.talk.model.Member;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.util.android.PreferencesUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Administrator on 15-9-8.
 */
public class MemberUtil {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static void saveMember(Context context,Member member){
        PreferencesUtils.putLong(context, Constant.MEMBER_ID,member.getId());
        PreferencesUtils.putString(context, Constant.ACCESS_TOKEN, member.getToken());

        PreferencesUtils.putString(context,Constant.MEMBER,gson.toJson(member));
        TalkApplication.getInstance().setLoginMember(member);

    }
}
