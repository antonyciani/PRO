package monitor.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Cette classe permet d'envelopper les informations relatives aux programmes dans des objets
 * observables. Ces objets permettent de notifier automatiquement la vue correspondante
 * si un champ venait à changer.
 *
 * @author CIANI Antony
 *
 */
public class ProgramViewWrapper {
	private StringProperty name;
	private StringProperty version;

	/**
	 * Constructeur, construit l'objet à partir des infos relatives au programme
	 *
	 * @param name, le nom du programme
	 * @param version, la version du programme
	 */
	public ProgramViewWrapper(String name, String version) {
		this.name = new SimpleStringProperty(name);
		this.version = new SimpleStringProperty(version);

	}

	/**
	 * Constructeur, construit l'objet à partir de l'objet Program
	 *
	 * @param program, l'objet contenant les infos relative au programme
	 */
	public ProgramViewWrapper(Program program) {
		this.name = new SimpleStringProperty(program.getName());
		this.version = new SimpleStringProperty(program.getVersion());

	}

	/**
	 * @return le nom du programme
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @return le nom du programme sous forme d'objet observable
	 */
	public StringProperty nameProperty() {
		return name;
	}

	/**
	 * @return la version du programme
	 */
	public String getVersion() {
		return version.get();
	}

	/**
	 * @return la version du programme sous forme d'objet observable
	 */
	public StringProperty versionProperty() {
		return version;
	}
}
