package communication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import monitor.model.PCInfo;
import utils.SystemInfoRecuperator;
import utils.Cryptography;

/**
 * Cette classe permet d'écouter la requête de récupération des informations système
 * du serveur et d'initier la communication avec ce dernier afin de les lui envoyer
 * 
 * @author CIANI Antony
 * @author STEINER Lucie
 *
 */
public class SystemInfoRetrieverClient {

	private static final Logger LOG = Logger.getLogger(SystemInfoRetrieverClient.class.getName());

	private MulticastSocket udpSocket;
	private Socket tcpSocket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean sendInfoMsgReceived = false;
	private boolean connected = false;

	private int udpPort;
	private int tcpPort;

	/**
	 * Constructeur, prends en paramètre le port udp surlequel écouter 
	 * la requête UDP du serveur et le port tcp surlequel se connecter au serveur
	 * 
	 * @param udpPort
	 * @param tcpPort
	 * @throws SocketException
	 */
	public SystemInfoRetrieverClient(int udpPort, int tcpPort) throws SocketException {
		this.udpPort = udpPort;
		this.tcpPort = tcpPort;

	}

	/**
	 * Permet de commencer le processus d'écoute de la requête du serveur et
	 * le processus d'envoi des informations à ce dernier
	 * 
	 * @throws IOException
	 */
	public void startListening() throws IOException {
		PCInfo pc = null;
		boolean ready = false;
		boolean isPublicKeySent = false;
		boolean receivedReady = false;
		boolean isInfoSent = false;
		KeyPair keyPair;
		RSAPublicKey publicKey;
		RSAPrivateKey privateKey;

		udpSocket = new MulticastSocket(udpPort);
		udpSocket.joinGroup(InetAddress.getByName(SystemInfoRetrieverProtocol.MULTICAST_ADDRESS));

		byte[] buffer = new byte[SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes().length];

		DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
		while (!sendInfoMsgReceived) {

			udpSocket.receive(udpPacket);
			String msg = new String(udpPacket.getData());
			LOG.info("Received: " + msg);
			if (msg.equals(SystemInfoRetrieverProtocol.REQUEST_INFO)) {

				sendInfoMsgReceived = true;
				LOG.info("Server requested infos");

			}
		}
		LOG.info("Connecting to server via TCP on " + udpPacket.getAddress().toString());
		connect(udpPacket.getAddress(), tcpPort);

		String msg = "";
		while (connected) {

			if (!ready) {
				pc = SystemInfoRecuperator.retrievePCInfo();
				System.out.println("Nb programs: " + pc.getPrograms().size());
				ready = true;
			} else {
				// G�n�ration de la paire de cl�s RSA
				keyPair = Cryptography.generateRSAKeyPair();
				publicKey = (RSAPublicKey) keyPair.getPublic();
				privateKey = (RSAPrivateKey) keyPair.getPrivate();

				out.println(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO);
				out.flush();
				LOG.info("SENT SOMETHING");
				ObjectOutputStream oos = null;
				while ((!isPublicKeySent) && (msg = in.readLine()) != null) {
					if (msg.equals(SystemInfoRetrieverProtocol.WAITING_FOR_PUBLIC_KEY)) {

						// Envoi de la cl� publique
						oos = new ObjectOutputStream(tcpSocket.getOutputStream());
						oos.writeObject(publicKey);
						LOG.info("PUBLIC KEY HAS BEEN SENT");
						isPublicKeySent = true;

					}
				}

				InputStream tmpIn = null;
				OutputStream tmpOut = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutput objOut = new ObjectOutputStream(byteArrayOutputStream);

				while ((!receivedReady) && (msg = in.readLine()) != null) {

					if (msg.equals(SystemInfoRetrieverProtocol.READY_TO_READ_INFO)) {
						LOG.info("RECEIVED READY");
						receivedReady = true;
						out.println(SystemInfoRetrieverProtocol.WAITING_FOR_SECRET_KEY);
						out.flush();

						// R�cup�rer la cl� secr�te et la d�chiffrer avec la cl�
						// priv�e RSA
						byte encryptedSecretKey[] = new byte[128];
						tmpIn = tcpSocket.getInputStream();
						while ((!isInfoSent) && tmpIn.read(encryptedSecretKey) != -1) {

							byte[] decryptedSecretKey = Cryptography.RSADecrypt(encryptedSecretKey, privateKey);
							SecretKey secretKey = new SecretKeySpec(decryptedSecretKey, "AES");

							LOG.info("RECEIVED SECRET KEY");

							// Chiffrement des informations avec la cl� secr�te
							objOut.writeObject(pc);
							byte[] encryptedPC = Cryptography.AESEncrypt(byteArrayOutputStream.toByteArray(),
									secretKey);

							// Envoi de la taille du message
							out.println(encryptedPC.length);
							out.flush();
							byteArrayOutputStream.close();
							objOut.close();

							// Envoi du message chiffr�
							tmpOut = tcpSocket.getOutputStream();
							tmpOut.write(encryptedPC);
							isInfoSent = true;
							LOG.info("SENT INFOS");
						}
					}
				}

				oos.close();
				tmpIn.close();
				tmpOut.close();
				in.close();
				out.close();
				connected = false;
				sendInfoMsgReceived = false;
			}
		}
	}

	/**
	 * @param serverAddress
	 * @param serverPort
	 */
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
