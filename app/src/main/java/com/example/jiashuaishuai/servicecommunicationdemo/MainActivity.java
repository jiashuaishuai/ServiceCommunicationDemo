package com.example.jiashuaishuai.servicecommunicationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.jiashuaishuai.servicecommunicationdemo.ActivityToService.ActivityToServiceDemo;
import com.example.jiashuaishuai.servicecommunicationdemo.ClientRequestServiceResponseClient.ClientRequestServiceResponseClientDemo;
import com.example.jiashuaishuai.servicecommunicationdemo.ServiceToActivity.ServiceHandlSendMessageToActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void click(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.button2:
                intent = new Intent(this, ServiceHandlSendMessageToActivity.class);
                break;
            case R.id.button3:
                intent = new Intent(this, ActivityToServiceDemo.class);
                break;
            case R.id.button4:
                intent = new Intent(this, ClientRequestServiceResponseClientDemo.class);
                break;
            case R.id.button5:
                intent = new Intent(this, ServiceHandlSendMessageToActivity.class);
                break;
            case R.id.button6:
                intent = new Intent(this, ServiceHandlSendMessageToActivity.class);
                break;

        }
        startActivity(intent);
    }
}
