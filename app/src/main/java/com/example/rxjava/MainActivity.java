package com.example.rxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.threadpoolexecutor.ThreadMainActivity;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("handler===", "msg:" + msg.what);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RxJavaThreadChange rxJavaThreadChange = new RxJavaThreadChange();
        findViewById(R.id.tv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxJavaThreadChange.setChangeThread();
//                Intent intent = new Intent(MainActivity.this, ThreadMainActivity.class);
                Intent intent = new Intent();
                intent.setClassName("com.example.rxjava", "com.example.threadpoolexecutor.ThreadMainActivity");
                intent.putExtra("params", "params");
                startActivity(intent);
            }
        });
    }
}
