package com.example.shuip.talk.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.shuip.talk.LoginActivity;
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

    public static void initMember(Context context){
        Member member = TalkApplication.getInstance().getLoginMember();
        if (member == null){
            String memberJson = PreferencesUtils.getString(context,Constant.MEMBER);
            if (!TextUtils.isEmpty(memberJson)){
                member = JSONUtil.fromJson(memberJson,Member.class);
                TalkApplication.getInstance().setLoginMember(member);
            }else {
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        }
    }

    public static  Member getMember(Context context){
        String memberJson= PreferencesUtils.getString(context, Constant.MEMBER);
        if(!TextUtils.isEmpty(memberJson)) {
            return  gson.fromJson(memberJson,Member.class);
        }
        return null;
    }

    public static void clearMember(Context context){
        PreferencesUtils.putLong(context,Constant.MEMBER_ID,-1);
        PreferencesUtils.putString(context, Constant.ACCESS_TOKEN, null);

        PreferencesUtils.putString(context,Constant.MEMBER,null);
        TalkApplication.getInstance().setLoginMember(null);

    }
}
