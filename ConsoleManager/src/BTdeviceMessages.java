import com.intel.bluetooth.RemoteDeviceHelper;
import java.io.BufferedReader;
import javax.bluetooth.BluetoothStateException; 
import javax.bluetooth.DeviceClass; 
import javax.bluetooth.DiscoveryListener; 
import javax.bluetooth.LocalDevice; 
import javax.bluetooth.RemoteDevice; 
import javax.bluetooth.ServiceRecord; 
import javax.bluetooth.UUID; 
import java.io.IOException; 
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList; 
import java.util.Arrays; 
import java.util.List; 
import javax.obex.*;
import javax.microedition.io.Connector;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.DataElement;
import static javax.microedition.io.Connector.READ_WRITE;

public class BTdeviceMessages {
    private boolean deviceOn = false;
    private static ClientSession clientSession;
    
    public static void main(String [] args){
        
      //  SessionNotifier serverConnection = (SessionNotifier)Connector.openDataInputStream(clientSession);
        
    }
    
    
    
    
}
