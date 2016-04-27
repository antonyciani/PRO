package monitor.model;

import java.io.Serializable;

public class Program implements Serializable{
	String name;
	String version;
	String lastUpdate;

	public Program(String name, String version, String lastUpdate){
		this.name = name;
		this.version = version;
		this.lastUpdate = lastUpdate;
	}

	public String getName(){
		return name;
	}

	public String getVersion(){
		return version;
	}

	public String getLastUpdate(){
		return lastUpdate;
	}
}
