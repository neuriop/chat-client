package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {

            System.out.println("Підключено до сервера.");
            File tempFile = new File("src/main/java/org/example/file2.txt");

            if (tempFile.length() <= 1024)
//                 Відправляємо файл на сервер
                sendFile(tempFile, outputStream, inputStream);
            else System.out.println("File is too large");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileHash(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return calculateHash(fileInputStream.readAllBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] calculateHashBytes(byte[] buffer) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(buffer);
    }

    private static String getFileHash(File file, int bufferLength) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[bufferLength];
            fileInputStream.read(buffer);
            return calculateHash(buffer);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendFile(File tempFile, OutputStream outputStream) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(tempFile);
             DataOutputStream out = new DataOutputStream(outputStream)) {

        }
    }

    private static void sendFile(File tempFile, OutputStream outputStream, InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        try (FileInputStream fileInputStream = new FileInputStream(tempFile);
             DataOutputStream out = new DataOutputStream(outputStream);
             DataInputStream in = new DataInputStream(inputStream)) {
            byte[] buffer = new byte[512];
            int bytesRead;
            int number = 0;
            in.readInt();
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.writeInt(number);
                out.writeInt(bytesRead);
                out.write(buffer, 0, bytesRead);
                System.out.println(in.readUTF());
                number++;
            }
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
}

