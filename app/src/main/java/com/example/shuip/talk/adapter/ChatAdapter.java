package com.example.shuip.talk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public class ChatAdapter extends BaseAdapter{

    private static final String TAG = ChatAdapter.class.getSimpleName();
    private Context mContext;
    private Contact mFriend;
    private List<ChatMsg> mList;
    private PrettyDateFormat dateFormat = new PrettyDateFormat("@", "yyyy-MM-dd HH:mm:dd");
    private Member member;
    private LayoutInflater mInflater;

    private VolleyHttpClient mHttpClient;
    private ChatMsgService mChatMsgService = ChatMsgService.getInstance();

    public ChatAdapter(Context context, List<ChatMsg> list, Contact friend) {
        this.mContext = context;
        this.mFriend = friend;
        this.mList = list;
        mInflater = LayoutInflater.from(context);
        this.member = TalkApplication.getInstance().getLoginMember();
        mHttpClient = new VolleyHttpClient(context);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ChatMsg getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId() == null ?
                0 : getItem(position).getId();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(viewType == ChatMsg.SEND ? R.layout.template_chat_send :
                                R.layout.template_chat_receive,parent,false);
            viewHolder.mTxtTime = (TextView) convertView.findViewById(R.id.txtTime);
            viewHolder.mTxtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            viewHolder.mImgHead = (ImageView) convertView.findViewById(R.id.imageviewHead);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatMsg chatMsg = getItem(position);
        viewHolder.mTxtTime.setText(dateFormat.format(chatMsg.getChattime()));
        viewHolder.mTxtMsg.setText(chatMsg.getChatmsg());
        if (viewType == ChatMsg.SEND){
            ImageUtil.displayImageUseDefOption(member.getHeadsmall(),viewHolder.mImgHead);
        }else {
            ImageUtil.displayImageUseDefOption(mFriend.getHeadsmall(),viewHolder.mImgHead);
        }
        
        if (chatMsg.getIsreceive() == ChatMsg.SEND && chatMsg.getStatus() == ChatMsg.STATUS_NOSEND){
            sendMsg(chatMsg,position);
        }

        return convertView;
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

                mList.remove(position);
                mList.add(chatMsg);
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

    private static final class ViewHolder{
        TextView mTxtTime;
        TextView mTxtMsg;
        ImageView mImgHead;
    }

    public void addData(ChatMsg msg){
        if (msg != null){
            mList.add(msg);
            notifyDataSetChanged();
        }
    }
}
