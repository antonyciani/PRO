package monitor;

import java.io.IOException;
import java.net.SocketException;

import communication.SystemInfoRetrieverClient;
import communication.SystemInfoRetrieverProtocol;
import communication.SystemInfoRetrieverServer;

public class ClientApp {

	public static void main(String[] args) {
		
		try {
			SystemInfoRetrieverClient sirc = new SystemInfoRetrieverClient(SystemInfoRetrieverProtocol.UDP_PORT, SystemInfoRetrieverProtocol.TCP_PORT);
			
			while(true){
				System.out.println("Listening to server message");
				sirc.startListening();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
