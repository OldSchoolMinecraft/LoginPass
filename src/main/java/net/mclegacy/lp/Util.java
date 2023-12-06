package net.mclegacy.lp;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Util
{
    public static String calculateFileSHA1(String filePath)
    {
        MessageDigest digest;
        FileInputStream inputStream;
        byte[] buffer = new byte[8192];
        int bytesRead;
        StringBuilder sha1 = new StringBuilder();

        try
        {
            digest = MessageDigest.getInstance("SHA-1");
            inputStream = new FileInputStream(filePath);

            while ((bytesRead = inputStream.read(buffer)) != -1)
                digest.update(buffer, 0, bytesRead);

            byte[] hashedBytes = digest.digest();

            // Convert byte array to a hexadecimal string
            for (byte hashedByte : hashedBytes)
                sha1.append(Integer.toString((hashedByte & 0xff) + 0x100, 16).substring(1));

            inputStream.close();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace(System.err);
            return null;
        }

        return sha1.toString();
    }

    public static String encodeFileToBase64(String filePath)
    {
        Path path = Paths.get(filePath);
        try
        {
            byte[] fileContent = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
