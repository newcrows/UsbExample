package com.crowsnet.usbexample;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractPollThread extends Thread {

    public static final int DEFAULT_PACKET_SIZE = 4;

    private volatile boolean isFinished = false;
    private InputStream inputStream;
    private int packetSize;

    public AbstractPollThread(@NonNull InputStream inputStream) {
        this(inputStream, DEFAULT_PACKET_SIZE);
    }

    public AbstractPollThread(@NonNull InputStream inputStream, int packetSize) {
        this.inputStream = inputStream;
        this.packetSize = packetSize;
    }

    public void finish() {
        isFinished = true;
        interrupt();
    }

    @Override
    public void run() {
        while (!isFinished) {
            try {
                byte[] packet = nextPacket();

                handlePacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void handlePacket(byte[] packet);

    private byte[] nextPacket() throws IOException {
        byte[] bytePacket = new byte[packetSize];

        int c = 0;
        while (c < packetSize) {
            int r = inputStream.read(bytePacket, c, packetSize - c);
            if (r != -1)
                c += r;
        }

        return bytePacket;
    }
}
