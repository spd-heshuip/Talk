package com.example.shuip.talk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.model.Profile;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.util.ImageUtil;
import com.example.shuip.util.android.PreferencesUtils;
import com.example.shuip.util.android.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-9-14.
 */
@ContentView(R.layout.activity_profile)
public class ProfileActivity extends BaseActivity{

    @InjectView(R.id.imageviewHead)
    private ImageView mHead;

    @InjectView(R.id.txtName)
    private TextView mTextName;

    @InjectView(R.id.txtEmail)
    private TextView mTextEmail;

    @InjectView(R.id.txtFrom)
    private TextView mTextFrom;

    @InjectView(R.id.txtAge)
    private TextView mTextAge;

    @InjectView(R.id.txtCompany)
    private TextView mTextCompany;

    @InjectView(R.id.btnMsgAt)
    private Button mTalk;

    @InjectView(R.id.btnRequest)
    private Button mRuequestAdd;

    @InjectExtra(Constant.FRIEND)
    private Contact mFriend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAcitonBarBasic();

        requestProfile();
        displayFriendInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取Profile
     */
    private void requestProfile(){


        Map<String,String> pramas = new HashMap<String, String>(2);
        pramas.put("memberid", mFriend.getContactid()+"");
        pramas.put("token", PreferencesUtils.getString(this, Constant.ACCESS_TOKEN));


        mHttpClient.post(Constant.API.URL_FRIEND_PROFILE, pramas, 0, new RequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestSuccess(BaseResponse response) {

                if (response.isSuccess())
                    disPlayProfile(response.getObj(Profile.class));
                else
                    ToastUtils.show(ProfileActivity.this, R.string.load_fail);
            }

            @Override
            public void onRequestError(int code, String msg) {
                ToastUtils.show(ProfileActivity.this, R.string.load_fail);
            }

            @Override
            public void onRequestFail(int code, String msg) {
                ToastUtils.show(ProfileActivity.this, R.string.load_fail);
            }
        });

    }

    public void msgAt(View view){
        Intent intent = new Intent(ProfileActivity.this,ChatActivity.class);
        intent.putExtra(Constant.FRIEND, mFriend);
        startActivity(intent);

    }

    private  void displayFriendInfo(){

        mTextName.setText(mFriend.getName());
        mTextEmail.setText("菜鸟通行证:"+mFriend.getEmail());
        ImageUtil.displayImageUseDefOption(mFriend.getHeadbig(), mHead);
    }


    private void disPlayProfile(Profile profile){


        if(TextUtils.isEmpty(profile.getProvince())&& TextUtils.isEmpty(profile.getCity())){
            mTextFrom.setText(R.string.planet);
        }else {
            String from = profile.getProvince()+"-"+profile.getCity();
            mTextFrom.setText(from);
        }

        mTextAge.setText(profile.getAge()+"");
        mTextCompany.setText(profile.getCompany());

    }
}
