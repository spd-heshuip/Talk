package com.example.shuip.talk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.shuip.talk.adapter.ChatAdapter;
import com.example.shuip.talk.db.dao.service.ChatMsgService;
import com.example.shuip.talk.model.ChatMsg;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.util.MemberUtil;
import com.example.shuip.util.android.ToastUtils;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-9-15.
 */
@ContentView(R.layout.activity_chat)
public class ChatActivity extends BaseActivity{

    @InjectView(R.id.listView)
    private ListView mListView;

    @InjectView(R.id.etxtMsg)
    private EditText mEditText;

    @InjectView(R.id.btnSend)
    private Button mSendButton;

    @InjectExtra(Constant.FRIEND)
    private Contact mFriend;

    private ChatAdapter mAdapter;
    private List<ChatMsg> mList;
    private ChatMsgService mChatMsgService = ChatMsgService.getInstance();
    private ChatBroadCastReceiverAsyncTask mChatBroadCastReceiverAsyncTask;
    private ChatBroadCastReceiver mChatBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAcitonBarBasic();
        MemberUtil.initMember(this);

        mChatBroadCastReceiverAsyncTask =  new ChatBroadCastReceiverAsyncTask(ChatActivity.this);
        mList = initChatMsgList();
        mAdapter = new ChatAdapter(this,mList,mFriend);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getCount() -1 );

        mChatMsgService.updateUnreadChatMsg(this,mFriend.getContactid());
    }

    public void sendMsg(View view){
        createMsg();
    }

    private void createMsg() {
        String msg = mEditText.getText().toString();
        if (TextUtils.isEmpty(msg)){
            ToastUtils.show(this,"请输入消息");
            return;
        }
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setChatmsg(msg);
        chatMsg.setIsreceive(ChatMsg.SEND);
        chatMsg.setChattime(new Date());
        chatMsg.setStatus(ChatMsg.STATUS_NOSEND);
        chatMsg.setContactid(mFriend.getContactid());

        mAdapter.addData(chatMsg);
        mEditText.setText("");
        mListView.setSelection(mAdapter.getCount() - 1);
    }

    private List<ChatMsg> initChatMsgList(){
        return mChatMsgService.findChatMsgByContactId(mFriend.getContactid(),0,1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mChatBroadCastReceiver = new ChatBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_MSG);

        registerReceiver(mChatBroadCastReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mChatBroadCastReceiverAsyncTask.isCancelled()){
            mChatBroadCastReceiverAsyncTask.cancel(true);
        }
        unregisterReceiver(mChatBroadCastReceiver);
    }

    private final class ChatBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constant.ACTION_MSG){
                    Serializable obj = intent.getSerializableExtra(Constant.EXTRA_MSG);
                    if (obj instanceof ChatMsg){
                        mChatBroadCastReceiverAsyncTask.execute((ChatMsg)obj,null,null);
                    }
            }
        }
    }

    private  static final class ChatBroadCastReceiverAsyncTask extends AsyncTask<ChatMsg,Void,Void>{

        private WeakReference<ChatActivity> mActivity;
        public ChatBroadCastReceiverAsyncTask(ChatActivity activity) {
            mActivity = new WeakReference<ChatActivity>(activity);
        }
        @Override
        protected Void doInBackground(ChatMsg... params) {
            if (params != null ){
                ChatActivity activity = mActivity.get();
                if (activity != null){
                    if (params[0].getContactid() == activity.mFriend.getContactid()){
                        activity.mAdapter.addData(params[0]);
                        activity.mChatMsgService.updateUnreadChatMsg(activity,activity.mFriend.getContactid());
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void integer) {
            super.onPostExecute(integer);
            ChatActivity activity = mActivity.get();
            if (activity != null){
                activity.mListView.setSelection(activity.mAdapter.getCount() - 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateUpTo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            navigateUpTo();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateUpTo(){

//        //获取到跳转至父Activity的Intent
//        Intent upIntent = NavUtils.getParentActivityIntent(this);
//        //父Activity和当前Activity是在同一个Task中的
//        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//            // 如果不是在同一个Task中的，则需要借助TaskStackBuilder来创建一个新的Task
//            TaskStackBuilder.create(this)
//                    .addNextIntentWithParentStack(upIntent)
//                    .startActivities();
//        } else {
//            //如果在同一TASK中 直接调用navigateUpTo()方法进行跳转
//            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//            NavUtils.navigateUpTo(this, upIntent);
//        }
        Intent intent = new Intent(this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
