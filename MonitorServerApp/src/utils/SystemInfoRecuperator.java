package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Properties;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import monitor.model.*;

/**
 * Cette classe utilitaire fournis des méthodes permettant la récupération
 * d'informations système d'une machine Windows et de les retourner sous forme
 * d'objet PCInfo
 * 
 * @author CIANI Antony
 * @see monitor.model
 *
 */
public class SystemInfoRecuperator {

	/**
	 * Permet de récupérer l'espace libre et l'espace total disponible du disque
	 * dur sur lequel le système est installé
	 * 
	 * @return l'objet HDDInfo contenant l'espace libre et l'espace total
	 *         disponible
	 */
	public static HDDInfo retrieveHDDInfo() {
		File f = new File("C:");
		double freeSpaceGB = f.getFreeSpace() / Math.pow(1024.0, 3);
		double totalSpaceGB = f.getTotalSpace() / Math.pow(1024.0, 3);

		return new HDDInfo(totalSpaceGB, freeSpaceGB);

	}

	/**
	 * Permet de récupérer les informations concernant le processeur de la
	 * machine - Constructeur - Modèle - Fréquence d'horloge - Nombre de coeurs
	 * physiques
	 * 
	 * @return l'objet CPUInfo contenant les informations récupérées
	 */
	public static CPUInfo retrieveCPUInfo() {

		Sigar s = new Sigar();

		try {
			String vendor = s.getCpuInfoList()[0].getVendor();
			String model = s.getCpuInfoList()[0].getModel();
			double frequency = s.getCpuInfoList()[0].getMhz() / 1000.0;
			int nbCores = s.getCpuInfoList()[0].getTotalCores();

			return new CPUInfo(vendor, model, frequency, nbCores);

		} catch (SigarException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Permet de récupérer la liste des programmes installés sur le système
	 * 
	 * @return la liste des programmes récupérés avec nom et numéro de version
	 */
	public static LinkedList<Program> retrieveInstalledPrograms() {

		try {

			LinkedList<Program> programs = new LinkedList<>();
			Process proc = Runtime.getRuntime().exec("wmic product get name,version /format:\""
					+ System.getenv("SystemRoot") + "\\System32\\wbem\\fr-FR\\csv\"");

			BufferedInputStream bs = new BufferedInputStream(proc.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(bs));
			String program = "";

			while ((program = br.readLine()) != null) {

				if (!program.equals("") && !program.equals("Node,Name,Version")) {
					String[] elems = program.split(",");
					String name = "";
					String version = "";
					System.out.println("YOOO: " + elems.length);
					for (int i = 0; i < elems.length; i++) {
						System.out.println(elems[i]);
					}

					System.out.println("P:" + program);
					if (elems.length > 1) {
						name = elems[1];
						if (elems.length == 3) {
							version = elems[2];
							System.out.println(elems[2]);
						}
						Program p = new Program(name, version);
						programs.add(p);
					}

				}

			}

			return programs;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Permet de récupérer toutes les informations système de la machine, les
	 * informations récupérées sont stockées dans un objet PCInfo.
	 * 
	 * @see monitor.model.PCInfo
	 * @return l'objet PCInfo contenant les informations récupérées
	 * 
	 */
	public static PCInfo retrievePCInfo() {

		String hostname = "";
		String ipAddress = "";
		String macAddress = "";
		String os = "";
		CPUInfo cpu = null;
		HDDInfo hdd = null;
		long ramSize = 0;
		LinkedList<Program> programs = new LinkedList<>();

		try {

			hostname = System.getenv("COMPUTERNAME");

			InetAddress ip = InetAddress.getLocalHost();
			ipAddress = ip.getHostAddress();
			System.out.println("Current IP address : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			System.out.print("Current MAC address : ");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			macAddress = sb.toString();
			System.out.println(sb.toString());

			Properties prop = System.getProperties();
			os = prop.getProperty("os.name");

			cpu = retrieveCPUInfo();
			hdd = retrieveHDDInfo();

			Sigar s = new Sigar();
			ramSize = (long) (Math.ceil(s.getMem().getRam() / 1024.0));
			System.out.println("RAM: " + ramSize);

			programs.addAll(retrieveInstalledPrograms());

			return new PCInfo(hostname, ipAddress, macAddress, os, cpu, hdd, ramSize, programs);

		} catch (UnknownHostException e) {
			e.printStackTrace();

		} catch (SocketException e) {
			e.printStackTrace();

		} catch (SigarException e) {
			e.printStackTrace();
		}
		return null;

	}

}
