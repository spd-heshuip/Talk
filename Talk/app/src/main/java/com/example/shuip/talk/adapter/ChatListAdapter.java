package com.example.shuip.talk.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuip.talk.R;
import com.example.shuip.talk.model.ChatMsgEx;
import com.example.shuip.talk.util.ImageUtil;
import com.example.shuip.talk.util.PrettyDateFormat;

import java.util.List;

/**
 * Created by Administrator on 15-9-21.
 */
public class ChatListAdapter extends AbstractAdapter<ChatMsgEx>{

    PrettyDateFormat dateFormat = new PrettyDateFormat("@", "yyyy-MM-dd HH:mm:dd");
    public ChatListAdapter(Context context, List<ChatMsgEx> datas) {
        super(context, datas);
    }

    @Override
    IViewHolder getViewHolder(int viewType) {
        return new ViewHolder();
    }

    @Override
    int getViewLayout(int viewType) {
        return R.layout.template_chat_list;
    }

    class ViewHolder implements IViewHolder<ChatMsgEx>{

        private TextView txtName;
        private TextView txtTime;
        private TextView txtMsg;
        private TextView txtBadge;
        private ImageView imageviewHead;
        @Override
        public void bindData(ChatMsgEx chatMsgEx, int position) {
            txtName.setText(chatMsgEx.getContact().getName());
            txtMsg.setText(chatMsgEx.getChatmsg());
            txtTime.setText(dateFormat.format(chatMsgEx.getChattime()));

            if (chatMsgEx.getUnreadCount() <= 0){
                txtBadge.setVisibility(View.GONE);
            }else {
                txtBadge.setVisibility(View.VISIBLE);
                if (chatMsgEx.getUnreadCount() >= 99){
                    txtBadge.setText("99+");
                }else {
                    txtBadge.setText(chatMsgEx.getUnreadCount() + "");
                }
            }

            ImageUtil.displayImageUseDefOption(chatMsgEx.getContact().getHeadbig(),imageviewHead);
        }
    }
}
