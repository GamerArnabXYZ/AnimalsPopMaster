package com.gax.bubbleshoot;

import android.app.Application;

import com.gax.bubbleshoot.util.FileLogger;

/**
 * App process start hote hi (kisi bhi Activity se pehle) crash-handler
 * install karta hai, taaki bahut early crashes (jaise MobileAds init,
 * DatabaseHelper) bhi miss na hon.
 */
public class BubbleShootApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileLogger.init(this);

        Thread.UncaughtExceptionHandler previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            FileLogger.logCrash(throwable);
            // Log karne ke baad normal crash-behavior continue karo (crash
            // dialog / process death) - warna app hang ho sakta hai
            if (previousHandler != null) {
                previousHandler.uncaughtException(thread, throwable);
            } else {
                System.exit(1);
            }
        });
    }
}
