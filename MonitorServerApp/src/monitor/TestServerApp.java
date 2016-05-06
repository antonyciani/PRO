package monitor;

import java.net.SocketException;
import java.util.LinkedList;

import communication.SystemInfoRetrieverProtocol;
import communication.SystemInfoRetrieverServer;
import monitor.model.PCInfo;

public class TestServerApp {

	public static void main(String[] args) {
		
		
		SystemInfoRetrieverServer sirs;
		try {
			sirs = new SystemInfoRetrieverServer(SystemInfoRetrieverProtocol.UDP_PORT, SystemInfoRetrieverProtocol.TCP_PORT);
			sirs.retrieveInfosFromClients();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
