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
public class LeakFixActivity extends Activity implements View.OnClickListener {

    // FIXED: remove static keywords
    TextView staticView;
    Activity staticActivity = null;
    SomeInnerClass someInnerClass;

    String InnerClassParam = "Static class holds external references";
    //single
    SingleDemo single = null;

    //handler
    private final Handler mLeakyHandler = new MyHandler();
    private final MyRunnable myRunnable = new MyRunnable();

    //thread
    private LeakedThread mThread;

    //Task
    private DoNothingTask doNothingTask = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);

        findViewById(R.id.leak_singleton).setOnClickListener(this);
        findViewById(R.id.leak_static).setOnClickListener(this);
        findViewById(R.id.leak_static_innerclass).setOnClickListener(this);
        findViewById(R.id.leak_thread).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // FIXED: unregister it onDestroy
        single.unRegister(this);
        // FIXED: kill the thread in activity onDestroy
        mThread.interrupt();
        // FIXED: remove callback in activity onDestroy
        mLeakyHandler.removeCallbacks(myRunnable);
        // FIXED: should cancel the task in activity onDestroy()
        doNothingTask.cancel(true);

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

    private void leakSingleton() {
        single = SingleDemo.getInstance(this);
        Toast.makeText(this,"Singleton context leak------fixed",Toast.LENGTH_SHORT).show();

    }

    private void leakStatic() {

        staticView = new TextView(LeakFixActivity.this);

        if (staticActivity == null) {
            staticActivity = this;
        }
        Toast.makeText(this,"Static leak------fixed",Toast.LENGTH_SHORT).show();

    }

    private void leakStaticInnerClass() {
        if (someInnerClass == null) {
            someInnerClass = new SomeInnerClass();
        }
        Toast.makeText(this,"Inner class call leak------fixed",Toast.LENGTH_SHORT).show();

    }

    private void leakThread() {
        //thread
        mThread = new LeakedThread();
        mThread.start();

        //handler
        mLeakyHandler.postDelayed(myRunnable, 1000 * 60 * 10);

        //AsyncTask
        doNothingTask = new DoNothingTask();
        doNothingTask.execute();
        Toast.makeText(this,"Thread call leak------fixed",Toast.LENGTH_SHORT).show();

    }


    class SomeInnerClass {
    }


    // FIXED: make it static. So it does not have referenced to the containing activity class
    private static class LeakedThread extends Thread {
        @Override
        public void run() {
            // FIXED: check interrupted before the next loop
            while (!isInterrupted()) {
                SystemClock.sleep(10000);
            }
        }
    }

    // FIXED: use static class instead of inner class. static class does not have reference to the containing activity
    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("FRANK", "handle message");
        }
    }

    // FIXED: use static class instead of inner class. static class does not have reference to the containing activity
    private static class MyRunnable implements Runnable {
        @Override
        public void run() {
            Log.e("FRANK", "in run()");
        }
    }


    private static class DoNothingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // FIXED: should check if cancelled before next loop
            while (!isCancelled()) {
                SystemClock.sleep(1000);
            }
            return null;
        }
    }

}