package monitor.model;


import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class PCInfo implements Serializable{

	private String hostname;
	private String ipAddress;
	private String macAddress;
	private String os;
	private CPUInfo cpu;
	private HDDInfo hdd;
	private long ramSize;
	private LinkedList<Program> programs;

	public PCInfo(String hostname, String ipAddress, String macAddress, String os, CPUInfo cpu, HDDInfo hdd, long ramSize, LinkedList<Program> programs){
		this.hostname = hostname;
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.os = os;
		this.cpu = cpu;
		this.hdd = hdd;
		this.ramSize = ramSize;
		this.programs = new LinkedList<>(programs);
	}

	public String getHostname() {
		return hostname;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public String getMacAddress() {
		return macAddress;
	}


	public String getOs() {
		return os;
	}


	public CPUInfo getCpu() {
		return cpu;
	}


	public HDDInfo getHdd() {
		return hdd;
	}

	public long getRamSize() {
		return ramSize;
	}


	public LinkedList<Program> getPrograms() {
		return programs;
	}
}
