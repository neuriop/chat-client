package org.example;

import java.io.*;
import java.net.Socket;

public class Main {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8081;

    public static void main(String[] args) {
//        new Client("localhost", 8081).run();

//        new Client("localhost", 8081).run();
        File file = new File("C:\\Users\\pushk\\Курсы\\chat-client-master\\firt.txt"); // Вкажіть шлях до вашого файлу

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {


            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeLong(file.length());

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[(int) file.length()];
                fis.read(buffer);
                dataOutputStream.write(buffer);
            }

            String response = dataInputStream.readUTF();
            System.out.println("Ответ с сервера: " + response);

            if (response.contains("выполненно")) {
                long fileSize = dataInputStream.readLong();
                byte[] buffer = new byte[(int) fileSize];
                dataInputStream.readFully(buffer);

                try (FileOutputStream fos = new FileOutputStream("name" + file.getName())) {
                    fos.write(buffer);
                }

                System.out.println("файл сохраненн: " + file.getName());
            }

        } catch (IOException e) {
            System.out.println("Помилка при підключенні до сервера: " + e.getMessage());
        }
    }
}