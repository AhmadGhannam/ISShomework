import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * An implementation of PGP encryption (PGP = Pretty Good Privacy)
 */
class PGPEncryption {

    //this method combines the encrypted symmetric key (encryptedSymmetricKey)
    //and the encrypted message (encryptedMessage) into a single byte array using a ByteBuffer.
    public static byte[] EncryptMess(byte[]encryptedSymmetricKey,byte[]encryptedMessage){
        ByteBuffer buffer = ByteBuffer.allocate(encryptedSymmetricKey.length + encryptedMessage.length + 4);
        buffer.putInt(encryptedSymmetricKey.length);
        buffer.put(encryptedSymmetricKey);
        buffer.put(encryptedMessage);
        return buffer.array();
    }

    //encrypt message using symmetric key
    public static byte[] encryptWithSymmetricKey(byte[] message, SecretKey key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message);
    }

    //this method used for generating a symmetric key
    public static SecretKey generateSymmetricKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Use a suitable key size for AES
        return keyGenerator.generateKey();
    }


    //this method is using to encrypt the session key (symmetric key) using public key receiver
    public static byte[] encryptWithPublicKey(byte[] data, PublicKey key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    //this method is using to decrypt Message
    public static byte[] decryptMess(byte[] encryptedData, byte[] decryptedSymKey) throws GeneralSecurityException {
        ByteBuffer buffer = ByteBuffer.wrap(encryptedData);
        // Extract the length of the encrypted symmetric key
        int encryptedKeyLength = buffer.getInt();
        System.out.println(encryptedKeyLength);
        // Extract the encrypted symmetric key
        byte[] encryptedSymmetricKey = new byte[encryptedKeyLength];
        buffer.get(encryptedSymmetricKey);
        // Extract the encrypted message
        byte[] encryptedMessage = new byte[buffer.remaining()];
        buffer.get(encryptedMessage);
        return decryptWithSymmetricKey(encryptedMessage, new SecretKeySpec(decryptedSymKey, "AES"));
    }

    //this method is using to decrypt session key using privateKey (is used on the server side)
    public static byte[] decryptWithPrivateKey(byte[] data, PrivateKey key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    //This method is used to decrypt message
    // with a symmetric key using the AES algorithm.
    public static byte[] decryptWithSymmetricKey(byte[] data, SecretKeySpec key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    protected static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, SecureRandom.getInstance("SHA1PRNG"));
        return keyPairGenerator.generateKeyPair();
    }

    static PublicKey parsePublicKey(String publicKeyString) throws IOException, GeneralSecurityException {
        // Create a reader for the public key string
        Reader reader = new StringReader(publicKeyString);

        // Parse the PEM content
        PemReader pemReader = new PemReader(reader);
        PemObject pemObject = pemReader.readPemObject();
        pemReader.close();

        // Create a PublicKey object from the DER-encoded bytes
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pemObject.getContent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


}
