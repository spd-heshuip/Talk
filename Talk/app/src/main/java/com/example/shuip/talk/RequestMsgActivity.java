package com.example.shuip.talk;

import android.os.Bundle;
import android.widget.ListView;

import com.example.shuip.talk.adapter.RequestMsgAdapter;
import com.example.shuip.talk.db.dao.service.RequestMsgService;
import com.example.shuip.talk.model.RequestMsg;
import com.example.shuip.talk.sys.Constant;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-9-22.
 */
@ContentView(R.layout.activity_request_msg)
public class RequestMsgActivity extends BaseActivity{

    @InjectView(R.id.listView_request)
    private ListView mListView;

    private List<RequestMsg> mListRequestMsg;

    private RequestMsgAdapter mAdapter;

    private RequestMsgService mRequestMsgService = RequestMsgService.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListRequestMsg = mRequestMsgService.findRequestMsg(RequestMsgActivity.this);
        mAdapter = new RequestMsgAdapter(this,mListRequestMsg);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        setResult(Constant.RESULT_CODE);
        this.finish();
    }
}
