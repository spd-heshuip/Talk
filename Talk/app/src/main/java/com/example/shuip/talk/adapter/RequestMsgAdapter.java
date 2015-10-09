package com.example.shuip.talk.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuip.talk.R;
import com.example.shuip.talk.db.dao.service.ContactService;
import com.example.shuip.talk.db.dao.service.RequestMsgService;
import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.http.VolleyHttpClient;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.model.RequestAnswerMsg;
import com.example.shuip.talk.model.RequestMsg;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.util.ImageUtil;
import com.example.shuip.talk.util.Pinyin4j;
import com.example.shuip.util.android.PreferencesUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 15-9-22.
 */
public class RequestMsgAdapter extends AbstractAdapter<RequestMsg> {

    private VolleyHttpClient mHttpClient;
    private ContactService mContactService;
    private RequestMsgService mRequestMsgService;

    public RequestMsgAdapter(Context context, List<RequestMsg> datas) {
        super(context, datas);
        this.mHttpClient = new VolleyHttpClient(context);
        this.mContactService = ContactService.getInstance();
        this.mRequestMsgService = RequestMsgService.getInstance();
    }

    @Override
    IViewHolder<RequestMsg> getViewHolder(int viewType) {
        return new ViewHolder();
    }

    @Override
    int getViewLayout(int viewType) {
        return R.layout.template_request_msg;
    }

    private final class ViewHolder implements IViewHolder<RequestMsg>{
        private TextView txtName;
        private TextView txtHandle;
        private TextView txtMsg;
        private Button btnHandle;
        private ImageView imageviewHead;
        @Override
        public void bindData(final RequestMsg msg, final int position) {
            txtName.setText(msg.getContactname());
            String requestMsg = TextUtils.isEmpty(msg.getRequestmsg()) ? mContext.getString(R.string.friend_ask_you_be) : msg.getRequestmsg();
            txtMsg.setText(requestMsg);

            if (msg.getStatus() == RequestMsg.STATUS_UN_HANDLE){
                btnHandle.setVisibility(View.VISIBLE);
                txtHandle.setVisibility(View.GONE);

                btnHandle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptRequest(msg, position);
                    }
                });
            }else {
                btnHandle.setVisibility(View.GONE);
                txtHandle.setVisibility(View.VISIBLE);
                if (msg.getStatus() == RequestMsg.STATUS_AGREE){
                    txtHandle.setText(R.string.accepted);
                }else if (msg.getStatus() == RequestMsg.STATUS_REFUSE){
                    txtHandle.setText(R.string.refused);
                }
            }

            ImageUtil.displayImageUseDefOption(msg.getContactheadmid(),imageviewHead);
        }

        private void acceptRequest(final RequestMsg msg, final int position) {
            Map<String,String> params = new HashMap<String,String>();
            params.put("requestid",msg.getRequestid() + "");
            params.put("answer", RequestAnswerMsg.TYPE_AGREE + "");
            params.put("token", PreferencesUtils.getString(mContext, Constant.ACCESS_TOKEN));

            mHttpClient.post(Constant.API.URL_FRIENDS_REQUEST_ANSWER, params, R.string.sending, new RequestListener() {
                @Override
                public void onPreRequest() {

                }

                @Override
                public void onRequestSuccess(BaseResponse response) {
                    if (response.isSuccess()) {
                        msg.setStatus(RequestMsg.STATUS_AGREE);
                        mRequestMsgService.update(msg);

                        saveFriend(msg);

                        mDatas.remove(msg);
                        mDatas.add(position,msg);
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onRequestError(int code, String msg) {

                }

                @Override
                public void onRequestFail(int code, String msg) {

                }
            });
        }

        private void saveFriend(RequestMsg msg) {
            Contact friend = new Contact();
            friend.setMemberid(msg.getMemberid());
            friend.setContactid(msg.getContactid());

            friend.setPinyin(Pinyin4j.getPingYin(msg.getContactname(), true).toUpperCase());
            friend.setCreatetime(new Date());
            friend.setEmail(msg.getContactemail());
            friend.setName(msg.getContactname());
            friend.setHeadbig(msg.getContactheadbig());
            friend.setHeadmid(msg.getContactheadmid());
            friend.setHeadsmall(msg.getContactheadsmall());

            mContactService.save(friend);
        }
    }
}
