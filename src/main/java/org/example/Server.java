package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Server {
    private static final int PORT = 50128;
    private static final int MAX_FILE_SIZE = 1024; // 1 KB

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущений і чекає на підключення...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                    System.out.println("Клієнт підключився.");

                    String fileName = in.readUTF();
                    int fileSize = in.readInt();

                    if (fileSize > MAX_FILE_SIZE) {
                        out.writeUTF("Файл занадто великий. Повинен бути ≤ 1 КБ.");
                        System.out.println("Файл не задовольняє умовам.");
                        continue;
                    }

                    byte[] fileData = new byte[fileSize];
                    in.readFully(fileData);

                    byte[] receivedHash = new byte[32];
                    in.readFully(receivedHash);

                    // Сохранение файла на сервере
                    File outFile = new File("received_" + fileName);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(fileData);
                    }

                    // Расчет хеша файла на сервере
                    byte[] serverHash = calculateHashBytes(fileData);

                    // Проверка хеша
                    if (MessageDigest.isEqual(receivedHash, serverHash)) {
                        out.writeUTF("Файл успішно отримано та збережено.");
                        out.writeUTF(calculateHash(fileData)); // Отправка хеша обратно клиенту
                        out.writeInt(fileSize);
                        out.write(fileData);
                        System.out.println("Файл отримано і збережено. Хеш збігається.");
                    } else {
                        out.writeUTF("Цілісність файлу порушена.");
                        System.out.println("Цілісність файлу порушена.");
                    }

                } catch (IOException | NoSuchAlgorithmException e) {
                    System.out.println("Помилка при отриманні файлу від клієнта: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для обчислення SHA-256 хешу
    private static String calculateHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);
        StringBuilder hashString = new StringBuilder();
        for (byte b : hashBytes) {
            hashString.append(String.format("%02x", b));
        }
        return hashString.toString();
    }

    private static byte[] calculateHashBytes(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }
}
