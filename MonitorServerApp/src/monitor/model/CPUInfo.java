package monitor.model;

import java.io.Serializable;

/**
 * @author CIANI Antony
 *
 */
@SuppressWarnings("serial")
public class CPUInfo implements Serializable {
	private String constructor;
	private String model;
	private double frequency;
	private int numbCore;

	/**
	 * @param constructor
	 * @param model
	 * @param frequency
	 * @param numbCore
	 */
	public CPUInfo(String constructor, String model, double frequency, int numbCore) {
		this.constructor = constructor;
		this.model = model;
		this.frequency = frequency;
		this.numbCore = numbCore;
	}

	/**
	 * @return
	 */
	public String getConstructor() {
		return constructor;
	}

	/**
	 * @return
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @return
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @return
	 */
	public int getNbCore() {
		return numbCore;
	}

}
