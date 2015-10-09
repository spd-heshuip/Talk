package com.example.shuip.talk;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.shuip.talk.http.VolleyHttpClient;

import roboguice.activity.RoboActionBarActivity;

/**
 * Created by Administrator on 15-9-2.
 */
public class BaseActivity extends RoboActionBarActivity{

    protected VolleyHttpClient mHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpClient = new VolleyHttpClient(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initAcitonBarBasic(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
