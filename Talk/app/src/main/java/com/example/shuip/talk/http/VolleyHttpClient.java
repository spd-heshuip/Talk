package com.example.shuip.talk.http;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.shuip.talk.widget.loadingDialog;
import com.example.shuip.util.android.ToastUtils;

import java.util.Map;

/**
 * Created by Administrator on 15-9-2.
 */
public class VolleyHttpClient {

    private loadingDialog mDialog;
    private Context mContext;
    private VolleySingleTon mVolleySingelTon;

    public VolleyHttpClient(Context context){
        this.mContext = context;
        mVolleySingelTon = VolleySingleTon.getInstance(context);
        InitDialog(mContext);
    }

    private void InitDialog(Context context){
        if(context instanceof Activity){
            mDialog = new loadingDialog(context);
        }
    }
/*
    public static VolleyHttpClient getInstance(Context context){
        if (mInstance == null){
            synchronized (VolleyHttpClient.class){
                if (mInstance == null){
                    mInstance = new VolleyHttpClient(context);
                }
            }
        }

        return mInstance;
    }*/

    public void post(String url,Map<String,String> params,int loadingMsg,final RequestListener requestListener){
        reuquest(Request.Method.POST,url,params,loadingMsg,requestListener);
    }

    public void get(String url,int loadingMsg,final RequestListener requestListener){
        reuquest(Request.Method.GET, url, null, loadingMsg, requestListener);
    }

    public void reuquest(int Method,String url,Map<String,String> params,int loadingMsg,final RequestListener listener){
        if(listener != null){
            listener.onPreRequest();
        }
        showDialog(loadingMsg);
        BaseRequest baseRequest = new BaseRequest(Method, url, params,
                new Response.Listener<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response) {
                        dismissDialod();
                        if (listener != null){
                            if (response.isSuccess()){
                                listener.onRequestSuccess(response);
                            }else {
                                listener.onRequestFail(response.getStatus(),response.getMsg());
                            }
                        }
                    }
                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        dismissDialod();
                        String errMsg = null;
                        int errCode=-1;
                        if(volleyError == null){
                            errMsg = "请求服务器出错，错误代码未知";
                        }
                        else{
                            errMsg = VolleyErrorHelper.getMessage(mContext,volleyError);
                            errCode = volleyError.networkResponse == null ? errCode :
                                    volleyError.networkResponse.statusCode;
                        }

                        ToastUtils.show(mContext,errMsg);
                        if (listener != null){
                            listener.onRequestError(errCode,errMsg);
                        }
                    }
        });
        mVolleySingelTon.addToRequestQueue(baseRequest);
    }

    private void showDialog(int msg){
        if (mDialog != null && msg > 0){
            mDialog.setMessage(msg);
            mDialog.showDialog();
        }
    }

    private void dismissDialod(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }
}
