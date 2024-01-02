
import org.bouncycastle.asn1.ocsp.Signature;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import sun.security.x509.*;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.cert.*;

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
class CertificateGenerator {

    private String ClientName;
    private String issuer;

    public CertificateGenerator(String ClientName,String issuer){
        this.ClientName=ClientName;
        this.issuer=issuer;
    }


    public static String convertCertToString(X509Certificate cert) throws CertificateEncodingException {
        byte[] certBytes = cert.getEncoded();
        return Base64.getEncoder().encodeToString(certBytes);
    }
    public static X509Certificate convertStringToCert(String certStr) throws Exception {
        byte[] certBytes = Base64.getDecoder().decode(certStr);
        java.security.cert.CertificateFactory certFactory = java.security.cert.CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(new java.io.ByteArrayInputStream(certBytes));
    }



    public X509Certificate generateCertificate(PublicKey ClientpublicKey, PrivateKey ServerprivateKey)
            throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // Add the Bouncy Castle provider
        Security.addProvider(new BouncyCastleProvider());
        // Generate a self-signed X.509 certificate
        X509Certificate cert = null;
        try {// Set the validity period for the certificate
            Date startDate = new Date();
            Date endDate = new Date(startDate.getTime() + 365 * 24 * 60 * 60 * 1000L); // 1 year validity
            // Generate a unique serial number for the certificate
            BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
            // Prepare the certificate information
            X500Principal issuer = new X500Principal("CN="+this.issuer);
            X500Principal subject = new X500Principal("CN="+this.ClientName);
            // Create a certificate using the provided information
            X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();
            certGenerator.setSerialNumber(serialNumber);
            certGenerator.setIssuerDN(issuer);
            certGenerator.setSubjectDN(subject);
            certGenerator.setPublicKey(ClientpublicKey);
            certGenerator.setNotBefore(startDate);
            certGenerator.setNotAfter(endDate);
            certGenerator.setSignatureAlgorithm("SHA256WithRSAEncryption");
            // Generate the certificate
            cert = certGenerator.generate(ServerprivateKey, "BC");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return cert;
    }

}