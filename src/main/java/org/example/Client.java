package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private String address;
    private int port;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(address, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             FileInputStream fis = new FileInputStream("src/main/java/org/example/file.txt")) {
            String message;
            if ((message = in.readLine()) != null){
                System.out.println(message);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
