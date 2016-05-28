package monitor.model;

import java.io.Serializable;

/**
 * @author CIANI Antony
 *
 */
@SuppressWarnings("serial")
public class Program implements Serializable {
	String name;
	String version;

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
