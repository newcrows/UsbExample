package com.crowsnet.usbexample;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BufferedPollThread extends AbstractPollThread {

    public static final int INITIAL_BUFFER_CAPACITY = 16;

    private ReadHandler readHandler;
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    private List<byte[]> packetBuffer = new ArrayList<>(INITIAL_BUFFER_CAPACITY);

    public BufferedPollThread(@NonNull InputStream inputStream) {
        super(inputStream);
    }

    public BufferedPollThread(@NonNull InputStream inputStream, int packetSize) {
        super(inputStream, packetSize);
    }

    public void setReadHandler(ReadHandler readHandler) {
        this.readHandler = readHandler;

        synchronized (this) {
            if (packetBuffer.size() > 0) {
                for (byte[] packet : packetBuffer)
                    handlePacket(packet);
            }
            packetBuffer.clear();
        }
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
        } else {
            synchronized (this) {
                packetBuffer.add(packet);
            }
        }
    }

    public interface ReadHandler {
        void onReadPacket(byte[] packet);
    }
}
