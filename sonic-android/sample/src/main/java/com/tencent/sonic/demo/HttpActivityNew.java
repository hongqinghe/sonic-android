package com.tencent.sonic.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.sonic.R;
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***********************************************
 * <P> dec:
 * <P> Author: gongtong
 * <P> Date: 17-8-21.
 * <P> Copyright  2008 二维火科技
 ***********************************************/
//@Route(path = BaseRoutePath.HTTP_ACTIVITY_NEW)
public class HttpActivityNew extends FragmentActivity{
    public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

    private static final int PERMISSION_REQUEST_CODE_STORAGE = 1;

    WebView myWebView;
   public static String mUrl = "http://m.2dfire.com/";
//   public static String mUrl = "http://mc.vip.qq.com/demo/indexv3";
    private SonicSession sonicSession;
    String title;

    boolean showDialog = false; //防止重复创建等待Dialog
    SonicSessionClientImpl sessionClient = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Bundle bundle = getIntent().getExtras();
//        if (!StringUtils.isEmpty((String) bundle.get(ApiConfig.KeyName.HTTP_URL))){
//            mUrl = (String) bundle.get(ApiConfig.KeyName.HTTP_URL);
//        }
//        title = bundle.getString(ApiConfig.KeyName.HTTP_TITLE, getString(R.string.http_title_null));
//        super.initActivity(true,title, R.layout.activity_http_new, BtnBar.BAR_EMPTY);


        // TODO: 17-8-18 接入登录修改
//        TDFBackGroundUtils.setBackground(restApplication.getPlatform(), getMaincontent());
         setContentView(R.layout.webview);

        myWebView = (WebView) findViewById(R.id.wb_view);
        super.onCreate(savedInstanceState);

        if (hasPermission()) {
            initSonic();
        } else {
            requestPermission();
        }

    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                initSonic();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     *
     * 创建SonicRuntime并且初始化sonicEngine
     */
    private void initSonic() {

        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(this), new SonicConfig.Builder().build());
        }

        SonicSessionConfig.Builder sonicSessionConfigBuilder = new SonicSessionConfig.Builder();
        sonicSessionConfigBuilder.setCacheInterceptor(new SonicCacheInterceptor(null) {
            @Override
            public String getCacheData(SonicSession session) {
                return null;
            }
        });
       sonicSessionConfigBuilder.setConnectionIntercepter(new SonicSessionConnectionInterceptor() {
           @Override
           public SonicSessionConnection getConnection(SonicSession session, Intent intent) {
               return new OfflinePkgSessionConnection(HttpActivityNew.this,session,intent);
           }
       });
       //为加载的 url创建一个sonicSession对象,同时绑定一个client,session创建之后sonic进行异步加载数据
        sonicSession = SonicEngine.getInstance().createSession(mUrl, sonicSessionConfigBuilder.build());
        if (null!=sonicSession) {
            sonicSession.bindClient(sessionClient = new SonicSessionClientImpl());
        }else {
            throw new UnknownError("设置失败");
        }


        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress==100) {
                    ProcessDialogUtils.dismessDialog();
//                    myWebView.setVisibility(View.VISIBLE);
                    showDialog = false;
                } else {
                    if (!showDialog) {
                            ProcessDialogUtils.creatAndShow(HttpActivityNew.this, "正在加载");
                        showDialog = true;
//                        myWebView.setVisibility(View.GONE);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                //在webView资源拦截的回调中调用方法,通过url获取对应的webResourceResponse数据,这样内核就可以根据这个
                //response内容进行渲染
                if (sonicSession != null) {
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                }
                return null;
            }
        });

        setWebView();
    }

    private void setWebView() {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.removeJavascriptInterface("webView");
        myWebView.addJavascriptInterface(new SonicJavaScriptInterface(sessionClient, new Intent()), "webView");

        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //为client绑定webView  webView ready可以开始loadUrl
        if (sessionClient != null) {
            sessionClient.bindWebView(myWebView);
            sessionClient.clientReady();
        } else {
            myWebView.loadUrl(mUrl);
        }
    }

//    @Override
//    protected void initEvent(Activity view) {
//  myWebView = (WebView) findViewById(R.id.webViewNew);
//        setHelpVisible(false);
//    }

//    @Override
//    protected void loadInitdata() {
//        setTitleName(title);
//    }

    @Override
    public void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        super.onDestroy();

    }

    private static class OfflinePkgSessionConnection extends SonicSessionConnection {

        private final WeakReference<Context> context;

        public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
            super(session, intent);
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected int internalConnect() {
            Context ctx = context.get();
            if (null != ctx) {
                try {
                    InputStream offlineHtmlInputStream = ctx.getAssets().open(mUrl);
                    responseStream = new BufferedInputStream(offlineHtmlInputStream);
                    return SonicConstants.ERROR_CODE_SUCCESS;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return SonicConstants.ERROR_CODE_UNKNOWN;
        }

        @Override
        protected BufferedInputStream internalGetResponseStream() {
            return responseStream;
        }

        @Override
        public void disconnect() {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getResponseCode() {
            return 200;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new HashMap<>(0);
        }

        @Override
        public String getResponseHeaderField(String key) {
            return "";
        }
    }
}
