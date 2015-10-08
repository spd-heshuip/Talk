package com.example.shuip.talk.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.shuip.talk.R;

/**
 * Created by Administrator on 15-9-1.
 */
public class loadingDialog {

    private Context mContext;
    private View mDialogView;
    private TextView mTxtMsg;
    private Dialog mDialog;

    public loadingDialog(Context context){
        this.mContext = context;
        mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading,null);
        mTxtMsg = (TextView)mDialogView.findViewById(R.id.txtMsg);

        initDialog();
    }

    private void initDialog(){
        mDialog = new Dialog(mContext,R.style.dialog);
        mDialog.setContentView(mDialogView);
        mDialog.setCanceledOnTouchOutside(true);

    }

    public void setMessage(CharSequence c){
        if (mTxtMsg != null){
            mTxtMsg.setText(c);
        }
    }

    public void setMessage(int c){
        mTxtMsg.setText(c);
    }


    public void showDialog(){
        if(mDialog != null){
            mDialog.show();
        }
    }

    public void dismiss(){
        if(mDialog != null){
            mDialog.dismiss();
        }
    }

    public boolean isShowing(){
        return mDialog.isShowing();
    }
}
