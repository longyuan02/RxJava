package com.example.rxjava

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 线程池
 */
internal class SecondeActivity : AppCompatActivity() {
    private var scheduleTaskExecutor: ScheduledExecutorService? = null
    private var isTimer = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val t = findViewById<TextView>(R.id.tv_click)
        t.setOnClickListener {
            Toast.makeText(this@SecondeActivity, "show", Toast.LENGTH_LONG).show()
            actionScheduledTask()
        }
    }

    /**
     * 开关线程池
     */
    private fun actionScheduledTask() {
        if (isTimer) {
            startScheduledTask()
        } else {
            if (!scheduleTaskExecutor!!.isShutdown) {
                scheduleTaskExecutor!!.shutdown()
            }
        }
        isTimer = !isTimer
    }

    // 开启执行
    private fun startScheduledTask() {
        scheduleTaskExecutor = ScheduledThreadPoolExecutor(2, object : ThreadFactory {
            private val atoInteger = AtomicInteger(0)

            override fun newThread(r: Runnable): Thread {
                val t = Thread(r)
                t.name = "App-Thread" + atoInteger.getAndIncrement()
                return t
            }
        })
        //开启第一个线程
        scheduleTaskExecutor!!.scheduleAtFixedRate({ Log.e("startScheduled=====", "======1") }, 0, 6, TimeUnit.SECONDS)
        //开启第二个线程
        scheduleTaskExecutor!!.scheduleAtFixedRate({ Log.e("startScheduled=====", "======2") }, 0, 3, TimeUnit.SECONDS)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (scheduleTaskExecutor != null && !scheduleTaskExecutor!!.isShutdown) {
            scheduleTaskExecutor!!.shutdown()
        }
    }
}
