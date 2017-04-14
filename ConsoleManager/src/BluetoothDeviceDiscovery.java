

import javax.bluetooth.*;
import java.io.*;
import java.util.Vector;


public class BluetoothDeviceDiscovery{

public static boolean indexChosen = false;
public DiscoveryAgent agent;     
public static String connectionURL = null;     //connectionURL for devices
public final static Vector deviceDiscovered = new Vector();
public static int index = 0; 
public static String chosenIndex;
//public static boolean started;

 
public static void main(String[] args) throws IOException,InterruptedException {
    
final Object inquiryCompletedEvent = new Object();
deviceDiscovered.clear();

 DiscoveryListener listener = new DiscoveryListener() {

            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
                deviceDiscovered.addElement(btDevice);               
                try {
                    System.out.println("Name: " + btDevice.getFriendlyName(false));                                
                }catch (IOException cantGetDeviceName) {}
            }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] srs) {}

    @Override
    public void serviceSearchCompleted(int i, int i1) {}

    @Override
    public void inquiryCompleted(int i) {
        System.out.println("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
    }    
};
          synchronized(inquiryCompletedEvent) {
          
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
          
            if (started) {
                System.out.println("waiting for device inquiry to complete...");
                inquiryCompletedEvent.wait(150000);
                System.out.println(deviceDiscovered.size() +  " device(s) found");
                if(deviceDiscovered.isEmpty()){
                    System.out.println("No devices were found!");
                    System.exit(0);
                }else{                  
                    deviceDiscovered.forEach((o) -> {
                    System.out.println(index + " :" + o  );
                    index++;
                    });
                }
            }
        }
}    
}

