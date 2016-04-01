package com.example.jiashuaishuai.servicecommunicationdemo.ClientRequestServiceResponseClient;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jiashuaishuai on 2016/4/1.
 */
public class Service3 extends Service {
    public static final int SERVICE_REQUEST_CODE = 0xcc;
    /**
     * 响应客户端信使
     */
    private Messenger responseClientMessenger;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            switch (msg.what) {
                case SERVICE_REQUEST_CODE:
                    Log.i("Service3", "接收到请求" + msg.obj);
                    try {
                        /**
                         * 休眠一秒
                         */
                        Thread.sleep(100);
                        /**
                         * 响应客户端的msg
                         */
                        Message serviceMessage = Message.obtain();
                        //            Message serviceMessage = Message.obtain(msg);
                        serviceMessage.what = ClientRequestServiceResponseClientDemo.ACTIVITY_RESPONSE_CONDE;
                        serviceMessage.obj = msg.obj + "响应zCode";
/**
 * 获得客户端的信使，从客户端请求服务器中的msg中剥离出来，
 */
                        responseClientMessenger = msg.replyTo;
                        /**
                         * 客户端信使发送服务端msg
                         */
                        responseClientMessenger.send(serviceMessage);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }

        }
    };
    /**
     * 服务端信使
     */
    private Messenger mMessenger = new Messenger(handler);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
