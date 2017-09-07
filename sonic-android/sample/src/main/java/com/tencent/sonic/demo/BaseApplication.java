//package com.tencent.sonic.demo;
//
//import android.app.AlarmManager;
//import android.app.Instrumentation;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.multidex.MultiDexApplication;
//import android.support.v4.content.LocalBroadcastManager;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Display;
//import android.view.View;
//import android.view.WindowManager;
//
//import com.dfire.sdk.util.MD5Util;
//import com.dodola.rocoofix.RocooFix;
//import com.facebook.common.logging.FLog;
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.sobot.chat.utils.ZhiChiConstant;
//import com.umeng.analytics.MobclickAgent;
//import com.umeng.socialize.PlatformConfig;
//import com.umeng.socialize.UMShareAPI;
//import com.zmsoft.constants.ApiConstants;
//import com.zmsoft.constants.CommonConstants;
//import com.zmsoft.constants.PreferenceConstants;
//import com.zmsoft.constants.SupplyPlatform;
//import com.zmsoft.crypto.ApkSecurity;
//import com.zmsoft.eatery.login.vo.MessageVo;
//import com.zmsoft.event.BizExceptionEvent;
//import com.zmsoft.event.LeftMenuClearEvent;
//import com.zmsoft.event.ResidentMenuShowEvent;
//import com.zmsoft.event.SystemNotificationEvent;
//import com.zmsoft.event.UnBindNotificationEvent;
//import com.zmsoft.event.UnReadMessageEvent;
//import com.zmsoft.handler.RestCrashHandler;
//import com.zmsoft.rest.commonview.R;
//import com.zmsoft.rest.commonview.message.MessageType;
//import com.zmsoft.retrofit.JsonUtils;
//import com.zmsoft.utils.ConvertUtils;
//import com.zmsoft.vo.SysNotificationVo;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//import cn.jpush.android.api.JPushInterface;
//import de.greenrobot.event.EventBus;
//import tdf.zmsoft.core.base.TDFConfig;
//import tdf.zmsoft.core.base.TDFPlatform;
//import tdf.zmsoft.core.constants.TDFJpushLoginConstants;
//import tdf.zmsoft.core.utils.TDFACache;
//import tdf.zmsoft.core.utils.TDFServiceUrlUtils;
//import tdf.zmsoft.login.manager.login.config.LoginRegister;
//import tdf.zmsoft.login.manager.login.constants.LoginApiConstants;
//import tdf.zmsoft.login.manager.login.service.listener.ILoginNetWork;
//import tdf.zmsoft.login.manager.login.utils.ReLoginUtils;
//import tdf.zmsoft.navigation.ARouterManager;
//import tdf.zmsoft.network.TDFNetWork;
//import zmsoft.rest.navigation.NavigationControlConstants;
//import zmsoft.rest.phone.config.INavigation;
//import zmsoft.rest.phone.config.INetWork;
//import zmsoft.rest.phone.config.Injector;
//import zmsoft.share.service.AbstractApiService;
//import zmsoft.share.service.errservice.ErrNetWorkService;
//import zmsoft.share.utils.FileUtils;
//import zmsoft.share.utils.LogUtils;
//import zmsoft.share.utils.NetWorkUtils;
//import zmsoft.share.utils.ZmStringUtils;
//import zmsoft.share.utils.observer.ObserverKeys;
//import zmsoft.share.utils.observer.SupplySubject;
//
////import com.crashlytics.android.Crashlytics;
//
///**
// * Created by tme on 2014/10/13.
// */
//public abstract class BaseApplication extends MultiDexApplication implements View.OnClickListener, INetWork, INavigation, TDFNetWork, ILoginNetWork {
//
//
//
//    @Inject
//    EventBus eventBus;
//    @Inject
//    JsonUtils jsonUtils;
//    @Inject
//    ObjectMapper objectMapper;
////    @Inject
////    Platform platform;
//
//    TDFPlatform platform;
//
//    private SupplyPlatform supplyPlatform = null;
//
//    public ImageLoader imageLoader;
//
//    public DisplayImageOptions options;
//
//
//    public Hashtable<String, String> preferences;
//
//    /**
//     * Create main application
//     */
//    public BaseApplication() {
//
//    }
//
//    /**
//     * Create main application
//     *
//     * @param context
//     */
//    public BaseApplication(final Context context) {
//        this();
//        attachBaseContext(context);
//    }
//
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        //打补丁
//        RocooFix.init(this);
//        File file = FileUtils.getHotFixPatch(this, getHotFixDir(), CommonConstants.FILE_PATCH_NAME);
//        if (file.exists()) {
//            String pathString = file.getAbsolutePath();
//            RocooFix.applyPatch(this, pathString);
////            LogUtils.e("cy",pathString+"存在");
//            file.delete();
//        } else {
////            LogUtils.e("cy",file.getAbsolutePath()+"不存在");
//        }
////        LogUtils.e("cy",pathString);
//    }
//
//    @Override
//    public void onCreate() {
////        Debug.startMethodTracing("after");
//        init();
//        super.onCreate();
//        initSDK();
//        ARouterManager.init(this, true);
//        Intent errNetWorkService = new Intent(this, ErrNetWorkService.class);
//        startService(errNetWorkService);
//        initCustomerService();
//        instance = this;
//        supplyPlatform = new SupplyPlatform();
//        platform = TDFPlatform.getInstance();
//        DisplayImageOptions opts = new DisplayImageOptions.Builder()
////                .showImageOnLoading(R.drawable.process)
//                .showStubImage(R.drawable.img_cardbox_b)
//                .showImageOnFail(R.drawable.img_picerror)
//                .displayer(new FadeInBitmapDisplayer(100))
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .considerExifParams(true)
//                .build();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .memoryCacheExtraOptions(480, 800)
//                .diskCacheExtraOptions(480, 800, null)
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .defaultDisplayImageOptions(opts)
//                .build();
//        imageLoader = ImageLoader.getInstance();
//        imageLoader.init(config);
//        // Perform injection
//        Injector.init(getRootModule(imageLoader), this, getPackageName());
//        Injector.inject(this);
//
//        FLog.setMinimumLoggingLevel(FLog.ERROR);
//        RestCrashHandler crashHandler = RestCrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
//        // 根据具体 application 进行初始化
//        initCrashlytics();
//        platform.setContext(this.getApplicationContext());
//        eventBus.register(this);
////        platform = new Platform();
//        PackageManager packageManager = getPackageManager();
//        try {
//            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
//            if (packInfo != null) {
//
//                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
//                if (appName.equals("二维火掌柜")) {
//                    appName = "掌柜";
//                } else if (appName.equals("二维火供应链")) {
//                    appName = "供应链";
//                }
//                platform.setAppRealName(appName);
//                platform.setVersionName(packInfo.versionName);
//                platform.setVersionCode(packInfo.versionCode);
//                platform.setPackageName(packInfo.packageName);
//            }
//
//            ApplicationInfo applicationInfo = null;
//            try {
//                packageManager = getApplicationContext().getPackageManager();
//                applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
//            } catch (PackageManager.NameNotFoundException e) {
//                applicationInfo = null;
//            }
//            String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
//            platform.setAppName(applicationName);
//
//        } catch (Exception e) {
//        }
//
//
//        initApiMap();
//
////        preferences = platform.preferences = getPreferences();
////        options = new DisplayImageOptions.Builder()
////                .showStubImage(R.drawable.img_cardbox_b)
////                .showImageOnFail(R.drawable.img_picerror)
////                .resetViewBeforeLoading(true)
////                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
////                .bitmapConfig(Bitmap.Config.RGB_565)
////                .cacheInMemory(true)  //加载图片时会在内存中加载缓存
////                .cacheOnDisc(true)   //加载图片时会在磁盘中加载缓存
////                .displayer(new FadeInBitmapDisplayer(100))
////                .displayer(new RoundedBitmapDisplayer(20))
////                .build();
////        platform.options = options;
////        Debug.stopMethodTracing();
//
//        toRegisterReceiver();
//
//    }
//
//
//
//    /**
//     * 开启线程初始化SDK，避免耗费主线程时间
//     */
//    private void initSDK(){
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                initUmeng();
//                initJpush();
//                Fresco.initialize(BaseApplication.this, ImagePipelineConfigUtils.getDefaultImagePipelineConfig(BaseApplication.this));
//            }
//        }.start();
//    }
//
//    private void initUmeng() {
//        UMShareAPI.get(this);
//        String packageName = getPackageName();
//        if(packageName.equals("zmsoft.rest.supply")){// 供应链的
//            // 友盟分享
//            PlatformConfig.setWeixin("wx6c9dc7640a88c9c2","45c6192711d035e6af04a964d96a3239");
//            PlatformConfig.setQQZone("1105132047","????");
//        }else if(packageName.equals("zmsoft.rest.phone")){// 掌柜的
//            // 友盟分享
//            PlatformConfig.setWeixin("wx9ea214b5b0a4de1e","33482ec38de4aeefb0fccf11f997fe77");
//        }
//        com.umeng.socialize.Config.isJumptoAppStore = false;
//        MobclickAgent.updateOnlineConfig(this);
//        MobclickAgent.setDebugMode(true); //日志开关
//        ApkSecurity.apkIsValid(this);
//    }
//
//
//    private void initJpush(){
//        JPushInterface.setDebugMode(false);    // 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);            // 初始化 JPush
//    }
//
//
//
//    /**
//     * 重启application
//     */
//    public void exitPro() {
//        platform.finishAllLiveActivity();
//        platform.writePreferences(PreferenceConstants.NEED_LOGIN, PreferenceConstants.NEED_LOGIN_YES);
//        platform.preferences.put(PreferenceConstants.NEED_LOGIN, PreferenceConstants.NEED_LOGIN_YES);
//        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
//    }
//
//
//    /**
//     * 重启application
//     */
//    public void restartApp() {
//        Intent intent = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage(getBaseContext().getPackageName());
//        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 1秒钟后重启应用
//        platform.finishAllActivity();
//        android.os.Process.killProcess(android.os.Process.myPid()); //获取PID
//        System.exit(0);
//    }
//
//
//    /**
//     * 监听访问数据库异常处理.
//     *
//     * @param bizExceptionEvent
//     */
////    public void onEvent(BizExceptionEvent bizExceptionEvent) {
////        Log.i("BizExceptionEvent", bizExceptionEvent.getKey() + "|" + bizExceptionEvent.getErrMessage());
////        if (BizExceptionEvent.NO_TOUCH_TIME_OUT.equals(bizExceptionEvent.getKey())
////                || (BizExceptionEvent.QUIT_EVENT.equals(bizExceptionEvent.getKey()))) {
////            exitPro();
////        } else if (BizExceptionEvent.SESSION_TIME_OUT.equals(bizExceptionEvent.getKey())) {
////            if (reLoginUtils == null) {
////                reLoginUtils = new ReLoginUtils();
////            }
////            reLoginUtils.login();
////        } else if (BizExceptionEvent.RESTART_APP.equals(bizExceptionEvent.getKey())) {
////            restartApp();
////        }
////    }
//
//    /**
//     * 监听清楚左边菜单.
//     *
//     * @param leftMenuClearEvent
//     */
//    public void onEvent(LeftMenuClearEvent leftMenuClearEvent) {
//        leftMenuClear();
//    }
//
//    /**
//     * 在oncreate()初始化一些数据如LogUtils.init
//     */
//    public abstract void init();
//
//
//    public abstract void initCrashlytics();
//
//    /**
//     * 判断当前包是否是 release 版
//     */
//    protected abstract boolean isRelease();
//
//    /**
//     * 清理左边菜单,不清理的试好不做任何处理
//     */
//    protected abstract void leftMenuClear();
//
//    public abstract String getHotFixDir();
//
//    /**
//     * RootModule初始化.
//     *
//     * @param imageLoader
//     * @return
//     */
//    protected abstract Object getRootModule(ImageLoader imageLoader);
//
//    public TDFPlatform getPlatform() {
//        return platform;
//    }
//
//
//
//    @NonNull
//    public abstract String getAppkeyValue();
//
//    @NonNull
//    public abstract String getBossApiAppkeyValue();
//
//    /**
//     * Create main application
//     *
//     * @param instrumentation
//     */
//    public BaseApplication(final Instrumentation instrumentation) {
//        this();
//        attachBaseContext(instrumentation.getTargetContext());
//    }
//
//    /**
//     * 得到adroid 缓存值.
//     *
//     * @return
//     */
//    private Hashtable<String, String> getPreferences() {
//        Hashtable<String, String> table = new Hashtable<String, String>();
//        Context context = getApplicationContext();
//        SharedPreferences preference = context.getSharedPreferences(
//                PreferenceConstants.SHOP_SETTING, Context.MODE_PRIVATE);
//        String backGround = preference.getString(PreferenceConstants.BACK_GROUND, PreferenceConstants.BACK_GROUND_01);
//        String userName = "";
//        String userPass = "";
//        String shopname = "";
//        String shopcode = "";
//        String shopId = preference.getString(PreferenceConstants.SHOP_ID, "");
//        String shopPhone = preference.getString(PreferenceConstants.SHOP_PHONE, "");
//        String shopAddress = preference.getString(PreferenceConstants.SHOP_ADDRESS, "");
//        String printMode = preference.getString(PreferenceConstants.PRINT_MODE, "");
//        String serverPath = preference.getString(PreferenceConstants.SERVER_PATH, "");
//        String clusterRoot = preference.getString(PreferenceConstants.CLUSTER_ROOT, "");
//        String menuUnit = preference.getString(PreferenceConstants.MENU_UNIT, getString(R.string.menu_unit_default));
//        String areaEmail = preference.getString(PreferenceConstants.AREA_CODE_EMAIL, "");
//        String needLogin = preference.getString(PreferenceConstants.NEED_LOGIN, PreferenceConstants.NEED_LOGIN_YES);
//        String lastMenuKindMenuId = preference.getString(PreferenceConstants.LAST_MENU_KIND_MENU, "");
//        String lastSuitMenuId = preference.getString(PreferenceConstants.LAST_SUIT_KIND_MENU, "");
//        String printerIP = preference.getString(PreferenceConstants.PRINTER_IP, "");
//        String printerWidth = preference.getString(PreferenceConstants.PRINTER_PAPER_WIDTH, "");
//        String printerCharCount = preference.getString(PreferenceConstants.PRINTER_CHAR_COUNT, "");
//        String RefreshToken = preference.getString(PreferenceConstants.REFRESH_TOKEN, "");
//        String cancelAutoLogin = preference.getString(PreferenceConstants.CANCEL_AUTO_LOGIN, "");
//        String isFirstLogin = preference.getString(PreferenceConstants.IS_FIRST_LOGIN, PreferenceConstants.IS_FIRST);//2.6版本功能大全引导页
//        String isFirstFunction = preference.getString(PreferenceConstants.IS_FIRST_Function, PreferenceConstants.IS_FIRST);//第一次修改功能大全列表
//        String versionName = preference.getString(PreferenceConstants.PRE_VERSION, "");//第一次修改功能大全列表
//        String menuEmail = preference.getString(PreferenceConstants.MENU_CODE_EMAIL, "");
//        String updateStatus = preference.getString(PreferenceConstants.UPDATE_STATUS, PreferenceConstants.FLASE);
//        String codeVersion = preference.getString(PreferenceConstants.CODE_VERSION, "0");
//        String patchVersion = preference.getString(PreferenceConstants.PATCH_VERSION, "0");
//        String lastChangeTime = preference.getString(PreferenceConstants.LAST_ACCOUNT_CHANGE_TIME, "");
//        String lastAccountChangeErrorTime = preference.getString(PreferenceConstants.LAST_ACCOUNT_CHANGE_ERROR_TIME, "");
//        if (!ZmStringUtils.integer2String(platform.getVersionCode()).equals(codeVersion)) {
//            patchVersion = "0";
//        }
//        table.put(PreferenceConstants.BACK_GROUND, backGround);
//        table.put(PreferenceConstants.USER_NAME, userName);
//        table.put(PreferenceConstants.USER_PASS, userPass);
//        table.put(PreferenceConstants.SHOP_CODE, shopcode);
//        table.put(PreferenceConstants.SHOP_NAME, shopname);
//        table.put(PreferenceConstants.SHOP_ID, shopId);
//        table.put(PreferenceConstants.SHOP_PHONE, shopPhone);
//        table.put(PreferenceConstants.SHOP_ADDRESS, shopAddress);
//        table.put(PreferenceConstants.PRINT_MODE, printMode);
//        table.put(PreferenceConstants.SERVER_PATH, serverPath);
//        table.put(PreferenceConstants.CLUSTER_ROOT, clusterRoot);
//        table.put(PreferenceConstants.MENU_UNIT, menuUnit);
//        table.put(PreferenceConstants.AREA_CODE_EMAIL, areaEmail);
//        table.put(PreferenceConstants.NEED_LOGIN, needLogin);
//        table.put(PreferenceConstants.LAST_MENU_KIND_MENU, lastMenuKindMenuId);
//        table.put(PreferenceConstants.LAST_SUIT_KIND_MENU, lastSuitMenuId);
//        table.put(PreferenceConstants.PRINTER_IP, printerIP);
//        table.put(PreferenceConstants.PRINTER_PAPER_WIDTH, printerWidth);
//        table.put(PreferenceConstants.PRINTER_CHAR_COUNT, printerCharCount);
//        table.put(PreferenceConstants.REFRESH_TOKEN, RefreshToken);
//        table.put(PreferenceConstants.CANCEL_AUTO_LOGIN, cancelAutoLogin);
//        table.put(PreferenceConstants.IS_FIRST_LOGIN, isFirstLogin);
//        table.put(PreferenceConstants.IS_FIRST_Function, isFirstFunction);
//        table.put(PreferenceConstants.PRE_VERSION, versionName);
//        table.put(PreferenceConstants.MENU_CODE_EMAIL, menuEmail);
//        table.put(PreferenceConstants.UPDATE_STATUS, updateStatus);
//        table.put(PreferenceConstants.PATCH_VERSION, patchVersion);
//        table.put(PreferenceConstants.CODE_VERSION, codeVersion);
//        table.put(PreferenceConstants.LAST_ACCOUNT_CHANGE_TIME, lastChangeTime);
//        table.put(PreferenceConstants.LAST_ACCOUNT_CHANGE_ERROR_TIME, lastAccountChangeErrorTime);
//        platform.setServerPath(serverPath);
//        return table;
//    }
//
//    public SupplyPlatform getSupplyPlatform() {
//        return supplyPlatform;
//    }
//    @Override
//    public void onClick(View view) {
//        eventBus.post(new ResidentMenuShowEvent(ResidentMenuShowEvent.RESIDENT_MENU_SHOW_LEFT));
//    }
//
//    public void handlerRemoteMessage(final String extra, final String message) {//判断entityid 是否一样，一样就踢掉，不一样就当做没发生直接过滤消息。
//        try {
//
//            MessageType messageType = objectMapper.readValue(extra, MessageType.class);
//            MessageVo vo = objectMapper.readValue(extra, MessageVo.class);
//            LogUtils.d("aaaaaaaa:type" + messageType.getType() + extra);
//            if (MessageType.TYPE_SYS_NOTIFICATION.equals(messageType.getType())) {
//                SysNotificationVo sysNotificationVoTemp = objectMapper.readValue(message, SysNotificationVo.class);
//                if (sysNotificationVoTemp != null) {
//                    //处理消息.
//                    platform.getSysNotificationVo().setTitle(sysNotificationVoTemp.getTitle());
//                    platform.getSysNotificationVo().setCount(sysNotificationVoTemp.getCount());
//                    eventBus.post(new SystemNotificationEvent(PreferenceConstants.SYSTEM_NOTIFICATION));
//                    platform.setJpushflag(true);
//                }
//            } else if (MessageType.TYPE_UNBIND_NOTIFICATION.equals(messageType.getType())) {
//                //下线通知
//                platform.setJpushUnbind(false);
//                platform.setJpushMessage(message);
//                getEventBus().post(new UnBindNotificationEvent("", message));
//
//            }
//            if (!TextUtils.isEmpty(vo.getMessageSubscId())) {
//                getEventBus().post(vo);
//                Map<String, Object> map = new HashMap<>();
//                map.put("num", vo.getUnreadCount());
//                SupplySubject.getInstance().change(map, ObserverKeys.MESSAGE_CENTER_PUSH_MESSAGE);
//            }
//
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//    }
//
//    @Override
//    public Map<String, String> getSignMap() {
//        return platform.getApiParams();
//    }
//
//    @Override
//    public String getCurrentViewID() {
//        return platform.getCurrentViewID();
//    }
//
//    @Override
//    public JsonUtils getJsonUtils() {
//        return jsonUtils;
//    }
//
//    @Override
//    public EventBus getEventBus() {
//        return eventBus;
//    }
//
//    /**
//     * 打印日志情况
//     */
//    @Override
//    public boolean getPrintStatus() {
//        return true;
//    }
//
//    /**
//     * 得到当前token.
//     */
//    @Override
//    public String getCurrentToken() {
//        return platform.getToken();
//    }
//
//    @Override
//    public Context getAppContext() {
//        return this;
//    }
//
//    /**
//     * 得到共用scheme
//     */
//    @Override
//    public String getCommonScheme() {
//        return NavigationControlConstants.SCHEME_COMMON;
//    }
//
//    @Override
//    public Map<String, String> getTDFSignMap() {
//        return platform.getApiParams();
//    }
//
//    @Override
//    public String getTDFCurrentViewID() {
//        return platform.getCurrentViewID();
//    }
//
//    @Override
//    public boolean getTDFPrintStatus() {
//        return true;
//    }
//
//    @Override
//    public String getTDFToken() {
//        return platform.getToken();
//    }
//
//    @Override
//    public Context getTDFContext() {
//        return this;
//    }
//
//    @Override
//    public Map<String, String> getLoginSignMap() {
//        return platform.getApiParams();
//    }
//
//    @Override
//    public Map<String, String> getLoginGatewaySignMap() {
//        return platform.getGatewayParams();
//    }
//
//    @Override
//    public String getLoginCurrentViewID() {
//        return platform.getCurrentViewID();
//    }
//
//    @Override
//    public tdf.zmsoft.login.manager.login.service.retrofit.JsonUtils getLoginJsonUtils() {
//        return LoginRegister.getJsonUtils();
//    }
//
//    @Override
//    public String getLoginCurrentApiRoot(Integer serviceType) {
//        if (AbstractApiService.SUPPLYCHAIN_API.equals(serviceType)) {
//            return TDFServiceUrlUtils.getCurrentUrl(TDFServiceUrlUtils.SUPPLYCHAIN_API_ROOT);
//        } else if (AbstractApiService.BOSS_API.equals(serviceType)) {
//            return TDFServiceUrlUtils.getCurrentUrl(TDFServiceUrlUtils.BOSS_API_ROOT);
//        } else if (AbstractApiService.DATA_RECORD_ROOT.equals(serviceType)) {
//            return TDFServiceUrlUtils.getCurrentUrl(TDFServiceUrlUtils.DATA_RECORD_ROOT);
//        } else if (AbstractApiService.MOCK_API.equals(serviceType)) {
//            return TDFConfig.MOCK_SERVICE;
//        } else if (AbstractApiService.INTEGRAL_API.equals(serviceType)){
//            return TDFServiceUrlUtils.getCurrentUrl(TDFServiceUrlUtils.INTEGRAL_API_ROOT);
//        } else {
//            return TDFServiceUrlUtils.getCurrentUrl(TDFServiceUrlUtils.API_ROOT);
//        }
//    }
//
//    @Override
//    public String getLoginGatewayUrl() {
//        return TDFServiceUrlUtils.getGatewayURL();
//    }
//
//    @Override
//    public String getLoginGatewayEnv() {
//        return TDFServiceUrlUtils.getGatewayEvn();
//    }
//
//    @Override
//    public org.greenrobot.eventbus.EventBus getLoginEventBus() {
//        return LoginRegister.getEventBus();
//    }
//
//    @Override
//    public boolean getLoginPrintStatus() {
//        return true;
//    }
//
//    @Override
//    public String getLoginCurrentToken() {
//        return platform.getToken();
//    }
//
//    @Override
//    public Context getLoginAppContext() {
//        return this;
//    }
//
//    @Override
//    public String getLoginSecret() {
//        return ApkSecurity.decryptString(LoginApiConstants.SECRET_VALUE);
//    }
//
//    @Override
//    public String getLoginNewSecret() {
//        return LoginApiConstants.SIGNATURE_SECRET_VALUE;
//    }
//
//    @Override
//    public String getLoginGatewaySecret() {
//        return LoginApiConstants.SIGNATURE_GATEWAY_SECRET_VALUE;
//    }
//
//    /**
//     * 写入首选项,设置系统参数.
//     *
//     * @param name
//     * @param val
//     */
//    public void writePreferences(String name, String val) {
//        writePreferences(PreferenceConstants.SHOP_SETTING, name, val);
//    }
//
//    /**
//     * 写入首选项可以指定类型.
//     *
//     * @param type
//     * @param name
//     * @param val
//     */
//    public void writePreferences(String type, String name, String val) {
//        Context context = getApplicationContext();
//        SharedPreferences preference = context.getSharedPreferences(
//                type, Context.MODE_PRIVATE);
//        Editor editor = preference.edit();
//        editor.putString(name, val);
//        editor.commit();
//    }
//
//
//    private void initCustomerService() {
////        MQManager.setDebugMode(true);
////
////        // 替换成自己的key
////        String meiqiaKey = ApkSecurity.decryptString(ApiConstants.SECRET_VALUE_CUSTOMER);
////        MQConfig.init(this, meiqiaKey, new OnInitCallback() {
////            @Override
////            public void onSuccess(String clientId) {
////
//////                Toast.makeText(BaseApplication.this, "init success", Toast.LENGTH_SHORT).show();
////            }
////
////            @Override
////            public void onFailure(int code, String message) {
//////                Toast.makeText(BaseApplication.this, "int failure message = " + message, Toast.LENGTH_SHORT).show();
////            }
////        });
//        // 可选
////        customMeiqiaSDK();
//    }
//
//    private void customMeiqiaSDK() {
//        // 配置自定义信息
////        MQConfig.ui.titleGravity = MQConfig.ui.MQTitleGravity.CENTER;
//////        MQConfig.ui.backArrowIconResId =R.drawable.ico_cancel;
//////        MQConfig.ui.titleBackgroundResId = R.color.test_red;
//////        MQConfig.ui.titleTextColorResId = R.color.test_blue;
//////        MQConfig.ui.leftChatBubbleColorResId = R.color.test_green;
//////        MQConfig.ui.leftChatTextColorResId = R.color.test_red;
//////        MQConfig.ui.rightChatBubbleColorResId = R.color.test_red;
//////        MQConfig.ui.rightChatTextColorResId = R.color.test_green;
//////        MQConfig.ui.robotEvaluateTextColorResId = R.color.test_red;
//////        MQConfig.ui.robotMenuItemTextColorResId = R.color.test_blue;
//////        MQConfig.ui.robotMenuTipTextColorResId = R.color.test_blue;
////
////
////        // 自定义留言表单引导文案，配置了该引导文案后将不会读取工作台配置的引导文案
////        MQConfig.leaveMessageIntro = "自定义留言表单引导文案";
////
////        // 初始化自定义留言表单字段，如果不配置该选项则留言表单界面默认有留言、邮箱、手机三个输入项
////        MQConfig.messageFormInputModels = new ArrayList<>();
////        MessageFormInputModel phoneMfim = new MessageFormInputModel();
////        phoneMfim.tip = "手机";
////        phoneMfim.key = "tel";
////        phoneMfim.required = true;
////        phoneMfim.hint = "请输入你的手机号";
////        phoneMfim.inputType = InputType.TYPE_CLASS_PHONE;
////
////        MessageFormInputModel emailMfim = new MessageFormInputModel();
////        emailMfim.tip = "邮箱";
////        emailMfim.key = "email";
////        emailMfim.required = true;
////        emailMfim.hint = "请输入你的邮箱";
////        emailMfim.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
////
////        MessageFormInputModel nameMfim = new MessageFormInputModel();
////        nameMfim.tip = "姓名";
////        nameMfim.key = "name";
////        nameMfim.hint = "请输入你的姓名";
////        nameMfim.inputType = InputType.TYPE_CLASS_TEXT;
////
////        MessageFormInputModel customMfim = new MessageFormInputModel();
////        customMfim.tip = "自定义";
////        customMfim.key = "自定义";
////        customMfim.hint = "请输入你的自定义信息";
////        customMfim.singleLine = false;
////        customMfim.inputType = InputType.TYPE_CLASS_TEXT;
////
////
////        MQConfig.messageFormInputModels.add(phoneMfim);
////        MQConfig.messageFormInputModels.add(emailMfim);
////        MQConfig.messageFormInputModels.add(nameMfim);
////        MQConfig.messageFormInputModels.add(customMfim);
//
////        IntentFilter intentFilter = new IntentFilter();
////        intentFilter.addAction(MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED);
////        intentFilter.addAction(MQMessageManager.ACTION_AGENT_CHANGE_EVENT);
//
//
//
////        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);
//
//    }
//
//
//
//}
