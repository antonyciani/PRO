package monitor.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import monitor.model.CPUInfo;
import monitor.model.HDDInfo;
import monitor.model.PCInfo;
import monitor.model.PCInfoViewWrapper;
import monitor.model.Program;

/**
 * @author STEINER Lucie
 *
 */
public class Database {
	private String address;
	private String username;
	private String password;
	private Connection connection;

	/**
	 * @param address
	 * @param username
	 * @param password
	 */
	public Database(String address, String username, String password) {
		this.address = address;
		this.username = username;
		this.password = password;
		connection = null;
	}

	/**
	 *
	 */
	public void connect() {
		try {
			// load the driver
			Class.forName("com.mysql.jdbc.Driver");
			// connect
			connection = DriverManager.getConnection(address, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("An error has occured during database connection, please check your connection parameters...");
			System.exit(1); // Si la connexion échoue on tue l'application
		}

	}

	/**
	 * @param pcInfos
	 */
	public void storePCs(LinkedList<PCInfo> pcInfos) {
		LocalDateTime currentDate = LocalDateTime.now();
		for (PCInfo pc : pcInfos) {
			try {
				// Cr�ation du processeur
				int processorId = 0;
				Statement statement = connection.createStatement();
				System.out.println(pc.getCpu().getConstructor() + " " + pc.getCpu().getFrequency() + " "
						+ pc.getCpu().getModel() + " " + pc.getCpu().getNbCore());
				ResultSet result = statement
						.executeQuery("SELECT id FROM processor WHERE constructor ='" + pc.getCpu().getConstructor()
								+ "'" + "AND frequency =" + pc.getCpu().getFrequency() + " AND model ='"
								+ pc.getCpu().getModel() + "' AND nmbrcores=" + pc.getCpu().getNbCore() + ";");
				if (result.next()) {
					processorId = result.getInt(1);
					System.out.println("Processor already exists, ID: " + processorId);
				} else {
					int ok = statement
							.executeUpdate("INSERT INTO processor (constructor, frequency, model, nmbrcores) VALUES('"
									+ pc.getCpu().getConstructor() + "', " + pc.getCpu().getFrequency() + ", '"
									+ pc.getCpu().getModel() + "', " + pc.getCpu().getNbCore() + ")");
					if (ok != 0) {
						System.out.println("Insert new processor: " + ok);
						result = statement.executeQuery(
								"SELECT id FROM processor WHERE constructor ='" + pc.getCpu().getConstructor() + "'"
										+ "AND frequency =" + pc.getCpu().getFrequency() + " AND model ='"
										+ pc.getCpu().getModel() + "' AND nmbrcores=" + pc.getCpu().getNbCore() + ";");
						if (result.next()) {
							processorId = result.getInt(1);
							System.out.println("New processor ID: " + processorId);
						}
					}
				}
				// Cr�ation de la machine
				System.out.println("Processor Id: " + processorId);
				statement.executeUpdate(
						"INSERT INTO machineState (MacAddress, captureTime, hostname, os, processorId, totalram, ipaddress, totalharddrivesize, freeharddrivesize) VALUES('"
								+ pc.getMacAddress() + "', '" + currentDate + "', '" + pc.getHostname() + "', '"
								+ pc.getOs() + "', " + processorId + ", " + pc.getRamSize() + ", '" + pc.getIpAddress()
								+ "', " + pc.getHdd().getTotalSize() + ", " + pc.getHdd().getFreeSize() + ")");

				// liste de programmes
				for (Program p : pc.getPrograms()) {
					// l'ajouter dans les programmes
					int programId = 0;
					statement = connection.createStatement();
					result = statement.executeQuery("SELECT id FROM program WHERE name ='" + p.getName() + "'"
							+ "AND version ='" + p.getVersion() + "';");
					if (result.next()) {
						programId = result.getInt(1);
					}
					// r�cup�rer l'id
					else {
						int ok = statement.executeUpdate("INSERT INTO program (name, version) VALUES('" + p.getName()
								+ "', '" + p.getVersion() + "');");
						if (ok != 0) {
							System.out.println(ok);
							result = statement.executeQuery("SELECT id FROM program WHERE name ='" + p.getName() + "'"
									+ "AND version ='" + p.getVersion() + "';");
							if (result.next()) {
								programId = result.getInt(1);
							}
						}
					}

					// ajouter � pc_program pcMacAddress, pcCaptureTime,
					// programId
					statement.executeUpdate("INSERT INTO pc_program VALUES ('" + pc.getMacAddress() + "','"
							+ currentDate + "'," + programId + ");");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param captureTime
	 * @return
	 */
	public HashMap<Float, Integer> nbPcByHddSize(String captureTime) {
		HashMap<Float, Integer> map = new HashMap<Float, Integer>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT TotalHardDriveSize, count(TotalHardDriveSize)" + "FROM machineState "
							+ "WHERE captureTime ='" + captureTime + "'" + "GROUP BY TotalHardDriveSize;");
			while (result.next()) {
				map.put(result.getFloat(1), result.getInt(2));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param captureTime
	 * @return
	 */
	public HashMap<Integer, Integer> nbPcByRamSize(String captureTime) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT TotalRam, count(TotalRam)" + "FROM machineState "
					+ "WHERE captureTime ='" + captureTime + "'" + "GROUP BY TotalRam;");
			while (result.next()) {
				map.put(result.getInt(1), result.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param captureTime
	 * @return
	 */
	public HashMap<Integer, Integer> nbPcByNbCores(String captureTime) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT nmbrCores, count(nmbrCores)"
					+ "FROM machineState INNER JOIN processor ON machineState.processorID = processor.ID WHERE captureTime ='"
					+ captureTime + "' GROUP BY nmbrCores;");
			while (result.next()) {
				map.put(result.getInt(1), result.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param captureTime
	 * @return
	 */
	public HashMap<String, Integer> nbPcByModel(String captureTime) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT model, count(model)"
					+ "FROM machineState INNER JOIN processor ON machineState.processorId = processor.ID WHERE captureTime ='"
					+ captureTime + "' GROUP BY model;");
			while (result.next()) {
				map.put(result.getString(1), result.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getCaptures() {
		ArrayList<String> captures = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT DISTINCT CaptureTime FROM machineState ORDER BY captureTime");
			while (result.next()) {
				captures.add(result.getString(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return captures;
	}

	/**
	 * @return
	 */
	public String getLastCapture() {
		String capture = "";
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT DISTINCT CaptureTime FROM machineState ORDER BY captureTime DESC");
			if (result.next()) {
				capture = result.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return capture;
	}

	/**
	 * @param captureTime
	 * @return
	 */
	public ArrayList<PCInfoViewWrapper> loadPCInfo(String captureTime) {
		ArrayList<PCInfoViewWrapper> pcs = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT * FROM machinestate WHERE CaptureTime ='" + captureTime + "';");
			while (result.next()) {
				// R�cup�ration infos pc
				String mac = result.getString(1);
				String hostname = result.getString(3);
				String os = result.getString(4);
				int ram = result.getInt(6);
				String ip = result.getString(7);

				// R�cup�ration et cr�ation hdd
				HDDInfo hdd = new HDDInfo(result.getDouble(8), result.getDouble(9));

				// R�cup�ration et cr�ation processeur
				int processorId = result.getInt(5);
				Statement statement2 = connection.createStatement();
				ResultSet resultTmp = statement2.executeQuery("SELECT * FROM processor WHERE id =" + processorId + ";");
				resultTmp.next();
				CPUInfo cpu = new CPUInfo(resultTmp.getString(2), resultTmp.getString(4), resultTmp.getDouble(3),
						resultTmp.getInt(5));

				// R�cup�ration programmes
				LinkedList<Program> programs = new LinkedList<>();
				resultTmp = statement2.executeQuery(
						"SELECT * FROM program WHERE ID IN ( " + "SELECT DISTINCT programID " + "FROM pc_program "
								+ "WHERE pcMacAddress = '" + mac + "' AND pcCaptureTime = '" + captureTime + "');");
				while (resultTmp.next()) {
					Program program = new Program(resultTmp.getString(2), resultTmp.getString(3));
					programs.add(program);
				}
				// Cr�ation pc
				PCInfoViewWrapper p = new PCInfoViewWrapper(new PCInfo(hostname, ip, mac, os, cpu, hdd, ram, programs));

				// Ajout � la liste
				pcs.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pcs;
	}

	/**
	 * @param captureTime
	 */
	public void deleteCapture(String captureTime) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM machinestate WHERE CaptureTime ='" + captureTime + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param pc
	 * @return
	 */
	public TreeMap<String, Double> storageLoadRate(PCInfoViewWrapper pc, String captureTime) {
		TreeMap<String, Double> map = new TreeMap<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(
					"SELECT captureTime, freeHardDriveSize, totalHardDriveSize FROM machineState WHERE MacAddress ='"
							+ pc.getMacAddress() + "' AND captureTime <= '"+captureTime+"';");
			while (result.next()) {
				double load = result.getDouble(3) - result.getDouble(2);
				double rate = load / result.getDouble(3);
				map.put(result.getString(1), rate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @return
	 */
	public TreeMap<String, Double> averageStorageLoadRate(String captureTime) {
		TreeMap<String, Double> map = new TreeMap<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(
					"SELECT captureTime, SUM(freeHardDriveSize), SUM(totalHardDriveSize) FROM machineState WHERE  captureTime <= '"+captureTime+"' GROUP BY captureTime;");
			while (result.next()) {
				double load = result.getDouble(3) - result.getDouble(2);
				double rate = load / result.getDouble(3);
				map.put(result.getString(1), rate);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param program
	 * @param captureTime
	 * @return
	 */
	public HashMap<String, Integer> nbProgramsInstalledByVersion(String program, String captureTime) {
		HashMap<String, Integer> map = new HashMap<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT program.version, count(program.version)" + "FROM pc_program "
							+ "INNER JOIN program " + "ON pc_program.programID = program.ID " + "WHERE program.name = '"
							+ program + "' " + "AND pcCaptureTime = '" + captureTime + "' GROUP BY program.version;");
			while (result.next()) {
				map.put(result.getString(1), result.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param max
	 * @param captureTime
	 * @return
	 */
	public HashMap<String, Integer> mostFrequentlyInstalledPrograms(int max, String captureTime) {
		HashMap<String, Integer> map = new HashMap<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT name, count(name)" + "FROM pc_program "
					+ "INNER JOIN program " + "ON pc_program.programID = program.ID " + "WHERE pcCaptureTime = '"
					+ captureTime + "' " + "GROUP BY name;");
			int counter = 0;
			while (result.next() && counter < max) {
				map.put(result.getString(1), result.getInt(2));
				counter++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param captureTime
	 * @return
	 */
	public ArrayList<String> getProgramsName(String captureTime) {
		ArrayList<String> programs = new ArrayList<>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT DISTINCT name FROM program " + "INNER JOIN pc_program "
					+ "ON pc_program.programID = program.ID " + "WHERE pcCaptureTime = '" + captureTime + "';");
			while (result.next()) {
				programs.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return programs;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
