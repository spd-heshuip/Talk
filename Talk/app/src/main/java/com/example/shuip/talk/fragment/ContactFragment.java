package com.example.shuip.talk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shuip.talk.ProfileActivity;
import com.example.shuip.talk.R;
import com.example.shuip.talk.RequestMsgActivity;
import com.example.shuip.talk.adapter.ContactAdapter;
import com.example.shuip.talk.db.dao.service.ContactService;
import com.example.shuip.talk.db.dao.service.RequestMsgService;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.widget.SideBar;
import com.example.shuip.util.android.PreferencesUtils;

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
    private RequestMsgService mRequestMsgService;
    private List<Contact> mContackList;
    private DataInitHandler handler;
    private LayoutInflater mInflater;

    private TextView mTxtBadge;

    private ContanctBroadCastReceiver mContactReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactService = ContactService.getInstance();
        mRequestMsgService = RequestMsgService.getInstance();
        mInflater = LayoutInflater.from(getActivity());
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

        initHeadView();
        initBadge();
    }

    public void initHeadView(){
        View view = mInflater.inflate(R.layout.template_contact_head,null);
        View newFriendView = view.findViewById(R.id.layoutNewFriend);

        newFriendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestMsgActivity.class);
                startActivityForResult(intent, Constant.REQUEST_CODE);
            }
        });
        mTxtBadge = (TextView) view.findViewById(R.id.txtBadge);
        mContactListView.addHeaderView(view, null, false);
    }

    private void initBadge(){
        int addFriendRequestCount = mRequestMsgService.countUnHandleMsg(getActivity());
        if (addFriendRequestCount > 0){
            mTxtBadge.setVisibility(View.VISIBLE);
            mTxtBadge.setText(addFriendRequestCount+"");
        }
        mTxtBadge.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        reloadContacts();
        initBadge();
    }

    @Override
    public void onStart() {
        super.onStart();
        mContactReceiver = new ContanctBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_UPDATE);
        intentFilter.addAction(Constant.ACTION_MAKE_FRIEND_REQUEST);

        getActivity().registerReceiver(mContactReceiver,intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mContactReceiver);
    }

    private void reloadContacts() {
        mContackList = mContactService.findMemberContacts(getActivity());
        mAdapter.clear();
        mAdapter.addData(mContackList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact contact = (Contact) parent.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(Constant.FRIEND,contact);
        intent.putExtra("from",1);

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
        public LoadContactRunable(ContactFragment fragment){
            mFragment = new WeakReference<ContactFragment>(fragment);
        }
        @Override
        public void run() {
            ContactFragment fragment = mFragment.get();
            boolean isDataInitFinish = PreferencesUtils.getBoolean(fragment.getActivity(),Constant.IS_DATA_INIT +
                    PreferencesUtils.getLong(fragment.getActivity(),Constant.MEMBER_ID),false);
            if (isDataInitFinish){
                fragment.mContackList = fragment.mContactService.findMemberContacts(fragment.getActivity());
                fragment.handler.sendEmptyMessage(Constant.SUCCESS);
            }else {
                fragment.handler.postDelayed(new LoadContactRunable(fragment),2000);
            }
        }
    }

    private final class ContanctBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_CONTACT_UPDATE)){
                reloadContacts();
            }else if (intent.getAction().equals(Constant.ACTION_MAKE_FRIEND_REQUEST)){
                initBadge();
            }
        }
    }
}
