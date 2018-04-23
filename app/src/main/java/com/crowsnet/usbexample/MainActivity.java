package com.crowsnet.usbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements BufferedPollThread.ReadHandler {

    private BufferedPollThread bufferedPollThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream usbInputStream = null; //Open the FileInputStream to USB here

        bufferedPollThread = new BufferedPollThread(usbInputStream);
        bufferedPollThread.start();
    }

    @Override
    public void onDestroy() {
        bufferedPollThread.finish();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        bufferedPollThread.setReadHandler(this);
    }

    @Override
    public void onPause() {
        bufferedPollThread.clearReadHandler();
        super.onPause();
    }

    @Override
    public void onReadPacket(byte[] packet) {
        //This method is called on the UIThread, so you can do all your View updates here
    }
}
