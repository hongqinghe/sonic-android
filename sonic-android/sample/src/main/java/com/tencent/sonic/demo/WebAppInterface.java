package com.tencent.sonic.demo;

import android.content.Context;
import android.webkit.JavascriptInterface;


/**
 * 优化一期
 * 作者：lamian on 17-3-7 上午10:27
 * 邮箱：lamian@2dfire.com
 */
public class WebAppInterface {




    Context mContext;


    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {
        mContext = c;
    }
    @JavascriptInterface
    public void callBackAndroid(){
//        eventBus.post(new CallBackAndroidEvent());
    }

    @JavascriptInterface
    public void sessionTimeOut(){
//        eventBus.post(new SessionTimeOutEvent());
    }
}