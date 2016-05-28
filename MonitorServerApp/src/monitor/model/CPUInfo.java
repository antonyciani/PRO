package monitor.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CPUInfo implements Serializable{
	private String constructor;
	private String model;
	private double frequency;
	private int numbCore;

	public CPUInfo(String constructor, String model, double frequency, int numbCore){
		this.constructor = constructor;
		this.model = model;
		this.frequency = frequency;
		this.numbCore = numbCore;
	}

	public String getConstructor() {
		return constructor;
	}

	public String getModel() {
		return model;
	}

	public double getFrequency() {
		return frequency;
	}

	public int getNbCore() {
		return numbCore;
	}

}
