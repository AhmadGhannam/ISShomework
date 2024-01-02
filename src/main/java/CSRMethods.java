import java.io.*;

public class CSRMethods {

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
        String stringWithNewFirstLine = "-----BEGIN NEW CERTIFICATE REQUEST-----" + "\n"
                + stringWithoutFirstLine+"\n"+"-----END NEW CERTIFICATE REQUEST-----";
        return stringWithNewFirstLine;
    }
}