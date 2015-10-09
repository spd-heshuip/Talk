package com.example.shuip.talk.db.dao.service;

import android.content.Context;
import android.database.Cursor;

import com.example.shuip.talk.db.dao.ChatMsgDao;
import com.example.shuip.talk.db.dao.DaoSession;
import com.example.shuip.talk.model.ChatMsg;
import com.example.shuip.talk.model.ChatMsgEx;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.util.android.PreferencesUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 15-9-15.
 */
public class ChatMsgService {

    private static ChatMsgService mInstance;
    private DaoSession mDaoSession;
    private ChatMsgDao mChatMsgDao;
    private ContactService mContactService = ContactService.getInstance();

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

    public void updateUnreadChatMsg(Context context,long contactId){
        QueryBuilder<ChatMsg> queryBuilder = mChatMsgDao.queryBuilder();
        queryBuilder.where(ChatMsgDao.Properties.Memberid.eq(PreferencesUtils.getLong(context,Constant.MEMBER_ID)));
        queryBuilder.where(ChatMsgDao.Properties.Contactid.eq(contactId));
        queryBuilder.where(ChatMsgDao.Properties.Status.eq(ChatMsg.STATUS_UNREAD)).build();

        List<ChatMsg> list = queryBuilder.listLazy();

        if (list != null && list.size() > 0){
            for (ChatMsg chatMsg : list){
                chatMsg.setStatus(ChatMsg.STATUS_READED);
                this.mChatMsgDao.updateInTx(chatMsg);
            }
        }
    }

    public List<ChatMsgEx> findHistoryChatMsg(Context context){
        Long memberId = PreferencesUtils.getLong(context, Constant.MEMBER_ID);

        Cursor cursor = mChatMsgDao.getDatabase().query(true, ChatMsgDao.TABLENAME,
                new String[]{ChatMsgDao.Properties.Id.columnName,
                        ChatMsgDao.Properties.Contactid.columnName,
                        ChatMsgDao.Properties.Chatmsg.columnName,
                        ChatMsgDao.Properties.Chattime.columnName,
                        ChatMsgDao.Properties.Chattype.columnName,
                        ChatMsgDao.Properties.Status.columnName,
                        ChatMsgDao.Properties.Isreceive.columnName
                },
                ChatMsgDao.Properties.Memberid.columnName + "=?",
                new String[]{memberId + ""},
                ChatMsgDao.Properties.Contactid.columnName,
                null,
                ChatMsgDao.Properties.Chattime.columnName + " desc",
                null);

        List<ChatMsgEx> chatMsgExes = new ArrayList<ChatMsgEx>();

        while (cursor.moveToNext()){
            ChatMsgEx chatMsgEx = new ChatMsgEx();
            chatMsgEx.setId(cursor.getLong(0));
            chatMsgEx.setContactid(cursor.getLong(1));
            chatMsgEx.setChatmsg(cursor.getString(2));
            chatMsgEx.setChattime(new Date(cursor.getLong(3)));
            chatMsgEx.setChattype(cursor.getInt(4));
            chatMsgEx.setStatus(cursor.getInt(5));
            chatMsgEx.setIsreceive(cursor.getInt(6));

            chatMsgEx.setContact(this.mContactService.getContact(context, chatMsgEx.getContactid()));

            chatMsgEx.setUnreadCount(countContactUnreadMsg(context,chatMsgEx.getContactid()));
            chatMsgExes.add(chatMsgEx);
        }
        cursor.close();

        return chatMsgExes;
    }

    /**
     * 统计联系人未读消息数量
     * @param context
     * @param contactId
     * @return
     */
    public int countContactUnreadMsg(Context context,long contactId)
    {

        QueryBuilder<ChatMsg> queryBuilder= this.mChatMsgDao.queryBuilder();

        queryBuilder.where(ChatMsgDao.Properties.Memberid.eq(PreferencesUtils.getLong(context, Constant.MEMBER_ID)));
        queryBuilder.where(ChatMsgDao.Properties.Contactid.eq(contactId));
        queryBuilder.where(ChatMsgDao.Properties.Status.eq(ChatMsg.STATUS_UNREAD));

        return (int) queryBuilder.count();
    }
}
