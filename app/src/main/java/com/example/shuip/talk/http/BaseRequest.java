package com.example.shuip.talk.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Administrator on 15-9-1.
 */
public class BaseRequest extends Request<BaseResponse>{

    private final Response.Listener<BaseResponse> mlistener;
    private Map<String,String> mParams;

    public BaseRequest(int Method,String url,Map<String,String> params,Response.Listener listener,Response.ErrorListener errorListener){
        super(Method,url,errorListener);
        this.mlistener = listener;
        this.mParams = params;
    }

    @Override
    protected Response<BaseResponse> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String json = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));

            BaseResponse baseReponse = parseJson(json);
            return Response.success(baseReponse,HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void deliverResponse(BaseResponse baseReponse) {
        mlistener.onResponse(baseReponse);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.mParams;
    }

    private BaseResponse parseJson(String json){
        int status = 0;
        String msg = null;
        String data = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            status = jsonObject.getInt("status");
            msg = jsonObject.getString("msg");
            data = jsonObject.getString("data");
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        BaseResponse response = new BaseResponse();
        response.setStatus(status);
        response.setMsg(msg);
        response.setData(data);
        return response;
    }

}
