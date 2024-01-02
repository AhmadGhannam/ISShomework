import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.Base64;
import java.util.Scanner;

public class StudentClient {

    public static void LevelOneTwoStud() throws Exception {

            Connection connection = null;
            Scanner scanner=new Scanner(System.in);
            System.out.println("to request ip address worked on our server press ipserver");
            System.out.println("to request port number worked on our server press ipport");
            System.out.println("to sign up press signup");
            System.out.println("to log in press login");
            String hostName = "localhost";
            int portNumber = 4999;
            try (
                    Socket clientSocket = new Socket(hostName, portNumber);
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
            ) {
                String userInput;
                String message="";
                while ((userInput = stdIn.readLine()) != null) {

                    boolean checkLogin=false;
                    boolean checkSignup=false;
                    // check if userInput=signup
                     if(userInput.equals("signup")){
                        checkLogin=true;
                        System.out.println("please enter your username");
                        String user_name = scanner.nextLine();
                        System.out.println("please enter your password");
                        String user_password = scanner.nextLine();
                        message=userInput+";"+user_name+" - "+user_password;
                        out.println(message);
                         System.out.println(in.readLine());
                    }
                    else  // check if userInput=login
                        if(userInput.equals("login")){
                        checkSignup=true;
                        System.out.println("please enter your username");
                        String user_name = scanner.nextLine();
                        System.out.println("please enter your password");
                        String user_password = scanner.nextLine();
                        message=userInput+";"+user_name+" - "+user_password;
                        //send message to the server
                        out.println(message);
                        //print response on student console
                        String res=in.readLine();
                        System.out.println(res);
                        //if username and password is correct then response will be "loggedin" from server
                        if(res.equals("loggedin")){
                            //tell user to enter rest info
                            System.out.println("please insert your rest information");
                            System.out.println("what's your national number");
                            //national id for user (symetric key)
                            String idcard=scanner.nextLine();//"0123456789abcdef";
                            //create obj from AES class
                            AES aes=new AES(idcard);
                            //initialize this obj
                            aes.init();
                            //send national id (symetric key) to the server
                            out.println("idcard;"+idcard);
                            ////print response from server on student console
                            System.out.println(in.readLine());
                            System.out.println("what's your phone number");
                            String phoneNumber=scanner.nextLine();
                            //encrypt the phoneNumber was student entered
                            String phoneNumberEncrypted= aes.encrypt(phoneNumber);
                            //print encrypted phoneNumber was student entered
                            System.out.println(phoneNumberEncrypted);
                            //send encrypted phoneNumber was student entered to the server
                            out.println("phoneNumber;"+phoneNumberEncrypted);
                            //print response from server on student console and so on
                            System.out.println(in.readLine());
                            System.out.println("what's your ِAddress");
                            String Address=scanner.nextLine();
                            String AddressEncrypted= aes.encrypt(Address);
                            System.out.println(AddressEncrypted);
                            out.println("Address;"+AddressEncrypted);
                            System.out.println(in.readLine());
                        }
                        else {
                            //if username or password is wrong will print this
                            System.out.println("your password or username is wrong");
                        }
                    }

                    if(!checkLogin&&!checkSignup){
                        out.println(userInput);
                        // Receive and display server's response
                        System.out.println( in.readLine());
                    }
                    // Break the loop if the user inputs "exit"
                    if (userInput.equalsIgnoreCase("exit")) {
                        break;
                    }
                    int delaySeconds = 1; // Number of seconds to pause

                    try {
                        // Pause the program execution for the specified number of seconds
                        Thread.sleep(delaySeconds * 1000); // Convert seconds to milliseconds

                        // System.out.println("Program resumed after " + delaySeconds + " seconds.");
                        System.out.println("to request ip address worked on our server press ipserver");
                        System.out.println("to request port number worked on our server press ipport");
                        System.out.println("to sign up press signup");
                        System.out.println("to log in press login");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + hostName);
                System.exit(1);
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                System.err.println("Couldn't get I/O for the connection to " + hostName);
                System.exit(1);
            }

        }

    static public void LevelThree() throws IOException, GeneralSecurityException {
        Scanner scanner=new Scanner(System.in);
        System.out.println("to log in press login");
        String hostName = "localhost";
        int portNumber = 4999;
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;
            String message="";
                userInput = "login"; //stdIn.readLine();
                 if(userInput.equals("login")){
                    System.out.println("please enter your username");
                    String user_name = scanner.nextLine();
                    System.out.println("please enter your password");
                    String user_password =  scanner.nextLine();
                    //by putting ';' the server will know this user is student
                    message=userInput+";"+user_name+" - "+user_password;
                    //send message to server
                    out.println(message);
                    //System.out.println("333:"+in.readLine());
                    String res=in.readLine();
                    System.out.println(res);
                    if(res.equals("loggedin")){
//                        System.out.println("please insert your rest information");
                        // create obj from PGPEncryption class
                        PGPEncryption pgpEncryption=new PGPEncryption();
                        //generated keyPair
                        KeyPair keyPair = pgpEncryption.generateKeyPair();
                        //extracted private key from keyPair
                        PrivateKey MyprivateKey = keyPair.getPrivate();
                        //extracted public key from keyPair
                        PublicKey MypublicKey = keyPair.getPublic();
                        //convert our public key to string to send it to server and store it in MypublicKeyString variable
                        String MypublicKeyString= DigitalSignature.publicKeyToString(MypublicKey);
                        //send this public key in string format and in one line by using multiLineToOneLine func
                        //and put pubKey to let server know we'll send a public key
                        out.println("pubKey;"+ DigitalSignature.multiLineToOneLine(MypublicKeyString));
//                      //we generated a sessionKey (symetric key) by using func generateSymmetricKey()
                        SecretKey sessionKey = pgpEncryption.generateSymmetricKey();
                        //here we converted response public key string was come in one line to multi line by using oneLineToMulti func
                        String serverpubKeyString = DigitalSignature.oneLineToMulti(in.readLine());
//                        System.out.println("pubserverKey:\n"+serverpubKeyString);
                        //here we converted the server public key from String format to PublicKey format
                        PublicKey serverpubKey = PGPEncryption.parsePublicKey(serverpubKeyString);
                        //here we use encryptWithPublicKey func to encrypt session key by using server public key (server as a reciver)
                        byte[]sessionKeyInBytes= PGPEncryption.encryptWithPublicKey(sessionKey.getEncoded(),serverpubKey);
//                        System.out.println(Base64.getEncoder().encodeToString(sessionKeyInBytes));
                        //here we send our session key as a encoded String format by using Base64.getEncoder().encodeToString
                        //we put sessionkeyencrypted to let server know what we'll send and to know what to proccess
                        out.println("sessionkeyencrypted;"+Base64.getEncoder().encodeToString(sessionKeyInBytes));
                        //print response from server on console student
                        System.out.println(in.readLine());
                        System.out.println("plz insert your projects completed in one line and put - between them");
                        String projectsCompletedMessage =  scanner.nextLine();
                        //String mess="Math - science - phyisics";
                        //convert the input of student to byte[]
                        byte[]messInBytes=projectsCompletedMessage.getBytes(StandardCharsets.UTF_8);
                        //here we encrypt messInBytes using session key by using func encryptWithSymmetricKey
                        byte[]encMessInBytes= PGPEncryption.encryptWithSymmetricKey(messInBytes,sessionKey);
                        // we use EncryptMess method to combines the encrypted symmetric key
                        // (encryptedSymmetricKey) and the encrypted message (encryptedMessage)
                        // into a single byte array using a ByteBuffer (MessEncrypted in our case).
                        byte[]MessEncrypted=PGPEncryption.EncryptMess(sessionKeyInBytes,encMessInBytes);
                        //here we send a message after we encrypted to server as String encoded format
                        out.println("messageencrypted;"+Base64.getEncoder().encodeToString(MessEncrypted));
                        //print response server on our console
                        System.out.println(in.readLine());

                    }
                    else {
                        System.out.println(in.readLine());
                        System.out.println("your password or username is wrong");
                    }
                }
        }
        catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostName);
            System.exit(1);
        }

    }

    static public void levelFiveStud(){
        String hostName = "localhost";
        int portNumber = 4999;
        Scanner scanner=new Scanner(System.in);
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ){
            String userInput="login";
            String message="";
            System.out.println("press login to continue");
            while ((userInput = stdIn.readLine()) != null) {
                boolean checkLogin=false;
                boolean checkSignup=false;
                if(userInput.equals("login")){
                    checkSignup=true;
                    System.out.println("please enter your username");
                    String user_name = scanner.nextLine();
                    System.out.println("please enter your password");
                    String user_password = scanner.nextLine();
                    message=userInput+";"+user_name+" - "+user_password;
                    out.println(message);
                    //System.out.println("333:"+in.readLine());
                    String res=in.readLine();
                    System.out.println(res);
                    if(res.equals("loggedin")){
                        String userHome = System.getProperty("user.home");
                        String path = userHome + File.separator + "Desktop";
                        String CSRStudentFile = path+"\\csr.txt";
                        File fileCSR = new File(CSRStudentFile);
                        File fileKeystore = new File(path +"\\keystore.jks");
                        if(fileCSR.exists()&&fileKeystore.exists()){
                            try {
                                String certificateStringFilePath = path+"\\certificateFile.txt";
                                File fileCert = new File(certificateStringFilePath);
                                if(!fileCert.exists()){
                                    // Read the file content into a string
                                    String CSRStudent ="";// new String(Files.readAllBytes(Paths.get(CSRStudentFile)));
                                    StringBuilder fileContent = new StringBuilder();
                                    try {
                                        File file = new File(CSRStudentFile);
                                        Scanner   scannerr = new Scanner(file);

                                        while (scannerr.hasNextLine()) {
                                            String line = scannerr.nextLine();
                                            fileContent.append(line).append(System.lineSeparator());
                                        }

                                        scannerr.close();
                                    } catch (FileNotFoundException e) {
                                        System.out.println("File not found: " + e.getMessage());
                                    }

                                    CSRStudent= fileContent.toString();
                                    // Use the fileContent variable as needed
                                    System.out.println(CSRStudent);
                                    String CSRStudentOneLine= DigitalSignature.multiLineToOneLine(CSRStudent);
                                    System.out.println(CSRStudentOneLine);
                                    out.println("csrdoc;"+CSRStudentOneLine);
                                    System.out.println(in.readLine());
                                    String result=scanner.nextLine();
                                    out.println("resultequation;"+result);
                                    String resp=in.readLine();
                                    if(!resp.equals("your solution is wrong")){
                                        String certificateString=resp;
                                        certificateStringFilePath = path+"\\certificateFile.txt";
                                        fileCert = new File(certificateStringFilePath);
                                        if(!fileCert.exists()){
                                            try (FileWriter writer = new FileWriter(certificateStringFilePath)) {
                                                writer.write(certificateString);
                                                System.out.println("your certificate has been written to the file exist on your Desktop.");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    else {
                                        System.out.println(resp);
                                    }
                                }
                                else {
                                    System.out.println("you've already have certificate");
                                    certificateStringFilePath = path+"\\certificateFile.txt";
                                    String certificateStringFile =
                                            new String(Files.readAllBytes(Paths.get(certificateStringFilePath)));
                                    // Use the fileContent variable as needed
                                    System.out.println(certificateStringFile);
                                    Certificate certificate=
                                            CertificateGenerator.convertStringToCert(certificateStringFile);
                                    X509Certificate x509Cert = (X509Certificate) certificate;
                                    out.println("mycertis;"+certificateStringFile);
                                    String resp=in.readLine();

                                    if(resp.equals("your marks of all your subjects is:"))
                                    {
                                        System.out.println(resp);
                                        //String MyNameToGetMarks=scanner.nextLine();
                                        out.println("mymarksis;"+user_name);
                                        System.out.println(in.readLine());
                                    }
                                    else System.out.println(resp);
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            System.out.println("your keystore.jks or csr.txt not exist");
                        }
                    }
                    else {
                        System.out.println("your password or username is wrong");
                    }
                }
                if(!checkLogin&&!checkSignup){
                    out.println(userInput);
                    // Receive and display server's response
                    System.out.println( in.readLine());
                }
                // Break the loop if the user inputs "exit"
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
                return;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        LevelOneTwoStud();
        //LevelThree();
        //levelFiveStud();

    }


}
