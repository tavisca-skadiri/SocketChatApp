import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatSocketClient {
    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private void createSocket() {
        try {
            socket = new Socket("localHost", 3339);
            System.out.println("Connected");
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
            createReadThread();
            createWriteThread();
        } catch (IOException u) {
            u.printStackTrace();
        }
    }
    private void createReadThread() {
        Thread readThread = new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    byte[] readBuffer = new byte[256];
                    int num = inStream.read(readBuffer);
                    if (num > 0) {
                        byte[] arrayBytes = new byte[num];
                        System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                        String recvedMessage = new String(arrayBytes, StandardCharsets.UTF_8);
                        System.out.println("Received message :" + recvedMessage);
                    }
                } catch (IOException i){
                    i.printStackTrace();
                }
            }
        });
        readThread.setPriority(Thread.MAX_PRIORITY);
        readThread.start();
    }
    private void createWriteThread() {
        Thread writeThread = new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                    Thread.sleep(100);
                    String typedMessage = inputReader.readLine();
                    if (typedMessage != null && typedMessage.length() > 0) {
                        synchronized (socket) {
                            outStream.write(typedMessage.getBytes(StandardCharsets.UTF_8));
                            Thread.sleep(100);
                        }
                    }
                } catch (IOException | InterruptedException i) {
                    i.printStackTrace();
                }
            }
        });
        writeThread.setPriority(Thread.MAX_PRIORITY);
        writeThread.start();
    }
    public static void main(String[] args){
        ChatSocketClient myChatClient = new ChatSocketClient();
        myChatClient.createSocket();
    }
}