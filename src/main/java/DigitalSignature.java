import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DigitalSignature {

    public static String publicKeyToString(PublicKey publicKey) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(new PemObject("PUBLIC KEY", publicKey.getEncoded()));
//                System.out.println( publicKey.getEncoded());
        pemWriter.flush();
        pemWriter.close();
        // Get the PEM-encoded public key as a string
        String publicKeyString = stringWriter.toString();
//        System.out.println("WWWWWWWWWWWWWWWWWWWWWW");
//        System.out.println(publicKeyString);
        return publicKeyString;
        // Send the public key to the client (here, we simply print it)
//                System.out.println("Public key:\n" + publicKeyString);

    }

    public static String multiLineToOneLine(String str){
        String singleLineStringpubKeyDigitalString = str.replace(System.lineSeparator(), " ");
        return singleLineStringpubKeyDigitalString;
    }


    //convert publickey in oneline to multiLine
    public static String oneLineToMulti(String req){
        int num=0;String str="";
        for (int i = 0; i < req.length(); i++) {
            if(req.charAt(i)=='-')num++;
            if(num==10&&req.charAt(i)!='-')str+=req.charAt(i);
        }
        String[] liness = str.split("\\s+"); // Split based on space delimiter
        String multiLineStringDigitalSigg = String.join(System.lineSeparator(), liness);
        int firstLineBreakIndex = multiLineStringDigitalSigg.indexOf("\n");
        String stringWithoutFirstLine = multiLineStringDigitalSigg.substring(firstLineBreakIndex + 1);
        String stringWithNewFirstLine = "-----BEGIN PUBLIC KEY-----" + "\n" + stringWithoutFirstLine+"\n"+"-----END PUBLIC KEY-----";
        return stringWithNewFirstLine;
        //        System.out.println(stringWithNewFirstLine);


    }


    //this method convert the MultiLine string to publicKey
    public static PublicKey StringToPublicKey(String publicKeyString) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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

    protected static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }


    // This method takes the original message and a private key of sender
    // as input and generates a digital signature for the message
    public static byte[] generateDigitalSignature(String message,PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // Create a Signature instance
        Signature signature = Signature.getInstance("SHA256withRSA");
        // Initialize Signature instance for signing using the private key
        signature.initSign(privateKey);
        // Update data to be signed
        signature.update(message.getBytes());
        // Generate digital signature
        byte[] digitalSignature = signature.sign();
        return digitalSignature;
    }


    // This method takes a public key, the original message as a byte array,
    // and the digital signature as a byte array.It verifies whether the digital signature
    //  is valid for the given message using the public key of reciver
    public static boolean isVerifiedSignature(PublicKey publicKey,byte[] messageInBytes,byte[]digitalSignature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        /**
         * This line creates a Signature object using the getInstance method,
         * specifying the algorithm "SHA256withRSA".
         * This algorithm uses the SHA-256 hash function and the RSA encryption algorithm for signature verification.
         * */
        Signature signature = Signature.getInstance("SHA256withRSA");

        /**
         * This line initializes the Signature object for verification mode using the provided publicKey.
         **/
        signature.initVerify(publicKey);


        /**
         * This line updates the Signature object with the original messageInBytes,
         * which means the signature algorithm will process this message for verification.
         * */
        signature.update(messageInBytes);

        /**
         * This line verifies the digitalSignature against the provided messageInBytes
         * using the initialized Signature object.
         * It returns true if the signature is valid and false otherwise.*/
        boolean verified = signature.verify(digitalSignature);

        return verified;
    }



}
