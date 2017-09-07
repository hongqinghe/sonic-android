package com.tencent.sonic.demo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.sonic.R;


/**
 * Created by 贯众 on 17-3-21.
 * guanzhong@2dfire.com
 * Description: 通用网页Activity
 */
//@Route(path = BaseRoutePath.HTTP_ACTIVITY)
public class HttpActivity extends FragmentActivity {

//    @Inject
//    ServiceUtils serviceUtils;

//    @Inject
//    JsonUtils jsonUtils;

    WebView myWebView;

    String mUrl ="http://m.2dfire.com/";
//    String mUrl ="http://mc.vip.qq.com/demo/indexv3";

    String title;

    boolean showDialog = false; //防止重复创建等待Dialog

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
//        if (!StringUtils.isEmpty((String) bundle.get(ApiConfig.KeyName.HTTP_URL))){
//            mUrl = (String) bundle.get(ApiConfig.KeyName.HTTP_URL);
//        }
//        title = bundle.getString(ApiConfig.KeyName.HTTP_TITLE, getString(R.string.http_title_null));
//        super.initActivity(true,title, R.layout.act_http, BtnBar.BAR_EMPTY);
        setContentView(R.layout.act_http);
        super.onCreate(savedInstanceState);
//        TDFBackGroundUtils.setBackground(restApplication.getPlatform(), getMaincontent());
        myWebView = (WebView) findViewById(R.id.webview);
        goToWebView();
    }

//    @Override
//    protected void loadInitdata() {
//        setTitleName(title);
//        goToWebView();
//    }
//
//    @Override
//    protected void initEvent(Activity view) {
//
//        setHelpVisible(false);
//    }
//
//    @Override
//    protected HelpVO getHelpContent() {
//        return null;
//    }
//
//    @Override
//    public void onRightClick() {

//    }

//    @Override
//    protected void onLeftClick() {
//        if ( myWebView.canGoBack()) {
//            myWebView.goBack();
//        }else{
//            onBackPressed();
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goToWebView() {
//        try {
//            mUrl = String.format(urlFormat2, ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.SUPPLYCHAIN_REPORT_URL),restApplication.getPlatform().getApiParams().get(com.zmsoft.constants.ApiConstants.SESSION_KEY), restApplication.getPlatform().getJssessionId(), restApplication.preferences.get(PreferenceConstants.SHOP_CODE), URLEncoder.encode(restApplication.preferences.get(PreferenceConstants.SHOP_NAME), "UTF-8"),
//                    restApplication.getPlatform().getEntityId(), restApplication.preferences.get(PreferenceConstants.SERVER_PATH), ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.DEV_TYPE), st);
//            if(restApplication.getPlatform().getIsHeadShop() && restApplication.getPlatform().isRefesh()){
//                urlExtend = String.format(urlExtend, restApplication.getPlatform().getUserId(), 1);
//                mUrl = mUrl + urlExtend;
//            }
//        mUrl = String.format(urlFormat, ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.REPORT_URL), restApplication.getPlatform().getVersionName(), "3",st);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    ProcessDialogUtils.dismessDialog();
                    myWebView.setVisibility(View.VISIBLE);
                    showDialog = false;
                } else {
                    if (!showDialog) {
                        ProcessDialogUtils.creatAndShow(HttpActivity.this, "正在加载");
                        showDialog = true;
                        myWebView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                if (!StringUtils.isEmpty(title)) {
//                    setTitleName(title);
//                }
            }
        });
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // hitTestResult==null解决重定向问题
                WebView.HitTestResult hitTestResult = view.getHitTestResult();
                if (!TextUtils.isEmpty(mUrl) && hitTestResult == null) {
                    view.loadUrl(mUrl);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, mUrl);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    String javascript = "(function() { " +
                            "var titles = document.getElementsByTagName(\"title\");" +
                            "if (titles == null || titles.length != 1) return \"\";" +
                            "return titles[0].textContent; " +
                            "})();";

                    myWebView.evaluateJavascript(javascript, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            String htmlTitleString = s.replace("\"", "");
                            if (htmlTitleString != null && htmlTitleString.length() > 0) {
//                                setTitleName(StringUtils.unicodeToString(htmlTitleString));
                            }
                        }
                    });
                } else {
                    String titleText = view.getTitle();
                    if (titleText != null && titleText.length() > 0) {
//                        setTitleName(titleText);
                    }
                }
            }
        });
        //加载本地中的html
        //myWebView.loadUrl("file:///android_asset/www/test2.html");
        //加上下面这段代码可以使网页中的链接不以浏览器的方式打开
        //得到webview设置
        WebSettings webSettings = myWebView.getSettings();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setDomStorageEnabled(true);
        //允许使用javascript
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //将WebAppInterface于javascript绑定
        myWebView.addJavascriptInterface(new WebAppInterface(this), "manager");
//        BackGroundUtils.setBackground(restApplication.getPlatform(), getMaincontent());
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
        //加载页面
        myWebView.loadUrl(mUrl);
    }
}