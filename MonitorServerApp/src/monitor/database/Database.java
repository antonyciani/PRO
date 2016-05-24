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
import java.util.Map.Entry;
import java.util.TreeMap;

import monitor.model.CPUInfo;
import monitor.model.HDDInfo;
import monitor.model.PCInfo;
import monitor.model.PCInfoViewWrapper;
import monitor.model.Program;


public class Database{
	private String address;
	private String username;
	private String password;
	private Connection connection;

	public Database(String address, String username, String password){
		this.address = address;
		this.username = username;
		this.password = password;
		connection = null;
	}
	public void connect(){
		try{
			//load the driver
			Class.forName("com.mysql.jdbc.Driver");
			//connect
			connection = DriverManager.getConnection(address, username, password);
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(SQLException e){
			e.printStackTrace();
		}

	}

	public void storePCs(LinkedList<PCInfo> pcInfos){
		LocalDateTime currentDate = LocalDateTime.now();
		for(PCInfo pc : pcInfos){
			try{
				//Cr�ation du processeur
				int processorId = 0;
				Statement statement = connection.createStatement();
				System.out.println(pc.getCpu().getConstructor()+" "+pc.getCpu().getFrequency()+" "+pc.getCpu().getModel()+" "+pc.getCpu().getNbCore());
				ResultSet result = statement.executeQuery("SELECT id FROM processor WHERE constructor ='"+pc.getCpu().getConstructor()+"'"
						+ "AND frequency ="+pc.getCpu().getFrequency()+" AND model ='"+pc.getCpu().getModel()+"' AND nmbrcores="+pc.getCpu().getNbCore()+";");
				if(result.next()){
					processorId = result.getInt(1);
					System.out.println("Processor already exists, ID: "+processorId);
				}
				else{
					int ok = statement.executeUpdate("INSERT INTO processor (constructor, frequency, model, nmbrcores) VALUES('"+pc.getCpu().getConstructor()+"', "+pc.getCpu().getFrequency()+", '"+pc.getCpu().getModel()+"', "+pc.getCpu().getNbCore()+")");
					if(ok != 0){
						System.out.println("Insert new processor: " +ok);
						result = statement.executeQuery("SELECT id FROM processor WHERE constructor ='"+pc.getCpu().getConstructor()+"'"
								+ "AND frequency ="+pc.getCpu().getFrequency()+" AND model ='"+pc.getCpu().getModel()+"' AND nmbrcores="+pc.getCpu().getNbCore()+";");
						if(result.next()){
							processorId = result.getInt(1);
							System.out.println("New processor ID: "+processorId);
						}
					}
				}
				//Cr�ation de la machine
				System.out.println("Processor Id: "+ processorId);
				statement.executeUpdate("INSERT INTO machineState (MacAddress, captureTime, hostname, os, processorId, totalram, ipaddress, totalharddrivesize, freeharddrivesize) VALUES('"+ pc.getMacAddress()+"', '" + currentDate +"', '" +pc.getHostname()+"', '"+pc.getOs()+"', "+processorId+", "
						+pc.getRamSize()+", '"+ pc.getIpAddress()+"', "+pc.getHdd().getTotalSize()+", "+pc.getHdd().getFreeSize()+")");

				//liste de programmes
				for(Program p : pc.getPrograms()){
					//l'ajouter dans les programmes
					int programId = 0;
					statement = connection.createStatement();
					result = statement.executeQuery("SELECT id FROM program WHERE name ='"+p.getName()+"'"
							+ "AND version ='"+p.getVersion()+"';");
					if(result.next()){
						programId = result.getInt(1);
					}
					//r�cup�rer l'id
					else{
						int ok = statement.executeUpdate("INSERT INTO program (name, version) VALUES('"+p.getName()+"', '"+p.getVersion()+"');");
						if(ok != 0){
							System.out.println(ok);
							result = statement.executeQuery("SELECT id FROM program WHERE name ='"+p.getName()+"'"
									+ "AND version ='"+p.getVersion()+"';");
							if(result.next()){
								programId = result.getInt(1);
							}
						}
					}

					//ajouter � pc_program pcMacAddress, pcCaptureTime, programId
					statement.executeUpdate("INSERT INTO pc_program VALUES ('"+pc.getMacAddress()+"','"+ currentDate+"',"+programId+");");
				}
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	public HashMap<Float, Integer> nbPcByHddSize(String captureTime){
		HashMap<Float, Integer> map = new HashMap<Float, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT TotalHardDriveSize, count(TotalHardDriveSize)"
					+ "FROM machineState "
					+ "WHERE captureTime ='"+captureTime+"'"
					+ "GROUP BY TotalHardDriveSize;");
			while(result.next()){
				map.put(result.getFloat(1), result.getInt(2));
			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<Integer, Integer> nbPcByRamSize(String captureTime){
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT TotalRam, count(TotalRam)"
					+ "FROM machineState "
					+ "WHERE captureTime ='"+captureTime+"'"
					+ "GROUP BY TotalRam;");
			while(result.next()){
				map.put(result.getInt(1), result.getInt(2));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<Integer, Integer> nbPcByNbCores(String captureTime){
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT nmbrCores, count(nmbrCores)"
					+ "FROM machineState INNER JOIN processor ON machineState.processorID = processor.ID WHERE captureTime ='"+captureTime+"' GROUP BY nmbrCores;");
			while(result.next()){
				map.put(result.getInt(1), result.getInt(2));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<String, Integer> nbPcByConstructor(String captureTime){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT constructor, count(constructor)"
					+ "FROM machineState INNER JOIN processor ON machineState.processorId = processor.ID WHERE captureTime ='"+captureTime+"' GROUP BY constructor;");
			while(result.next()){
				map.put(result.getString(1), result.getInt(2));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}

	public ArrayList<String> getCaptures(){
		ArrayList<String> captures = new ArrayList<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT DISTINCT CaptureTime FROM machineState");
			while(result.next()){
				captures.add(result.getString(1));
			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return captures;
	}
	public String getLastCapture(){
		String capture ="";
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT DISTINCT CaptureTime FROM machineState ORDER BY captureTime DESC");
			if(result.next()){
				capture = result.getString(1);
			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return capture;
	}

	public ArrayList<PCInfoViewWrapper> loadPCInfo(String captureTime){
		ArrayList<PCInfoViewWrapper> pcs = new ArrayList<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM machinestate WHERE CaptureTime ='"+captureTime+"';");
			while(result.next()){
				//R�cup�ration infos pc
				String mac = result.getString(1);
				String hostname = result.getString(3);
				String os = result.getString(4);
				int ram = result.getInt(6);
				String ip = result.getString(7);

				//R�cup�ration et cr�ation hdd
				HDDInfo hdd = new HDDInfo(result.getDouble(8), result.getDouble(9));

				//R�cup�ration et cr�ation processeur
				int processorId = result.getInt(5);
				Statement statement2 = connection.createStatement();
				ResultSet resultTmp = statement2.executeQuery("SELECT * FROM processor WHERE id ="+processorId+";");
				resultTmp.next();
				CPUInfo cpu = new CPUInfo(resultTmp.getString(2), resultTmp.getString(4), resultTmp.getDouble(3), resultTmp.getInt(5));

				//R�cup�ration programmes
				LinkedList<Program> programs = new LinkedList<>();
				resultTmp = statement2.executeQuery("SELECT * FROM program WHERE ID IN ( "
												+ "SELECT DISTINCT programID "
												+ "FROM pc_program "
												+ "WHERE pcMacAddress = '"+ mac+"' AND pcCaptureTime = '"+ captureTime +"');");
				while(resultTmp.next()){
					Program program = new Program(resultTmp.getString(2), resultTmp.getString(3));
					programs.add(program);
				}
				//Cr�ation pc
				PCInfoViewWrapper p = new PCInfoViewWrapper(new PCInfo(hostname, ip, mac, os, cpu, hdd, ram, programs));

				//Ajout � la liste
				pcs.add(p);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return pcs;
	}

	public void deleteCapture(String captureTime){
		try{
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM machinestate WHERE CaptureTime ='"+ captureTime +"';");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public TreeMap<String, Double> storageLoadRate(PCInfoViewWrapper pc){
		TreeMap<String, Double> map = new TreeMap<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT captureTime, freeHardDriveSize, totalHardDriveSize FROM machineState WHERE MacAddress ='"+pc.getMacAddress()+"';");
			while(result.next()){
				double load = result.getDouble(3) - result.getDouble(2);
				double rate = load/result.getDouble(3);
				map.put(result.getString(1), rate);
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}
	public TreeMap<String, Double> averageStorageLoadRate(){
		TreeMap<String, Double> map = new TreeMap<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT captureTime, SUM(freeHardDriveSize), SUM(totalHardDriveSize) FROM machineState GROUP BY captureTime;");
			while(result.next()){
				double load = result.getDouble(3) - result.getDouble(2);
				double rate = load/result.getDouble(3);
				map.put(result.getString(1), rate);
			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}
	public HashMap<String, Integer> nbProgramsInstalledByVersion(String program, String captureTime){
		HashMap<String, Integer> map = new HashMap<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT program.version, count(program.version)"
													+ "FROM pc_program "
													+ "INNER JOIN program "
													+ "ON pc_program.programID = program.ID "
													+ "WHERE program.name = '"+program+"' "
													+ "AND pcCaptureTime = '"+captureTime+"' GROUP BY program.version;");
			while(result.next()){
				map.put(result.getString(1), result.getInt(2));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}
	public HashMap<String, Integer> mostFrequentlyInstalledPrograms(int max, String captureTime){
		HashMap<String, Integer> map = new HashMap<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT name, count(name)"
													+ "FROM pc_program "
													+ "INNER JOIN program "
													+ "ON pc_program.programID = program.ID "
													+ "WHERE pcCaptureTime = '"+captureTime+"' "
													+ "GROUP BY name;");
			int counter = 0;
			while(result.next() && counter < max){
				map.put(result.getString(1), result.getInt(2));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}
	public ArrayList<String> getProgramsName(String captureTime){
		ArrayList<String> programs = new ArrayList<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT DISTINCT name FROM program "
													+ "INNER JOIN pc_program "
													+ "ON pc_program.programID = program.ID "
													+ "WHERE pcCaptureTime = '"+captureTime+"';");
			while(result.next()){
				programs.add(result.getString(1));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return programs;
	}
	public void finalize(){
		if(connection != null){
			try{
				connection.close();
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]){
		Database db = new Database("jdbc:mysql://localhost:3306/inventory", "root", "1234");
		db.connect();
		/*HashMap<Integer,Integer> map = db.nbPcByNbCores();

		for(Entry<Integer, Integer> entry : map.entrySet()){
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + " : "+ value);
		}*/

		String hostname = "LuciePc";
		String ipAddress = "192.168.1.11";
		String macAddress = "5E:FF:56:A2:AF:30";
		String os = "Windows 7";
		CPUInfo cpu = new CPUInfo("Intel", "i5", 2.3, 4);
		HDDInfo hdd = new HDDInfo(500.8, 400.4);
		long ramSize = 8;
		LinkedList<Program> programs = new LinkedList<>();
		programs.add(new Program("ls", "1.4"));
		programs.add(new Program("cat", "2.4"));

		LinkedList<Program> programs2 = new LinkedList<>();
		programs.add(new Program("ls", "1.3"));
		programs.add(new Program("cat", "2.3"));
		PCInfo pc = new PCInfo(hostname, ipAddress, macAddress, os, cpu, hdd, ramSize, programs);
		hostname = "MichaelPc";
		ipAddress = "192.168.1.10";
		macAddress = "5E:FF:56:A2:AF:15";
		os = "Windows 10";
		cpu = new CPUInfo("Intel", "i7", 2.7, 8);
		hdd = new HDDInfo(500.8, 209.4);
		ramSize = 16;

		PCInfo pc2 = new PCInfo(hostname, ipAddress, macAddress, os, cpu, hdd, ramSize, programs2);
		LinkedList<PCInfo> pcs = new LinkedList<>();
		pcs.add(pc);
		pcs.add(pc2);
		db.storePCs(pcs);

		/*
		ArrayList<String> capt = db.getCaptures();
		for(String s : capt){
			System.out.println(s);
		}
		ArrayList<PCInfo> tmp = db.loadPCInfo(capt.get(0));
		for(PCInfo p : tmp){
			System.out.println(p.getHostname());
		}
		db.deleteCapture(capt.get(0));
		capt = db.getCaptures();
		for(String s : capt){
			System.out.println(s);
		}*/
		//db.deleteCapture("2016-05-06 15:36:07");
		/*
		HashMap<String, Double> map = db.freeHardDriveSizeRate(pc2);
		for(Entry<String, Double> entry : map.entrySet()){
			String key = entry.getKey();
			Double value = entry.getValue();
			System.out.println(key + " : "+ value);
		}
		map = db.averageFreeHardDriveSizeRate();
		for(Entry<String, Double> entry : map.entrySet()){
			String key = entry.getKey();
			Double value = entry.getValue();
			System.out.println(key + " : "+ value);
		}
		hdd = new HDDInfo(500.8, 101.6);
		pc2 = new PCInfo(hostname, ipAddress, macAddress, os, cpu, hdd, ramSize, new LinkedList<Program>());
		pcs.add(pc);
		pcs.add(pc2);
		db.storePCs(pcs);*/
		String lastCapture = db.getLastCapture();

		System.out.println("Last capture: "+lastCapture);

		System.out.println("Nb PCs by nb Cores:");
		HashMap<Integer,Integer> map2 = db.nbPcByNbCores(lastCapture);

		for(Entry<Integer, Integer> entry : map2.entrySet()){
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + " : "+ value);}
		System.out.println("Nb PCs by hard drive size:");

		HashMap<Float,Integer> map3 = db.nbPcByHddSize(lastCapture);

		for(Entry<Float, Integer> entry : map3.entrySet()){
			Float key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + " : "+ value);}
		System.out.println("Nb PCs by ram size:");

		HashMap<Integer,Integer> map4 = db.nbPcByRamSize(lastCapture);

		for(Entry<Integer, Integer> entry : map4.entrySet()){
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + " : "+ value);}
		System.out.println("Nb PCs by constructor");

		HashMap<String,Integer> map5 = db.nbPcByConstructor(lastCapture);

		for(Entry<String, Integer> entry : map5.entrySet()){
			String key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + " : "+ value);}
/*
		System.out.println("\n===== Versions =====");
		HashMap<String, Integer> map6 = db.nbProgramsInstalledByVersion("ls");
		for(Entry<String, Integer> entry : map6.entrySet()){
			String key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + " : "+ value);}

		System.out.println("\n===== Most frequently installed programs =====");
		HashMap<String, Integer> map7 = db.mostFrequentlyInstalledPrograms(10);
		for(Entry<String, Integer> entry : map7.entrySet()){
			String key = entry.getKey();
			Integer value = entry.getValue();
			System.out.println(key + " : "+ value);}*/
	}

}
