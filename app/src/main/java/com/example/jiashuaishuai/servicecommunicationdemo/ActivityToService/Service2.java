package com.example.jiashuaishuai.servicecommunicationdemo.ActivityToService;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.jiashuaishuai.servicecommunicationdemo.ServiceToActivity.ServiceToActivityCallBack;
import com.example.jiashuaishuai.servicecommunicationdemo.ServiceToActivity.ServiceToActivityEventType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by jiashuaishuai on 2016/4/1.
 */
public class Service2 extends Service {

    public static final int SEND_PROGRESS = 0xbb;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                /**
                 * 使用Messenger方式，客户端请求服务器。
                 */
                case SEND_PROGRESS:
                    Log.i("Service2", "客户端请求成功Code：" + msg.obj);
                    break;
            }
        }
    };
    private Messenger mMessenger = new Messenger(handler);

    /**
     * EventBus接受代码
     *
     * @param type
     */
    @Subscribe
    public void onEventMainTread(ServiceToActivityEventType type) {
        Log.i("Service2", "EventBus接受成功Code：" + type.progress);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }


    ActivityToServiceDemo activityToServiceDemo = new ActivityToServiceDemo();

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 接收端注册
         */
        EventBus.getDefault().register(this);

/**
 * 接口回调方式不成功******************
 */
        activityToServiceDemo.setServiceToActivityCallBack(new ServiceToActivityCallBack() {
            @Override
            public void progressCall(int progress) {
                Log.i("Service2", "接口回调请求服务器成功Code：" + progress);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 解除
         */
        EventBus.getDefault().unregister(this);
    }

    public class BinderType {
        public Service2 getService() {
            return Service2.this;
        }
    }
}
