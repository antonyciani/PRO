package monitor.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author CIANI Antony
 *
 */
public class ProgramViewWrapper {
	private StringProperty name;
	private StringProperty version;
	private StringProperty lastUpdate;

	/**
	 * @param name
	 * @param version
	 */
	public ProgramViewWrapper(String name, String version) {
		this.name = new SimpleStringProperty(name);
		this.version = new SimpleStringProperty(version);

	}

	/**
	 * @param program
	 */
	public ProgramViewWrapper(Program program) {
		this.name = new SimpleStringProperty(program.getName());
		this.version = new SimpleStringProperty(program.getVersion());

	}

	/**
	 * @return
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @return
	 */
	public StringProperty nameProperty() {
		return name;
	}

	/**
	 * @return
	 */
	public String getVersion() {
		return version.get();
	}

	/**
	 * @return
	 */
	public StringProperty versionProperty() {
		return version;
	}
}
