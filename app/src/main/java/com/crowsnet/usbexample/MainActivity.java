public class MainActivity extends AppCompatActivity implements BufferedPollThread.ReadHandler {

//hold the reference to the pollThread
private BufferedPollThread bufferedPollThread;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //do your setup here (FileInputStream implements InputStream, so no problems here)
    InputStream usbInputStream = null; //Open the FileInputStream to USB here

    //create and start the pollThread that will deliver USB read packets
    bufferedPollThread = new BufferedPollThread(usbInputStream);
    bufferedPollThread.start();
}

@Override
public void onDestroy() {
    //if this activity is about to be destroyed, stop the background poll thread as well
    bufferedPollThread.finish();
    super.onDestroy();
}

@Override
public void onResume() {
    super.onResume();

    //if this activity is in foreground, listen to packets from USB
    bufferedPollThread.setReadHandler(this);
}

@Override
public void onPause() {
    //if this activity is about to be paused, stop listening to packets from USB
    //this is to avoid memory leaks on configuration changes
    bufferedPollThread.clearReadHandler();
    super.onPause();
}

@Override
public void onReadPacket(byte[] packet) {
    //This method is called on the UIThread, so you can do all your View updates here
}
