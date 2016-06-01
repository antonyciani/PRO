package monitor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Cette classe permet d'envelopper les informations relatives au CPU dans des objets
 * observables. Ces objets permettent de notifier automatiquement la vue correspondante
 * si un champ venait à changer.
 *
 * @author CIANI Antony
 *
 */
public class CPUInfoViewWrapper {
	private StringProperty constructor;
	private StringProperty model;
	private DoubleProperty frequency;
	private IntegerProperty nbCores;

	/**
	 * Constructeur, construit l'objet à partir des infos relatives au CPU
	 *
	 * @param constructor, le nom du fabriquant
	 * @param model, le modèle
	 * @param frequency, la fréquence
	 * @param numbCore, le nombre de coeurs
	 */
	public CPUInfoViewWrapper(String constructor, String model, double frequency, int nbCores) {
		this.constructor = new SimpleStringProperty(constructor);
		this.model = new SimpleStringProperty(model);
		this.frequency = new SimpleDoubleProperty(frequency);
		this.nbCores = new SimpleIntegerProperty(nbCores);
	}

	/**
	 * Constructeur, construit l'objet à partir de l'objet CPUInfo
	 *
	 * @param cpu, l'objet contenant les infos relative au CPU
	 */
	public CPUInfoViewWrapper(CPUInfo cpu) {
		this.constructor = new SimpleStringProperty(cpu.getConstructor());
		this.model = new SimpleStringProperty(cpu.getModel());
		this.frequency = new SimpleDoubleProperty(cpu.getFrequency());
		this.nbCores = new SimpleIntegerProperty(cpu.getNbCore());
	}

	/**
	 * @return le nom du fabriquant
	 */
	public String getConstructor() {
		return constructor.get();
	}

	/**
	 * @return le nom du fabriquant sous forme d'objet observable
	 */
	public StringProperty constructorProperty() {
		return constructor;
	}

	/**
	 * @return le modèle du CPU
	 */
	public String getModel() {
		return model.get();
	}

	/**
	 * @return le modèle du CPU sous forme d'objet observable
	 *
	 */
	public StringProperty modelProperty() {
		return model;
	}

	/**
	 * @return la fréquence du CPU
	 */
	public double getFrequency() {
		return frequency.get();
	}

	/**
	 * @return la fréquence du CPU sous forme d'objet observable
	 */
	public DoubleProperty frequencyProperty() {
		return frequency;
	}

	/**
	 * @return le nombre de coeur du CPU
	 */
	public int getNbCore() {
		return nbCores.get();
	}

	/**
	 * @return le nombre de coeur du CPU sous forme d'objet observable
	 */
	public IntegerProperty nbCoreProperty() {
		return nbCores;
	}

}
