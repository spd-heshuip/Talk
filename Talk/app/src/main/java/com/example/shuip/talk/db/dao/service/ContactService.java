package com.example.shuip.talk.db.dao.service;

import android.content.Context;

import com.example.shuip.talk.db.dao.ContactDao;
import com.example.shuip.talk.db.dao.DaoSession;
import com.example.shuip.talk.model.Contact;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.util.android.PreferencesUtils;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 15-9-10.
 */
public class ContactService {
    private volatile  static ContactService sContactService;
    private DaoSession mDaoSession;
    private ContactDao mContactDao;

    private ContactService() {
    }

    public static ContactService getInstance(){
        if (sContactService == null){
            sContactService = new ContactService();
            sContactService.mDaoSession = TalkApplication.getInstance().getmDaoSession();
            sContactService.mContactDao = sContactService.mDaoSession.getContactDao();
        }
        return sContactService;
    }

    public  void save(List<Contact> contacts){
        mContactDao.insertOrReplaceInTx(contacts);
    }

    public void save(Contact contact){
        mContactDao.insert(contact);
    }

    public List<Contact> findMemberContacts(Context context){
        Long memberId = PreferencesUtils.getLong(context, Constant.MEMBER_ID);
        QueryBuilder<Contact> builder = mContactDao.queryBuilder();
        builder.where(ContactDao.Properties.Memberid.eq(memberId)).orderAsc(ContactDao.Properties.Pinyin);
        return builder.list();
    }

    public Contact getContact(Context context,long contactId){
        QueryBuilder<Contact> queryBuilder = mContactDao.queryBuilder();
        Long memberId = PreferencesUtils.getLong(context,Constant.MEMBER_ID);
        queryBuilder.where(ContactDao.Properties.Memberid.eq(memberId));
        queryBuilder.where(ContactDao.Properties.Contactid.eq(contactId));

        return queryBuilder.unique();
    }
}
