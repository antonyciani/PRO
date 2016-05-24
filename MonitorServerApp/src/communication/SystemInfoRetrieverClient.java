package communication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import monitor.model.PCInfo;
import utils.SystemInfoRecuperator;
import utils.Cryptography;

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
		boolean isPublicKeySent = false;
		boolean receivedReady = false;
		boolean isInfoSent = false;
		String msg = "";
		while(connected){

			if(!ready){
				pc = SystemInfoRecuperator.retrievePCInfo();
				ready = true;
			}
			else{
				//Génération de la paire de clés RSA
				KeyPair keyPair = Cryptography.generateRSAKeyPair();
				RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
				RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();


				out.println(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO);
				out.flush();
				LOG.info("SENT SOMETHING");

				while((!isPublicKeySent) && (msg = in.readLine()) != null){
					if(msg.equals("WAITING_FOR_PUBLIC_KEY")){
					//Envoi de la clé publique
						ObjectOutputStream oos = new ObjectOutputStream(tcpSocket.getOutputStream());
						oos.writeObject(publicKey);
						LOG.info("PUBLIC KEY HAS BEEN SENT");
						isPublicKeySent = true;
					}
				}

				while((!receivedReady) &&(msg = in.readLine()) != null){

					if(msg.equals(SystemInfoRetrieverProtocol.READY_TO_READ_INFO)){
						LOG.info("RECEIVED READY");
						receivedReady = true;
						out.println("WAITING_FOR_SECRET_KEY");
						out.flush();
						//Récupérer la clé secrète et la déchiffrer avec la clé privée RSA
						byte encryptedSecretKey[] = new byte[128];
						InputStream tmpIn= tcpSocket.getInputStream();
						OutputStream tmpOut = tcpSocket.getOutputStream();
						while((!isInfoSent) && tmpIn.read(encryptedSecretKey) != -1){
							byte[] decryptedSecretKey = Cryptography.RSADecrypt(encryptedSecretKey, privateKey);
							SecretKey secretKey = new SecretKeySpec(decryptedSecretKey, "AES");
							LOG.info("RECEIVED SECRET KEY");
							//Chiffrement des informations avec la clé secrète
							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
							ObjectOutput objOut = new ObjectOutputStream(byteArrayOutputStream);
							objOut.writeObject(pc);
							byte[] encryptedPC = Cryptography.AESEncrypt(byteArrayOutputStream.toByteArray(), secretKey);
							out.println(encryptedPC.length);
							out.flush();
							objOut.close();
							tmpOut.write(encryptedPC);
							isInfoSent = true;
							LOG.info("SENT INFOS");
							//break;
						}
						tmpIn.close();
						tmpOut.close();

					}
					//break;
				}
				in.close();
				out.close();
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
