package com.example.shuip.talk;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.shuip.talk.adapter.ChatAdapter;
import com.example.shuip.talk.db.dao.service.ChatMsgService;
import com.example.shuip.talk.model.ChatMsg;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.util.android.ToastUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAcitonBarBasic();

        mList = initChatMsgList();
        mAdapter = new ChatAdapter(this,mList,mFriend);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getCount() -1 );
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

}
