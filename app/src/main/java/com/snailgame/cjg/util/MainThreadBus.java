package com.snailgame.cjg.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * OTTO Bus
 * Created by xixh on 2015/3/9.
 */
public class MainThreadBus extends Bus {
    private static MainThreadBus BUS;
    private final Handler mHandler = new Handler(Looper.getMainLooper());


    public static MainThreadBus getInstance() {
        if (BUS == null) {
            synchronized(MainThreadBus.class) {
                if (BUS == null) {
                    BUS = new MainThreadBus();
                }
            }
        }
        return BUS;
    }

    private MainThreadBus() {
        // No instances.
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadBus.super.post(event);
                }
            });
        }
    }
}
