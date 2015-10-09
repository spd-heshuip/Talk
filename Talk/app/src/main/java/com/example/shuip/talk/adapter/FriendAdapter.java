package com.example.shuip.talk.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuip.talk.R;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.util.ImageUtil;

import java.util.List;

/**
 * Created by Administrator on 15-9-21.
 */
public class FriendAdapter extends AbstractAdapter<Contact>{

    public FriendAdapter(Context context, List<Contact> datas) {
        super(context, datas);
    }

    @Override
    public IViewHolder getViewHolder(int viewType) {
        return new ViewHolder();
    }

    @Override
    public int getViewLayout(int viewType) {
        return R.layout.template_friend;
    }

    private final class ViewHolder implements IViewHolder<Contact>{

        private ImageView imageviewHead;
        private TextView txtName;
        @Override
        public void bindData(Contact contact, int position) {
            txtName.setText(contact.getName());
            ImageUtil.displayImageUseDefOption(contact.getHeadmid(),imageviewHead);
        }
    }
}
