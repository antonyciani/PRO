package monitor.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Program implements Serializable{
	String name;
	String version;


	public Program(String name, String version){
		this.name = name;
		this.version = version;

	}

	public String getName(){
		return name;
	}

	public String getVersion(){
		return version;
	}

}
