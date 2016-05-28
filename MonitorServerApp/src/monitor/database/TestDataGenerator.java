package monitor.database;

import java.util.LinkedList;
import java.util.Random;

import monitor.model.*;

/**
 * @author STEINER Lucie
 *
 */
public class TestDataGenerator {

	private static final String[] PROGRAM_NAMES = { "Internet Explorer", "Microsoft Word", "Microsoft Excel",
			"Notepad++", "Eclipse", "Windows Media Player", "Google Chrome", "Mozilla Firefox", "Netbeans", "Skype",
			"Paint", "TeXstudio", "StarUML", "Telegram", "MySQL Workbench", "MarkdownPad", "IntelliJ IDEA", "PyCharm",
			"Atom", "Wireshark" };

	private static final String[] PROGRAM_VERSIONS = { "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "2.0", "2.1", "2.2",
			"2.3", "2.4", "2.5" };

	private static final String[] OS = { "Windows 7", "Windows 8", "Windows 8.1", "Windows 10" };
	private static final double[] FREQUENCY = { 2.1, 2.4, 2.7, 3.0 };
	private static final int[] NB_CORES = { 2, 4, 8, 16, 32 };
	private static final double[] TOTAL_HDD_SIZE = { 250, 500, 1000, 2000 };
	private static final long[] RAM_SIZE = { 4, 8, 16, 32, 64, 128 };
	private static final String[] CONSTRUCTOR = { "Intel", "AMD" };
	private static final String[] MODEL = { "Core i3", "Core i5", "Corei7", "Pentium", "Celeron", "FX-8350", "FX-6350",
			"FX-4300", "A4-4020" };
	// But: remplir la base de donn�es avec des infos permettant de montrer
	// des graphiques repr�sentatifs et l'utilisation des filtres

	/**
	 * @return
	 */
	public static String generateIPAddress() {
		int rand = 0;
		StringBuilder ip = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			rand = (int) (Math.random() * 256);
			ip.append(rand).append('.');
		}
		rand = (int) (Math.random() * 256);
		return (ip.append(rand)).toString();
	}

	/**
	 * @return
	 */
	public static String generateMACAddress() {
		int rand = 0;
		StringBuilder ip = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			rand = (int) (Math.random() * 256);
			ip.append(Integer.toHexString(rand)).append(':');
		}
		rand = (int) (Math.random() * 256);
		return (ip.append(Integer.toHexString(rand))).toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database db = new Database("jdbc:mysql://localhost:3306/inventory", "root", "1234");
		db.connect();
		Random r = new Random();
		// Cr�ation de processeurs
		LinkedList<PCInfo> parc = new LinkedList<>();

		// Construction du parc informatique --> cr�er 20 postes
		/*
		 * private String hostname; --> pc*(variable de boucle) private String
		 * ipAddress; --> g�n�r�e une fois al�atoirement au d�but private String
		 * macAddress; --> g�n�r�e une fois al�atoirement au d�but private
		 * String os; --> tableau private CPUInfo cpu; private String
		 * constructor; --> tableau private String model; --> tableau private
		 * double frequency; --> tableau private int numbCore; --> tableau
		 * private HDDInfo hdd; private double totalSize; --> tableau private
		 * double freeSize; --> g�n�rer al�atoirement en fonction de total,puis
		 * faire �voluer private long ramSize; --> tableau private
		 * LinkedList<Program> programs; String name; --> tableau String
		 * version; --> tableau
		 */
		for (int i = 0; i < 20; i++) {
			String hostname = "PC" + i;
			String ip = generateIPAddress();
			String mac = generateMACAddress();
			String os = OS[r.nextInt(OS.length)];
			long ram = RAM_SIZE[r.nextInt(RAM_SIZE.length)];
			String constructor = CONSTRUCTOR[r.nextInt(CONSTRUCTOR.length)];
			String model = MODEL[r.nextInt(MODEL.length)];
			double freq = FREQUENCY[r.nextInt(FREQUENCY.length)];
			int nbCores = NB_CORES[r.nextInt(NB_CORES.length)];
			double totalSize = TOTAL_HDD_SIZE[r.nextInt(TOTAL_HDD_SIZE.length)];
			double freeSize = Math.random() * totalSize;
			// G�n�rer programmes-> nombre al�atoire de programmes
			// install�s, choix al�atoire des prorgammes et des versions
			LinkedList<Program> programs = new LinkedList<>();
			// G�n�rer nb programs
			int nbPrograms = r.nextInt(PROGRAM_NAMES.length);
			// Boucle avec choix programme et version
			for (int j = 0; j < nbPrograms; j++) {
				programs.add(new Program(PROGRAM_NAMES[r.nextInt(PROGRAM_NAMES.length)],
						PROGRAM_VERSIONS[r.nextInt(PROGRAM_VERSIONS.length)]));
			}
			parc.add(new PCInfo(hostname, ip, mac, os, new CPUInfo(constructor, model, freq, nbCores),
					new HDDInfo(totalSize, freeSize), ram, programs));

		}
		db.storePCs(parc);
		// R�p�ter 10 fois
		// Modification de l'espace
		for (int i = 0; i < 9; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (PCInfo pc : parc) {
				double freesize = pc.getHdd().getFreeSize();
				pc.getHdd().setFreeSize(Math.random() * freesize);
			}
			// Enregistrement des PC dans la DB
			db.storePCs(parc);
			// Modifications PC
		}
	}
}
