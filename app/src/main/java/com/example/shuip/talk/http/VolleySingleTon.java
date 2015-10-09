package com.example.shuip.talk.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Objects;

/**
 * Created by Administrator on 15-9-1.
 */
public class VolleySingleTon {

    private volatile static VolleySingleTon mInstance;
    private RequestQueue mQueue;
    private VolleySingleTon(Context context){
        mQueue = Volley.newRequestQueue(context);
    }

    public static VolleySingleTon getInstance(Context context){
        if (mInstance == null){
            synchronized (VolleySingleTon.class){
                if (mInstance == null){
                    mInstance = new VolleySingleTon(context.getApplicationContext());
                }
            }
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return this.mQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public void cancelRequest(Objects tag){
        getRequestQueue().cancelAll(tag);
    }
}
