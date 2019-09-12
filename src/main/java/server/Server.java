package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server implements Runnable {
    private static Server instance;
    private ServerSocket server;
    private Socket client;
    private DataInputStream inputStream;
    private PrintWriter outputStreamWriter;

    public boolean stop = false;

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!stop) {
            while (client.isConnected()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println(client.getInetAddress().isReachable(5000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (inputStream.available() > 0) {
                        byte[] input = new byte[inputStream.available()];
                        inputStream.readFully(input);
                        for (byte inputByte : input) {
                            System.out.print((char) inputByte);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File index = new File("C://users//jakob//desktop//downloadtest.html");
                StringBuilder indexStringBuilder = new StringBuilder();
                try {
                    byte[] indexData = getFileData(index);

                    for (byte indexByte : indexData) {
                        indexStringBuilder.append((char) indexByte);
                    }
                    outputStreamWriter.println("HTTP/1.1 200 OK");
                    outputStreamWriter.println("Server: Songreporter GUI Server");
                    outputStreamWriter.println("Date: " + new Date());
                    outputStreamWriter.println("Content-type: text/html;charset=utf-8");
                    outputStreamWriter.println("Content-length: " + indexStringBuilder.length());
                    outputStreamWriter.println("Connection: close");
                    outputStreamWriter.println();
                    outputStreamWriter.println(indexStringBuilder.toString().trim());
                    outputStreamWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!client.isConnected()) {
                try {
                    waitForClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void start() throws IOException {
        server = new ServerSocket(5000);
        waitForClient();
    }

    private void waitForClient() throws IOException {
        while (client == null) {
            client = server.accept();
        }
        inputStream = new DataInputStream(client.getInputStream());
        outputStreamWriter = new PrintWriter(client.getOutputStream());
    }

    private byte[] getFileData(File file) throws IOException {
        byte[] fileData = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        int fileLength = fileInputStream.read(fileData);
        if (fileLength == -1 || fileLength == 0) {
            System.out.println("No Data in the File!");
        }
        return fileData;
    }

    public static Server getInstance() {
        if (Server.instance == null) {
            Server.instance = new Server();
        }
        return Server.instance;
    }
}
