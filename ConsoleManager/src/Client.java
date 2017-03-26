
import java.io.*;
import javax.obex.*;
import javax.microedition.io.*;
import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.BluetoothStateException;



public class Client {
    private static String serverURL = null;
    private static boolean isConnected = false;
    private static int PORT_NUM = 8888;
    private static ClientSession clientSession;
    private static int responseCode;
    private static boolean choise = false;
    
    public static void main(String [] args) throws IOException, InterruptedException{
        System.setProperty("bluecove.jsr82.psm_minimum_off","true");
        BluetoothDeviceDiscovery.deviceDiscovered.clear();
        virtualizeDev.originalDevice.clear();
        
        if((args!=null && args.length > 0)){
            serverURL = args[0];
        }
       try{ 
        Client.run(); 
        if(!BluetoothDeviceDiscovery.deviceDiscovered.isEmpty() && !ServiceDiscoveryAgent.serviceFound.isEmpty()){
            
            System.out.println("Would you like to virtualize a device? (y/n)");
            BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in)); 
            String message =bReader.readLine().toLowerCase();   
            while(choise!=true){
            switch(message){
                case "y":
                    virtualizeDev.run();
                    choise = true;
                case "n":
                    System.exit(0);
            }
          }
        }
        //virtualizeDev.run();
       }catch(BluetoothStateException e){
          System.out.println("Bluetooth Adapter on this device is not enabled; please enable it before running the client!");
          System.exit(0);
       }
      
    }
     public static  byte [] getServiceInput()throws IOException{
        
        HeaderSet header = clientSession.createHeaderSet();
        header.setHeader(HeaderSet.NAME,"Service Responce");
        header.setHeader(HeaderSet.TYPE,"text");
        
        Operation getOperation = clientSession.get(header);
        InputStream is = getOperation.openInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int getData = is.read();
        while(getData!= -1){
            out.write(getData);
            getData = is.read();
        }
        is.close();
        switch(responseCode){
            case ResponseCodes.OBEX_HTTP_OK:
                return out.toByteArray();
            case ResponseCodes.OBEX_HTTP_NOT_FOUND:
                return null;
        }
        
       return null;
    }
  
    
    public static void run() throws IOException, InterruptedException{
        
        if(serverURL == null){
            String [] searchArgs = null;
            ServiceDiscoveryAgent.main(searchArgs);
           
            
            if(ServiceDiscoveryAgent.serviceFound.equals(0)){
                System.out.println("No services were found.");
                return;
            }
            
            if(ServiceDiscoveryAgent.serviceFound.isEmpty()){
                System.out.println("No services are left to explore!");
                return;
            }else{
            serverURL = (String)ServiceDiscoveryAgent.serviceFound.elementAt(0);
            }
        }
         System.out.println("Connecting to " + serverURL);
         
       try{     
        ClientSession clientSession = (ClientSession) Connector.open(serverURL);  
        //ClientSession serverSession = (ClientSession) Connector.openDataInputStream(serverURL);
        HeaderSet hsConnectReply = clientSession.connect(null);
        if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
            System.out.println("Failed to connect");
            return;
        }else{
            isConnected = true;
        }
        
        HeaderSet hsOperation = clientSession.createHeaderSet();
        hsOperation.setHeader(HeaderSet.NAME, "Client Message");
        hsOperation.setHeader(HeaderSet.TYPE, "text");

        //Create PUT Operation
        System.out.println("Connected to " + serverURL);
        
        
        while(isConnected!=false){
            //PUT operations
            Operation putOperation = clientSession.put(hsOperation);
           
            System.out.println("Write a message!");
            BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in)); 
            String message =bReader.readLine();   
            OutputStream os = putOperation.openOutputStream();
            InputStream is = putOperation.openInputStream();
        
            if("virtualize".equals(message)){
                virtualizeDev.run();
            }
            
            if("close".equals(message)){
                System.out.println("Disconnecting from " + serverURL);
                isConnected = false;                   
            }
            byte data[] = message.getBytes("iso-8859-1");
            os.write(data);
            
            os.close();
            putOperation.close();                                
            }
        
        clientSession.disconnect(null);

        clientSession.close();
        if(isConnected == false){
            System.out.println("Disconnect from device..");
        }
        }catch(BluetoothConnectionException e){
           System.out.println("Pairing issue occured, corresponding device did not pair in time!");            
         }catch(IOException d){
             System.out.println("Connection between client and device has been dropped!. Closing Client connection..");    
         }
    } 
    
   
}



  /*   
            //GET operations
             HeaderSet header = clientSession.createHeaderSet();
             header.setHeader(HeaderSet.NAME,"Service Responce");
             header.setHeader(HeaderSet.TYPE,"text");
        
            Operation getOperation = clientSession.get(header);
            InputStream is = getOperation.openInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int getData = is.read();
            
                while(getData!= -1){
                    out.write(getData);
                    getData = is.read();
                }
            is.close();
            
            
            switch(responseCode){
                case ResponseCodes.OBEX_HTTP_OK:
                    System.out.println(out.toByteArray());
                case ResponseCodes.OBEX_HTTP_NOT_FOUND:
                    System.out.println("No messages recieved from Server");
            } */ 