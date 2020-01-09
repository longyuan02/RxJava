package com.example.threadpoolexecutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadMainActivity extends AppCompatActivity {
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String params=getIntent().getStringExtra("params");
        Toast.makeText(this,params,Toast.LENGTH_SHORT).show();
        ThreadPool();
        threadPoolExecutor = new ThreadPoolExecutor(1, 1000, 50,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128),
                new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable r) {
                        return null;
                    }
                }, new MyIgnorePolicy());
    }

    private void ThreadPool() {
        ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
            AtomicInteger atomicInteger = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("ThreadName======" + atomicInteger.getAndIncrement());
                return t;
            }
        });
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.e("log=", "3: " + Thread.currentThread().getName());
            }
        }, 0, 3, TimeUnit.SECONDS);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.e("log=====", "7: " + Thread.currentThread().getName());
            }
        }, 0, 7, TimeUnit.SECONDS);
    }

    //错误回执
    private class MyIgnorePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            doLog(r, executor);
        }

        private void doLog(Runnable r, ThreadPoolExecutor e) {
            // 可做日志记录等
            System.err.println(r.toString() + " rejected");
//          System.out.println("completedTaskCount: " + e.getCompletedTaskCount());
        }
    }


}
