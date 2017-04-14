
import com.intel.bluetooth.MicroeditionConnector;
import com.intel.bluetooth.NotSupportedIOException;
import java.io.*;
import java.net.Socket;
import javax.obex.*;
import javax.microedition.io.*;
import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.BluetoothStateException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;



public class Client{
    private static String serverURL = null;
    private static boolean isConnected = false;
    private static int responseCode;
    private static boolean choise = false;
    public static final String STACK_WIDCOMM = "widcomm";
    
    public static void main(String [] args) throws IOException, InterruptedException{  
        //removes PCM value restriction for JSR-82,set the default bluetooth stack from winsock to widcomm
        System.setProperty("bluecove.jsr82.psm_minimum_off","true");
      //11  System.setProperty("bluecove.stack","widcomm");
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
       }catch(BluetoothStateException e){
          System.out.println("Bluetooth Adapter on this device is not enabled; please enable it before running the client!");
          System.exit(0);
       }
      
    }
     public static  byte [] getServiceInput(Operation op)throws IOException{
        
        
        InputStream is = op.openInputStream();
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
         
        if(ServiceDiscoveryAgent.obexChosen == true){
            getOperation();
        }else if(ServiceDiscoveryAgent.rfComm == true){
            rfCommOperation();              
        }else if(ServiceDiscoveryAgent.l2Cap == true){   
          l2capOperation();
        }else if(ServiceDiscoveryAgent.sdp == true){
          getOperation();
        }else if(ServiceDiscoveryAgent.sdp == true){
          l2capOperation();
        }
        else{
            System.out.println("No protcol seems to have been chosen; will carry out and OBEX operation...");
            try{
                ClientSession clientSession = (ClientSession) Connector.open(serverURL);
                HeaderSet hsCon = clientSession.connect(null);
                if(hsCon.getResponseCode()!= ResponseCodes.OBEX_HTTP_ACCEPTED){
                    System.out.println("pinged " + serverURL + " :Recieved ACCEPTED Responce.");
                }else if((hsCon.getResponseCode()!= ResponseCodes.OBEX_HTTP_BAD_REQUEST)){
                    System.out.println("Responce Code BAD_REQUEST recieved.");
                }else{
                    System.out.println("Could not ping the device: " + serverURL);
                }
            }catch(NotSupportedIOException d){
                System.out.println("Not supported on Winsock");
                System.exit(0);
            }
        }
      }
   

public static void getOperation(){
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


        
        
        while(isConnected!=false){
            //PUT operations
            Operation putOperation = clientSession.put(hsOperation);
           
            System.out.println("Write a message!");
            BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in)); 
            String message =bReader.readLine();   
            OutputStream os = putOperation.openOutputStream();
          //  InputStream is = putOperation.openInputStream();
        
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

     public static void rfCommOperation()throws IOException{
           try {   
        StreamConnection con = 
            (StreamConnection) Connector.open(serverURL);
        OutputStream os = con.openOutputStream();
        InputStream is = con.openInputStream();
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(isr);
        RemoteDevice dev = RemoteDevice.getRemoteDevice(con);
        
    
      while (isConnected!=false) {   
     System.out.println("Server Found:" 
         +dev.getBluetoothAddress()+"\r\n"+"Put your string"+"\r\n");
        String str = bufReader.readLine();
        if("close".equals(str)){
            System.out.println("Disconnecting from server...");
            isConnected = false;
        }
        os.write( str.getBytes());
        byte buffer[] = new byte[1024];
        int bytes_read = is.read( buffer );
        String received = new String(buffer, 0, bytes_read);
        System.out.println("client: " + received
         + "from:"+dev.getBluetoothAddress()); 
      } 
         
        }
  catch(Exception e){}  
     }
     
     public static void l2capOperation() throws IOException{
         try {
                String UUID = "7140b25b7bd741d6a3ad0426002febcd";
                String url = serverURL;

        if (url == null) {
            System.out.println("No receiver in range");
            return;
        }
            url=url+";ReceiveMTU=1691;TransmitMTU=1691";
            System.out.println("Connecting to " + url);
    
    L2CAPConnection conn = (L2CAPConnection) Connector.open(url);
    System.out.println("max MTU that server can receive="+conn.getTransmitMTU( ));
    System.out.println("max MTU that server can send="+conn.getReceiveMTU( ));
    byte[] test=new byte[1691];
    
    for(int i=0;i<test.length;i++){
        test[i]=(byte)0xff;
    }
    int received=1;
    System.out.println("Send test packet");
    conn.send(test);
    byte[]receive_from_server=new byte[1691];
    int receive = conn.receive(receive_from_server);
    System.out.println("receive: "+receive);
    System.out.println("Received packet from server:");

for(int i=0; i<received; i++)System.out.println("Elem "+i+" : "+
(receive_from_server[i]&0xff));
System.out.println("");
}
catch (IOException ex) {
    ex.printStackTrace( );
}
     }


}


