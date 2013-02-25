import java.io.*;
import java.lang.*;

public class RemoteObjectRef {
    InetAddress iAddr;
    int port;
    int objKey;
    String remoteInterfaceName;

    public RemoteObjectRef(InetAddress _iAddr, int _port, int _objKey, String iname) {
        iAddr  = _iAddr;
        port   = _port;
        objKey = _objKey;
        remoteInterfaceName = iname;
    }

    Object localise() {
    

    }
