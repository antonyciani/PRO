package monitor;

import java.io.IOException;
import communication.SystemInfoRetrieverClient;
import communication.SystemInfoRetrieverProtocol;

/**
 * Cette classe est le programme principal de l'application côté client.
 * Elle se charge d'envoyer les informations du poste sur lequel elle s'exécute
 * au côté serveur.
 * 
 * @author CIANI Antony
 *
 */
public class ClientApp {

	/**
	 * Programme principal
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			SystemInfoRetrieverClient sirc = new SystemInfoRetrieverClient(SystemInfoRetrieverProtocol.UDP_PORT,
					SystemInfoRetrieverProtocol.TCP_PORT);

			// Le client est toujours en écoute
			while (true) {
				System.out.println("Listening to server");
				sirc.startListening();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
