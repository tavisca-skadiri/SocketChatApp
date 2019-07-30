import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ChatSocketServer {
    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private void createSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(3339);
            System.out.println("Server listening . . . ");
            while (true) {
                socket = serverSocket.accept();
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
                System.out.println("Connected");
                createReadThread();
                createWriteThread();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    private void createReadThread() {
        Thread readThread = new Thread() {
            public void run() {
                while (socket.isConnected()) {
                    try {
                        byte[] readBuffer = new byte[256];
                        int num = inStream.read(readBuffer);
                        if (num > 0) {
                            byte[] arrayBytes = new byte[num];
                            System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                            String recvedMessage = new String(arrayBytes, StandardCharsets.UTF_8);
                            System.out.println("Received message :" + recvedMessage);
                        } else {
                            notify();
                        }
                    } catch (SocketException se) {
                        System.exit(0);
                    } catch (IOException i) {
                        i.printStackTrace();
                    }
                }
            }
        };
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
    public static void main(String[] args) {
        ChatSocketServer chatServer = new ChatSocketServer();
        chatServer.createSocket();
    }
}