package com.gax.bubbleshoot.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Crash aur debug logs ko seedha /Android/data/com.gax.bubbleshoot/files/
 * mein save karta hai -> Termux/file-manager se turant copy karke chat mein
 * paste kar sakte ho, logcat/adb ki zaroorat nahi (phone-only workflow ke
 * liye specifically banaya hai).
 * <p>
 * Do files:
 * - crash_log.txt  -> har uncaught crash yahan append hota hai (timestamp
 *                     + full stack trace + device info)
 * - debug_log.txt  -> {@link #d(String, String)} se manual debug messages
 */
public final class FileLogger {

    private static final String CRASH_FILE = "crash_log.txt";
    private static final String DEBUG_FILE = "debug_log.txt";
    private static final SimpleDateFormat TIMESTAMP =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private static File sExternalFilesDir;

    private FileLogger() {
    }

    /** BubbleShootApplication.onCreate() se ek hi baar call hota hai. */
    public static void init(Context context) {
        sExternalFilesDir = context.getExternalFilesDir(null);
        // App start hote hi ek marker likhte hain -> pata chal jata hai
        // session kab shuru hua tha (crash-log ke context ke liye useful)
        d("Lifecycle", "App started. Android " + Build.VERSION.RELEASE
                + " (API " + Build.VERSION.SDK_INT + "), " + Build.MANUFACTURER + " " + Build.MODEL);
    }

    /** Uncaught crash ka poora stack trace + device info file mein likhta hai. */
    public static void logCrash(Throwable throwable) {
        if (sExternalFilesDir == null) return;

        StringWriter stackTrace = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTrace));

        String entry = "\n========== CRASH " + TIMESTAMP.format(new Date()) + " ==========\n"
                + "Device: " + Build.MANUFACTURER + " " + Build.MODEL
                + " | Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")\n"
                + stackTrace + "\n";

        writeToFile(CRASH_FILE, entry);
    }

    /** Kahin se bhi ek debug message log karo - logcat aur file dono mein jaata hai. */
    public static void d(String tag, String message) {
        Log.d(tag, message);
        if (sExternalFilesDir == null) return;
        String entry = "[" + TIMESTAMP.format(new Date()) + "] " + tag + ": " + message + "\n";
        writeToFile(DEBUG_FILE, entry);
    }

    private static void writeToFile(String fileName, String content) {
        File file = new File(sExternalFilesDir, fileName);
        try (FileWriter writer = new FileWriter(file, true)) {   // append = true
            writer.write(content);
        } catch (IOException e) {
            Log.e("FileLogger", "Log file likhne mein fail: " + e.getMessage());
        }
    }
}
