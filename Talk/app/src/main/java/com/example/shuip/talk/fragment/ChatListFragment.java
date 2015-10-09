package com.example.shuip.talk.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.shuip.talk.ChatActivity;
import com.example.shuip.talk.R;
import com.example.shuip.talk.adapter.ChatListAdapter;
import com.example.shuip.talk.db.dao.service.ChatMsgService;
import com.example.shuip.talk.model.ChatMsg;
import com.example.shuip.talk.model.ChatMsgEx;
import com.example.shuip.talk.sys.Constant;

import java.io.Serializable;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-8-31.
 */
public class ChatListFragment extends RoboFragment implements AdapterView.OnItemClickListener{

    private static final String TAG = "ChatListFragment";

    @InjectView(R.id.chat_list_view)
    private ListView mListView;

    private List<ChatMsgEx> mChatMsgExList;
    private ChatListAdapter mAdapter;

    private ChatMsgService mChatMsgService;
    private ChatListener mChatListener;

    private ChatBroadCastReceiver mChatBroadCastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatMsgService = ChatMsgService.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChatMsgExList = mChatMsgService.findHistoryChatMsg(getActivity());
        mAdapter = new ChatListAdapter(getActivity(),mChatMsgExList);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        showUnreadMsgConut(mChatMsgExList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChatMsgEx chatMsgEx = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(),ChatActivity.class);

        intent.putExtra(Constant.FRIEND, chatMsgEx.getContact());
        startActivityForResult(intent, 10000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            loadMsg();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ChatListener){
            mChatListener = (ChatListener) activity;
        }else {
            throw new ClassCastException(activity.toString() + "implements ChatListener interface");
        }
    }

    private void loadMsg(){
        mChatMsgExList = mChatMsgService.findHistoryChatMsg(getActivity());

        mAdapter.clear();
        mAdapter.addData(mChatMsgExList);

        showUnreadMsgConut(mChatMsgExList);
    }

    private void showUnreadMsgConut(List<ChatMsgEx> msgs) {
        int count = 0;
        if (msgs != null && msgs.size() > 0){
            for (ChatMsgEx chatMsgEx : msgs){
                count += chatMsgEx.getUnreadCount();
                mChatListener.unReadMsgCount(count);

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mChatBroadCastReceiver = new ChatBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_MSG);
        getActivity().registerReceiver(mChatBroadCastReceiver,intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mChatBroadCastReceiver);
    }

    private final class ChatBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constant.ACTION_MSG){
                Serializable obj = intent.getSerializableExtra(Constant.EXTRA_MSG);
                if (obj instanceof ChatMsg){
                    loadMsg();
                }
            }
        }
    }

    public interface ChatListener{
        public void unReadMsgCount(int count);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"ondestyoy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"ondetach");
    }
}
