package com.example.weakref;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

@SuppressWarnings("deprecation")
public class AsyncTaskActivity extends AppCompatActivity {
    private  MyAsyncTask myAsyncTask;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myAsyncTask != null){
                        finish();
                    }
                    MyAsyncTask myAsyncTask = new MyAsyncTask(MyAsyncTask.this);
                    myAsyncTask.execute();
                }
            });


        }

        private class MyAsyncTask extends AsyncTask<Void, Void, Void> implements com.example.weakref.MyAsyncTask {

            private Context mContext;

            public MyAsyncTask(Context context) {
                mContext = context;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_background);


                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }


            @Override
            protected void onDestroy() {
                super.onDestroy();
                Log.d(TAG, "activity is destroyed");
            }
}



