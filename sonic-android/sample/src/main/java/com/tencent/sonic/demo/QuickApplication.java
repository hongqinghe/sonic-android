//package com.tencent.sonic.demo;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.Point;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//import android.view.Display;
//import android.view.View;
//import android.view.WindowManager;
//
//import com.dfire.sdk.util.MD5Util;
//import com.dodola.rocoofix.RocooFix;
//import com.facebook.common.logging.FLog;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jiongbull.jlog.JLog;
//import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.sobot.chat.utils.ZhiChiConstant;
//import com.tencent.bugly.crashreport.CrashReport;
//import com.zmsoft.constants.ApiConstants;
//import com.zmsoft.constants.CommonConstants;
//import com.zmsoft.constants.PreferenceConstants;
//import com.zmsoft.constants.SupplyPlatform;
//import com.zmsoft.crypto.ApkSecurity;
//import com.zmsoft.event.BizExceptionEvent;
//import com.zmsoft.event.LeftMenuClearEvent;
//import com.zmsoft.event.UnReadMessageEvent;
//import com.zmsoft.handler.RestCrashHandler;
//import com.zmsoft.rest.commonview.application.BaseApplication;
//import com.zmsoft.retrofit.JsonUtils;
//import com.zmsoft.utils.ConvertUtils;
//
//import java.io.File;
//import java.util.Hashtable;
//
//import javax.inject.Inject;
//
//import de.greenrobot.event.EventBus;
//import tdf.zmsfot.utils.AppUtilsContextWrapper;
//import tdf.zmsoft.charge.TDFChargeUtils;
//import tdf.zmsoft.chargecore.protocol.ChargeApi;
//import tdf.zmsoft.core.base.TDFBaseApplication;
//import tdf.zmsoft.core.base.TDFPlatform;
//import tdf.zmsoft.login.constants.LoginAccess;
//import tdf.zmsoft.login.manager.login.service.listener.ILoginNetWork;
//import tdf.zmsoft.login.manager.login.utils.ReLoginUtils;
//import tdf.zmsoft.navigation.ARouterManager;
//import tdf.zmsoft.network.TDFNetWork;
//import tdf.zmsoft.network.utils.ServiceUtils;
//import tdfire.supply.basemoudle.R;
//import tdfire.supply.basemoudle.base.config.Config;
//import tdfire.supply.basemoudle.base.module.RootModule;
//import tdfire.supply.basemoudle.utils.ServiceUrlUtils;
//import tdfire.supply.basemoudle.utils.jump.URLJumpControl;
//import zmsoft.rest.navigation.NavigationControlConstants;
//import zmsoft.rest.phone.config.INavigation;
//import zmsoft.rest.phone.config.INetWork;
//import zmsoft.rest.phone.config.Injector;
//import zmsoft.share.service.AbstractApiService;
//import zmsoft.share.service.ReflexUtils;
//import zmsoft.share.service.errservice.ErrNetWorkService;
//import zmsoft.share.utils.FileUtils;
//import zmsoft.share.utils.LogUtils;
//import zmsoft.share.utils.NetWorkUtils;
//import zmsoft.share.utils.record.DataRecordUtils;
//
//
///**
// * Created by tme on 2014/10/13.
// */
//public class QuickApplication extends TDFBaseApplication implements View.OnClickListener, INetWork, INavigation, TDFNetWork, ILoginNetWork {
//
//    @Inject
//    EventBus eventBus;
//    @Inject
//    JsonUtils jsonUtils;
//    @Inject
//    ObjectMapper objectMapper;
//
//    private MessageReceiver messageReceiver;
//
//    private ReLoginUtils reLoginUtils;
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
//    public static String TAG = "rest_phone_manager";
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
//    /**
//     * 在oncreate()初始化一些数据如LogUtils.init
//     */
//    @Override
//    public void init() {
//        if (ReflexUtils.getBuildConfigValue(this, "ENABLE_DEBUG") != null) {
//            LogUtils.init(TAG, (boolean) ReflexUtils.getBuildConfigValue(this, "ENABLE_DEBUG"));
//        }
//        JLog.init(this).setTimeFormat("yyyy年MM月dd日 HH时mm分ss秒 ").setDebug(false).writeToFile(true).setLogDir(getString(R.string.app_name_path));//用于闪退记录bug
//        // 埋点管理初始化
//        if (ReflexUtils.getBuildConfigValue(this, "BUILD_ENVIRONMENT") != null) {
//            DataRecordUtils.init(getPackageName(), (int) ReflexUtils.getBuildConfigValue(this, "BUILD_ENVIRONMENT"));
//        }
//        URLJumpControl.init();
//
//        //utils 初始化
//        AppUtilsContextWrapper.init(getApplicationContext());
//    }
//
////    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        instance = this;
//        platform = TDFPlatform.getInstance();
//        supplyPlatform = new SupplyPlatform();
//
//        // 线上环境初始化Bugly
//        if (platform != null && platform.getBuild_environment() == 4) {
//            initBugly();
//        }
//
//        /**
//         * 接入登录模块
//         */
//        LoginAccess.init(getApplicationContext(), this);
//        LoginAccess.initLoginGatewayMap(platform, platform.getVersionName(), getBossApiAppkeyValue());
//
//        ChargeApi.initRegister();
//        TDFChargeUtils.init(getPlatform(), new ServiceUtils(this));
//
//        ARouterManager.init(this, true);
//
//        //errNetWorkService
//        Intent errNetWorkService = new Intent(this, ErrNetWorkService.class);
//        startService(errNetWorkService);
//
//
//        DisplayImageOptions opts = new DisplayImageOptions.Builder()
////                .showImageOnLoading(R.drawable.process)
//                .showStubImage(com.zmsoft.rest.commonview.R.drawable.img_cardbox_b)
//                .showImageOnFail(com.zmsoft.rest.commonview.R.drawable.img_picerror)
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
//        // RestCrashHandler
//        FLog.setMinimumLoggingLevel(FLog.ERROR);
//        RestCrashHandler crashHandler = RestCrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
//
//        eventBus.register(this);
//        initPlatform();
//
//        initApiMap();
//
//        toRegisterReceiver();
//    }
//
//    //    @Override
////    public void initCrashlytics() {
////
//
//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        eventBus.unregister(this);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
//    }
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
//    public void onEvent(BizExceptionEvent bizExceptionEvent) {
//        Log.i("BizExceptionEvent", bizExceptionEvent.getKey() + "|" + bizExceptionEvent.getErrMessage());
//        if (BizExceptionEvent.NO_TOUCH_TIME_OUT.equals(bizExceptionEvent.getKey())
//                || (BizExceptionEvent.QUIT_EVENT.equals(bizExceptionEvent.getKey()))) {
//            exitPro();
//        } else if (BizExceptionEvent.SESSION_TIME_OUT.equals(bizExceptionEvent.getKey())) {
//            if (reLoginUtils == null) {
//                reLoginUtils = new ReLoginUtils();
//            }
//            reLoginUtils.login();
//        } else if (BizExceptionEvent.RESTART_APP.equals(bizExceptionEvent.getKey())) {
//            restartApp();
//        }
//    }
//    private void initPlatform() {
//        platform.setContext(this.getApplicationContext());
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
//    }
//
//    public TDFPlatform getPlatform() {
//        return platform;
//    }
//
//
//    /**
//     * 注册广播
//     */
//    private void toRegisterReceiver() {
//        messageReceiver = new QuickApplication().MessageReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
//        registerReceiver(messageReceiver, filter);
//    }
//
//    private void initApiMap() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                Point size = new Point();
//                WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
//                Display display = wm.getDefaultDisplay();
//                display.getSize(size);
//                int width = size.x;
//                int height = size.y;
//
//                platform.getApiParams().put(ApiConstants.S_OS, ApiConstants.S_OS_VALUE);
//                platform.getApiParams().put(ApiConstants.S_OSV, ConvertUtils.toString(android.os.Build.VERSION.SDK_INT));
//                platform.getApiParams().put(ApiConstants.S_APV, platform.getVersionName());
//                platform.getApiParams().put(ApiConstants.S_NET, ApiConstants.S_NET_VALUE_LINE);
//                platform.getApiParams().put(ApiConstants.S_SC, width + "*" + height);
//                platform.getApiParams().put(ApiConstants.S_BR, android.os.Build.MODEL);
//                platform.getApiParams().put(ApiConstants.S_EID, "00000123");
//                platform.getApiParams().put(ApiConstants.SESSION_KEY, "");
//                platform.getApiParams().put(ApiConstants.S_TK, "");
////        platform.getApiParams().put(ApiConstants.S_UID,"");
//                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//                platform.getApiParams().put(ApiConstants.S_DID, MD5Util.encode(androidId));
//                platform.getApiParams().put(ApiConstants.FORMAT, ApiConstants.FORMAT_VALUE);
//                platform.getApiParams().put(ApiConstants.VERSION_CODE, String.valueOf(platform.getVersionCode()));
//                platform.getApiParams().put(ApiConstants.APPKEY, getAppkeyValue());
//                platform.getApiParams().put(ApiConstants.APPKEY_BOSS, getBossApiAppkeyValue());
//                platform.setAppKey(getBossApiAppkeyValue());
//                platform.setSignAppSecret(getSecret());
//                platform.getApiParams().put(ApiConstants.CLIENT_IP, NetWorkUtils.getLocalIpAddress());
//                platform.getApiParams().put(ApiConstants.MAC, NetWorkUtils.getMac(QuickApplication.this));
//                preferences = platform.preferences = getPreferences();
//                options = new DisplayImageOptions.Builder()
//                        .showStubImage(com.zmsoft.rest.commonview.R.drawable.img_cardbox_b)
//                        .showImageOnFail(com.zmsoft.rest.commonview.R.drawable.img_picerror)
//                        .resetViewBeforeLoading(true)
//                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//                        .bitmapConfig(Bitmap.Config.RGB_565)
//                        .cacheInMemory(true)  //加载图片时会在内存中加载缓存
//                        .cacheOnDisc(true)   //加载图片时会在磁盘中加载缓存
//                        .displayer(new FadeInBitmapDisplayer(100))
//                        .displayer(new RoundedBitmapDisplayer(20))
//                        .build();
//                platform.options = options;
//            }
//        }.start();
//    }
//
//
//    //腾讯Bugly的集成
//    private void initBugly() {
//        /*//Bugly在启动5S后联网同步数据
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
//        strategy.setAppReportDelay(5000);*/
//        //9c15381a97，，第三参数为调试模式开关
//        CrashReport.initCrashReport(getApplicationContext(), "9c15381a97", false);
//    }
//
//
//    @Override
//    protected boolean isRelease() {
//        if (ReflexUtils.getBuildConfigValue(this, "BUILD_ENVIRONMENT") != null) {
//            return (int) ReflexUtils.getBuildConfigValue(this, "BUILD_ENVIRONMENT") == 4;
//        }
//        return false;
//    }
//
//    /**
//     * 清理左边菜单,不清理的试好不做任何处理
//     */
////    @Override
//    protected void leftMenuClear() {
//    }
//    /**
//     * 监听清楚左边菜单.
//     *
//     * @param leftMenuClearEvent
//     */
//    public void onEvent(LeftMenuClearEvent leftMenuClearEvent) {
//        leftMenuClear();
//    }
//    @Override
//    public String getHotFixDir() {
//        return CommonConstants.FILE_DIR_SUPPLY;
//    }
//
//    /**
//     * RootModule初始化.
//     *
//     * @param imageLoader
//     * @return
//     */
//    @Override
//    protected Object getRootModule(ImageLoader imageLoader) {
//        RootModule rootModule = new RootModule(imageLoader);
//        return rootModule;
//    }
//
//    @NonNull
//    @Override
//    public String getAppkeyValue() {
//        return ApiConstants.APPKEY_OLD_VALUE_SUPPLY;
//    }
//
//    @NonNull
//    @Override
//    public String getBossApiAppkeyValue() {
//        return ApiConstants.APPKEY_VALUE_SUPPLY;
//    }
//
//    public static QuickApplication getInstance() {
//        return (QuickApplication) instance;
//    }
//
//    /**
//     * 得到所需的Scheme
//     *
//     * @return
//     */
//    @Override
//    public String getCurrentScheme() {
//        return NavigationControlConstants.SCHEME_SUPPLYCHAIN;
//    }
//
//    /**
//     * 得到当前的Secret
//     */
//    @Override
//    public String getSecret() {
//        return ApkSecurity.decryptString(ApiConstants.SECRET_VALUE_SUPPLY);
//    }
//
//    @Override
//    public String getNewSecret() {
//        return ApiConstants.SIGNATURE_SECRET_SUPPLY_VALUE;
//    }
//
//    @Override
//    public String getCurrentApiRoot(Integer serviceType) {
//        if (AbstractApiService.SUPPLYCHAIN_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.SUPPLYCHAIN_API_ROOT);
//        } else if (AbstractApiService.BOSS_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.BOSS_API_ROOT);
//        } else if (AbstractApiService.DATA_RECORD_ROOT.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.DATA_RECORD_ROOT);
//        } else if (AbstractApiService.MOCK_API.equals(serviceType)) {
//            return Config.MOCK_SERVICE;
//        } else if (AbstractApiService.DMALL_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.DMALL_API_ROOT);
//        } else if (AbstractApiService.INTEGRAL_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.INTEGRAL_API_ROOT);
//        } else {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.API_ROOT);
//        }
//    }
//
//    @Override
//    public String getTDFSecret() {
//        return ApkSecurity.decryptString(ApiConstants.SECRET_VALUE_SUPPLY);
//    }
//
//    @Override
//    public String getTDFNewSecret() {
//        return ApiConstants.SIGNATURE_SECRET_SUPPLY_VALUE;
//    }
//
//    @Override
//    public String getTDFCurrentApi(Integer serviceType) {
//        if (AbstractApiService.SUPPLYCHAIN_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.SUPPLYCHAIN_API_ROOT);
//        } else if (AbstractApiService.BOSS_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.BOSS_API_ROOT);
//        } else if (AbstractApiService.DATA_RECORD_ROOT.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.DATA_RECORD_ROOT);
//        } else if (AbstractApiService.MOCK_API.equals(serviceType)) {
//            return Config.MOCK_SERVICE;
//        } else if (AbstractApiService.DMALL_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.DMALL_API_ROOT);
//        } else if (AbstractApiService.INTEGRAL_API.equals(serviceType)) {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.INTEGRAL_API_ROOT);
//        } else {
//            return ServiceUrlUtils.getCurrentUrl(ServiceUrlUtils.API_ROOT);
//        }
//    }
//
//
//    public class MessageReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            int noReadNum = intent.getIntExtra("noReadCount", 0);
//            String content = intent.getStringExtra("content"); //未读消息数
//
//            if (noReadNum > 0) {
//                eventBus.post(new UnReadMessageEvent(noReadNum));
//            }
//
////            // 获取 ACTION
////            final String action = intent.getAction();
////            Log.e("cy", "action ==" + action);
////            // 接收新消息
////            if (MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED.equals(action)) {
////                // 从 intent 获取消息 id
//////                String msgId = intent.getStringExtra("msgId");
//////                // 从 MCMessageManager 获取消息对象
//////                MQMessageManager messageManager = MQMessageManager.getInstance(context);
//////                final MQMessage message = messageManager.getMQMessage(msgId);
////                MQManager.getInstance(context).getUnreadMessages(new OnGetMessageListCallback() {
////                    @Override
////                    public void onSuccess(List<MQMessage> messageList) {
////                        Logger.e("当前有离线消息 = " + messageList.size());
////                        if (messageList.size() > 0) {
////                            eventBus.post(new UnReadMessageEvent(messageList.size()));
////                        }
////                    }
////
////                    @Override
////                    public void onFailure(int code, String message) {
////                    }
////                });
//            // do something
////            }
//        }
//    }
//}