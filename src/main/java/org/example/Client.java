package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

//public class Client implements Runnable {
//    private String address;
//    private int port;
//
//    public Client(String address, int port) {
//        this.address = address;
//        this.port = port;
//    }
//
//    @Override
//    public void run() {
//        try (Socket socket = new Socket(address, port);
//             OutputStream out = socket.getOutputStream();
//             FileInputStream fis = new FileInputStream("src/main/java/org/example/file.txt")) {
//            byte[] b = new byte[1024];
//            int length;
//            while ((length = fis.read(b)) > 0){
//                out.write(fis.read(b, 0, length));
//            }
//            System.out.println("file sent");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
