//package com.cs440.lab1.classes;

//TODO send slaves messages for load balancing

import java.util.*;
import java.nio.channels.*;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.io.FileInputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.Serializable;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.io.InputStream;
import java.io.OutputStream;
//import com.cs440.lab1.interfaces.MigratableProcess;

//import com.cs440.lab1.interfaces.MigratableProcess;

/** SlaveHost contains methods for load balancing, most importantly
 *  a list of processes that its host currently holds
 *  the process list is FIFO, so processes that have been on this machine
 *  the longest will be packaged and mailed somewhere
 *  @param _iaddr  The Inet Address which the master will use to communicate
 *	          with the slave
*/
class SlaveHost {
    private InetAddress iaddr;
    private int processCount;
    private LinkedList<Integer> processList;

    SlaveHost(InetAddress _iaddr) {
        iaddr         = _iaddr;
        processCount = 0;
        processList  = new LinkedList<Integer>();
    }

    public InetAddress getInetAddress() {
        return iaddr;
    }

    public Integer popProcess() {
        try {
            if (processList.size() == 0) {
                System.err.println("popProcess: No Processes Remain.  Can't pop");
    			return new Integer(-1);
    		}
        } catch (Exception e) {
            return new Integer(-1);
		}
		return processList.remove(0);
	}
	public void pushProcess(int _pid) {
		try {
			processList.add(_pid);
		} catch (Exception e) {
			System.err.println("pushProcess " + _pid + " failed");
		}
	}
	public LinkedList<Integer> getProcessList() {
		return processList;
	}
    
    public int getLoad() {
        return processList.size();
    }
}


