package communication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import monitor.model.PCInfo;
import utils.Cryptography;

public class SystemInfoRetrieverServer {


		private final static Logger LOG = Logger.getLogger(SystemInfoRetrieverServer.class.getName());

		private int udpPort;
		private int tcpPort;
		private LinkedList<PCInfo> pcInfos;


		/**
		 * Constructor
		 *
		 * @param port the port to listen on
		 * @throws SocketException
		 */
		public SystemInfoRetrieverServer(int udpPort, int tcpPort) throws SocketException {

			this.udpPort = udpPort;
			this.tcpPort = tcpPort;
			this.pcInfos = new LinkedList<>();

		}


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

		public LinkedList<PCInfo> getPcInfos(){

			return pcInfos;
		}


		private class ReceptionistWorker implements Runnable {


			@Override
			public void run() {
				ServerSocket serverSocket;
				MulticastSocket udpSocket;
				//DatagramSocket udpSocket;
				LinkedList<Thread> servants = new LinkedList<>();

				try {
					serverSocket = new ServerSocket(tcpPort);
					udpSocket = new MulticastSocket();
					udpSocket.joinGroup(InetAddress.getByName(SystemInfoRetrieverProtocol.MULTICAST_ADDRESS));

					udpSocket.send(new DatagramPacket(SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes(), SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes().length, InetAddress.getByName(SystemInfoRetrieverProtocol.MULTICAST_ADDRESS), udpPort ));

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
						Logger.getLogger(SystemInfoRetrieverServer.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				for(Thread t : servants){
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
		 * This inner class implements the behavior of the "servants", whose
		 * responsibility is to take care of clients once they have connected. This
		 * is where we implement the application protocol logic, i.e. where we read
		 * data sent by the client and where we generate the responses.
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
					PCInfo data;
					boolean shouldRun = true;
					boolean isInfoReceived = false;
					boolean isInfoReady = false;
					String msg = "";
					SecretKey secretKey = null;

					try {
						ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
						LOG.info("Waiting for client INFOISREADY");

						LOG.info(msg);
						while ((!isInfoReady) && (msg = in.readLine()) != null) {
							LOG.info(msg);
							if(msg.equals(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO)){

								LOG.info("RECEIVED READY");
								isInfoReady = true;

								//Récupérer clé publique du client
								RSAPublicKey publicKey = (RSAPublicKey)ois.readObject();

								//Générer clé secrète symétrique
								secretKey = Cryptography.generateAESSecretKey();

								//Chiffrer clé secrète avec clé publique
								byte[] encryptedSecretKey = Cryptography.RSAEncrypt(secretKey.getEncoded(), publicKey);

								out.println(SystemInfoRetrieverProtocol.READY_TO_READ_INFO);
								out.flush();

								//Envoyer la clé secrète
								out.println(encryptedSecretKey);
								out.flush();

							}
						}
						PCInfo pc = null;

						while (!isInfoReceived && (msg = in.readLine()) != null){
							LOG.info("READING OBJECT");
							//Déchiffrer le message avec la clé secrète
							byte[] decryptedPC = Cryptography.AESDecrypt(msg.getBytes(), secretKey);

							//Reconstruire l'objet à partir des bytes
							ObjectInputStream tmpInput = new ObjectInputStream(new ByteArrayInputStream(decryptedPC));
							pc = (PCInfo)tmpInput.readObject();

							pcInfos.add(pc);
							isInfoReceived = true;
							System.out.println(pc.getHostname());
							System.out.println(pc.getIpAddress());
							System.out.println(pc.getMacAddress());
							System.out.println(pc.getOs());
							System.out.println(pc.getRamSize());
							System.out.println(pc.getCpu().getConstructor());
							System.out.println(pc.getCpu().getModel());
							System.out.println(pc.getHdd().getFreeSize());


							LOG.info("Cleaning up resources...");
						}
						clientSocket.close();
						in.close();
						out.close();

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

