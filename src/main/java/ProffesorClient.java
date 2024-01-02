import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.*;

public class ProffesorClient {

    static public void LevelOneTwoDoc() throws Exception {
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
                // Send user input to the server
                if(userInput.equals("signup")){
                    checkLogin=true;
                    System.out.println("please enter your username");
                    String user_name = scanner.nextLine();
                    System.out.println("please enter your password");
                    String user_password = scanner.nextLine();
                    message=userInput+":"+user_name+" - "+user_password;
                    out.println(message);
                }
                else if(userInput.equals("login")){
                    checkSignup=true;
                    System.out.println("please enter your username");
                    String user_name = scanner.nextLine();
                    System.out.println("please enter your password");
                    String user_password = scanner.nextLine();
                    message=userInput+":"+user_name+" - "+user_password;
                    out.println(message);
                    //System.out.println("333:"+in.readLine());
                    String res=in.readLine();
                    System.out.println(res);
                    if(res.equals("loggedin")){
                        System.out.println("please insert your rest information");
                        System.out.println("what's your national number");
                        //national id for user (symetric key)
                        String idcard=scanner.nextLine();//"0123456789abcdef";
                        // idcard+="abcdesf";
                        AES aes=new AES(idcard);
                        aes.init();
                        out.println("idcard:"+idcard);
                        System.out.println(in.readLine());
                        System.out.println("what's your phone number");
                        String phoneNumber=scanner.nextLine();
                        String phoneNumberEncrypted= aes.encrypt(phoneNumber);
                        System.out.println(phoneNumberEncrypted);
                        out.println("phoneNumber:"+phoneNumberEncrypted);
                        System.out.println(in.readLine());
                        System.out.println("what's your ِAddress");
                        String Address=scanner.nextLine();
                        String AddressEncrypted= aes.encrypt(Address);
                        System.out.println(AddressEncrypted);
                        out.println("Address:"+AddressEncrypted);
                        System.out.println(in.readLine());
                        System.out.println("what's your Subject");
                        String Subject=scanner.nextLine();
                        String SubjectEncrypted= aes.encrypt(Subject);
                        System.out.println(SubjectEncrypted);
                        out.println("Subject:"+SubjectEncrypted);
                        System.out.println(in.readLine());
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
                int delaySeconds = 1; // Number of seconds to pause

                try {
                    //   System.out.println("Program started.");

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

        }

        catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostName);
            System.exit(1);
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    static public void levelFourDoc(){
        String hostName = "localhost";
        int portNumber = 4999;
        Scanner scanner=new Scanner(System.in);
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ){
            String userInput;
            String message="";
            System.out.println("please login to continue");
            while ((userInput = stdIn.readLine()) != null) {
                boolean checkLogin=false;
                boolean checkSignup=false;
                    if(userInput.equals("login")){
                    checkSignup=true;
                    System.out.println("please enter your username");
                    String user_name = scanner.nextLine();
                    System.out.println("please enter your password");
                    String user_password = scanner.nextLine();
                    message=userInput+":"+user_name+" - "+user_password;
                    out.println(message);
                    //System.out.println("333:"+in.readLine());
                    String res=in.readLine();
                    System.out.println(res);
                    if(res.equals("loggedin")){
                        System.out.println("please insert subject name ");
                        String subject_name = scanner.nextLine();
                        out.println("subject_name:"+subject_name);
                        System.out.println(in.readLine());
//                        Date current_date= new Date();
//                        String current_date_String=current_date.toString();
//                        System.out.println(current_date_String);
                        //we create a map to store the students mark and name
                        Map<String,String>map=new HashMap<>();
                        System.out.println("enter number of students you want put marks for them");
                        String stud_name;
                        String stud_mark;
                        int num_stud = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline character
                        for (int i = 0; i < num_stud; i++) {
                            System.out.println("Enter the student name");
                            stud_name = scanner.nextLine();
                            System.out.println("Enter the student mark");
                            stud_mark = scanner.nextLine();
                            // Store student name as key and mark as a value
                            map.put(stud_name, stud_mark);
                        }
//                        System.out.println(map.get("mhmd"));
                        //convert a map to String format to send it to server
                        String mapStringMarks = map.toString();
                        KeyPair keyPair= DigitalSignature.generateKeyPair();
                        PrivateKey MyprivateKey=keyPair.getPrivate();
                        PublicKey MypublicKey=keyPair.getPublic();

                        //here we convert public key for doctor to String format
                        String pubKeyDigitalString= DigitalSignature.publicKeyToString(MypublicKey);
                        //here we convert publicKey in multiLine String format to oneLine String format
                        String singleLineStringpubKeyDigitalString = DigitalSignature.multiLineToOneLine(pubKeyDigitalString);
                        //here we put pubKeyDigitalString to let server know what we we'll sended to
                        out.println("pubKeyDigitalString:"+singleLineStringpubKeyDigitalString);
                        // the response from server will be the publicKey in oneLine String format
                        // here we converted it to oneLine String format
                        String serverpubKeyString = DigitalSignature.oneLineToMulti(in.readLine());
                        //here we converted the the publicKey in oneLine String format to PublicKey type
                        PublicKey serverpubKey= DigitalSignature.StringToPublicKey(serverpubKeyString);
                        //here we are generate Digital Signature by using MyprivateKey and mapStringMarks
                        //that done by calling the func generateDigitalSignature
                        byte[] digitalSignatureSigned=
                                DigitalSignature.generateDigitalSignature(mapStringMarks,MyprivateKey);
                        // here we convert the digitalSignatureSigned to encoded String and store
                        // it into digitalSignatureSignedString variable
                        String digitalSignatureSignedString=
                                Base64.getEncoder().encodeToString(digitalSignatureSigned);
                        //here we send our message in normal case
                        out.println("marksmessage:"+mapStringMarks);
                        System.out.println(in.readLine());
                        // here we send our digitalSignatureSignedString to the server and
                        // put the marksofstudents to let server know what will proccess
                        out.println("marksofstudents:"+digitalSignatureSignedString);
                        System.out.println(in.readLine());


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

    static public void levelFiveDoc(){
        String hostName = "localhost";   //the ip of server we want to connected with it
        int portNumber = 4999;
        Scanner scanner=new Scanner(System.in);
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ){
            String userInput;
            String message="";
            System.out.println("press login to continue");
            while ((userInput = stdIn.readLine()) != null) {
                boolean checkLogin=false;
                boolean checkSignup=false;
                if(userInput.equals("login")){
                    checkSignup=true;
                    System.out.println("please enter your username");
                    //input user name
                    String user_name = scanner.nextLine();
                    System.out.println("please enter your password");
                    //input user password
                    String user_password = scanner.nextLine();
                    message=userInput+":"+user_name+" - "+user_password;
                    //send request to the server
                    out.println(message);
                    //storing response in a res variable
                    String res=in.readLine();
                    System.out.println(res);
                    if(res.equals("loggedin")){
                        String userHome = System.getProperty("user.home");
                        //here we specify the desired path as the Desktop folder
                        String path = userHome + File.separator + "Desktop";
                        //path of csr file on proffessorClient side
                        String CSRDoctorFile=path + "\\csr.txt";
                        File fileCSR = new File(CSRDoctorFile);
                        File fileKeystore = new File(path +"\\keystore.jks");
                        //check if csr.txt and keystore.jks files are exists
                        if(fileCSR.exists()&&fileKeystore.exists())
                        {
                            try {
                                //path of certificateFile.txt file on proffessorClient machine
                                String certificateStringFilePath = path + "\\certificateFile.txt";
                                File fileCert = new File(certificateStringFilePath);
                                //check if certificateFile.txt file are exist
                                if (!fileCert.exists()) {

                                    //firstly we want to store csr.txt in variable called CSRDoctor
                                    String CSRDoctor ="";
                                    StringBuilder fileContent = new StringBuilder();
                                    try {
                                        File file = new File(CSRDoctorFile);
                                        Scanner scannerr = new Scanner(file);
                                        while (scannerr.hasNextLine()) {
                                            String line = scannerr.nextLine();
                                            fileContent.append(line).append(System.lineSeparator());
                                        }
                                        scannerr.close();
                                    } catch (FileNotFoundException e) {
                                        System.out.println("File not found: " + e.getMessage());
                                    }
                                    CSRDoctor= fileContent.toString();

                                    System.out.println(CSRDoctor);

                                    //here convert our csr string into oneline and store it in a variable called CSRDoctorOneLine
                                    String CSRDoctorOneLine = DigitalSignature.multiLineToOneLine(CSRDoctor);
                                    System.out.println(CSRDoctorOneLine);
                                    //here we send the csr.txt as a one line string to the server
                                    out.println("csrdoc:" + CSRDoctorOneLine);
                                    //here print the response of the server (this contains equation)
                                    System.out.println(in.readLine());
                                    String result = scanner.nextLine();
                                    //send a answer from proffessor to the server
                                    out.println("resultequation:" + result);
                                    String resp = in.readLine();
                                    //if answer was correct will enter to this
                                    if (!resp.equals("your solution is wrong")) {
                                        String certificateString = resp;
                                        //store a certificateFile.txt in a variable called certificateStringFilePath
                                        certificateStringFilePath = path + "\\certificateFile.txt";
                                        fileCert = new File(certificateStringFilePath);
                                        //if this file wasn't exists
                                        if (!fileCert.exists()) {
                                            try (FileWriter writer = new FileWriter(certificateStringFilePath)) {
                                                writer.write(certificateString);
                                                System.out.println("your certificate has been written to " +
                                                        "the file exist on your Desktop.");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        System.out.println(resp);
                                    }
                                }
                                //if certificate was exist we will enter this
                                else {
                                    System.out.println("you've already have certificate");
                                    certificateStringFilePath = path+"\\certificateFile.txt";
                                    //read content of the certificateFile.txt
                                    String certificateStringFile =
                                            new String(Files.readAllBytes(Paths.get(certificateStringFilePath)));
                                   //send the certificate to the server
                                    out.println("mycertis:" + certificateStringFile);
                                    String resp = in.readLine();
                                    //if certificate is valid we will enter this
                                    if (resp.equals("your certificate is correct now plz give us name of" +
                                            " your subject to give u marks of student")) {
                                        //print the response of the server
                                        System.out.println(resp);
                                        //enter subject name of teacher
                                        String subj_name = scanner.nextLine();
                                        //send the subject name of teacher
                                        out.println("mysubjis:" + subj_name);
                                        //print the response of the server
                                        System.out.println(in.readLine());
                                    } else System.out.println(resp);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //if keystore.jks or csr.txt not exist we will enter to this
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

        //LevelOneTwoDoc();
        levelFiveDoc();
//        levelFourDoc();

    }
}
