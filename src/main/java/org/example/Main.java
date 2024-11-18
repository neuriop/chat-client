package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;

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



            if (tempFile.length() <= 1024)
                // Відправляємо файл на сервер
                sendFile(tempFile, outputStream);
            else System.out.println("File is too large");

            System.out.println(tempFile.length());

            System.out.println("Full hash");
            System.out.println(getFileHash(tempFile));
            String hash = getFileHash(tempFile);



            String receivedHash = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(receivedHash);

            if (hash.equals(receivedHash))
                System.out.println("File sent successfully");
            else System.out.println("File damaged");



            // Видаляємо тимчасовий файл
//            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileHash(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            return calculateHash(fileInputStream.readAllBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileHash(File file, int bufferLength) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            byte[] buffer = new byte[bufferLength];
            fileInputStream.read(buffer);
            return calculateHash(buffer);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendFile(File tempFile, OutputStream outputStream) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {

                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Файл з текстом 'бум бум' відправлено на сервер.");
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