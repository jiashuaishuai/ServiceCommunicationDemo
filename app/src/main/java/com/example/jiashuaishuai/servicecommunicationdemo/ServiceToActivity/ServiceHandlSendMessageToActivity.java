package com.example.jiashuaishuai.servicecommunicationdemo.ServiceToActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.jiashuaishuai.servicecommunicationdemo.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Service给Activity传值，Activity更新进度条 Handl实现
 * MyService定义内部类，返回MyService本身，onBind返回实例化的内部类：
 * Handl在Activity中实例化，通过MyService中的getProgress方法传到Service中，发送Message，Activity接受
 * <p/>
 * 也可使用接口回调：
 * 还可以使用广播：这里不详细
 * EventBus
 * Aidl：后期会专门针对aidl做详解
 * 注意：
 * Service需要清单文件下注册
 */
public class ServiceHandlSendMessageToActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button button;
    private MyService myService;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyService.GET_PROGRESS:
                    /**
                     * 设置进度条
                     */
                    progressBar.setProgress((Integer) msg.obj);
                    break;
            }
        }
    };


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /**
             * service内部类返回myService本身，
             */
            myService = ((MyService.LoackBinder) service).getService();

            /**
             * 设置progress接口回调
             */
            myService.setProgressCallBack(new ServiceToActivityCallBack() {
                @Override
                public void progressCall(int progress) {
                    progressBar.setProgress(progress);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /**
             * 停止服务时清除
             */
            myService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_handl_send_message_to);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * handl方式
                 */
//                myService.getMyServiceProgress(handler);
                /**
                 * 接口回调方式
                 */
//                myService.getMyServiceProgress();
                /**
                 * eventBus实现
                 */
                myService.getProgressEventBus();
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        /**
         * 启动绑定服务，intent，
         */
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
/**
 * 注册接收方EventBUs
 */
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /**
         * 关闭服务
         */
        unbindService(mServiceConnection);

        /**
         * 解除注册EventBus
         */
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(ServiceToActivityEventType type) {
        Log.i("ACTIVIYT","EventBus接受");
        progressBar.setProgress(type.progress);
    }


}
