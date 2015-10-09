package com.example.shuip.talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shuip.talk.R;
import com.example.shuip.talk.StartActivity;
import com.example.shuip.talk.model.Member;
import com.example.shuip.talk.util.ImageUtil;
import com.example.shuip.talk.util.MemberUtil;
import com.example.shuip.talk.widget.CustomDialog;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by Administrator on 15-8-31.
 */
public class MoreFragment extends RoboFragment implements View.OnClickListener{
    @InjectView(R.id.imageviewHead)
    private ImageView mImgHead;

    @InjectView(R.id.txtName)
    private TextView mTxtName;

    @InjectView(R.id.txtEmail)
    private TextView mTxtEmail;

    @InjectView(R.id.viewSetting)
    private View mViewSetting;

    @InjectView(R.id.viewAboutus)
    private View mViewAboutus;

    @InjectView(R.id.btnLogout)
    private Button mBtnLogout;

    private CustomDialog mConfirmDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayProfile();
        mBtnLogout.setOnClickListener(this);
    }

    private void showConfirmDialog(){
        buildConfirmDialog().show();
    }

    private  CustomDialog buildConfirmDialog()
    {
        if (mConfirmDialog==null){

            View layoutView =LayoutInflater.from(getActivity()).inflate(R.layout.dialog_info, null);

            mConfirmDialog = new CustomDialog(getActivity(),layoutView);

            TextView txtMsg = (TextView) layoutView.findViewById(R.id.txtMsg);
            txtMsg.setText(R.string.logout_confirm);

            Button mBtnCancel = (Button) layoutView.findViewById(R.id.btnCancel);
            Button mBtnConfirm = (Button) layoutView.findViewById(R.id.btnConfirm);

            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mConfirmDialog.dismiss();
                }
            });

            mBtnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });
        }
        return mConfirmDialog;
    }

    private void logout() {
        MemberUtil.clearMember(getActivity());
        startActivity(new Intent(getActivity(), StartActivity.class));
        getActivity().finish();
    }

    private  void displayProfile(){
        Member member = MemberUtil.getMember(getActivity());
        if(member!=null){

            mTxtName.setText(member.getName());
            mTxtEmail.setText(member.getEmail());
            ImageUtil.displayImageUseDefOption(member.getHeadbig(), mImgHead);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogout){
            showConfirmDialog();
        }
    }
}
