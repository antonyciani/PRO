package communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Logger;

import monitor.model.PCInfo;
import utils.SystemInfoRecuperator;

public class SystemInfoRetrieverClient {

	private static final Logger LOG = Logger.getLogger(SystemInfoRetrieverClient.class.getName());

	DatagramSocket udpSocket;
	Socket tcpSocket;
	InputStream in;
	OutputStream out;
	boolean sendInfoMsgReceived = false;
	boolean connected = false;

	private int port;
	
	
	public SystemInfoRetrieverClient(int portToListen) throws SocketException {
		this.port = portToListen;
		udpSocket = new DatagramSocket(portToListen);
		udpSocket.setBroadcast(true);
		

		
	}

	public void startListening() throws IOException{
		
		byte[] buffer = new byte[SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes().length];
		
		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		while(!sendInfoMsgReceived){
			
			udpSocket.receive(udpPacket);
			String msg = new String(udpPacket.getData());
			LOG.info("Received: " +msg);
			if(msg.equals(SystemInfoRetrieverProtocol.REQUEST_INFO)){
				
				sendInfoMsgReceived = true;
				LOG.info("Server requested infos");
				
			}
		}
		try {
			wait(10000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("Connecting to server via TCP");
		connect(udpPacket.getAddress());
		//PCInfo pc = SystemInfoRecuperator.retrievePCInfo();
		PrintWriter pw = new PrintWriter(out);
		pw.write(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO);
		//sendInfos(pc);
		//disconnect
		
		
	}
	
	public void connect(InetAddress serverAddress) {
		
		try {
			tcpSocket = new Socket(serverAddress, SystemInfoRetrieverProtocol.SERVER_PORT);
			in = tcpSocket.getInputStream();
			out = tcpSocket.getOutputStream();
			connected = true;
			LOG.info("Connected to server in TCP");
		} catch (IOException e) {
			System.out.println("PROBLEME CONNEXION");
			e.printStackTrace();
		}
		
		
	}


	public void sendInfos(PCInfo pcInfo) throws IOException {
		
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(pcInfo);

	}
	


}
