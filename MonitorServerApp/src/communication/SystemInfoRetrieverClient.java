package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

import monitor.model.PCInfo;
import utils.SystemInfoRecuperator;

public class SystemInfoRetrieverClient {

	private static final Logger LOG = Logger.getLogger(SystemInfoRetrieverClient.class.getName());

	MulticastSocket udpSocket;
	Socket tcpSocket;
	BufferedReader in;
	PrintWriter out;
	boolean sendInfoMsgReceived = false;
	boolean connected = false;

	int udpPort;
	int tcpPort;


	public SystemInfoRetrieverClient(int udpPort, int tcpPort) throws SocketException {
		this.udpPort = udpPort;
		this.tcpPort = tcpPort;

	}

	public void startListening() throws IOException{

		udpSocket = new MulticastSocket(udpPort);
		udpSocket.joinGroup(InetAddress.getByName(SystemInfoRetrieverProtocol.MULTICAST_ADDRESS));

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
		LOG.info("Connecting to server via TCP");
		LOG.info(udpPacket.getAddress().toString());
		connect(udpPacket.getAddress(), tcpPort);
		PCInfo pc = null;
		boolean ready = false;
		String msg = "";
		while(connected){

			if(!ready){
				pc = SystemInfoRecuperator.retrievePCInfo();
				ready = true;
			}
			else{
				//TODO générer paire de clé RSA et envoyer clé pulbique
				out.println(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO);
				out.flush();
				LOG.info("SENT SOMETHING");
				while((msg = in.readLine()) != null){

					if(msg.equals(SystemInfoRetrieverProtocol.READY_TO_READ_INFO)){
						//TODO récupérer clé secrète symétrique
						ObjectOutputStream oos = new ObjectOutputStream(tcpSocket.getOutputStream());
						//TODO chiffrer avec clé secrète symétrique avant envoi
						oos.writeObject(pc);
						LOG.info("INFO HAS BEEN SENT");
					}
				}
				connected = false;
				sendInfoMsgReceived = false;
			}
		}


	}

	public void connect(InetAddress serverAddress, int serverPort) {

		try {
			tcpSocket = new Socket(serverAddress, serverPort);
			in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
			out = new PrintWriter(tcpSocket.getOutputStream());
			connected = true;
			LOG.info("Connected to server in TCP");
		} catch (IOException e) {
			System.out.println("PROBLEME CONNEXION");
			e.printStackTrace();
		}
	}




}
