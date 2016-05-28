package monitor.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author CIANI Antony
 *
 */
@SuppressWarnings("serial")
public class PCInfo implements Serializable {

	private String hostname;
	private String ipAddress;
	private String macAddress;
	private String os;
	private CPUInfo cpu;
	private HDDInfo hdd;
	private long ramSize;
	private LinkedList<Program> programs;

	/**
	 * @param hostname
	 * @param ipAddress
	 * @param macAddress
	 * @param os
	 * @param cpu
	 * @param hdd
	 * @param ramSize
	 * @param programs
	 */
	public PCInfo(String hostname, String ipAddress, String macAddress, String os, CPUInfo cpu, HDDInfo hdd,
			long ramSize, LinkedList<Program> programs) {
		this.hostname = hostname;
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.os = os;
		this.cpu = cpu;
		this.hdd = hdd;
		this.ramSize = ramSize;
		this.programs = new LinkedList<>(programs);
	}

	/**
	 * @return
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @return
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * @return
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @return
	 */
	public CPUInfo getCpu() {
		return cpu;
	}

	/**
	 * @return
	 */
	public HDDInfo getHdd() {
		return hdd;
	}

	/**
	 * @return
	 */
	public long getRamSize() {
		return ramSize;
	}

	/**
	 * @return
	 */
	public LinkedList<Program> getPrograms() {
		return programs;
	}
}
