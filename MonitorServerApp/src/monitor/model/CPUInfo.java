package monitor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CPUInfo {
	private StringProperty constructor;
	private StringProperty model;
	private DoubleProperty frequency;
	private IntegerProperty numbCore;

	public CPUInfo(String constructor, String model, double frequency, int numbCore){
		this.constructor = new SimpleStringProperty(constructor);
		this.model = new SimpleStringProperty(model);
		this.frequency = new SimpleDoubleProperty(frequency);
		this.numbCore = new SimpleIntegerProperty(numbCore);
	}

	public String getConstructor() {
		return constructor.get();
	}

	public StringProperty constructorProperty() {
		return constructor;
	}

	public String getModel() {
		return model.get();
	}

	public StringProperty modelProperty() {
		return model;
	}

	public double getFrequency() {
		return frequency.get();
	}

	public DoubleProperty frequencyProperty() {
		return frequency;
	}

	public int getNumbCore() {
		return numbCore.get();
	}

	public IntegerProperty numbCoreProperty() {
		return numbCore;
	}
}
