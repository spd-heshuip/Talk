/*
*Member.java
*Created on 2014-7-16 下午4:49 by Ivan
*Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
*http://www.cniao5.com
*/
package com.example.shuip.talk.model;

import java.io.Serializable;

/**
 * Created by Ivan on Shuip_He.
 * Copyright(c)2014 Guangzhou Eardatek Information Technology Co., Ltd.
 * http://www.eardatek.com
 */
public class Member implements Serializable {

    private Long id;
    private String email;
    private String name;
    private String token;
    private String headbig;
    private String headmid;
    private String headsmall;



    public Member(){}

    public Member(String name)
    {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return "{id:"+getId()+",email:"+getEmail()+"}";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHeadbig() {
        return headbig;
    }

    public void setHeadbig(String headbig) {
        this.headbig = headbig;
    }

    public String getHeadmid() {
        return headmid;
    }

    public void setHeadmid(String headmid) {
        this.headmid = headmid;
    }

    public String getHeadsmall() {
        return headsmall;
    }

    public void setHeadsmall(String headsmall) {
        this.headsmall = headsmall;
    }
}
