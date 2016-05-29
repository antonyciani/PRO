package communication;

/**
 * Définit les constantes du protocole de communication
 * 
 * @author CIANI Antony
 *
 */
public class SystemInfoRetrieverProtocol {

	public static final String REQUEST_INFO = "GIVEMEINFO";
	public static final String READY_TO_SEND_INFO = "INFOISREADY";
	public static final String READY_TO_READ_INFO = "SENDINFO";
	public static final String WAITING_FOR_PUBLIC_KEY = "WAITINGFORPUBLICKEY";
	public static final String WAITING_FOR_SECRET_KEY = "WAITINGFORSECRETKEY";
	public static final String MULTICAST_ADDRESS = "224.1.1.1";
	public static final int UDP_PORT = 2000;
	public static final int TCP_PORT = 2001;
	public static final int TIMEOUT = 10000;  // Temps après lequel le serveur n'accepte plus de connexions, en ms

}
