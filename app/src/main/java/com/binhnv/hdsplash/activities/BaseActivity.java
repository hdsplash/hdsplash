package com.binhnv.hdsplash.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.binhnv.hdsplash.R;
import com.binhnv.hdsplash.widget.SystemBarTintManager;

import java.lang.reflect.Field;

/**
 * Created by sev_user on 2/1/2016.
 */
public class BaseActivity  extends Activity implements View.OnClickListener {

    protected final static String LOG_TAG = "app lock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*AppLockApplication.getInstance().doForCreate(this);
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("finish");
        registerReceiver(mFinishReceiver, filter);*/
        super.onCreate(savedInstanceState);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(getResources().getColor(
                R.color.lock_status_alpha));
    }

    @Override
    protected void onDestroy() {
        //unregisterReceiver(mFinishReceiver);
        super.onDestroy();
    }

    @Override
    public void finish() {
        //LogUtil.d("demo3", "finish:" + this.getClass());
        //AppLockApplication.getInstance().doForFinish(this);
        super.finish();
    }

    public final void clear() {
        super.finish();
    }

    /**
     * onClickEvent
     */
    public void onClickEvent(View view) {

    }

    /**
     * OnClickListener
     */
    @Override
    public void onClick(View v) {

    }

    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("finish")) {
                //LogUtil.e("colin", "to finish and close activity");
                finish();
            }
        }

    };

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    private static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    protected void setStatusBarMargin(View view) {
        if (Build.VERSION.SDK_INT < 19 || view == null
                || view.getLayoutParams() == null) {
            return;
        }
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view
                    .getLayoutParams();
            lp.topMargin = lp.topMargin + getStatusBarHeight(this);
            view.requestLayout();
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view
                    .getLayoutParams();
            lp.topMargin = lp.topMargin + getStatusBarHeight(this);
            view.requestLayout();
        } else if (view.getLayoutParams() instanceof DrawerLayout.LayoutParams) {
            DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) view
                    .getLayoutParams();
            lp.topMargin = lp.topMargin + getStatusBarHeight(this);
            view.requestLayout();
        }

    }

}