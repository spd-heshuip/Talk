/*
*Message.java
*Created on 2014-10-13 下午5:47 by Ivan
*Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
*http://www.cniao5.com
*/
package com.example.shuip.talk.message;

/**
 * Created by Ivan on 14-10-13.
 * Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
 * http://www.cniao5.com
 */
public class Message<T> {


    public static final int MSG_TYPE_CHAT=1;
    public static final int MSG_TYPE_ADD_FRIEND_REQUEST=2;
    public static final int MSG_TYPE_ADD_FRIEND_ANSWER=3;
    public static final int MSG_TYPE_FRIEND_INFO_UPDATE=4;
    public static final int MSG_TYPE_FRIEND_RECOMMEND=5;
    public static final int MSG_TYPE_FRIEND_MOMENT=6;

    private Long id;

    private int msgType;

    private T data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
