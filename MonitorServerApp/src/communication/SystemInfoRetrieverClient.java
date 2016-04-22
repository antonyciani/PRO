package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

public class SystemInfoRetrieverClient {

	private static final Logger LOG = Logger.getLogger(SystemInfoRetrieverClient.class.getName());

	DatagramSocket udpSocket;
	Socket tcpSocket;
	BufferedReader in;
	PrintWriter out;
	boolean connected = false;
	
	
	public SystemInfoRetrieverClient(int portToListen) throws SocketException {
		udpSocket = new DatagramSocket(portToListen);
		
	}

	public void startListening() throws IOException{
		
		connected = false;
		DatagramPacket udpPacket = null;
		while(!connected){
			
			udpSocket.receive(udpPacket);
			String msg = new String(udpPacket.getData().toString());
			
			if(msg == SystemInfoRetrieverProtocol.REQUEST_INFO){
				
				connected = true;
				LOG.info("Server requested infos");
				
			}
		}
		sendInfos(udpPacket.getAddress());
		
		
	}


	public void sendInfos(InetAddress serverAddress) throws IOException {
		
		tcpSocket = new Socket(serverAddress, SystemInfoRetrieverProtocol.RECEPTION_PORT);
		
		LOG.info("Connected to server in TCP");
		
		// Envoi avec TCP
		
		

	}

}
