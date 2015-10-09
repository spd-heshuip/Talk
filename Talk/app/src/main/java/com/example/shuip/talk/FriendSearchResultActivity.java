package com.example.shuip.talk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.shuip.talk.adapter.FriendAdapter;
import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.util.android.PreferencesUtils;
import com.example.shuip.util.android.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-9-21.
 */
@ContentView(R.layout.activity_friend_search_result)
public class FriendSearchResultActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    @InjectView(R.id.listView_friend_search)
    private ListView mListView;

    private List<Contact> mContactList;
    private FriendAdapter mFriendAdapter;

    @InjectExtra(Constant.WORD)
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAcitonBarBasic();

        mListView.setOnItemClickListener(this);
        queryFriends();
    }

    private void queryFriends() {
        Map<String,String> params = new HashMap<String,String>();
        params.put("word",word);
        params.put("token", PreferencesUtils.getString(this,Constant.ACCESS_TOKEN));

        mHttpClient.post(Constant.API.URL_FRIENDS_QUERY, params, R.string.loading, new RequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestSuccess(BaseResponse response) {
                if (response.isSuccess()){
                    mContactList = response.getList(Contact.class);
                    if (mContactList != null){
                        mFriendAdapter = new FriendAdapter(FriendSearchResultActivity.this,mContactList);
                        mListView.setAdapter(mFriendAdapter);
                    }else {
                        ToastUtils.show(FriendSearchResultActivity.this,R.string.empty_result);
                        finish();
                    }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact friend = mFriendAdapter.getItem(position);
        Intent intent = new Intent(FriendSearchResultActivity.this, ProfileActivity.class);
        intent.putExtra(Constant.FRIEND,friend);
        intent.putExtra("from",1);

        startActivity(intent);
    }
}
