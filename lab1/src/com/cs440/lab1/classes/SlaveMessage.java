package com.cs440.lab1.classes;

import java.io.Serializable;

class SlaveMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 101380471340961146L;
	
	private int processId;
	private char action;
    private boolean first;

	SlaveMessage(int pid, char act) {
		processId = pid;
		action    = act;
        first     = false;
	}

    SlaveMessage(int pid, char act, boolean _first) {
        first     = _first;
        processId = pid;
        action    = act;
    }

	public int getProcessId() {
		return processId;
	}

	public char getAction() {
		return action;
	}
    
    public boolean firstTime() {
        return first;
    }
}
