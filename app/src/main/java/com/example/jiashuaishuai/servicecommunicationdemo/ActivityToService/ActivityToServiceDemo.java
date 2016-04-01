package com.example.jiashuaishuai.servicecommunicationdemo.ActivityToService;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jiashuaishuai.servicecommunicationdemo.R;
import com.example.jiashuaishuai.servicecommunicationdemo.ServiceToActivity.ServiceToActivityCallBack;
import com.example.jiashuaishuai.servicecommunicationdemo.ServiceToActivity.ServiceToActivityEventType;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 实现思路：
 * 1.使用Messenger，Service端声明，然后通过onBind方法返回mMessenger。getBinder(); 客户端Activity使用返回的binder声明一个mMessenger，两端建立起通信桥梁，具体实现方式使用aidl（后续详解）
 * 然后service端等待客户端sendMessage，进行处理，此方法可跨进程、
 * 2.广播，不说了
 * 3.EventBus，和ServictToActivity中的实现方法一样，换个位置，
 * <p/>
 * 下边两种方式不成功，
 * 4.还是使用handl。Activity中获取Service，不断调用Service中的方法，
 * 5.接口回调：和4的实现方式差不多。换成接口回调而已
 */
public class ActivityToServiceDemo extends AppCompatActivity {

    private ServiceToActivityEventType type = new ServiceToActivityEventType();
    private Button btn;
    /**
     * 计时器不再解释
     */
    private Timer timer = new Timer();
    private int progress = 0;
    private Messenger messenger;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /**
             * 根据服务端返回的Binder声明messenger,搭建通信桥梁（粗糙理解）
             */
            messenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_to_service_demo);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        progress++;
                        if (progress > 100) {
                            timer.cancel();
                        } else {
/**
 * 不断发送
 */
                            /**
                             * 使用messenger方式请求服务端OK
                             */
//                            messengerSendProgressToService(progress);
                            /**
                             * EventBus实现Ok
                             */
                            eventBusSendPRogressToService(progress);
                            /**
                             * 接口回调方式   不成功。#############
                             */
//                            callBackSendProgressToService(progress);
                        }
                    }
                };
                timer.schedule(timerTask, 100, 100);
            }
        });
    }

    /**
     * 第一种，最经典的一种
     */
    private void messengerSendProgressToService(int progress) {
        Message message = Message.obtain();
        message.what = Service2.SEND_PROGRESS;
        message.obj = progress;
        try {
            /**
             * 发送客户端请求，这里只做发送请求，服务端响应请见下一个Demo
             */
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用eventBus实现客户端请求服务器
     *
     * @param progress
     */
    private void eventBusSendPRogressToService(int progress) {
        type.progress = progress;
        /**
         * 发送
         */
        EventBus.getDefault().post(type);
    }

    private ServiceToActivityCallBack serviceToActivityCallBack;

    /**
     * 接口回调方式，其实没必要，以防万一还是写一下吧；还是用之前的借口吧懒得写   不成功######################
     *
     * @param progress
     */
    private void callBackSendProgressToService(int progress) {
        if (serviceToActivityCallBack != null)
            serviceToActivityCallBack.progressCall(progress);
        else
            Log.i("Activity", "没有设置监听" + progress);
    }

    /**
     * 回调
     *
     * @param serviceToActivityCallBack
     */
    public void setServiceToActivityCallBack(ServiceToActivityCallBack serviceToActivityCallBack) {
        this.serviceToActivityCallBack = serviceToActivityCallBack;

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, Service2.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
        timer.cancel();
    }
}
