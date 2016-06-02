package monitor.model;

import java.io.Serializable;

/**
 * Cette classe permet de stocker les informations relatives au CPU.
 *
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
	 * Constructeur, construit l'objet à partir des infos relatives au CPU
	 *
	 * @param constructor, le nom du fabriquant
	 * @param model, le modèle
	 * @param frequency, la fréquence
	 * @param numbCore, le nombre de coeurs
	 */
	public CPUInfo(String constructor, String model, double frequency, int numbCore) {
		this.constructor = constructor;
		this.model = model;
		this.frequency = frequency;
		this.numbCore = numbCore;
	}

	/**
	 * @return le nom du fabriquant du CPU
	 */
	public String getConstructor() {
		return constructor;
	}

	/**
	 * @return le modèle du CPU
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @return la fréquence du CPU
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @return le nombre de coeur du CPU
	 */
	public int getNbCore() {
		return numbCore;
	}
}
