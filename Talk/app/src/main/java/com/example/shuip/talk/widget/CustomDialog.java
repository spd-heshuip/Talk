package com.example.shuip.talk.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.shuip.talk.R;

/**
 * Created by Administrator on 15-9-22.
 */
public class CustomDialog extends Dialog{

    private Context mContext;
    private View mLayoutView;

    public CustomDialog(Context context,View view) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.mLayoutView = view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutView);
    }
}
