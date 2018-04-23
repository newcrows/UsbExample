public abstract class AbstractPollThread extends Thread {

//the default amount of bytes to read at once
public static final int DEFAULT_PACKET_SIZE = 4;

//flag indicating wether this thread should still run
private volatile boolean isFinished = false;

//the input stream to obtain bytes from
private InputStream inputStream;

//the packet size specified in constructor
private int packetSize;

//constructor using packetSize = DEFAULT_PACKET_SIZE
public AbstractPollThread(@NonNull InputStream inputStream) {
    this(inputStream, DEFAULT_PACKET_SIZE);
}

//constructor using custom packetSize
public AbstractPollThread(@NonNull InputStream inputStream, int packetSize) {
    this.inputStream = inputStream;
    this.packetSize = packetSize;
}

//notify this Thread that it should finish
public void finish() {
    isFinished = true;
    interrupt();
}

//this thread's loop method
@Override
public void run() {
    //loop while finish() has not been called
    while (!isFinished) {
        try {
            //obtain the next packetSize bytes as array
            //this blocks until at least packetSize bytes are available
            byte[] packet = nextPacket();

            //invoke abstract handle method and pass the packet we just read
            handlePacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    try {
        //try to close the underlying stream
        inputStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

//Child classes need to implement this method
protected abstract void handlePacket(byte[] packet);

private byte[] nextPacket() throws IOException {
    //create a new byte array of packetSize length
    byte[] bytePacket = new byte[packetSize];

    //read in a loop, until exactly packetSize bytes are read
    int c = 0;
    while (c < packetSize) {
        int r = inputStream.read(bytePacket, c, packetSize - c);
        if (r == -1) {
            //if this stream is closed, call finish() on this Thread
            finish();
        } else {
            //increment the index by the amount of bytes we could read at once
            c += r;
        }
    }

    //return the bytePacket we just read
    return bytePacket;
}
