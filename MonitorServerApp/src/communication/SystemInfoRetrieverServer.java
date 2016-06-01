package communication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import monitor.model.PCInfo;
import utils.Cryptography;

/**
 * Cette classe permet la récupération des informations système auprès des PC clients
 * Elle se comporte comme un serveur allant interroger les clients pour qu'il lui
 * renvoient leurs informations
 * 
 * @author CIANI Antony
 * @author STEINER Lucie
 *
 */
public class SystemInfoRetrieverServer {

	private final static Logger LOG = Logger.getLogger(SystemInfoRetrieverServer.class.getName());

	private int udpPort;
	private int tcpPort;
	private String multicastGroupAddress;
	private LinkedList<PCInfo> pcInfos; // liste des informations récupérées

	/** 
	 * Constructeur, prends en paramètre le port udp pour l'envoi du message de demande de
	 * récupération des informations au client et le port tcp sur lequel les connexions des
	 * clients sont acceptées
	 * 
	 * @param udpPort
	 * @param tcpPort
	 * @param multicastGroupAddress 
	 * @throws SocketException
	 */
	public SystemInfoRetrieverServer(int udpPort, int tcpPort, String multicastGroupAddress) throws SocketException {

		this.udpPort = udpPort;
		this.tcpPort = tcpPort;
		this.multicastGroupAddress = multicastGroupAddress;
		this.pcInfos = new LinkedList<>();
	}

	/**
	 * Lance le processus de récupération des informations auprès des clients.
	 * 
	 */
	public void retrieveInfosFromClients() {
		
		LOG.info("Starting the Receptionist Worker on a new thread...");
		Thread receptionist = new Thread(new ReceptionistWorker());
		receptionist.start();
		
		try {
			receptionist.join(); // On attends que le thread réceptionniste se termine
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet de récupérer les informations des PC qui ont répondu au serveur
	 * 
	 * @return La liste des informations récoltées
	 */
	public LinkedList<PCInfo> getPcInfos() {

		return pcInfos;
	}

	/**
	 * Permet de gérer les connexions de plusieurs clients de façon multi-threadée
	 * Pour chaque client se connectant, accepte sa connexion et lance un nouveau
	 * thread permettant de gérer l'échange d'informations
	 * 
	 * @author CIANI Antony
	 *
	 */
	private class ReceptionistWorker implements Runnable {

		@Override
		public void run() {
			
			ServerSocket serverSocket;
			MulticastSocket udpSocket;
			LinkedList<Thread> servants = new LinkedList<>();

			try {
				serverSocket = new ServerSocket(tcpPort);
				udpSocket = new MulticastSocket();
				udpSocket.joinGroup(InetAddress.getByName(multicastGroupAddress));

				udpSocket.send(new DatagramPacket(SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes(),
						SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes().length,
						InetAddress.getByName(multicastGroupAddress), udpPort));
				udpSocket.close();

			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
				return;
			}

			boolean listening = true;
			try {
				serverSocket.setSoTimeout(SystemInfoRetrieverProtocol.TIMEOUT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			while (listening) {

				LOG.log(Level.INFO, "Waiting for a new client on port {0}", tcpPort);
				try {
					Socket clientSocket = serverSocket.accept();
					LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
					Thread newServant = new Thread(new ServantWorker(clientSocket));
					newServant.start();
					servants.add(newServant);
				} catch (IOException ex) {
					listening = false;
					Logger.getLogger("Receptionist is no longer accepting connections");
				}
			}

			for (Thread t : servants) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * Permet la gestion des échanges avec les clients s'étant connectés 
		 * Un thread est créé par client, les informations reçues sont stockées
		 * dans la liste de PCInfos
		 * 
		 * @author CIANI Antony
		 * @author STEINER Lucie
		 *
		 */
		private class ServantWorker implements Runnable {

			private Socket clientSocket;
			private BufferedReader in;
			private PrintWriter out;

			public ServantWorker(Socket clientSocket) {
				try {
					this.clientSocket = clientSocket;
					LOG.info(clientSocket.getInetAddress().toString());
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					out = new PrintWriter(clientSocket.getOutputStream());

				} catch (IOException ex) {
					Logger.getLogger(SystemInfoRetrieverServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			@Override
			public void run() {
				boolean isSizeReceived = false;
				boolean isInfoReceived = false;
				boolean isPublicKeyReceived = false;
				boolean isInfoReady = false;
				String msg = "";
				SecretKey secretKey = null;
				RSAPublicKey publicKey = null;

				try {
					LOG.info("Waiting for client INFOISREADY");
					ObjectInputStream ois = null;
					OutputStream tmpOut = null;
					while ((!isInfoReady) && (msg = in.readLine()) != null) {
						LOG.info(msg);
						if (msg.equals(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO)) {

							LOG.info("RECEIVED READY");
							isInfoReady = true;

							out.println(SystemInfoRetrieverProtocol.WAITING_FOR_PUBLIC_KEY);
							out.flush();

							// Récupération de la clé publique du client
							ois = new ObjectInputStream(clientSocket.getInputStream());
							while ((!isPublicKeyReceived) && (publicKey = (RSAPublicKey) ois.readObject()) != null) {

								isPublicKeyReceived = true;
								LOG.info("RECEIVED PUBLIC KEY");

								out.println(SystemInfoRetrieverProtocol.READY_TO_READ_INFO);
								out.flush();
								LOG.info("SENT READY TO READ");

							}

							// Génération et envoi de la clé secrète chiffrée avec la clé publique
							tmpOut = clientSocket.getOutputStream();
							while ((msg = in.readLine()) != null) {
								if (msg.equals(SystemInfoRetrieverProtocol.WAITING_FOR_SECRET_KEY)) {

									// Génération de la clé secrète symétrique
									secretKey = Cryptography.generateAESSecretKey();

									// Chiffrement de la clé secrète avec la clé publique
									byte[] encryptedSecretKey = Cryptography.RSAEncrypt(secretKey.getEncoded(),
											publicKey);

									// Envoi de la clé secrète
									tmpOut.write(encryptedSecretKey);
									break;
								}
							}
						}
					}

					// Réception et déchiffrement des données
					PCInfo pc = null;
					InputStream tmpIn = null;
					byte[] encryptedPC = null;
					while (!isSizeReceived && (msg = in.readLine()) != null) {
						isSizeReceived = true;
						// Réception de la taille du message
						int msgSize = Integer.parseInt(msg);
						encryptedPC = new byte[msgSize];
						out.println(SystemInfoRetrieverProtocol.MSG_SIZE_RECEIVED);
						out.flush();
					}
					
						// Réception des données chiffrées
						tmpIn = clientSocket.getInputStream();
						while (tmpIn.read(encryptedPC) != -1) {

							isInfoReceived = true;
							LOG.info("READING OBJECT");

							// Déchiffrement du message avec la clé secrète
							byte[] decryptedPC = Cryptography.AESDecrypt(encryptedPC, secretKey);

							// Reconstruction l'objet à partir des octets 
							ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(decryptedPC));
							pc = (PCInfo) objIn.readObject();

							// Ajout dans la liste des PCInfos
							pcInfos.add(pc);
							objIn.close();
							LOG.info("Informations retrieved");

						}
					
					LOG.info("Cleaning up resources...");
					tmpIn.close();
					tmpOut.close();
					clientSocket.close();
					in.close();
					out.close();
					ois.close();

				} catch (IOException | ClassNotFoundException ex) {
					if (in != null) {
						try {
							in.close();
						} catch (IOException ex1) {
							LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
						}
					}
					if (out != null) {
						out.close();
					}
					if (clientSocket != null) {
						try {
							clientSocket.close();
						} catch (IOException ex1) {
							LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
						}
					}
					LOG.log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		}
	}
}
