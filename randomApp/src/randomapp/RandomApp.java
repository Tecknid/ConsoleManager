package randomapp;
import java.io.*;
import java.net.*;

public class RandomApp {
     public static void main(String[] args) throws IOException {

        Object test = new Object();
        test = "test";
        Socket echoSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Object returnedDeviceVal = null;

        try {
            
            echoSocket = new Socket("127.0.0.1", 8888);
            
            out = new ObjectOutputStream(echoSocket.getOutputStream());
            in = new ObjectInputStream(echoSocket.getInputStream());

        } catch (UnknownHostException e) {
            System.out.println("ost not recognised!.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Couldn't get I/O from host ");
            System.exit(0);
        }

        System.out.println ("Sending object to virtual device");
	out.writeObject(test);
        out.flush();

        try {
             returnedDeviceVal = (Object) in.readObject();
            }
        catch (Exception ex)
            {
             System.out.println (ex.getMessage());
            }

	setAttr(returnedDeviceVal);

	out.close();
	in.close();
	echoSocket.close();
        
        
    }
     
     
     
     public static String setAttr(Object returnedDeviceVal){
         String n = (String)returnedDeviceVal;
         System.out.println("Obtained " + n + " from virtual copy!");                  
         return n;
     }
     
     
     public void accessDevice(String n){
         
     }
}


