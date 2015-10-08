package com.example.shuip.talk.db.dao.service;

import android.content.Context;

import com.example.shuip.talk.db.dao.ChatMsgDao;
import com.example.shuip.talk.db.dao.DaoSession;
import com.example.shuip.talk.model.ChatMsg;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.util.android.PreferencesUtils;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 15-9-15.
 */
public class ChatMsgService {

    private static ChatMsgService mInstance;
    private DaoSession mDaoSession;
    private ChatMsgDao mChatMsgDao;

    public static ChatMsgService getInstance(){
        if (mInstance == null){
            mInstance = new ChatMsgService();
            mInstance.mDaoSession = TalkApplication.getInstance().getmDaoSession();
            mInstance.mChatMsgDao = mInstance.mDaoSession.getChatMsgDao();
        }
        return mInstance;
    }

    public  long save(ChatMsg chatMsg){
        return this.mChatMsgDao.insert(chatMsg);
    }

    public void update(ChatMsg chatMsg){
        this.mChatMsgDao.updateInTx(chatMsg);
    }

    public List<ChatMsg> findChatMsgByContactId(Long contactId,int pageIndex,int pageSize){
        QueryBuilder<ChatMsg> queryBuilder = mChatMsgDao.queryBuilder();
        queryBuilder.where(ChatMsgDao.Properties.Contactid.eq(contactId));

        int offset = pageIndex * pageSize;
        queryBuilder.offset(offset);
        queryBuilder.limit(pageSize);

        return queryBuilder.list();
    }

    public List<ChatMsg> findUnreadMsg(Context context){
        QueryBuilder<ChatMsg> queryBuilder = mChatMsgDao.queryBuilder();
        queryBuilder.where(ChatMsgDao.Properties.Memberid.eq(PreferencesUtils.getLong(context, Constant.MEMBER_ID)));
        queryBuilder.where(ChatMsgDao.Properties.Status.eq(ChatMsg.STATUS_UNREAD));

        List<ChatMsg> chatMsgs = queryBuilder.listLazy();

        return chatMsgs;
    }
}
