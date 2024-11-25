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
                     InputStream inputStream = clientSocket.getInputStream();
                     OutputStream outputStream = clientSocket.getOutputStream()) {

                    System.out.println("Клієнт підключився.");

                    File outFile = new File("new_file.txt");
                    outFile.delete();

                    receiveFile(outFile, inputStream, outputStream);

                } catch (IOException e) {
                    System.out.println("Помилка при отриманні файлу від клієнта: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private static String getFileHash(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return calculateHash(fileInputStream.readAllBytes());
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

    private static byte[] calculateHashBytes(byte[] buffer) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(buffer);
    }

    private static boolean areHashesEqual(byte[] hash1, byte[] hash2) {
        return MessageDigest.isEqual(hash1, hash2);
    }

    private static void receiveFile(File file, InputStream inputStream) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
            byte[] buffer = new byte[512];
            int totalBytesRead = 0; // Counter for total bytes read
            int bytesRead;

            while (totalBytesRead < 1024 && (bytesRead = inputStream.read(buffer)) != -1) {
                // Calculate how many bytes to write
                int bytesToWrite = Math.min(bytesRead, 1024 - totalBytesRead);
                fileOutputStream.write(buffer, 0, bytesToWrite);
                totalBytesRead += bytesToWrite; // Update the total bytes read
                System.out.println(bytesToWrite); // Print the number of bytes written
            }
        }
    }

    private static void receiveFile(File file, InputStream inputStream, OutputStream outputStream) throws IOException, NoSuchAlgorithmException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
            byte[] buffer = new byte[512];
            byte[] hash = new byte[32];
            int totalBytesRead = 0; // Counter for total bytes read
            int bytesRead;
            // byte[] bytes = new byte[bytesRead]

            while (totalBytesRead < 1024 && (bytesRead = inputStream.read(buffer)) != -1) {
                // Calculate how many bytes to write

                inputStream.read(hash);
                if (areHashesEqual(calculateHashBytes(byteArrayByLength(bytesRead, buffer)), hash)) {
                    System.out.println("Hashes are equal, saving file");
                    int bytesToWrite = Math.min(bytesRead, 1024 - totalBytesRead);
                    fileOutputStream.write(buffer, 0, bytesToWrite);
                    totalBytesRead += bytesToWrite; // Update the total bytes read
                    System.out.println(bytesToWrite); // Print the number of bytes written
                    outputStream.write(1); // Confirm receiving correct hash;
                } else {
                    System.out.println("Hashes are different. Requesting to resend bytes");
                    outputStream.write(0); // Confirm receiving incorrect hash, for sender to send it again
                }
            }
        }
    }

    private static byte[] byteArrayByLength(int length, byte[] bytes){
        byte[] newBytes = new byte[length];
        for (int i = 0; i < newBytes.length; i++) {
            newBytes[i] = bytes[i];
        }
        return newBytes;
    }
}