package com.example.shuip.talk.message;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.example.shuip.talk.ChatActivity;
import com.example.shuip.talk.HomeActivity;
import com.example.shuip.talk.LoginActivity;
import com.example.shuip.talk.R;
import com.example.shuip.talk.db.dao.service.ChatMsgService;
import com.example.shuip.talk.db.dao.service.ContactService;
import com.example.shuip.talk.db.dao.service.RequestMsgService;
import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.http.VolleyHttpClient;
import com.example.shuip.talk.model.ChatMsg;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.model.Member;
import com.example.shuip.talk.model.RequestAnswerMsg;
import com.example.shuip.talk.model.RequestMsg;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.talk.util.AppUtil;
import com.example.shuip.talk.util.JSONUtil;
import com.example.shuip.talk.util.MemberUtil;
import com.example.shuip.talk.util.Pinyin4j;
import com.example.shuip.util.android.PreferencesUtils;
import com.example.shuip.util.android.ToastUtils;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 15-9-8.
 */
public class MessageHandler {

    private VolleyHttpClient mHttpClient;
    private Context mContext;
    private ChatMsgService mChatMsgService;
    private ContactService mContactService;
    private RequestMsgService mRequestService;
    private NotificationManager mNotificationManager;

    public MessageHandler(Context context) {
        this.mContext = context;
        mHttpClient = new VolleyHttpClient(context);
        this.mChatMsgService = ChatMsgService.getInstance();
        this.mContactService = ContactService.getInstance();
        this.mRequestService = RequestMsgService.getInstance();
    }

    public  void bindSuccess(Context context,String userID, String chanelId){
        PreferencesUtils.putString(context, Constant.BD_USERID, userID);
        PreferencesUtils.putString(context, Constant.BD_CHANNELID, chanelId);

        String memberJson = PreferencesUtils.getString(context,Constant.MEMBER,null);
        String token = PreferencesUtils.getString(context,Constant.ACCESS_TOKEN,null);

        if (!TextUtils.isEmpty(memberJson) && !TextUtils.isEmpty(token)){
            Member member = JSONUtil.fromJson(memberJson, Member.class);
            if (member != null){
                login(context,userID,chanelId,member.getEmail(),token);
            }
        }
    }

    private  void login(final Context context, String userID, String chanelId, String email, String token) {


        Map<String,String>  map = new HashMap<String,String>(4);
        map.put("email",email);
        map.put("userId", userID);
        map.put("channelId", chanelId);
        map.put("token", token);

        mHttpClient.post(Constant.API.URL_LOGIN_TOKEN, map, 0, new RequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestSuccess(BaseResponse response) {
                Member member = response.getObj(Member.class);

                MemberUtil.saveMember(context, member);
                TalkApplication.getInstance().setLoginMember(member);
            }

            @Override
            public void onRequestError(int code, String msg) {
                authEroor(context);
            }

            @Override
            public void onRequestFail(int code, String msg) {
                authEroor(context);
            }
        });
    }

    private  void authEroor(Context context){
        ToastUtils.show(context, "认证过期，请重新认证");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void handleMsg(Context context,String msg){
        int msgType = -1;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            msgType = jsonObject.getInt("msgType");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (msgType){
            case Message.MSG_TYPE_CHAT:
                handleChatMsg(context, msg);
                break;
            case Message.MSG_TYPE_ADD_FRIEND_REQUEST:
                handleAddFriendRequestMsg(context, msg);
                break;
            case Message.MSG_TYPE_ADD_FRIEND_ANSWER:
                handleAnswerRequestMsg(context,msg);
                break;
            default:
                break;

        }
    }

    private void handleAnswerRequestMsg(Context context, String msg) {
        Message<RequestAnswerMsg> message = JSONUtil.fromJson(msg,new TypeToken<Message<RequestAnswerMsg>>(){}.getType());

        RequestAnswerMsg requestAnswerMsg = message.getData();

        if (requestAnswerMsg.getType() == RequestAnswerMsg.TYPE_AGREE){
            long memberId =  PreferencesUtils.getLong(context,Constant.MEMBER_ID);
            Contact contact = new Contact();
            contact.setMemberid(memberId);
            contact.setContactid(requestAnswerMsg.getContactid());
            contact.setPinyin(Pinyin4j.getPingYin(requestAnswerMsg.getContactname(), true).toLowerCase());
            contact.setCreatetime(new Date());
            contact.setEmail(requestAnswerMsg.getContactemail());
            contact.setName(requestAnswerMsg.getContactname());
            contact.setHeadbig(requestAnswerMsg.getContactheadbig());
            contact.setHeadmid(requestAnswerMsg.getContactheadmid());
            contact.setHeadsmall(requestAnswerMsg.getContactheadsmall());

            mContactService.save(contact);

            context.sendBroadcast(new Intent(Constant.ACTION_CONTACT_UPDATE));
        }else {
            //refuse
        }
    }

    private void handleAddFriendRequestMsg(Context context, String msg) {
        Message<RequestMsg> message = JSONUtil.fromJson(msg,new TypeToken<Message<RequestMsg>>(){}.getType());
        RequestMsg requestMsg = message.getData();

        if (requestMsg != null){
            long loginMemberId= PreferencesUtils.getLong(context,Constant.MEMBER_ID);

            List<RequestMsg> msgs = mRequestService.findFriendRequestMsg(loginMemberId,requestMsg.getContactid());
            if (msgs != null && msgs.size() > 0){
                return;
            }

            requestMsg.setMemberid(loginMemberId);
            requestMsg.setStatus(RequestMsg.STATUS_UN_HANDLE);

            mRequestService.save(requestMsg);


        }


    }

    private void handleChatMsg(Context context, String msg) {
        int msgType = -1;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            msgType = jsonObject.getJSONObject("data").getInt("msgType");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (msgType){
            case ChatBaseMessage.MSG_TYPE_TEXT:
                handleChatTextMsg(context, msg);
                break;
            default:
                break;
        }
    }

    private void handleChatTextMsg(Context context, String msg) {
        Message<ChatTextMessage> message = JSONUtil.fromJson(msg,new TypeToken<Message<ChatTextMessage>>(){}.getType());
        ChatTextMessage chatTextMessage = message.getData();

        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setChattime(new Date(chatTextMessage.getChatTime()));
        chatMsg.setChatmsg(chatTextMessage.getMsg());
        chatMsg.setStatus(ChatMsg.STATUS_UNREAD);
        chatMsg.setIsreceive(ChatMsg.RECEVIER);
        chatMsg.setChattype(ChatMsg.MSG_TYPE_TEXT);
        chatMsg.setContactid(chatTextMessage.getFromMemberId());
        chatMsg.setMemberid(chatTextMessage.getToMemberId());

        mChatMsgService.save(chatMsg);
        
        notifyUser(context,chatMsg);
    }

    private void notifyUser(Context context,ChatMsg chatMsg) {
        if (!isTopActivity(context)){
            notification(context,chatMsg);
        }
        sendBroadcast(context,chatMsg);
    }

    private void notification(Context context, ChatMsg chatMsg) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        List<ChatMsg> msgs = mChatMsgService.findUnreadMsg(context);
        Intent intent = null;

        if (msgs != null && msgs.size() > 0){
            builder.setTicker("收到" + (msgs.size() > 99 ? "99+" : msgs.size() + "条新消息"));
            builder.setContentInfo(msgs.size() + "条");
            builder.setContentTitle("有" + (msgs.size() > 99 ? "99+" : msgs.size()) + "条消息");
            builder.setContentText("点击查看");

            intent = new Intent(context, HomeActivity.class);
        }else {
            Contact friend = mContactService.getContact(context,chatMsg.getContactid());
            builder.setTicker("收到新消息");
            builder.setContentInfo("1条");
            builder.setContentTitle(friend.getName());
            builder.setContentText(chatMsg.getChatmsg());

            intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Constant.FRIEND,friend);
        }

        builder.setSmallIcon(R.drawable.ic_launcher); //设置小图标
        builder.setWhen(System.currentTimeMillis());  //设置时间
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        mNotificationManager.notify(0,notification);
    }

    private void sendBroadcast(Context context,ChatMsg chatMsg){
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_MSG);
        intent.putExtra(Constant.EXTRA_MSG, chatMsg);
        intent.setPackage(AppUtil.getAppInfo(context).getPkgName());//指定接收广播的抱名

        context.sendBroadcast(intent);
    }

    private boolean isTopActivity(Context context){
        String homeActivityName = "com.example.shuip.talk.HomeActivity";
        String chatActivityNmae = "com.example.shuip.talk.ChatActivity";
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        if (taskInfo.size() > 0){
            if (homeActivityName.equals(taskInfo.get(0).topActivity.getClassName()) || chatActivityNmae.equals(taskInfo.get(0).topActivity.getClassName())){
                return true;
            }
        }
        return false;
    }
}
