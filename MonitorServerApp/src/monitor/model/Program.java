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
	 * @param name
	 * @param version
	 */
	public Program(String name, String version) {
		this.name = name;
		this.version = version;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getVersion() {
		return version;
	}

}
