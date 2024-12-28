package com.example.memoryleak;

import android.content.Context;

public class SingleDemo {
    private static SingleDemo singleDemo;
    private Context context;

    private SingleDemo(Context context) {
        this.context = context;
    }

    public static synchronized SingleDemo getInstance(Context context)
    {
        if(singleDemo==null)
        {
            singleDemo=new SingleDemo(context);
        }
        return singleDemo;
    }

    public void unRegister(Context context)
    {
        if(this.context==context)
        {
            this.context=null;
        }
    }
}