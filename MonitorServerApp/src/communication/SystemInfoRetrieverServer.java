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
 * @author CIANI Antony
 * @author STEINER Lucie
 *
 */
public class SystemInfoRetrieverServer {

	private final static Logger LOG = Logger.getLogger(SystemInfoRetrieverServer.class.getName());

	private int udpPort;
	private int tcpPort;
	private LinkedList<PCInfo> pcInfos;

	/**
	 * @param udpPort
	 * @param tcpPort
	 * @throws SocketException
	 */
	public SystemInfoRetrieverServer(int udpPort, int tcpPort) throws SocketException {

		this.udpPort = udpPort;
		this.tcpPort = tcpPort;
		this.pcInfos = new LinkedList<>();
	}

	/**
	 * 
	 */
	public void retrieveInfosFromClients() {
		LOG.info("Starting the Receptionist Worker on a new thread...");
		Thread receptionist = new Thread(new ReceptionistWorker());
		receptionist.start();
		try {
			receptionist.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @return
	 */
	public LinkedList<PCInfo> getPcInfos() {

		return pcInfos;
	}

	/**
	 * @author CIANI Antony
	 *
	 */
	private class ReceptionistWorker implements Runnable {

		@Override
		public void run() {
			ServerSocket serverSocket;
			MulticastSocket udpSocket;
			// DatagramSocket udpSocket;
			LinkedList<Thread> servants = new LinkedList<>();

			try {
				serverSocket = new ServerSocket(tcpPort);
				udpSocket = new MulticastSocket();
				udpSocket.joinGroup(InetAddress.getByName(SystemInfoRetrieverProtocol.MULTICAST_ADDRESS));

				udpSocket.send(new DatagramPacket(SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes(),
						SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes().length,
						InetAddress.getByName(SystemInfoRetrieverProtocol.MULTICAST_ADDRESS), udpPort));
				udpSocket.close();

			} catch (IOException ex) {
				LOG.log(Level.SEVERE, null, ex);
				return;
			}

			boolean listening = true;
			try {
				serverSocket.setSoTimeout(20000);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (listening) {

				LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", tcpPort);
				try {
					Socket clientSocket = serverSocket.accept();
					LOG.info("Socket " + clientSocket);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
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
					LOG.info("Socket " + clientSocket);
					LOG.info(clientSocket.getInetAddress().toString());
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					out = new PrintWriter(clientSocket.getOutputStream());

				} catch (IOException ex) {
					Logger.getLogger(SystemInfoRetrieverServer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

			@Override
			public void run() {
				boolean isInfoReceived = false;
				boolean isPublicKeyReceived = false;
				boolean isInfoReady = false;
				String msg = "";
				SecretKey secretKey = null;
				RSAPublicKey publicKey = null;

				try {
					LOG.info("Waiting for client INFOISREADY");

					LOG.info(msg);

					ObjectInputStream ois = null;
					OutputStream tmpOut = null;
					while ((!isInfoReady) && (msg = in.readLine()) != null) {
						LOG.info(msg);
						if (msg.equals(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO)) {

							LOG.info("RECEIVED READY");
							isInfoReady = true;

							out.println(SystemInfoRetrieverProtocol.WAITING_FOR_PUBLIC_KEY);
							out.flush();

							// R�cup�rer cl� publique du client
							ois = new ObjectInputStream(clientSocket.getInputStream());
							while ((!isPublicKeyReceived) && (publicKey = (RSAPublicKey) ois.readObject()) != null) {

								isPublicKeyReceived = true;
								LOG.info("RECEIVED PUBLIC KEY");

								out.println(SystemInfoRetrieverProtocol.READY_TO_READ_INFO);
								out.flush();
								LOG.info("SENT READY TO READ");

							}

							// G�n�ration et envoi de la cl� secr�te chiffr�e
							// avec la cl� publique
							tmpOut = clientSocket.getOutputStream();
							while ((msg = in.readLine()) != null) {
								if (msg.equals(SystemInfoRetrieverProtocol.WAITING_FOR_SECRET_KEY)) {

									// G�n�rer cl� secr�te sym�trique
									secretKey = Cryptography.generateAESSecretKey();

									// Chiffrer cl� secr�te avec cl� publique
									byte[] encryptedSecretKey = Cryptography.RSAEncrypt(secretKey.getEncoded(),
											publicKey);

									// Envoyer la cl� secr�te

									tmpOut.write(encryptedSecretKey);
									break;
								}
							}
						}
					}

					// R�ception et d�chiffrement des donn�es
					PCInfo pc = null;
					InputStream tmpIn = null;
					while (!isInfoReceived && (msg = in.readLine()) != null) {

						// R�ception de la taille du message
						int msgSize = Integer.parseInt(msg);
						byte[] encryptedPC = new byte[msgSize];

						// R�ception des donn�es chiffr�es

						tmpIn = clientSocket.getInputStream();
						while (tmpIn.read(encryptedPC) != -1) {

							isInfoReceived = true;
							LOG.info("READING OBJECT");

							// D�chiffrer le message avec la cl� secr�te
							byte[] decryptedPC = Cryptography.AESDecrypt(encryptedPC, secretKey);

							// Reconstruire l'objet � partir des bytes
							ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(decryptedPC));
							pc = (PCInfo) objIn.readObject();

							pcInfos.add(pc);
							objIn.close();

							System.out.println(pc.getHostname());
							System.out.println(pc.getIpAddress());
							System.out.println(pc.getMacAddress());
							System.out.println(pc.getOs());
							System.out.println(pc.getRamSize());
							System.out.println(pc.getCpu().getConstructor());
							System.out.println(pc.getCpu().getModel());
							System.out.println(pc.getHdd().getFreeSize());
							System.out.println(pc.getPrograms().size());

							LOG.info("Cleaning up resources...");

						}
					}
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
