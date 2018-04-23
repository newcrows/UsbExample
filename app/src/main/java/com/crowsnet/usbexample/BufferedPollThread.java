public class BufferedPollThread extends AbstractPollThread {

//the initial capacity of the packet buffer
public static final int INITIAL_BUFFER_CAPACITY = 16;

//the read handler that wants to receive packets
private ReadHandler readHandler;

//handler hooked to the UIThread's message loop
private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

//a simple list implementation acting as packetBuffer
private List<byte[]> packetBuffer = new ArrayList<>(INITIAL_BUFFER_CAPACITY);

//same constructor as in AbstractPollThread
public BufferedPollThread(@NonNull InputStream inputStream) {
    super(inputStream);
}

//same constructor as in AbstractPollThread
public BufferedPollThread(@NonNull InputStream inputStream, int packetSize) {
    super(inputStream, packetSize);
}

public void setReadHandler(ReadHandler readHandler) {
    //set the Handler that should handle packets
    this.readHandler = readHandler;

    //if the buffer contains packets, push them to the readHandler
        if (packetBuffer.size() > 0) {
            for (byte[] packet : packetBuffer)
                handlePacket(packet);
        }
        packetBuffer.clear();
}

public void clearReadHandler() {
    //clear the reference to the Handler to avoid memory leaks
    this.readHandler = null;
}

@Override
protected void handlePacket(final byte[] packet) {
    //if a handler is set, push the packet directly
    //otherwise add it to buffer
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (readHandler != null)
                    readHandler.onReadPacket(packet);
                else
                    packetBuffer.add(packet);
            }
        });
}

public interface ReadHandler {
    //this is called for every packet in your custom read handler
    void onReadPacket(byte[] packet);
}
}
