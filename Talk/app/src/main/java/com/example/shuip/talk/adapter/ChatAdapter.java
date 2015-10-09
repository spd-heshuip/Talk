package com.example.shuip.talk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuip.talk.R;
import com.example.shuip.talk.db.dao.service.ChatMsgService;
import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.http.VolleyHttpClient;
import com.example.shuip.talk.model.ChatMsg;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.model.Member;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.talk.util.ImageUtil;
import com.example.shuip.talk.util.PrettyDateFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 15-9-15.
 */
public class ChatAdapter extends AbstractAdapter<ChatMsg>{

    private Contact mFriend;
    private PrettyDateFormat dateFormat = new PrettyDateFormat("@", "yyyy-MM-dd HH:mm:dd");
    private Member member;
    private LayoutInflater mInflater;

    private VolleyHttpClient mHttpClient;
    private ChatMsgService mChatMsgService = ChatMsgService.getInstance();

    public ChatAdapter(Context context, List<ChatMsg> list, Contact friend) {
        super(context,list);
        this.mFriend = friend;
        this.member = TalkApplication.getInstance().getLoginMember();
        mHttpClient = new VolleyHttpClient(context);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getIsreceive();
    }


    @Override
    IViewHolder getViewHolder(int viewType) {
        if (viewType == ChatMsg.SEND){
            return new ViewHolderSend();
        }else
            return new ViewHolderReceive();
    }

    @Override
    int getViewLayout(int viewType) {
        if (viewType == ChatMsg.SEND)
            return R.layout.template_chat_send;
        else
            return R.layout.template_chat_receive;
    }

    private void sendMsg(final ChatMsg chatMsg, final int position) {
        Map<String,String> params = new HashMap<String,String>(4);

        params.put("memberId",member.getId() + "");
        params.put("msg", chatMsg.getChatmsg());
        params.put("friendId", mFriend.getContactid() + "");
        params.put("token",member.getToken());

        mHttpClient.post(Constant.API.URL_CHART_SEND, params, 0, new RequestListener() {
            @Override
            public void onPreRequest() {
                chatMsg.setMemberid(member.getId());
                long id = mChatMsgService.save(chatMsg);
                chatMsg.setId(id);
            }

            @Override
            public void onRequestSuccess(BaseResponse response) {
                if (response.isSuccess()){
                    chatMsg.setStatus(ChatMsg.STATUS_SEND_SUCCESS);
                    Log.d(TAG,"Send Message: " + chatMsg.getChatmsg() + " Success!");
                }else {
                    chatMsg.setStatus(ChatMsg.STATUS_SEND_FAIL);
                }

                mChatMsgService.update(chatMsg);

                mDatas.remove(position);
                mDatas.add(chatMsg);
            }

            @Override
            public void onRequestError(int code, String msg) {
                chatMsg.setStatus(ChatMsg.STATUS_SEND_FAIL);
                mChatMsgService.update(chatMsg);
            }

            @Override
            public void onRequestFail(int code, String msg) {
                chatMsg.setStatus(ChatMsg.STATUS_SEND_FAIL);
                mChatMsgService.update(chatMsg);
            }
        });
    }

    private  final class ViewHolderSend implements IViewHolder<ChatMsg>{
        TextView txtTime;
        TextView txtMsg;
        ImageView imageviewHead;

        @Override
        public void bindData(ChatMsg chatMsg, int position) {
            txtTime.setText(dateFormat.format(chatMsg.getChattime()));
            txtMsg.setText(chatMsg.getChatmsg());
            ImageUtil.displayImageUseDefOption(member.getHeadsmall(), imageviewHead);
            if (chatMsg.getIsreceive() == ChatMsg.SEND && chatMsg.getStatus() == ChatMsg.STATUS_NOSEND){
                sendMsg(chatMsg,position);
            }
        }
    }

    private final class ViewHolderReceive implements IViewHolder<ChatMsg>{
        TextView txtTimeRecive;
        TextView txtMsgReceive;
        ImageView imageviewHeadReceice;

        @Override
        public void bindData(ChatMsg chatMsg, int position) {
            txtTimeRecive.setText(dateFormat.format(chatMsg.getChattime()));
            txtMsgReceive.setText(chatMsg.getChatmsg());
            ImageUtil.displayImageUseDefOption(mFriend.getHeadsmall(),imageviewHeadReceice);
        }
    }

    public void addData(ChatMsg msg){
        if (msg != null){
            mDatas.add(msg);
            notifyDataSetChanged();
        }
    }
}
