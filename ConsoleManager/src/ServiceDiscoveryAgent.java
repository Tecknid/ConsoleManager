
import java.io.BufferedReader;
import javax.bluetooth.DeviceClass; 
import javax.bluetooth.DiscoveryListener; 
import javax.bluetooth.LocalDevice; 
import javax.bluetooth.RemoteDevice; 
import javax.bluetooth.ServiceRecord; 
import javax.bluetooth.UUID; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.util.Vector;
import javax.bluetooth.DataElement;

public class ServiceDiscoveryAgent{

    public static int index = 0;
    public static final Vector serviceFound = new Vector();
    public static UUID  serviceUUIDT;
    public static String url;
    public static boolean protChosen = false; 
    public static boolean opAll = false;
    public static Object devicesFound = BluetoothDeviceDiscovery.deviceDiscovered;
    public static boolean corrIndex = false;
    public static Vector deviceDiscovered = BluetoothDeviceDiscovery.deviceDiscovered;
    public static int chosenProt =0;
    public static String [] protoName = new String[3];
    public static boolean obexChosen = false;
    public static boolean bnepChosen = false;
    public static boolean httpChosen = false;
    public static boolean rfComm = false;
    public static boolean l2Cap = false;
    public static boolean sdp = false;
    public static boolean att = false;
    public static RemoteDevice btDevice;
   
    public static void main(String[] args) throws IOException, InterruptedException {
       BluetoothDeviceDiscovery.main(null);
       System.out.println("Please Select a device from the index to connect to..");
        while(corrIndex!=true){
                    try{
                    BufferedReader read = new BufferedReader(new InputStreamReader(System.in));                   
                    chosenProt = Integer.parseInt(read.readLine().trim());     
                    rangeCheck(chosenProt,deviceDiscovered);
                    }catch(NumberFormatException e){
                        System.out.println("Please enter an index value; not a string!");
                    }                    
                }
                btDevice= (RemoteDevice)deviceDiscovered.elementAt(chosenProt);
                
                

        // First run RemoteDeviceDiscovery and use discoved device
       
        serviceFound.clear();
        System.out.println(" Available Protocols: ");
        System.out.println("1: OBEX (Object Exchange Protocol)");
        System.out.println("2: RFCOMM (Radio Frequency communication)");   
        System.out.println("3: L2CAP (Logical Link Control and Adaptation Layer Protocol)");
        System.out.println("4: SDP (Service Discovery Protocl)");
        System.out.println("5: HTTP (Hyper Text Transfer Protocol)");
        System.out.println("6: BNEP (Bluetooth Netwoek Encapsulation Protocol)");
        System.out.println("7: ATT (Low Energy Attribute Protocol)");
        System.out.println("Please select a protocol to search with..");
        UUID [] chosenProtocol = new UUID[1];
        
        while(protChosen == false){
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        String chosenProt=read.readLine().toLowerCase(); 
      
        
               
            switch (chosenProt) {
                case "obex":                 
                    obexChosen =  true;
                    chosenProtocol[0] = UUIDs.OBEX;
                    protChosen = true;
                    System.out.println("OBEX chosen!");
                    break;
                case "rfcomm":
                    chosenProtocol[0] = UUIDs.RFCOMM;
                    protChosen = true;
                    rfComm = true;
                    System.out.println("RFCOMM chosen!");                  
                    break;      
                case "l2cap":
                    chosenProtocol[0] = UUIDs.L2CAP;
                    l2Cap = true;
                    protChosen = true;
                    System.out.println("L2CAP chosen!");
                    break;       
                case "sdp":
                    chosenProtocol[0] = UUIDs.SDP;
                    sdp = true;
                    protChosen = true;
                    System.out.println("SDP chosen!");
                    break;  
                case "http":
                    chosenProtocol[0] = UUIDs.HTTP;
                    httpChosen = true;
                    protChosen = true;
                    System.out.println("SDP chosen!");
                    break;    
                case "bnep":
                    chosenProtocol[0] = UUIDs.BNEP;
                    bnepChosen = true;
                    protChosen = true;
                    System.out.println("SDP chosen!");
                    break;    
                 case "att":
                    chosenProtocol[0] = UUIDs.ATT;
                    att = true;
                    protChosen = true;
                    System.out.println("ATT chosen!");
                    break; 
                default:
                    System.out.println("Please select a listed Protocol!");
                    break;
            }
         
    }
        
        
        

        final Object serviceSearchCompletedEvent = new Object();

     
        if ((args != null) && (args.length > 0)) {
            serviceUUIDT  = new UUID(args[0], false);
        }

        DiscoveryListener listener = new DiscoveryListener() {

            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            }

            @Override
            public void inquiryCompleted(int discType) {
            }

            @Override
            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
               
                for (ServiceRecord servRecord1 : servRecord) {
                    String url = servRecord1.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    serviceFound.add(url);
                    DataElement serviceName = servRecord1.getAttributeValue(2);
                    if (serviceName != null) {
                          System.out.println("service " + serviceName.getValue() + " found " + url);
                    }else{
                          System.out.println("service found : " + url);
                    }
                }
            }
          

            @Override
            public void serviceSearchCompleted(int transID, int respCode) {
                System.out.println("service search completed!");
                synchronized(serviceSearchCompletedEvent){
                    serviceSearchCompletedEvent.notifyAll();
                }
            }

        };

     
            
        
           
            synchronized(serviceSearchCompletedEvent) {              
                System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(null, chosenProtocol, btDevice, listener);
                serviceSearchCompletedEvent.wait();
                
                if(serviceFound.isEmpty()){
                    System.out.println("No services were found on device " + btDevice.getFriendlyName(false));
                }
                else{
                    serviceFound.forEach((o) -> {
                        index++;
                        System.out.println(index + " :" + o );                         
                    });                    
                }              
        }
    }
    
    //make sure the chosen index for devices are in range
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



