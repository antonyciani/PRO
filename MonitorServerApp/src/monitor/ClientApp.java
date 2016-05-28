package monitor;

import java.io.IOException;
import communication.SystemInfoRetrieverClient;
import communication.SystemInfoRetrieverProtocol;

/**
 * @author CIANI Antony
 *
 */
public class ClientApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			SystemInfoRetrieverClient sirc = new SystemInfoRetrieverClient(SystemInfoRetrieverProtocol.UDP_PORT,
					SystemInfoRetrieverProtocol.TCP_PORT);

			while (true) {
				System.out.println("Listening to server");
				sirc.startListening();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
