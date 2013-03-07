import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;


public class RMIClient1 {

    public static Map<String, RemoteObjectRef> nameToROR
                        = Collections.synchronizedMap(new HashMap());



    public static void main (String[] args) {
        
        if (args.length != 2) {
            System.err.println("Wrong # of args!");
            return;
        }

        int port = new Integer(args[1]).intValue();

        RMIRegistry rmireg;
        try {
            rmireg = new RMIRegistry(args[0], port);
        } catch (UnknownHostException e) {
            System.err.println("could not find host: " + args[0]);
            return;
        }
        try {

            RemoteObjectRef ref  = rmireg.getRemoteObjectRef("TestRemoteObject");
            TestRemoteObject_stub myStub  = (TestRemoteObject_stub)ref.localise();
            RemoteObjectRef ref2 = rmireg.getRemoteObjectRef("KevinBravo");  
            TestRemoteObject_stub myStub2 = (TestRemoteObject_stub)ref2.localise();
            
            //test small methods
            try {
                myStub.setS("anus");
                System.out.println(myStub.getS());
                System.out.println(myStub.concatS(" prickle"));
                try {
                    myStub.throwException();
                } catch (Exception e) {
                    System.out.println("hi");
                }
                myStub.incrementErp(myStub2);
                System.out.print(myStub.getErp(myStub2));
            } catch (Remote440Exception re) {
                re.printErrorMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Remote440Exception e) {
            e.printErrorMessage();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{Thread.sleep(2000);}catch (Exception e){}

    }
 }

