package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String message;
            if ((message = in.readLine()) != null){
                System.out.println(message);
            }
            Scanner scanner = new Scanner(System.in);
            out.println(scanner.nextLine());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
