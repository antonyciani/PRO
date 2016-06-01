package monitor.model;

import java.io.Serializable;

/**
 * Cette classe permet de stocker les informations relatives au disque dur.
 *
 * @author CIANI Antony
 *
 */
@SuppressWarnings("serial")
public class HDDInfo implements Serializable {

	private double totalSize;
	private double freeSize;

	/**
	 * Constructeur, construit l'objet à partir des infos relatives au disque dur.
	 *
	 * @param totalSize, la taile totale du disque dure
	 * @param freeSize, l'espace libre restant dans le disque dur
	 */
	public HDDInfo(double totalSize, double freeSize) {
		this.totalSize = totalSize;
		this.freeSize = freeSize;
	}

	/**
	 * @return la taille totale du disque dur
	 */
	public double getTotalSize() {
		return totalSize;
	}

	/**
	 * @return l'espace libre du disque dur
	 */
	public double getFreeSize() {
		return freeSize;
	}

	/**
	 * @param d, l'espace libre du disque dur
	 */
	public void setFreeSize(double d) {
		freeSize = d;
	}
}
