package com.example.shuip.talk.model;

/**
 * Created by Administrator on 15-9-21.
 */
public class ChatMsgEx extends ChatMsg {

    private Contact contact;

    private Integer unreadCount;

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
