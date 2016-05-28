package monitor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author CIANI Antony
 *
 */
public class CPUInfoViewWrapper {
	private StringProperty constructor;
	private StringProperty model;
	private DoubleProperty frequency;
	private IntegerProperty nbCores;

	/**
	 * @param constructor
	 * @param model
	 * @param frequency
	 * @param nbCores
	 */
	public CPUInfoViewWrapper(String constructor, String model, double frequency, int nbCores) {
		this.constructor = new SimpleStringProperty(constructor);
		this.model = new SimpleStringProperty(model);
		this.frequency = new SimpleDoubleProperty(frequency);
		this.nbCores = new SimpleIntegerProperty(nbCores);
	}

	/**
	 * @param cpu
	 */
	public CPUInfoViewWrapper(CPUInfo cpu) {
		this.constructor = new SimpleStringProperty(cpu.getConstructor());
		this.model = new SimpleStringProperty(cpu.getModel());
		this.frequency = new SimpleDoubleProperty(cpu.getFrequency());
		this.nbCores = new SimpleIntegerProperty(cpu.getNbCore());
	}

	/**
	 * @return
	 */
	public String getConstructor() {
		return constructor.get();
	}

	/**
	 * @return
	 */
	public StringProperty constructorProperty() {
		return constructor;
	}

	/**
	 * @return
	 */
	public String getModel() {
		return model.get();
	}

	/**
	 * @return
	 */
	public StringProperty modelProperty() {
		return model;
	}

	/**
	 * @return
	 */
	public double getFrequency() {
		return frequency.get();
	}

	/**
	 * @return
	 */
	public DoubleProperty frequencyProperty() {
		return frequency;
	}

	/**
	 * @return
	 */
	public int getNbCore() {
		return nbCores.get();
	}

	/**
	 * @return
	 */
	public IntegerProperty nbCoreProperty() {
		return nbCores;
	}

}
