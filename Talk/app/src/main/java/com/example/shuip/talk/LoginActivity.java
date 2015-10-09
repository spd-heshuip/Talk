package com.example.shuip.talk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.shuip.talk.http.BaseResponse;
import com.example.shuip.talk.http.RequestListener;
import com.example.shuip.talk.model.Member;
import com.example.shuip.talk.service.DataInitService;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.util.MemberUtil;
import com.example.shuip.talk.widget.ClearEditText;
import com.example.shuip.util.android.PreferencesUtils;
import com.example.shuip.util.android.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-9-7.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity{

    @InjectView(R.id.etxtEmail)
    private ClearEditText mEtxtEmail;

    @InjectView(R.id.etxtPwd)
    private ClearEditText mExtPwd;

    @InjectView(R.id.btnLogin)
    private Button mBtnLogin;

    private MyHandler mHandler;
    private static final String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(LoginActivity.this);

        mBtnLogin.setEnabled(false);

        mBtnLogin.setText(R.string.connectioning);

        mHandler.post(new ConnectCheckRunable(LoginActivity.this));

    }

    public void login(View view){
        String eMail = this.mEtxtEmail.getText().toString();
        String pwd = this.mExtPwd.getText().toString();

        if(TextUtils.isEmpty(eMail)){
            mEtxtEmail.setShakeAnimation();
            return;
        }

        if (TextUtils.isEmpty(pwd)){
            mExtPwd.setShakeAnimation();
            return;
        }

        doLogin(eMail, pwd);
    }

    private void doLogin(String email,String pwd) {
        Map<String,String> map = new HashMap<String,String>(4);
        map.put("email",email);
        map.put("pwd",pwd);
        map.put("userId", PreferencesUtils.getString(this, Constant.BD_USERID, ""));
        map.put("channelid",PreferencesUtils.getString(this,Constant.BD_CHANNELID,""));

        mHttpClient.post(Constant.API.URL_LOGIN, map, R.string.logining, new RequestListener() {
            @Override
            public void onPreRequest() {

            }

            @Override
            public void onRequestSuccess(BaseResponse response) {
                Member member = response.getObj(Member.class);
                saveMember(member);
                jump(member);
            }

            @Override
            public void onRequestError(int code, String msg) {

            }

            @Override
            public void onRequestFail(int code, String msg) {
                ToastUtils.show(LoginActivity.this, msg);
            }
        });

    }

  /*  @Override
    public void onClick(View v) {
        login(v);
    }*/

    private static final class ConnectCheckRunable implements Runnable{
        private WeakReference<LoginActivity> mActivity;
        public ConnectCheckRunable(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void run() {
            LoginActivity loginActivity = mActivity.get();
            if(loginActivity != null){
                String channelId = PreferencesUtils.getString(loginActivity, Constant.BD_CHANNELID, null);
                if (!TextUtils.isEmpty(channelId)){
                    loginActivity.mHandler.sendEmptyMessage(1);
                }else{
                    loginActivity.mHandler.postDelayed(new ConnectCheckRunable(loginActivity),3000);
                }
            }

        }
    }

    private static final class MyHandler extends Handler{
        private WeakReference<LoginActivity> mActivity;

        public MyHandler(LoginActivity loginActivity){
            mActivity = new WeakReference<LoginActivity>(loginActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity loginActivity = mActivity.get();
            if (loginActivity != null){
                if(msg.what == 1){
                    loginActivity.mBtnLogin.setEnabled(true);
                    loginActivity.mBtnLogin.setText(R.string.login);
                }
            }
        }
    }

    private void jump(Member member){
        StartInitDataService(member);
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        this.finish();
    }

    private void StartInitDataService(Member member) {
        boolean isDataInit = PreferencesUtils.getBoolean(LoginActivity.this,Constant.IS_DATA_INIT +
                member.getId(),false);
        if (!isDataInit){
            startService(new Intent(LoginActivity.this, DataInitService.class));
        }
    }

    private void saveMember(Member member){
        MemberUtil.saveMember(LoginActivity.this,member);
    }
}
