package org.example;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8080;

    private static void sendFile(Socket socket) throws Exception {
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
             OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {

            System.out.println("Підключено до сервера.");

            // Створюємо файл з текстом "бум бум"
            String content = "ха ха";
            File tempFile = new File("src/main/java/org/example/file2.txt");
//            try (FileWriter writer = new FileWriter(tempFile)) {
//                writer.write(content);
//            }

            // Відправляємо файл на сервер
            try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                byte[] buffer = new byte[512];
                int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    if (bytesRead != 0 && inputStream.read() != 2) {
                        outputStream.write(buffer, 0, bytesRead);
                        outputStream.write(calculateHash(buffer).getBytes());
                    }
//                        outputStream.write(buffer, 0, bytesRead);
//                        outputStream.write(calculateHash(buffer).getBytes());

                    }
//                    if (inputStream.read() != 2){
//                        outputStream.write(buffer, 0, bytesRead);
//                        outputStream.write(calculateHash(buffer).getBytes());
//                    }
            }

            System.out.println("Файл з текстом 'бум бум' відправлено на сервер.");

            // Видаляємо тимчасовий файл
//            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для обчислення хешу (SHA-256) файлу
    private static String calculateHash(byte[] fileContent) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileContent);
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
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