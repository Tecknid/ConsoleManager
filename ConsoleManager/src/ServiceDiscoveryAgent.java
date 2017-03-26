
import java.io.BufferedReader;
import javax.bluetooth.DeviceClass; 
import javax.bluetooth.DiscoveryListener; 
import javax.bluetooth.LocalDevice; 
import javax.bluetooth.RemoteDevice; 
import javax.bluetooth.ServiceRecord; 
import javax.bluetooth.UUID; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DataElement;

public class ServiceDiscoveryAgent{

    public static int index = 0;
    public static final Vector serviceFound = new Vector();
    public static final UUID testUUID = new UUID("00112233445566778899AABBCCDDEEFF",false);
    public static UUID  serviceUUIDT;
    public static String url;
    public static boolean protChosen = false; 
    public static boolean opAll = false;
    public static String chosenDev = BluetoothDeviceDiscovery.chosenIndex;
   
    public static void main(String[] args) throws IOException, InterruptedException {

        // First run RemoteDeviceDiscovery and use discoved device
        BluetoothDeviceDiscovery.main(null);
        serviceFound.clear();
        System.out.println(" Available Protocols: ");
        System.out.println("1: OBEX");
        System.out.println("2: RFCOMM");   
        System.out.println("3: L2CAP");
        System.out.println("Please select a protocol to search with..");
        UUID [] chosenProtocol = new UUID[2];
        int[] attrIDs =  new int[1];
        
        while(protChosen == false){
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        String chosenProt=read.readLine().toLowerCase(); 
      
        
               
            switch (chosenProt) {
                case "obex":                   
                    chosenProtocol[0] = UUIDs.OBEX;
                    protChosen = true;
                    System.out.println("OBEX chosen!");
                    break;
                case "rfcomm":
                    chosenProtocol[0] = UUIDs.RFCOMM;
                    protChosen = true;
                    System.out.println("RFCOMM chosen!");
                    break;
       
                case "l2cap":
                    chosenProtocol[0] = UUIDs.L2CAP;
                    protChosen = true;
                    System.out.println("L2CAP chosen!");
                    break;
                  case "all":
                    chosenProtocol[0] = UUIDs.L2CAP;
                    chosenProtocol[1] = UUIDs.RFCOMM;
                    chosenProtocol[2] = UUIDs.OBEX;
                    opAll = true;
                    protChosen = true;
                    System.out.println("L2CAP chosen!");
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

       
        
           
            for(Enumeration en = BluetoothDeviceDiscovery.deviceDiscovered.elements(); en.hasMoreElements(); ) {
            RemoteDevice btDevice = (RemoteDevice)en.nextElement();

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
            
        

    }

} 


