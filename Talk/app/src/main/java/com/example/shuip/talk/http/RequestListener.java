package com.example.shuip.talk.http;

/**
 * Created by Administrator on 15-9-2.
 */
public interface RequestListener {
    /**
     * 在请求之前调用的方法
     */
    public  void onPreRequest();

    /**
     * 请求成功调用
     * @param response
     */
    public  void onRequestSuccess(BaseResponse response);

    /**
     * 请求失败调用，致命错误
     * @param code
     * @param msg
     */
    public  void onRequestError(int code ,String msg);

    /**
     * 服务器返回失败调用
     * @param code
     * @param msg
     */
    public  void onRequestFail(int code ,String msg);
}
