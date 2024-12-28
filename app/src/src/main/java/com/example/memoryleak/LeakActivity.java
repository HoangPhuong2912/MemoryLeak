package com.example.memoryleak;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

@SuppressWarnings("deprecation")
public class LeakActivity extends Activity implements View.OnClickListener {

    //static
    static TextView staticView;
    static Activity staticActivity = null;
    static SomeInnerClass someInnerClass;

    String InnerClassParam = "Static class holds external reference";
    //singleton
    SingleDemo single = null;
    //handler
    private final Handler mLeakyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("FRANK", "handle message");
        }
    };
    //thread
    private LeakedThread mThread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);

        findViewById(R.id.leak_singleton).setOnClickListener(this);
        findViewById(R.id.leak_static).setOnClickListener(this);
        findViewById(R.id.leak_static_innerclass).setOnClickListener(this);
        findViewById(R.id.leak_thread).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.leak_singleton) {
            leakSingleton();
        } else if (id == R.id.leak_static) {
            leakStatic();
        } else if (id == R.id.leak_static_innerclass) {
            leakStaticInnerClass();
        } else if (id == R.id.leak_thread) {
            leakThread();
        }
    }

    /*Singleton khởi tạo bằng context của Activity, dẫn đến việc giữ một tham chiếu mạnh
    ngăn Activity được thu gom rác (Garbage Collection) khi nó bị hủy (destroyed).*/
    private void leakSingleton() {
        single = SingleDemo.getInstance(this);
        Toast.makeText(this,"Singleton context leak",Toast.LENGTH_SHORT).show();
    }

    //staticView được khởi tạo và giữ tham chiếu đến LeakActivity.
    //staticActivity giữ một tham chiếu mạnh đến Activity hiện tại.
    private void leakStatic() {

        staticView = new TextView(LeakActivity.this);

        if (staticActivity == null) {
            staticActivity = this;
        }

        Toast.makeText(this,"Static leak",Toast.LENGTH_SHORT).show();
    }

    /*SomeInnerClass được khởi tạo và giữ tham chiếu tĩnh, là một inner class của LeakActivity,
    nó sẽ ngầm giữ một tham chiếu mạnh đến LeakActivity, gây ra memory leak.*/
    private void leakStaticInnerClass() {
        if (someInnerClass == null) {
            someInnerClass = new SomeInnerClass();
        }
        Toast.makeText(this,"Inner class call leak",Toast.LENGTH_SHORT).show();

    }

    //LeakedThread chạy vô hạn và giữ một tham chiếu đến Activity.
    //Handler trong mLeakyHandler cũng có thể gây ra leak vì nó giữ tham chiếu ngầm đến Activity.
    //AsyncTask chạy trong background mà không bao giờ hoàn thành dẫn đến việc giữ tham chiếu đến Activity.
    private void leakThread() {
        //thread
        mThread = new LeakedThread();
        mThread.start();

        //handler
        mLeakyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String test = InnerClassParam;
                Log.e("FRANK", "in run()");
            }
        }, 1000 * 60 * 10);

        //AsyncTask
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (true) {
                    SystemClock.sleep(10000);
                }
            }
        }.execute();

        Toast.makeText(this,"Thread call leak",Toast.LENGTH_SHORT).show();

    }


    class SomeInnerClass {
    }


    private class LeakedThread extends Thread {
        @Override
        public void run() {
            while (true) {
                SystemClock.sleep(10000);
            }
        }
    }

}