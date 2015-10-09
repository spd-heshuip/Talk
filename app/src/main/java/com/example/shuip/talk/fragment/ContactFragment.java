package com.example.shuip.talk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuip.talk.ProfileActivity;
import com.example.shuip.talk.R;
import com.example.shuip.talk.adapter.ContactAdapter;
import com.example.shuip.talk.db.dao.service.ContactService;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.widget.SideBar;
import com.example.shuip.util.android.PreferencesUtils;
import com.example.shuip.util.android.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Administrator on 15-8-31.
 */
public class ContactFragment extends RoboFragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.listcontact)
    private StickyListHeadersListView mContactListView;

    @InjectView(R.id.progressbar)
    private ProgressBar mProgress;

    @InjectView(R.id.text_first)
    private TextView mTextView;

    @InjectView(R.id.sidebar)
    private SideBar mSideBar;


    private ContactAdapter mAdapter;
    private ContactService mContactService;
    private List<Contact> mContackList;
    private DataInitHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactService = ContactService.getInstance();
        handler = new DataInitHandler(ContactFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactListView.setOnItemClickListener(this);
        handler.post(new LoadContactRunable(ContactFragment.this));
        mSideBar.setTextView(mTextView);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mAdapter.getPositionForSection(s.charAt(0));
                mContactListView.setSelection(position);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = (Contact) parent.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(Constant.FRIEND,contact);

        startActivity(intent);
    }

    private static final class DataInitHandler extends Handler{
        private WeakReference<ContactFragment> mFragment;

        public DataInitHandler(ContactFragment fragment){
            mFragment = new WeakReference<ContactFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ContactFragment fragment = mFragment.get();
            if (fragment != null){
                if (msg.what == Constant.SUCCESS){
                    fragment.mProgress.setVisibility(View.GONE);
                    fragment.mAdapter = new ContactAdapter(fragment.getActivity(),fragment.mContackList);
                    fragment.mContactListView.setAdapter(fragment.mAdapter);
                }
            }

        }
    }

    private static final class LoadContactRunable implements Runnable{
        private WeakReference<ContactFragment> mFragment;
        private int count = 5;
        public LoadContactRunable(ContactFragment fragment){
            mFragment = new WeakReference<ContactFragment>(fragment);
        }
        @Override
        public void run() {
            ContactFragment fragment = mFragment.get();
            boolean isDataInitFinish = PreferencesUtils.getBoolean(fragment.getActivity(),Constant.IS_DATA_INIT,false);
            if (isDataInitFinish){
                fragment.mContackList = fragment.mContactService.findMemberContacts(fragment.getActivity());
                fragment.handler.sendEmptyMessage(Constant.SUCCESS);
            }else {
                if(count > 0) {
                    fragment.handler.postDelayed(new LoadContactRunable(fragment),2000);
                    count--;
                }else{
                    ToastUtils.show(fragment.getActivity(),"获取好友列表失败，请检查 网络设置",
                            Toast.LENGTH_SHORT);
                }
            }
        }
    }
}
