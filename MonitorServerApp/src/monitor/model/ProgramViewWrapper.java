package monitor.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Cette classe permet d'envelopper les informations relatives aux programmes dans des objets
 * observables. Ces objets permettent de notifier automatiquement la vue correspondante
 * si un camp venait à changer.
 * 
 * @author CIANI Antony
 *
 */
public class ProgramViewWrapper {
	private StringProperty name;
	private StringProperty version;
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
