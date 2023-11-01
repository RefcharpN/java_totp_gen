package org.example;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;


public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(System.currentTimeMillis());

        String totp = generateTOTP("JV4UYZLHN5CG633S", 30, 6);
        System.out.println("Current TOTP:" + totp);
    }

    public static String generateTOTP(String base32Key, int timeStep, int digits) throws Exception {
        long counter = (System.currentTimeMillis() / 1000) / timeStep;

        byte[] key = new Base32().decode(base32Key);

        SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKey);

        byte[] counterBytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            counterBytes[7 - i] = (byte) (counter >> (8 * i));
        }

        byte[] hash = mac.doFinal(counterBytes);
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24 | (hash[offset + 1] & 0xFF) << 16 | (hash[offset + 2] & 0xFF) << 8 | (hash[offset + 3] & 0xFF));

        int otp = binary % (int) Math.pow(10, digits);
        return String.format("%0" + digits + "d", otp);
    }
}