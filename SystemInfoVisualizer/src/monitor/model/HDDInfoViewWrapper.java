package monitor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Cette classe permet d'envelopper les informations relatives au disque dur dans des objets
 * observables. Ces objets permettent de notifier automatiquement la vue correspondante si un
 * champ venait à changer.
 *
 * @author CIANI Antony
 *
 */
public class HDDInfoViewWrapper {
	private DoubleProperty totalSize;
	private DoubleProperty freeSize;

	/**
	 * Constructeur, construit l'objet à partir des infos relatives au CPU
	 *
	 * @param totalSize, la taile totale du disque dure
	 * @param freeSize, l'espace libre restant dans le disque dur
	 */
	public HDDInfoViewWrapper(double totalSize, double freeSize) {
		this.totalSize = new SimpleDoubleProperty(totalSize);
		this.freeSize = new SimpleDoubleProperty(freeSize);
	}

	/**
	 * Constructeur, construit l'objet à partir de l'objet HDDInfo
	 *
	 * @param hdd, l'objet contenant les infos relative au disque dur
	 */
	public HDDInfoViewWrapper(HDDInfo hdd) {
		this.totalSize = new SimpleDoubleProperty(hdd.getTotalSize());
		this.freeSize = new SimpleDoubleProperty(hdd.getFreeSize());
	}

	/**
	 * @return la taille totale du disque dur
	 */
	public double getTotalSize() {
		return totalSize.get();
	}

	/**
	 * @return la taille totale du disque dur sous forme d'objet observable
	 */
	public DoubleProperty totalSizeProperty() {
		return totalSize;
	}

	/**
	 * @return l'espace libre du disque dur
	 */
	public double getFreeSize() {
		return freeSize.get();
	}

	/**
	 * @return l'espace libre du disque dur sous forme d'objet observable
	 */
	public DoubleProperty freeSizeProperty() {
		return freeSize;
	}
}
