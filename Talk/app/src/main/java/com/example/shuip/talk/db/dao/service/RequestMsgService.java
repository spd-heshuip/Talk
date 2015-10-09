package com.example.shuip.talk.db.dao.service;

import android.content.Context;

import com.example.shuip.talk.db.dao.DaoSession;
import com.example.shuip.talk.db.dao.RequestMsgDao;
import com.example.shuip.talk.model.RequestMsg;
import com.example.shuip.talk.sys.Constant;
import com.example.shuip.talk.sys.TalkApplication;
import com.example.shuip.util.android.PreferencesUtils;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 15-9-22.
 */
public class RequestMsgService {
    private static RequestMsgService mInstance;
    private DaoSession mDaoSession;
    private RequestMsgDao mRequestMsgDao;

    private RequestMsgService(){

    }

    public static RequestMsgService getInstance(){
        if (mInstance == null){
            mInstance = new RequestMsgService();
            mInstance.mDaoSession = TalkApplication.getInstance().getmDaoSession();
            mInstance.mRequestMsgDao = mInstance.mDaoSession.getRequestMsgDao();
        }
        return  mInstance;
    }

    public void save(RequestMsg msg){
        this.mRequestMsgDao.insertInTx(msg);
    }

    public List<RequestMsg> findFriendRequestMsg(long memberId,long friendId){
        QueryBuilder<RequestMsg> queryBuilder = mRequestMsgDao.queryBuilder();
        queryBuilder.where(RequestMsgDao.Properties.Memberid.eq(memberId));
        queryBuilder.where(RequestMsgDao.Properties.Contactid.eq(friendId));

        return queryBuilder.list();
    }

    public List<RequestMsg> findRequestMsg(Context context){
        long memberId = PreferencesUtils.getLong(context, Constant.MEMBER_ID);
        QueryBuilder<RequestMsg> queryBuilder = mRequestMsgDao.queryBuilder();
        queryBuilder.where(RequestMsgDao.Properties.Memberid.eq(memberId));
        queryBuilder.orderDesc(RequestMsgDao.Properties.Requesttime);

        return queryBuilder.list();
    }

    public int countUnHandleMsg(Context context){
        Long memberId = PreferencesUtils.getLong(context,Constant.MEMBER_ID);
        QueryBuilder<RequestMsg> queryBuilder = mRequestMsgDao.queryBuilder();
        queryBuilder.where(RequestMsgDao.Properties.Memberid.eq(memberId));
        queryBuilder.where(RequestMsgDao.Properties.Status.eq(RequestMsg.STATUS_UN_HANDLE));

        return (int) queryBuilder.count();
    }

    public void update(RequestMsg msg){
        this.mRequestMsgDao.update(msg);
    }
}
