package monitor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CPUInfoViewWrapper {
	private StringProperty constructor;
	private StringProperty model;
	private DoubleProperty frequency;
	private IntegerProperty nbCores;

	
	public CPUInfoViewWrapper(String constructor, String model, double frequency, int nbCores){
		this.constructor = new SimpleStringProperty(constructor);
		this.model = new SimpleStringProperty(model);
		this.frequency = new SimpleDoubleProperty(frequency);
		this.nbCores = new SimpleIntegerProperty(nbCores);
	}
	
	public CPUInfoViewWrapper(CPUInfo cpu){
		this.constructor = new SimpleStringProperty(cpu.getConstructor());
		this.model = new SimpleStringProperty(cpu.getModel());
		this.frequency = new SimpleDoubleProperty(cpu.getFrequency());
		this.nbCores = new SimpleIntegerProperty(cpu.getNbCore());
	}

	public String getConstructor() {
		return constructor.get();
	}
	
	public StringProperty constructorProperty(){
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

	public int getNbCore() {
		return nbCores.get();
	}
	
	public IntegerProperty nbCoreProperty() {
		return nbCores;
	}

}
