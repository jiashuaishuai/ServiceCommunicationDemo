package com.example.jiashuaishuai.servicecommunicationdemo.ClientRequestServiceResponseClient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jiashuaishuai.servicecommunicationdemo.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jiashuaishuai on 2016/4/1.
 *实现思路：
 将Service端信使通过onBind方法传送到Client，Client拿到ServiceMessenger发送Mesage，通过将要发送到Service端的Message将Client的信使绑定，然后Service端接收到Message时，将Message中的ClientMessenger分离出来，在通过ClientMessenger发送Message到Client，这样就实现了请求响应
 经典案例：http://blog.csdn.net/lmj623565791/article/details/47017485；
 服务端和客户端各自获得各自的Messenger，服务端通过onBind方法将Messenger传到客户端给自己发送消息，在发送消息时，将客户端的Messenger也一并发送到服务端，然后客户端的Messenger在给自己发消息，
 */

public class ClientRequestServiceResponseClientDemo extends AppCompatActivity {
    public static final int ACTIVITY_RESPONSE_CONDE = 0xac;
    private Timer timer = new Timer();
    private int progress = 0;

    private Button bun3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTIVITY_RESPONSE_CONDE:
                    Log.i("Activity", "" + msg.obj);
                    break;
            }

        }
    };
    /**
     * 服务器端的messenger信使
     */
    private Messenger messengerService;
    /**
     * 客户端的messenger信使
     * 获得服务端的信使
     */
    private Messenger messengerClient = new Messenger(handler);


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /**
             * 获得服务端信使
             */
            messengerService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messengerService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiyt3_layout);
        bun3 = (Button) findViewById(R.id.bun3);
        bun3.setOnClickListener(new View.OnClickListener() {
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
                            Message msgClient = Message.obtain();
                            msgClient.what = Service3.SERVICE_REQUEST_CODE;
                            msgClient.obj = progress;
                            /**
                             * 将服务端信使封装到请求信息内，到服务端后，再将其剥离出来，客户端信使在发送消息，客户端接收
                             */
                            msgClient.replyTo = messengerClient;

                            try {
                                /**
                                 * 服务端信使发送请求
                                 */
                                messengerService.send(msgClient);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                };
                timer.schedule(timerTask, 100, 100);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, Service3.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }
}
