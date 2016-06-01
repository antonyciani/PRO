package monitor.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import monitor.model.*;

/**
 * Cette classe permet de remplir la base de données avec des données générées aléatoirement.
 * Elle est utilisée pour tester le bon fonctionnement de l'application et permet également de
 * procéder à une démonstration de l'application.
 *
 *
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

	/**
	 * Permet de générer une adresse IP aléatoire.
	 *
	 * @return l'adresse IP
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
	 * Permet de générer une adresse MAC aléatoire.
	 *
	 * @return l'adresse MAC
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
	 * Méthode principale de la classe, effectue la génération aléatoire des données de plusieurs
	 * captures ainsi que leur stockage dans la base de données
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		final String confFilename = "app.properties";
		String dbAddress = "";
		String dbUsername = "";
		String dbPassword = "";
		Properties prop = new Properties();

		//Récupération des informations de configuration à partir du fichier de configuration
        try {

        	//Chargement du fichier de configuration
            BufferedReader br = new BufferedReader(new FileReader(confFilename));
            //load a properties file from class path, inside static method
            prop.load(br);

            //Récupération des informations de configuration
            dbAddress = prop.getProperty("dbaddress");
            dbUsername = prop.getProperty("dbusername");
            dbPassword = prop.getProperty("dbpassword");

        } catch (IOException ex) {
            System.out.println("app.properties couldn't be loaded, please check it is present in the same folder as the application");
            System.exit(1);
        }

		Database db = new Database(dbAddress, dbUsername, dbPassword);
		db.connect();

		Random r = new Random();

		LinkedList<PCInfo> parc = new LinkedList<>();

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

			LinkedList<Program> programs = new LinkedList<>();
			// Générer nb programs
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

		// Répéter 10 fois
		// Modification de l'espace
		for (int i = 0; i < 9; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (PCInfo pc : parc) {
				double freesize = pc.getHdd().getFreeSize();
				pc.getHdd().setFreeSize(freesize/1.5 + Math.random() * (freesize - freesize/1.5));
			}
			// Enregistrement des PC dans la DB
			db.storePCs(parc);
		}
	}
}
