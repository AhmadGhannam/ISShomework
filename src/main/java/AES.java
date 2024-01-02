import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Possible KEY_SIZE values are 128, 192 and 256
 * Possible T_LEN values are 128, 120, 112, 104 and 96
 */


class AES {
    private String key;
    private SecretKeySpec secretKeySpec;
    private final int KEY_SIZE = 128;
    private final int T_LEN = 128;
    private Cipher encryptionCipher;
    public AES(String key){
        byte[] keyBytes = key.getBytes();
        // Ensure the key length is appropriate
        if (keyBytes.length != KEY_SIZE / 8) {
            // Truncate the key if it's longer than the required size
            if (keyBytes.length > KEY_SIZE / 8) {
                byte[] truncatedKey = new byte[KEY_SIZE / 8];
                System.arraycopy(keyBytes, 0, truncatedKey, 0, truncatedKey.length);
                keyBytes = truncatedKey;
            }
            else {
                byte[] paddedKey = new byte[KEY_SIZE / 8];
                System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, paddedKey.length));
                keyBytes = paddedKey;
            }
        }
        this.key=key;
        System.out.println(this.key);
        secretKeySpec = new SecretKeySpec(keyBytes, "AES");
    }

    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");

        generator.init(KEY_SIZE);
    }

     String encrypt(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
         byte[] encryptedBytes = new byte[100000];try{
             Cipher cipher = Cipher.getInstance("AES");
             cipher.init(Cipher.ENCRYPT_MODE, this.secretKeySpec);
             encryptedBytes = cipher.doFinal(plainText.getBytes());

         }catch (Exception e){e.printStackTrace();}return Base64.getEncoder().encodeToString(encryptedBytes);
    }

     String decrypt(String encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
         Cipher cipher = Cipher.getInstance("AES");
         cipher.init(Cipher.DECRYPT_MODE, this.secretKeySpec); // Use the appropriate IV
         byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
         return new String(decryptedBytes);
    }

}