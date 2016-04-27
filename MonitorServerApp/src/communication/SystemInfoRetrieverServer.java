package communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import monitor.model.PCInfo;

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
			pcInfos = new LinkedList<>();
			
		}

		/**
		 * This method initiates the process. The server creates a socket and binds it
		 * to the previously specified port. It then waits for clients in a infinite
		 * loop. When a client arrives, the server will read its input line by line
		 * and send back the data converted to uppercase. This will continue until the
		 * client sends the "BYE" command.
		 */
		public LinkedList<PCInfo> retrieveInfosFromClients() {
			LOG.info("Starting the Receptionist Worker on a new thread...");
			new Thread(new ReceptionistWorker()).start();
			return pcInfos;
		}
		

		/**
		 * This inner class implements the behavior of the "receptionist", whose
		 * responsibility is to listen for incoming connection requests. As soon as a
		 * new client has arrived, the receptionist delegates the processing to a
		 * "servant" who will execute on its own thread.
		 */
		private class ReceptionistWorker implements Runnable {

			
			@Override
			public void run() {
				ServerSocket serverSocket;
				DatagramSocket udpSocket;
				

				try {
					serverSocket = new ServerSocket(tcpPort);
					udpSocket = new DatagramSocket(SystemInfoRetrieverProtocol.SERVER_PORT);
					udpSocket.setBroadcast(true);
					
					udpSocket.send(new DatagramPacket(SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes(), SystemInfoRetrieverProtocol.REQUEST_INFO.getBytes().length, InetAddress.getByName("localhost"), udpPort ));
					
				} catch (IOException ex) {
					LOG.log(Level.SEVERE, null, ex);
					return;
				}
				
				while (true) {
					
					LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", tcpPort);
					try {
						Socket clientSocket = serverSocket.accept();
						LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
						new Thread(new ServantWorker(clientSocket)).start();
					} catch (IOException ex) {
						Logger.getLogger(SystemInfoRetrieverServer.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

			}

		/**
		 * This inner class implements the behavior of the "servants", whose
		 * responsibility is to take care of clients once they have connected. This
		 * is where we implement the application protocol logic, i.e. where we read
		 * data sent by the client and where we generate the responses.
		 */
			private class ServantWorker implements Runnable {

				Socket clientSocket;
				InputStream in = null;
				OutputStream out = null;
				

				public ServantWorker(Socket clientSocket) {
					try {
						this.clientSocket = clientSocket;
						LOG.info(clientSocket.getInetAddress().toString());
						in = clientSocket.getInputStream();
						out = clientSocket.getOutputStream();
					} catch (IOException ex) {
						Logger.getLogger(SystemInfoRetrieverServer.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				@Override
				public void run() {
					PCInfo data;
					boolean shouldRun = true;
					boolean isInfoReady = false;
					String msg = "";
					
					
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(in));
						LOG.info("Waiting for client INFOISREADY");
						while ((!isInfoReady) && (msg = br.readLine()) != null) {
							if(msg.equals(SystemInfoRetrieverProtocol.READY_TO_SEND_INFO)){
								isInfoReady = true;
								LOG.info("RECEIVED READY");
							}
						}
						LOG.info("Reading object data from clients");
						ObjectInputStream ois = new ObjectInputStream(in);
						while ((shouldRun) && (data = (PCInfo) ois.readObject()) != null) {
							pcInfos.add(data);
							shouldRun = false;
						}

						LOG.info("Cleaning up resources...");
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
							try {
								out.close();
							} catch (IOException e) {
								LOG.log(Level.SEVERE, e.getMessage(), e);
							}
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

