package monitor.database;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import monitor.model.CPUInfo;
import monitor.model.HDDInfo;
import monitor.model.PCInfo;
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

	public void storePCs(ArrayList<PCInfo> pcList){
		LocalDateTime currentDate = LocalDateTime.now();
		for(PCInfo pc : pcList){
			try{
				//Creation du processeur
				int processorId = 0;
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery("SELECT id FROM processor WHERE constructor ='"+pc.getCpu().getConstructor()+"'"
						+ "AND frequency ="+pc.getCpu().getFrequency()+" AND model ='"+pc.getCpu().getModel()+"' AND nmbrcores="+pc.getCpu().getNbCore()+";");
				if(result.next()){
					processorId = result.getInt(1);
				}
				else{
					int ok = statement.executeUpdate("INSERT INTO processor (constructor, frequency, model, nmbrcores) VALUES('"+pc.getCpu().getConstructor()+"', "+pc.getCpu().getFrequency()+", '"+pc.getCpu().getModel()+"', "+pc.getCpu().getNbCore()+")");
					if(ok != 0){
						System.out.println(ok);
						result = statement.executeQuery("SELECT id FROM processor WHERE constructor ='"+pc.getCpu().getConstructor()+"'"
								+ "AND frequency ="+pc.getCpu().getFrequency()+" AND model ='"+pc.getCpu().getModel()+"' AND nmbrcores="+pc.getCpu().getNbCore());
						if(result.next()){
							processorId = result.getInt(1);
						}
					}
				}
				//CrÈation de la machine
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
					//rÈcupÈrer l'id
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

					//ajouter ‡ pc_program pcMacAddress, pcCaptureTime, programId
					statement.executeUpdate("INSERT INTO pc_program VALUES ('"+pc.getMacAddress()+"','"+ currentDate+"',"+programId+");");
				}
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	public HashMap<Float, Integer> nbPcByHddSize(){
		HashMap<Float, Integer> map = new HashMap<Float, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT TotalHardDriveSize, count(TotalHardDriveSize)"
					+ "FROM machineState "
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

	public HashMap<Float, Integer> nbPcByRamSize(){
		HashMap<Float, Integer> map = new HashMap<Float, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT champ1, count(champ1)"
					+ "FROM infos "
					+ "GROUP BY champ1;");
			while(result.next()){
				map.put(result.getFloat(1), result.getInt(2));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<Integer, Integer> nbPcByNbCores(){
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT champ1, count(champ1)"
					+ "FROM infos "
					+ "GROUP BY champ1;");
			while(result.next()){
				map.put(result.getInt(1), result.getInt(2));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}

	public HashMap<String, Integer> nbPcByConstructor(){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT champ1, count(champ1)"
					+ "FROM infos "
					+ "GROUP BY champ1;");
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

	public ArrayList<PCInfo> loadPCInfo(String captureTime){
		ArrayList<PCInfo> pcs = new ArrayList<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM machinestate WHERE CaptureTime ='"+captureTime+"';");
			while(result.next()){
				//RÈcupÈration infos pc
				String mac = result.getString(1);
				String hostname = result.getString(3);
				String os = result.getString(4);
				int ram = result.getInt(6);
				String ip = result.getString(7);

				//RÈcupÈration et crÈation hdd
				HDDInfo hdd = new HDDInfo(result.getDouble(8), result.getDouble(9));

				//RÈcupÈration et crÈation processeur
				int processorId = result.getInt(5);
				Statement statement2 = connection.createStatement();
				ResultSet resultTmp = statement2.executeQuery("SELECT * FROM processor WHERE id ="+processorId+";");
				resultTmp.next();
				CPUInfo cpu = new CPUInfo(resultTmp.getString(2), resultTmp.getString(4), resultTmp.getDouble(3), resultTmp.getInt(5));

				//RÈcupÈration programmes
				LinkedList<Program> programs = new LinkedList<>();
				resultTmp = statement2.executeQuery("SELECT * FROM program WHERE ID IN ( "
												+ "SELECT DISTINCT programID "
												+ "FROM pc_program "
												+ "WHERE pcMacAddress = '"+ mac+"' AND pcCaptureTime = '"+ captureTime +"');");
				while(resultTmp.next()){
					Program program = new Program(result.getString(1), result.getString(2),"");
					programs.add(program);
				}
				//CrÈation pc
				PCInfo p = new PCInfo(hostname, ip, mac, os, cpu, hdd, ram, programs);

				//Ajout ‡ la liste
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

	public HashMap<String, Double> freeHardDriveSizeRate(PCInfo pc){
		HashMap<String, Double> map = new HashMap<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT captureTime, freeHardDriveSize, totalHardDriveSize FROM machineState WHERE MacAddress ='"+pc.getMacAddress()+"';");
			while(result.next()){
				double rate = result.getDouble(2)/result.getDouble(3);
				map.put(result.getString(1), rate);
			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
	}
	public HashMap<String, Double> averageFreeHardDriveSizeRate(){
		HashMap<String, Double> map = new HashMap<>();
		try{
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT captureTime, SUM(freeHardDriveSize), SUM(totalHardDriveSize) FROM machineState GROUP BY captureTime;");
			while(result.next()){
				double rate = result.getDouble(2)/result.getDouble(3);
				map.put(result.getString(1), rate);
			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return map;
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
}

