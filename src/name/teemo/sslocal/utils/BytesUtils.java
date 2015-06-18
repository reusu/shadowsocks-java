package name.teemo.sslocal.utils;

import java.security.SecureRandom;

public class BytesUtils {
    public static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }
}
