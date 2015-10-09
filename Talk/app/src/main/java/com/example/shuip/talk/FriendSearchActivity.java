package com.example.shuip.talk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shuip.talk.sys.Constant;
import com.example.shuip.util.android.ToastUtils;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-9-21.
 */
@ContentView(R.layout.activity_firend_search)
public class FriendSearchActivity extends BaseActivity implements TextView.OnEditorActionListener{

    @InjectView(R.id.etxtWord)
    private EditText mEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAcitonBarBasic();
        mEdit.setOnEditorActionListener(this);
    }

    public void query(View view){
        queryFriends();
    }

    private void queryFriends() {
        String word = mEdit.getText().toString();
        if (TextUtils.isEmpty(word)){
            ToastUtils.show(this,"请输入关键字搜索！");
            return;
        }

        Intent intent = new Intent(FriendSearchActivity.this,FriendSearchResultActivity.class);
        intent.putExtra(Constant.WORD,word);
        startActivity(intent);

    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                && KeyEvent.ACTION_DOWN == event.getAction())){
            queryFriends();
            return  true;
        }
        return false;
    }
}
