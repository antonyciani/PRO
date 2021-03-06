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
	public static final String WAITING_FOR_PUBLIC_KEY = "WAITINGFORPUBLICKEY";
	public static final String PUBLIC_KEY_RECEIVED = "PUBLICKEYRECEIVED";
	public static final String WAITING_FOR_SECRET_KEY = "WAITINGFORSECRETKEY";
	public static final String READY_TO_READ_INFO = "READYTOREADINFO";

	public static final int TIMEOUT = 10000;  // Temps après lequel le serveur n'accepte plus de connexions, en ms

}
