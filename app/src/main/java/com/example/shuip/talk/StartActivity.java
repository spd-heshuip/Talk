package com.example.shuip.talk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.util.MainfestUtil;
import com.example.shuip.util.android.PreferencesUtils;

/**
 * Created by Administrator on 15-9-6.
 */
public class StartActivity extends AppCompatActivity{
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, MainfestUtil.getMetaValue(this, Constant.BD_API_KEY));

        mWebView = (WebView)findViewById(R.id.web);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);

        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.loadUrl(Constant.API.URL_START);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jump();
            }
        },1000);
    }

    private void jump(){
        String memberJson = PreferencesUtils.getString(this,Constant.MEMBER);
        if (TextUtils.isEmpty(memberJson)){
            startActivity(new Intent(this,LoginActivity.class));
        }else {
            startActivity(new Intent(this,HomeActivity.class));
            this.finish();
        }
    }
}
