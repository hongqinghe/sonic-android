package com.tencent.sonic.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.sonic.R;


/**
 * Created by 李锦运 on 2015/1/15.
 */
public class ProcessDialogUtils {

    private static Dialog loadingDialog;

    private static boolean isinit = false;
    static TextView tipTextView;
    private static Context context ;


    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static void creatAndShow(Context context, String msg) {
        ProcessDialogUtils.context = context;
        if(isValidContext(context)){
            if (!isinit) {
                isinit = false;
                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.customprogressdialog_view, null);// 得到加载view
                LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
                // main.xml中的ImageView
                ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
                tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
                // 加载动画
                Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                        context, R.anim.loading_animation);
                // 使用ImageView显示动画
                spaceshipImage.startAnimation(hyperspaceJumpAnimation);

                loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
                loadingDialog.setCancelable(false);// 不可以用“返回键”取消
                loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.FILL_PARENT));// 设置布局

            }
            tipTextView.setText(msg);// 设置加载信息
            loadingDialog.show();
        }
    }

    /**
     * 消失.
     */
    public static void dismessDialog() {
        if (loadingDialog != null && context!=null && loadingDialog.isShowing()) {
            if(isValidContext(context)) {
                loadingDialog.dismiss();
            }
        }
    }

    private static boolean  isValidContext (Context c){
        Activity a = (Activity)c;
        if(Build.VERSION.SDK_INT >= 17){
            if (a.isDestroyed() || a.isFinishing()){
                return false;
            }else{
                return true;
            }
        }else{
            if (a.isFinishing()){
                return false;
            }else{
                return true;
            }
        }
    }

    /**
     * 可以点击物理返回键.
     */
    public static void setCancelable() {
        if (loadingDialog != null && context!=null) {
            if(isValidContext(context)) {
                loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                            dismessDialog();
                            Activity activity = (Activity)context;
                            activity.finish();
                        }
                        return false;
                    }
                });
            }
        }
    }
}

