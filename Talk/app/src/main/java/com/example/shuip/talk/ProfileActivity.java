package com.example.shuip.talk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuip.talk.db.dao.service.ContactService;
import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.model.Member;
import com.example.shuip.talk.model.Profile;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.talk.util.ImageUtil;
import com.example.shuip.talk.widget.CustomDialog;
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
public class ProfileActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = ProfileActivity.class.getSimpleName();
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
    private Button mRequestAdd;

    @InjectExtra(Constant.FRIEND)
    private Contact mFriend;

    @InjectExtra("from")
    private int from;

    private ContactService mContactService = ContactService.getInstance();

    private CustomDialog mCustomDialog;

    private EditText mEditMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAcitonBarBasic();
        requestProfile();
        displayFriendInfo();

        if (from == 1){
            long memberId = PreferencesUtils.getLong(this,Constant.MEMBER_ID);
            if (mFriend.getContactid() == memberId){
                mTalk.setVisibility(View.GONE);
                mRequestAdd.setVisibility(View.GONE);
            }
            Contact tempFriend = mContactService.getContact(this,mFriend.getContactid());
            if (tempFriend == null){
                mTalk.setVisibility(View.GONE);
                mRequestAdd.setVisibility(View.VISIBLE);
            }else {
                mTalk.setVisibility(View.VISIBLE);
                mRequestAdd.setVisibility(View.GONE);
            }
        }
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
        mTextEmail.setText("菜鸟通行证:" + mFriend.getEmail());
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel){
            if (mCustomDialog != null){
                mCustomDialog.dismiss();
            }
        }else if (v.getId() == R.id.btnYes){
            sendRequest();
        }
    }

    private void sendRequest() {
        String msg = mEditMsg.getText().toString();

        Member member = TalkApplication.getInstance().getLoginMember();
        long memberId = PreferencesUtils.getLong(this, Constant.MEMBER_ID);
        Map<String,String> params = new HashMap<String,String>();

//        Log.d(TAG, "memberid" + memberId + "");
//        Log.d(TAG, "friendid" + mFriend.getContactid() + "");
//        Log.d(TAG, "msg" + msg);
//        Log.d(TAG, "token" + PreferencesUtils.getString(this, Constant.ACCESS_TOKEN));
        params.put("memberid", member.getId() + "");
        params.put("friendid",mFriend.getContactid() + "");
        params.put("msg",msg);
        params.put("token", PreferencesUtils.getString(this, Constant.ACCESS_TOKEN));

        mHttpClient.post(Constant.API.URL_FRIENDS_REQUEST, params, R.string.sending, new RequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestSuccess(BaseResponse response) {
                if (response.isSuccess()){
                    ToastUtils.show(ProfileActivity.this,R.string.send_success);
                    mCustomDialog.dismiss();
                }else {
                    ToastUtils.show(ProfileActivity.this,R.string.send_fail);
                }
            }

            @Override
            public void onRequestError(int code, String msg) {
                ToastUtils.show(ProfileActivity.this, R.string.send_fail);
            }

            @Override
            public void onRequestFail(int code, String msg) {
                ToastUtils.show(ProfileActivity.this, R.string.send_fail);
            }
        });

    }

    public void requestClick(View view){
        buildDialog();
        mCustomDialog.show();
    }

    private void buildDialog(){
        if (mCustomDialog == null){
            View view = getLayoutInflater().inflate(R.layout.dialog_friend_request,null);

            Button btnCancel = (Button)view.findViewById(R.id.btnCancel);
            Button btnRequest = (Button) view.findViewById(R.id.btnYes);
            mEditMsg = (EditText) view.findViewById(R.id.etxtMsg);

            btnCancel.setOnClickListener(this);
            btnRequest.setOnClickListener(this);
            mCustomDialog = new CustomDialog(this,view);
        }
    }
}
