
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DamascusServer {


    public static void main(String[] args) throws IOException {

        int port = 4999; // Specify the port number you want to listen on

        try {
            // Create a ServerSocket that listens on the specified port
            ServerSocket serverSocket = new ServerSocket(port);

            System.out.println("Server started and listening on port " + port);

            while (true) {
                // Accept client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Handle the client request in a separate thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ClientHandler class to handle each client request in a separate thread
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String url = "jdbc:mysql://localhost:3306/isshomework"; // Replace with your database URL
        private String user_name;
        private String user_password;
        private String idCard;
        private boolean isStud=false;
        private boolean isDoc=false;
        private PrivateKey MyprivateKey;
        private PublicKey MypublicKey;
//        private byte [] decryptedKeyBytes;
//        private String encryptedKey="";
        private byte[] decryptedSessionKeyH;
        private PublicKey publicKeyDigitalSignatureClient;
        private PublicKey MypublicKeyDigitalSignature;
        private PrivateKey MyprivateKeyDigitalSignature;
        private String MapStringMarks;
        private String subject_name;
        private String CSRClient;
        private PublicKey CSRpublicKeyClient;
        private int rand1;
        private int rand2;
        private int resultEquation;
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }


        //this method for handling client requests in a server application
        @Override
        public void run() {
            try {
                // we sets up input and output streams to communicate with the client.It uses BufferedReader
                // to read input from the client and PrintWriter to send output back to the client.
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()), 8192);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String request;
                // It enters a loop that reads client requests line by line until
                // the client closes the connection or sends an "exit" command.
                while ((request = in.readLine()) != null) {
                    System.out.println("Client request: " + request);
                    //we initializes two empty strings, req1 and req2,
                    //which will hold the parsed parts of the request.
                    String req1="",req2="";int idx=0;
                for (int i = 0; i < request.length(); i++) {
                    if(request.charAt(i)==':') {
                        idx=i+1;
                        this.isDoc=true;
                        break;
                    }
                    else if(request.charAt(i)==';') {
                        idx=i+1;
                        this.isStud=true;
                        break;
                    }
                    req1+=request.charAt(i);
                }
                for (int i = idx; i < request.length(); i++) {
                    req2+=request.charAt(i);
                }

                // Process the client request based on the command
                String response="no response";

                // Process the client request and send a response
                response = processRequest(req1,req2);
                System.out.println("LL\n"+req1);
                    System.out.println(req2);
                    // Respond to the client
                    out.println(response);
                    // Break the loop if the client sends "exit"
                    if (request.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
            }
            catch (Exception e) {
                System.err.println("Exception caught when trying to listen on port " + 4999 + " or listening for a connection");
                System.err.println(e.getMessage());
            }
        }

        //////////////////////////////////////// testing functions /////////////////////////////////////////
        private String performFunction1() {
            // Implement the logic for function1
            return "Response from function1";
        }

        private String performFunction2() {
            // Implement the logic for function2
            return "Response from function2";
        }

        //////////////////////////////////////// LEVEL 1 ///////////////////////////////////////////////////

        //give u the server ip
        private String serverIp(){
            Connection connection = null;
            String serverIp="";
            try {
                // Provide database credentials
                String url = "jdbc:mysql://localhost:3306/isshomework";
                String username = "root";
                String password = "root";
                // Create a connection
                connection = DriverManager.getConnection(url, username, password);
                // Perform database operations...
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT USER()");
                while (resultSet.next()) {
//                 Retrieve data from the result set
                     serverIp = resultSet.getString("USER()");
//                 Process the retrieved data
                    System.out.println("the server IP our server worked on is : " + serverIp);
                }
                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the connection
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "the server IP our server worked on is : "+serverIp;
        }

        //give u the server port
        private String severPort(){
            Connection connection = null;
            String portNumber="";
            try {
                // Provide database credentials
                String url = "jdbc:mysql://localhost:3306/isshomework";
                String username = "root";
                String password = "root";
                // Create a connection
                connection = DriverManager.getConnection(url, username, password);
                // Perform database operations...
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SHOW VARIABLES WHERE Variable_name = 'port'");
                while (resultSet.next()) {
//                 Retrieve data from the result set
                    portNumber = resultSet.getString("value");
//                 Process the retrieved data
                    System.out.println("the port number our server worked on is : " + portNumber);
                }
                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the connection
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "the port number our server worked on is : "+portNumber;
        }

        private String signup(String user_name, String user_password){
            String mess="";
            String username = user_name;
            String userpassword = user_password;
            try {
                // Establish the database connection
                Connection connection = DriverManager.getConnection( url,"root", "root");

                String sql="";
                // Prepare the SQL statement for insertion
                if(isDoc)
                sql = "INSERT INTO professors (name, password) VALUES (?, ?)";
                else sql = "INSERT INTO students (name, password) VALUES (?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);

                // Set the parameter values for the SQL statement
                statement.setString(1, username);
                statement.setString(2, userpassword);

                // Execute the SQL statement
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    mess="Data inserted successfully.";
                    System.out.println("Data inserted successfully.");
                } else {
                    mess="Data inserted successfully.";
                    System.out.println("Data insertion failed.");
                }
                // Close the statement and connection
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }
        private String login(String user_name, String user_password){
            String mess="";
            try (Connection connection = DriverManager.getConnection(url, "root", "root")) {
                String query="";
                System.out.println("D:"+isDoc);
                System.out.println("S:"+isStud);
                if(isDoc)
                query = "SELECT COUNT(*) FROM professors WHERE name = ? && password = ? ";
                else query = "SELECT COUNT(*) FROM students WHERE name = ? && password = ? ";
                String searchData = "$user_name"; // Replace with the data you want to search

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, user_name);
                statement.setString(2, user_password);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count > 0) {
                    mess="loggedin";
                    this.user_name=user_name;
                    this.user_password=user_password;
                    System.out.println("Data exists in the database.");
                } else {
                    mess="Data does not exist in the database.";
                    System.out.println("Data does not exist in the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }

        //store national id for users
        private String storeIdCard(String idCard) {
            String mess = "";
            this.idCard=idCard;
            System.out.println("D:"+isDoc);
            System.out.println("S:"+isStud);
            String name = this.user_name; // Replace with the specific name
            String passwordInput = this.user_password; // Replace with the specific password
            String nationalId = idCard; // Replace with the national ID you want to insert

            try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
                String query="";
                if(isDoc)
                query = "UPDATE professors SET nationalid = ? WHERE name = ? AND password = ?";
                else query = "UPDATE students SET nationalid = ? WHERE name = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, nationalId);
                    stmt.setString(2, name);
                    stmt.setString(3, passwordInput);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        mess="National ID inserted successfully for " + name;
                        System.out.println("National ID inserted successfully for " + name);
                    } else {
                        mess="idCard: No matching records found for the given name and password.";
                        System.out.println("idCard: No matching records found for the given name and password.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }

        //////////////////////////////////////// LEVEL 2 ///////////////////////////////////////////////////

        //store phone number for users
        private String storephoneNumber(String phoneNumberEncrypted) throws Exception {
            String mess="";
            AES aes=new AES(this.idCard);
            aes.init();
            //decrypt honeNumber by using decrypt method in AES class
            String DecryptedPhoneNumber=aes.decrypt(phoneNumberEncrypted);
            System.out.println("KK:"+DecryptedPhoneNumber);
            String name = this.user_name; // Replace with the specific name
            String passwordInput = this.user_password; // Replace with the specific password
            //String nationalId = idCard; // Replace with the national ID you want to insert

            try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
                String query="";
                if(isDoc)
                query = "UPDATE professors SET phonenumber = ? WHERE name = ? AND password = ?";
                else query = "UPDATE students SET phonenumber = ? WHERE name = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, DecryptedPhoneNumber);
                    stmt.setString(2, name);
                    stmt.setString(3, passwordInput);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        mess="Decrypted Phone Number inserted successfully for " + name;
                        System.out.println("Decrypted Phone Number inserted successfully for " + name);
                    } else {
                        mess="No matching records found for the given name and password.";
                        System.out.println("No matching records found for the given name and password.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }

        //store address for users
        private String storeAddress(String EncryptedAddress) throws Exception {
            String mess="";
            AES aes=new AES(this.idCard);
            aes.init();
            String DecryptedAddress=aes.decrypt(EncryptedAddress);
            System.out.println("KK:"+DecryptedAddress);
            String name = this.user_name; // Replace with the specific name
            String passwordInput = this.user_password; // Replace with the specific password
            //String nationalId = idCard; // Replace with the national ID you want to insert

            try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
                String query="";
                if(isDoc)
                query = "UPDATE professors SET address = ? WHERE name = ? AND password = ?";
                else query = "UPDATE students SET address = ? WHERE name = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, DecryptedAddress);
                    stmt.setString(2, name);
                    stmt.setString(3, passwordInput);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        mess="Decrypted Address inserted successfully for " + name;
                        System.out.println("Decrypted Address inserted successfully for " + name);
                    } else {
                        mess="No matching records found for the given name and password.";
                        System.out.println("No matching records found for the given name and password.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }

        //Fill subject_name culomn for specific proffessor
        private String storeSubjectDoc(String SubjectEncrypted) throws Exception {
            String mess="";
            AES aes=new AES(this.idCard);
            aes.init();
            String DecryptedSubject=aes.decrypt(SubjectEncrypted);
            System.out.println("KK:"+DecryptedSubject);
            String name = this.user_name; // Replace with the specific name
            String passwordInput = this.user_password; // Replace with the specific password
            //String nationalId = idCard; // Replace with the national ID you want to insert

            try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
                String query = "UPDATE professors SET subject_name = ? WHERE name = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, DecryptedSubject);
                    stmt.setString(2, name);
                    stmt.setString(3, passwordInput);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        mess="DecryptedSubject inserted successfully for " + name;
                        System.out.println("DecryptedSubject inserted successfully for " + name);
                    } else {
                        mess="No matching records found for the given name and password.";
                        System.out.println("No matching records found for the given name and password.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }


        //////////////////////////////////////// LEVEL 3 ///////////////////////////////////////////////////

        //this insert a project completed of student in DB
        private String insertProjectCompleted(String projectCompleted) throws SQLException {

            String[] array = projectCompleted.split(" - ");
            for (int i = 0; i < array.length; i++) {
                System.out.println(array[i]);
            }
            String url = "jdbc:mysql://localhost:3306/isshomework";
            String username = "root";
            String password = "root";
            String response="";
            String user_name = this.user_name; // Replace with the specific name
            String user_password = this.user_password; // Replace with the specific password


            String sql = "UPDATE students SET projectscompleted = ? WHERE name = ? AND password = ?";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                // Iterate over the array and insert each string
                for (String str : array) {
                    statement.setString(1, projectCompleted); // Set the parameter value
                    statement.setString(2,user_name);
                    statement.setString(3,user_password);
                    statement.executeUpdate(); // Execute the INSERT statement
                }
                response="Data inserted successfully.";
                System.out.println("Data inserted successfully.");

            } catch (SQLException e) {

                e.printStackTrace(); response="Data inserted failed.";
            }
            return response;
        }

        //////////////////////////////////////// LEVEL 4 ///////////////////////////////////////////////////

        //this method used to know id of student to store it as a foregin key in a DB
        private int getStudentId(String studentName) {
            int studentId = -1;
            try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
                String query = "SELECT student_id FROM students WHERE name = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, studentName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            studentId = rs.getInt("student_id");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return studentId;
        }

        //this method used to know id of subject to store it as a foregin key in a DB
        private int getSubjectId(String subjectName) {
            int subjectId = -1;
            try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
                String query = "SELECT subject_id FROM subjects WHERE subject_name = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, subjectName);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            subjectId = rs.getInt("subject_id");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return subjectId;
        }


        //storing marks for specific subject
        private String storeMarks(Map<String, String> markStudMap) {
            String mess = "";
            java.util.Date current_date = new java.util.Date();
            String current_date_String = current_date.toString();
            try (Connection conn = DriverManager.getConnection(url, "root", "root")) {
                String query = "INSERT INTO marks (student_id, subject_id, student_name, subject_name, mark, Date) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    for (Map.Entry<String, String> entry : markStudMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        int studentId = getStudentId(key);
                        int subjectId = getSubjectId(this.subject_name);
                        stmt.setInt(1, studentId);
                        stmt.setInt(2, subjectId);
                        stmt.setString(3, key);
                        stmt.setString(4, this.subject_name);
                        stmt.setString(5, value);
                        stmt.setString(6, current_date_String);
                        stmt.addBatch();  // Add batch for batch processing
                    }
                    int[] rowsInserted = stmt.executeBatch();  // Execute the batch

                    // Check the result for each individual insert
                    int totalRowsInserted = 0;
                    for (int rows : rowsInserted) {
                        if (rows > 0) {
                            totalRowsInserted += rows;
                        }
                    }

                    if (totalRowsInserted > 0) {
                        mess = "Marks of students have been verified and inserted successfully for subject: " + this.subject_name;
                    } else {
                        mess = "Failed to insert marks of students.";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }

        //////////////////////////////////////// LEVEL 5 ///////////////////////////////////////////////////

        //storing serial number for certificate we generate from this CA
        private String storeSerialNumber(BigInteger bigInteger){
            String serialNumber=bigInteger.toString(),sql="",mess="";
            try {
                // Establish the database connection
                Connection connection = DriverManager.getConnection( url,"root", "root");

                sql = "INSERT INTO serialnumbercertificate (serial_number_certificate, user_name,stud_or_doc) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);

                // Set the parameter values for the SQL statement
                statement.setString(1, serialNumber);
                statement.setString(2, this.user_name);
                if(isDoc)
                statement.setString(3, "professor");
                else  statement.setString(3, "student");

                // Execute the SQL statement
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    mess="serial number inserted successfully.";
//                    System.out.println("Data inserted successfully.");
                } else {
                    mess="serial number insertion failed.";
//                    System.out.println("Data insertion failed.");
                }
                // Close the statement and connection
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return mess;
        }

        //ensure if the serial number of certificate is from our CA or not by checking if exist in our DB or not
        private boolean isSerialNumberExist(String serial_number){
            try (Connection connection = DriverManager.getConnection(url, "root", "root")) {
                String query="";
                 query = "SELECT COUNT(*) FROM serialnumbercertificate WHERE serial_number_certificate = ? ";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, serial_number);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
                if (count > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        //this for return the marks of subject that proffessor teach it
        private String  giveMeMarksForMySubject(String subject_name) {
            // JDBC URL, username, and password
            String jdbcURL = "jdbc:mysql://localhost:3306/isshomework";
            // SQL query to retrieve data from columns
            String columnName1 = "student_name"; // Replace with your column name 1
            String columnName2 = "mark"; // Replace with your column name 2
            String query = "SELECT " + columnName1 + ", " + columnName2 + " FROM marks WHERE subject_name = ?";
            String ans = "";
            try (Connection connection = DriverManager.getConnection(jdbcURL, "root", "root");
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, subject_name); // Set the subject_name parameter
                ResultSet resultSet = statement.executeQuery();
                ans = "{{";
                // Process the result set
                while (resultSet.next()) {
                    // Retrieve data from the columns and store in variables
                    String dataFromColumn1 = resultSet.getString(columnName1);
                    String dataFromColumn2 = resultSet.getString(columnName2);
                    // Use the retrieved data as needed
                    ans += dataFromColumn1;ans += '=';ans += dataFromColumn2;ans += ',';
                }ans += '}';ans += '}';
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ans;
        }

        //checking if the person who has a certificate is the real person and don't fake client it
        private boolean isYouTeacherOfSubject(String subject_name){
            try (Connection connection = DriverManager.getConnection(url, "root", "root")) {
                String query="";
                query = "SELECT COUNT(*) FROM subjects WHERE subject_name = ? AND professor_name = ? ";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, subject_name);
                statement.setString(2, this.user_name);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);
                if (count > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        //this return the marks of student who has a certificate valid
        private String  giveMeMyMarks() {
            // JDBC URL, username, and password
            String jdbcURL = "jdbc:mysql://localhost:3306/isshomework";
            // SQL query to retrieve data from columns
            String columnName1 = "subject_name"; // Replace with your column name 1
            String columnName2 = "mark"; // Replace with your column name 2
            String query = "SELECT " + columnName1 + ", " + columnName2 + " FROM marks WHERE student_name = ?";
            String ans = "";
            try (Connection connection = DriverManager.getConnection(jdbcURL, "root", "root");
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, this.user_name); // Set the subject_name parameter
                ResultSet resultSet = statement.executeQuery();
                ans = "{";
                // Process the result set
                while (resultSet.next()) {
                    // Retrieve data from the columns and store in variables
                    String dataFromColumn1 = resultSet.getString(columnName1);
                    String dataFromColumn2 = resultSet.getString(columnName2);
                    // Use the retrieved data as needed
                    ans += dataFromColumn1;ans += '=';ans += dataFromColumn2;ans += ',';
                }ans += '}';
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ans;
        }
        //this execute "openssl command" to extract a public key from CSR of client and put in the Desktop of server
        private void ExecuteCommandToExtractPublicKeyFromCSRofClient(){
            try {// Specify the command you want to run
                String command = "openssl.exe req -in C:\\Users\\Ahm\\Desktop\\csr.txt" +
                        " -pubkey -noout > C:\\Users\\Ahm\\Desktop\\publickey.pem";
                // Specify the desired path
                String path = "C:\\Program Files\\OpenSSL-Win64\\bin";
                // Create a ProcessBuilder with the command and working directory
                ProcessBuilder processBuilder;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                    processBuilder.directory(new File(path));
                } else {
                    processBuilder = new ProcessBuilder(command.split("\\s+"));
                }
                // Redirect the error stream to the input stream
                processBuilder.redirectErrorStream(true);
                // Start the process
                Process process = processBuilder.start();
                // Read the output of the process
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                // Wait for the process to complete
                int exitCode = process.waitFor();
                System.out.println("Command exited with code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        //this store the public key (was extracted to the Desktop) in local variable called "CSRpublicKeyClient"
        private void CSRtoPublicKey(String publicKeyClientPath) throws Exception {
            try (PemReader reader = new PemReader(new FileReader(publicKeyClientPath))) {
                PemObject pemObject = reader.readPemObject();
                byte[] publicKeyBytes = pemObject.getContent();
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(keySpec);
                this.CSRpublicKeyClient =publicKey;
                // Use the publicKey variable as needed
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }










        //////////////////////////////////////////////////////////////////////////////////////////////////////
        private String processRequest(String request,String body) throws Exception {
            // Process the client request and generate a response

            String response="no response";
            //////////////////////////////////////// for testing ///////////////////////////////////////////////
            if (request.equals("function1")) {
                response = performFunction1();
                System.out.println(response);
            }
            //////////////////////////////////////// LEVEL 1 ///////////////////////////////////////////////////
            else if (request.equals("ipserver")) {
                response = serverIp();
//                    System.out.println(response);
            }
            else if (request.equals("ipport"))   {
                response = severPort();
//                    System.out.println(response);
            }
            else if (request.equals("signup"))   {
                //here we split message of signup into user_name and user_password
                String user_name="",user_password="";int idx=0;
                for (int i = 0; i < body.length(); i++) {
                    if(body.charAt(i+1)=='-'){
                        idx=i+2;
                        break;
                    }
                    user_name+=body.charAt(i);
                }
                for (int i = idx+1; i < body.length(); i++) {
                    user_password+=body.charAt(i);
                }
               response= signup(user_name,user_password);
            }
            else if (request.equals("login"))    {
                String user_name="",user_password="";int idx=0;
                for (int i = 0; i < body.length(); i++) {
                    if(body.charAt(i+1)=='-'){
                        idx=i+2;
                        break;
                    }
                    user_name+=body.charAt(i);
                }
                for (int i = idx+1; i < body.length(); i++) {
                    user_password+=body.charAt(i);
                }
                response= login(user_name,user_password);
            }
            else if (request.equals("idcard"))   {
                response = storeIdCard(body);
            }
            //////////////////////////////////////// LEVEL 2 ///////////////////////////////////////////////////
            else if (request.equals("phoneNumber")) {
                System.out.println("phon:"+body);
                response = storephoneNumber(body);
            }
            else if (request.equals("Address"))     {
                response = storeAddress(body);
            }
            else if (request.equals("Subject"))     {
                response = storeSubjectDoc(body);
            }
            //////////////////////////////////////// LEVEL 3 ///////////////////////////////////////////////////
            else if (request.equals("pubKey"))              {
                //here we generate and send public key to client and storet it in a variable
                PGPEncryption pgpEncryption=new PGPEncryption();
                KeyPair keyPair = pgpEncryption.generateKeyPair();
                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();
                this.MypublicKey  =publicKey;
                this.MyprivateKey =privateKey;
                String publicKeyString= DigitalSignature.publicKeyToString(this.MypublicKey);
                publicKeyString= DigitalSignature.multiLineToOneLine(publicKeyString);
                response=publicKeyString;
            }
            else if (request.equals("sessionkeyencrypted")) {
                //here we decrypt session key was arriveed from client using privatekey server
                byte[] byteArr = Base64.getDecoder().decode(body);
//                System.out.println("ENC:"+Base64.getEncoder().encodeToString(byteArr));
                byte[] decryptedSessionKey = PGPEncryption.decryptWithPrivateKey(byteArr, this.MyprivateKey);
                this.decryptedSessionKeyH=decryptedSessionKey;
//                System.out.println("DEC:"+Base64.getEncoder().encodeToString(decryptedSessionKey));
                response="sessionKey decrypted";
            }
            else if (request.equals("messageencrypted"))    {
                //here we decrypt message from client and insert it project completed in our DB
                byte[] byteArr = Base64.getDecoder().decode(body);
//                System.out.println("ENCMess:"+Base64.getEncoder().encodeToString(byteArr));
                byte[] decryptedMessage = PGPEncryption.decryptMess(byteArr, decryptedSessionKeyH);
//                System.out.println("DEC:"+new String(decryptedMessage));
//                System.out.println();
                response=insertProjectCompleted(new String(decryptedMessage));
            }
            //////////////////////////////////////// LEVEL 4 ///////////////////////////////////////////////////
            else if (request.equals("subject_name"))        {
                this.subject_name=body;
                response="your subject name set successfully it's waiting a verify your digital signature to insert in server";
            }
            else if (request.equals("pubKeyDigitalString")) {
                System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");

                //here we recieve a public key of client and generate our public key and send it to the client
                this.publicKeyDigitalSignatureClient= DigitalSignature.StringToPublicKey(DigitalSignature.oneLineToMulti(body));
                KeyPair keyPair= DigitalSignature.generateKeyPair();
                PublicKey publicKey=keyPair.getPublic();
                PrivateKey privateKey=keyPair.getPrivate();
                this.MypublicKeyDigitalSignature=publicKey;
                this.MyprivateKeyDigitalSignature=privateKey;
                response= DigitalSignature.publicKeyToString(publicKey);
                response= DigitalSignature.multiLineToOneLine(response);
            }
            else if (request.equals("marksmessage"))        {
                this.MapStringMarks=body;
                response="your marks: "+body+" arrived";
            }
            else if (request.equals("marksofstudents"))     {
                System.out.println("QWEWQEWQE:"+this.MapStringMarks);
                //here we ensure if the message is verify or not using publicKey of client
                //and message (this.MapStringMarks.getBytes() in our case) and
                //digital signature of client and if was verified we'll store marks
                //in our DB
                byte[] decodingdigitalSignatureSignedString = Base64.getDecoder().decode(body);
                boolean isVerified=
                        DigitalSignature.isVerifiedSignature
                                (this.publicKeyDigitalSignatureClient,
                                        this.MapStringMarks.getBytes(),
                                        decodingdigitalSignatureSignedString);
                this.MapStringMarks=this.MapStringMarks.replace("Map:", "").trim();
                String cleanedString = this.MapStringMarks.replaceAll("[{}]", "");
                Map<String, String> convertedMap = new HashMap<>();
                String[] keyValuePairs = cleanedString.split(",");

// Iterate over the key-value pairs and populate the converted map
                for (String pair : keyValuePairs) {
//                    System.out.println(pair);
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
//                        System.out.println(key+"   "+value);
                        convertedMap.put(key, value);
                    }
                }
                System.out.println(convertedMap.get("mhmd"));
                System.out.println(convertedMap);
                if(isVerified){
                    response=storeMarks(convertedMap);
                }
                else
                response= "your data is not verified";
            }
            //////////////////////////////////////// LEVEL 5 ///////////////////////////////////////////////////
            else if (request.equals("csrdoc"))         {
                //the body of request will be the csr.txt of the client in one line but we store it in a CSRClient
                //variable using CSRMethods.oneLineToMulti function which takes the body as a parameter
                this.CSRClient =CSRMethods.oneLineToMulti(body);
                System.out.println(this.CSRClient);
                //here we write the csr.txt of client on the file stores on the server
                    try (FileWriter writer = new FileWriter("C:\\Users\\Ahm\\Desktop\\csr.txt",false)) {
                        writer.write(this.CSRClient);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //this called here to execute "openssl command" to extract a
                // public key from CSR of client and put in the Desktop of server
                ExecuteCommandToExtractPublicKeyFromCSRofClient();
                //this store the public key (was extracted to the Desktop) in local variable called "CSRpublicKeyClient"
                CSRtoPublicKey("C:\\Users\\Ahm\\Desktop\\publickey.pem");
                //here we prepare the Equation
                Random random = new Random();
                this.rand1=random.nextInt(10)+1;
                this.rand2=random.nextInt(10)+1;
                System.out.println(this.rand1);
                System.out.println(this.rand2);
                //store solution in a local variable
                this.resultEquation=this.rand1+this.rand2;
                //store the response to send it to the client
                response="the CSR File was Arrived " + this.user_name
                        +",, now plz solve this Equation: "+String.valueOf(this.rand1)+" + "+String.valueOf(this.rand2)+" =";
            }
            //this process the solution of  client
            else if (request.equals("resultequation")) {
                //here we convert the correct solution of equation to string
                String result= Integer.toString(this.resultEquation);
                //here we compare the solution of user (body) with correct solution if correct will enter to this
                if(result.equals(body)){
                    KeyPair keyPair= DigitalSignature.generateKeyPair();
                    //here we create object from class called CertificateGenerator we use this class to generate a certificate
                    //and pass two args user name and name of CA
                    CertificateGenerator certificateGenerator=
                            new CertificateGenerator(this.user_name,"Certificate Authority is Damascus University");

                    //here we generate a certificate using generateCertificate function exists in class CertificateGenerator
                    //and pass two parameters the private key of server (the CA) and public key of client
                    X509Certificate certificate=
                            certificateGenerator.generateCertificate
                                    (this.CSRpublicKeyClient,keyPair.getPrivate());
                    //here storing serial number for certificate we generated above
                    storeSerialNumber(certificate.getSerialNumber());
                    //store the certificate to send it to the client after we
                    //converted to string using convertCertToString func to send it
                    response= CertificateGenerator.convertCertToString(certificate);
                }
                else response="your solution is wrong";
            }
            //this process pf certificate from client
            else if (request.equals("mycertis"))       {
                //firstly we coonverted the body from client to certificate type
                Certificate certificate=CertificateGenerator.convertStringToCert(body);
                //here we cast the type to X509Certificate to reach for specific values exist in the certificate
                X509Certificate x509Cert = (X509Certificate) certificate;
                //this is the current date
                java.util.Date current_date = new java.util.Date();
                //this compare the date will certificate become invalid before it and the current date
                int comparisonBefore = x509Cert.getNotBefore().compareTo(current_date);
                //this compare the date will certificate become invalid after it and the current date
                int comparisonAfter  = x509Cert.getNotAfter().compareTo(current_date);
                // this checking if the date of certificate is valid and the serial number of certificate
                // exists of CA database and if the person who send this certificate is the same name
                // exists inside this certificate
                if(comparisonBefore<0&&comparisonAfter>0&&isSerialNumberExist
                        (((X509Certificate) certificate).getSerialNumber().toString())&&
                        x509Cert.getSubjectDN().toString().equals("CN="+this.user_name)){
                    //if the sender is proffessor
                    if(isDoc) response= "your certificate is correct now plz give us " +
                                    "name of your subject to give u marks of student";
                    else response="your marks of all your subjects is:";
                }
                else //if the certificate was invalid the response will be
                response="your certificate is invalid";
            }
            //after ensuring the certificate is valid this give the proffessorClient marks of all students for his subject
            else if (request.equals("mysubjis"))       {
                //store the students marks of subject
                String ans= giveMeMarksForMySubject(body);
                //ensure the teacher is the real teacher of the subject not other teacher
                if(isYouTeacherOfSubject(body))
                response="the marks is: "+ans;
                else response="sorry you're not the teacher of this subject";
            }
            //this process the request of the student client
            else if (request.equals("mymarksis"))      {
                //give the student his marks
                response="your Marks is: "+giveMeMyMarks();
            }

            //////////////////////////////////////// otherwise /////////////////////////////////////////////////

            else {
                response = "Invalid command";
                System.out.println(response);
            }
            return response;
        }
    }



}



