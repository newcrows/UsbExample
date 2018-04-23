package com.crowsnet.usbexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements DefaultPollThread.ReadHandler {

    private DefaultPollThread defaultPollThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream usbInputStream = null; //Open the FileInputStream to USB here

        usbInputStream = new ByteArrayInputStream("test".getBytes());

        defaultPollThread = new DefaultPollThread(usbInputStream);
        defaultPollThread.start();
    }

    @Override
    public void onDestroy() {
        defaultPollThread.finish();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultPollThread.setReadHandler(this);
    }

    @Override
    public void onPause() {
        defaultPollThread.clearReadHandler();
        super.onPause();
    }

    @Override
    public void onReadPacket(byte[] packet) {
        //This method is called on the UIThread, so you can do all your View updates here
        Log.d("", "packet read");
    }
}
