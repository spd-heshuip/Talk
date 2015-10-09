package com.example.shuip.talk.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Administrator on 15-9-1.
 */
public class GsonRequest<T> extends Request<T>{

    private Gson mGson;
    private final Response.Listener mListener;
    private Class<T> mClass;
    private Map<String,String> mParams;

    public GsonRequest(int Method,String url,Class<T> tClass,Response.Listener listener,Response.ErrorListener errorListener,Map<String,String> params){
        super(Method,url,errorListener);
        mGson = new Gson();
        mListener = listener;
        mClass = tClass;
    }

    public GsonRequest(String url,Class<T> tClass,Response.Listener listener, Response.ErrorListener errorListener){
        this(Method.GET,url,tClass,listener,errorListener,null);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonString = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            return Response.success(mGson.fromJson(jsonString,mClass),HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

}
