package monitor.model;

import java.io.Serializable;

/**
 * Cette classe permet de stocker les informations relatives au programmes.
 *
 * @author CIANI Antony
 *
 */
@SuppressWarnings("serial")
public class Program implements Serializable {
	private String name;
	private String version;

	/**
	 * Constructeur, construit l'objet à partir des infos relatives au programmes.
	 *
	 * @param name, le nom du programme
	 * @param version, la version du programme
	 */
	public Program(String name, String version) {
		this.name = name;
		this.version = version;
	}

	/**
	 * @return le nom du programme
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return la version du programme
	 */
	public String getVersion() {
		return version;
	}

}
