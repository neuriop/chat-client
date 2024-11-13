package org.example;

import java.io.*;
import java.net.Socket;

public class Main {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8080;

    private static void sendFile(Socket socket) throws Exception{
        byte[] fileData = getBytes();

        // Відправка файлу на сервер
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(fileData);
        System.out.println("Файл відправлено на сервер.");
    }

    private static byte[] getBytes() throws IOException {
        File file = new File("src/main/java/org/example/file2.txt");
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        }
        return fileData;
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             OutputStream outputStream = socket.getOutputStream()) {

            System.out.println("Підключено до сервера.");

            // Створюємо файл з текстом "бум бум"
            String content = "бум бум";
            File tempFile = new File("src/main/java/org/example/file2.txt");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(content);
            }

            // Відправляємо файл на сервер
            try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Файл з текстом 'бум бум' відправлено на сервер.");

            // Видаляємо тимчасовий файл
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
////        new Client("localhost", 8081).run();
//
////        new Client("localhost", 8081).run();
//        File file = new File("src/main/java/org/example/file2.txt"); // Вкажіть шлях до вашого файлу
//
//        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) {
//
//            sendFile(socket);
//
//        } catch (IOException e) {
//            System.out.println("Помилка при підключенні до сервера: " + e.getMessage());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}