package monitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import communication.SystemInfoRetrieverClient;

/**
 * Cette classe est le programme principal de l'application côté client.
 * Elle se charge d'envoyer les informations du poste sur lequel elle s'exécute
 * au côté serveur.
 * 
 * @author CIANI Antony
 *
 */
public class ClientApp {
	

	private static String confFilename = "app.properties";
	
	/**
	 * Programme principal
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		
Properties prop = new Properties();
		
		//Récupération des informations de configuration à partir du fichier de configuration
        try {
        	
        	//Chargement du fichier de configuration
            BufferedReader br = new BufferedReader(new FileReader(confFilename));
            //load a properties file from class path, inside static method
            prop.load(br);

            //Récupération des informations de configuration            
            int udpPort = Integer.parseInt(prop.getProperty("udpport"));
            int tcpPort = Integer.parseInt(prop.getProperty("tcpport"));
            String multicastGroupAddress = prop.getProperty("multicastaddress");
            
            try {
    			SystemInfoRetrieverClient sirc = new SystemInfoRetrieverClient(udpPort,tcpPort,multicastGroupAddress);

    			// Le client est toujours en écoute
    			while (true) {
    				System.out.println("Listening to server");
    				sirc.startListening();
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
        } catch (IOException ex) {
            System.out.println("app.properties couldn't be loaded, please check it is present in the same folder as the application");
            System.exit(1);
        }
		
	}
}
