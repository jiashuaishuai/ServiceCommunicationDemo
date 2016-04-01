package com.example.jiashuaishuai.servicecommunicationdemo.ServiceToActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jiashuaishuai on 2016/4/1.
 */
public class MyService extends Service {
    private ServiceToActivityCallBack serviceToActivityCallBack;

    public static final int GET_PROGRESS = 0xaa;
    /**
     * 计时器
     */
    private Timer timer = new Timer();
    private int progress;
    /**
     * 实例化内部类，onBind返回给Activity
     */
    private LoackBinder loackBinder = new LoackBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return loackBinder;
    }

    /**
     * 内部类，返回Service本身
     */
    public class LoackBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    /**
     * 接口回调方式实现
     */
    public void getMyServiceProgress() {
        /**
         * 计时器工作线程
         */
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                progress++;
                if (progress > 100) {
                    /**
                     *停止计时，
                     */
                    timer.cancel();
                } else {
/**
 * 接口回调方式，实现
 */
                    if (serviceToActivityCallBack != null)
                        serviceToActivityCallBack.progressCall(progress);
                    Log.i("MyService", "接口回调方式：test+++" + progress);
                }
            }
        };
        /**
         * 启动计时器，计时器线程，每个多少秒执行一次，执行时间
         */
        timer.schedule(timerTask, 100, 100);

    }

    public void getMyServiceProgress(final Handler handl) {
        /**
         * 计时器工作线程
         */
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                progress++;
                if (progress > 100) {
                    /**
                     *停止计时，
                     */
                    timer.cancel();
                } else {
/**
 * 接口回调方式，实现
 */


                    Message message = handl.obtainMessage();
                    message.what = GET_PROGRESS;
                    message.obj = progress;
                    handl.sendMessage(message);
                    Log.i("MyService", "Handl方式：test+++" + progress);
                }
            }
        };
        /**
         * 启动计时器，计时器线程，每个多少秒执行一次，执行时间
         */
        timer.schedule(timerTask, 100, 100);
    }

    /**
     * 设置progress的借口回调
     *
     * @param serviceToActivityCallBack
     */
    public void setProgressCallBack(ServiceToActivityCallBack serviceToActivityCallBack) {
        this.serviceToActivityCallBack = serviceToActivityCallBack;
    }

    /**
     * eventbus实现
     */
    private ServiceToActivityEventType type = new ServiceToActivityEventType();

    public void getProgressEventBus() {
        /**
         * 计时器工作线程
         */
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                progress++;
                if (progress > 100) {
                    /**
                     *停止计时，
                     */
                    timer.cancel();
                } else {
/**
 * eventbus实现
 */
                    type.progress = progress;
                    EventBus.getDefault().post(type);
                    Log.i("Service", "eventbus：" + progress);

                }
            }
        };
        /**
         * 启动计时器，计时器线程，每个多少秒执行一次，执行时间
         */
        timer.schedule(timerTask, 100, 100);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
