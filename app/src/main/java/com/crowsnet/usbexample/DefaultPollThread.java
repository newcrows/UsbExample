package com.crowsnet.usbexample;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.InputStream;

public class DefaultPollThread extends AbstractPollThread {

    private ReadHandler readHandler;
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public DefaultPollThread(@NonNull InputStream inputStream) {
        super(inputStream);
    }

    public void setReadHandler(ReadHandler readHandler) {
        this.readHandler = readHandler;
    }

    public void clearReadHandler() {
        this.readHandler = null;
    }

    @Override
    protected void handlePacket(final byte[] packet) {
        if (readHandler != null) {
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    readHandler.onReadPacket(packet);
                }
            });
        } else
            Log.d("", "No readHandler set.");
    }

    public interface ReadHandler {
        void onReadPacket(byte[] packet);
    }
}
