package ozosync.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {

    private final static String[] digits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private String byteToString(byte byteVal) {
        int intVal = byteVal;

        if (intVal < 0) {
            intVal += 256;
        }

        int digit1 = intVal / 16;
        int digit2 = intVal % 16;

        return digits[digit1] + digits[digit2];
    }

    private String bytesToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append(byteToString(bytes[i]));
        }

        return stringBuilder.toString();
    }

    public String getMD5Digest(String clearStr) throws NoSuchAlgorithmException {
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        return bytesToString(md5Digest.digest(clearStr.getBytes()));
    }

    public String getSHADigest(String clearStr) throws NoSuchAlgorithmException {
        MessageDigest shaDigest = MessageDigest.getInstance("SHA");
        return bytesToString(shaDigest.digest(clearStr.getBytes()));
    }
}
