import java.io.*;
import java.net.*;
import java.lang.*;

public class RMIServerThread extends Thread {

    Socket clientSock;

    public RMIServerThread (Socket _clientSock) {
        clientSock = _clientSock;
    }


    public void run () {
        //do message things
    }

}
