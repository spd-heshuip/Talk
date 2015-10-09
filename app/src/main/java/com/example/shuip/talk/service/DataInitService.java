package com.example.shuip.talk.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.shuip.talk.db.dao.service.ContactService;
import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.http.VolleyHttpClient;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.talk.util.Pinyin4j;
import com.example.shuip.util.android.PreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 15-9-10.
 */
public class DataInitService extends IntentService{

    private static final String TAG = "DataInitService";
    private VolleyHttpClient mHttpClient = new VolleyHttpClient(this);
    private ContactService mContactService = ContactService.getInstance();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public DataInitService() {
        super("DataInitService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Long memberId = TalkApplication.getInstance().getLoginMember().getId();
        final Map<String,String> map = new HashMap<String,String>(2);

        map.put("memberid",memberId + "");
        map.put("token", PreferencesUtils.getString(DataInitService.this, Constant.ACCESS_TOKEN));

        mHttpClient.post(Constant.API.URL_FRIENDS, map, 0, new RequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestSuccess(BaseResponse response) {
                List<Contact> contacts = response.getList(Contact.class);
                if (contacts != null){
                    List<Contact> temp = new ArrayList<Contact>(contacts.size());
                    for (Contact contact : contacts){
                        String pinyin = Pinyin4j.getPingYin(contact.getName(),true);
                        contact.setPinyin(pinyin);
                        contact.setMemberid(memberId);
                        contact.setContactid(contact.getId());
                        contact.setId(null);

                        temp.add(contact);
                        mContactService.save(temp);
                    }

                    PreferencesUtils.putBoolean(DataInitService.this,Constant.IS_DATA_INIT,true);

                }


            }

            @Override
            public void onRequestError(int code, String msg) {
                Log.d(TAG,"Request Data fail!");
            }

            @Override
            public void onRequestFail(int code, String msg) {
                Log.d(TAG,"Request Data fail!");
            }
        });

    }
}
