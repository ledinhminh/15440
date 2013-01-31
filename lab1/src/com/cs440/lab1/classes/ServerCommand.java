package com.cs440.lab1.classes;

import java.io.Serializable;


public class ServerCommand implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1057915750237578761L;
	String type;
	String arguments;
	
	public ServerCommand(String _type, String _args) {
		this.type = _type;
		this.arguments = _args;
	}
	
	public String getType() {
		return type;
	}
	
	public String getArgs() {
		return arguments;
	}
	
}
