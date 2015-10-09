package com.example.shuip.talk.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-9-1.
 */
public class BaseResponse {

    private Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private Integer status;
    private String msg;
    private String data;

    private static final int SUCCESS = 1;
    private static final int FAIL= 2;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



    public boolean isSuccess(){
        return status == SUCCESS;
    }

    public <T> T getObj(Class<T> tClass){
        if(TextUtils.isEmpty(data)){
            return null;
        }
        return mGson.fromJson(data,tClass);
    }

    public <T> List<T> getList(Class<T> tClass){
        if(TextUtils.isEmpty(data)){
            return null;
        }

        List<T> list = new ArrayList<T>();

        Type listType = type(List.class,tClass);

        list = mGson.fromJson(data,listType);

        return list;
    }

    static ParameterizedType type(final Class raw,final Type ... args){
        return  new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }

            @Override
            public Type getRawType() {
                return raw;
            }
        };
    }
}
