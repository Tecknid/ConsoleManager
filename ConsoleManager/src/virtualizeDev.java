import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.bluetooth.*;
import java.util.Vector;
import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

public class virtualizeDev {
    
    public static Vector originalDevice = new Vector(BluetoothDeviceDiscovery.deviceDiscovered);
    public static Vector deviceServices = new Vector(ServiceDiscoveryAgent.serviceFound);
    public static RemoteDevice deviceName;
    public static Object serviceName;
    public static int index = 0;
    public static boolean virtualOn = true;
    public static Queue queue = new PriorityQueue();
    public static int portNum = 8888;
    public static ServerSocket serverSocket = null;
    public static int connectedApps = 0;
    public static boolean devicePicked = false;
    public static int timeActive = 0;
    public static boolean corrIndex = false;
    public static int chosenProt;
      
    public  static void run() throws IOException{
    // HashMap<Object,Object> map = new HashMap<>();
           
     Vector originalDevice = new Vector(BluetoothDeviceDiscovery.deviceDiscovered);
     Vector deviceServices = new Vector(ServiceDiscoveryAgent.serviceFound);
        
        //System.out.println(originalDevice);       
        for(int i = 0; i < deviceServices.size();i++){           
            //map.put(originalDevice.elementAt(i),deviceServices.elementAt(i));
            System.out.println(index + ":" + deviceServices.elementAt(i));
            index++;
        }
        
        
            System.out.println("Select a device service to virtualize");
                while(corrIndex!=true){
                    try{
                    BufferedReader read = new BufferedReader(new InputStreamReader(System.in));                   
                    int chosenProt = Integer.parseInt(read.readLine().trim());
                    rangeCheck(chosenProt,deviceServices);
                    }catch(NumberFormatException e){
                        System.out.println("Please enter an index value; not a string!");
                    }
                }
                deviceName = ServiceDiscoveryAgent.btDevice;
                serviceName = deviceServices.elementAt(chosenProt);
                deviceCreation dc = new deviceCreation(deviceName,serviceName);            
                System.out.println("virtual copy created for device: "+ dc.deviceName + " : with service " + dc.serviceName);
                devicePicked = true;
            
           
         do{  
            try{
            if(timeActive > 1){
                 System.out.println("Virtual device has been accessed!" + timeActive);
            }else{
                 System.out.println("Virtual device Accessed :" + timeActive + " times!");
            }
            timeActive++;
            serverSocket = new ServerSocket(portNum);           
            }catch(IOException e){
                System.out.println("Couldn't access port num");
            }
            
            Socket clientSocket = null;
            try{
                clientSocket = serverSocket.accept();
                if(clientSocket.isConnected()){
                    connectedApps++;                   
                }
            }catch(IOException e){
                System.out.println("Accept failed");
            }
            ObjectOutputStream obos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream obis = new ObjectInputStream(clientSocket.getInputStream());
            
            try{
            obis.readObject();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            obos.writeObject(serviceName);
            obos.flush();
            obis.close();
            clientSocket.close();
            serverSocket.close();
            connectedApps--;
            
         }while(connectedApps!=-1);
    }
    
    //inner class to construct the virtual copy
     public final static class deviceCreation{

         private static  RemoteDevice deviceName;
         private static  Object serviceName  ;
         
         public deviceCreation(RemoteDevice d,Object e){
             deviceCreation.deviceName = d;
             deviceCreation.serviceName = e;           
         }
         
         public RemoteDevice getDevice(){
             return deviceName;
         }
         
         public Object getService(){
             return serviceName;
         }
     }
     
     public static void rangeCheck(int index,Vector v){
         try{
         if(index >= v.size() | index < 0){
             System.out.println("Please choose a listed index!");             
         }
         else{
             corrIndex = true;
         }
         }catch(NumberFormatException e){
             System.out.println("Please enter an index, not a string!");
         }
     }
     
}
     

