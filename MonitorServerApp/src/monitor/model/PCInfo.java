package monitor.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Cette classe permet de stocker les informations relatives au PC.
 *
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
	 * Constructeur, construit l'objet à partir des infos relatives au PC.
	 *
	 * @param hostname, le nom d'hôte du PC
	 * @param ipAddress, l'adresse IP du PC
	 * @param macAddress, l'adresse MAC du PC
	 * @param os, l'OS du PC
	 * @param cpu, les informations relatives au CPU du PC
	 * @param hdd, les informations relatives au disque dure
	 * @param ramSize, la taille de la mémoire RAM
	 * @param programs, les informations relatives aux programmes installés sur le PC
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
	 * @return le nom d'hôte du PC
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @return l'adresse IP du PC
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return l'adresse MAC du PC
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * @return l'OS du PC
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @return les informations relatives au CPU du PC
	 */
	public CPUInfo getCpu() {
		return cpu;
	}

	/**
	 * @return les informations relatives au disque dure
	 */
	public HDDInfo getHdd() {
		return hdd;
	}

	/**
	 * @return la taille de la mémoire RAM
	 */
	public long getRamSize() {
		return ramSize;
	}

	/**
	 * @return les informations relatives aux programmes installés sur le PC
	 */
	public LinkedList<Program> getPrograms() {
		return programs;
	}
}
