package monitor.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Cette classe permet d'envelopper une date au format string dans un objet observables.
 * Cet objet permet de notifier automatiquement la vue correspondante si un champ venait
 * à changer.
 *
 * @author ROHRER Michaël
 *
 */
public class ObservableDate {

	private SimpleStringProperty date;

	/**
	 * @param date
	 */
	public ObservableDate(String date) {
		this.date = new SimpleStringProperty(date);
	}

	/**
	 * @return
	 */
	public SimpleStringProperty getDateProperty() {
		return date;
	}

	/**
	 * @return
	 */
	public String getDate() {
		return date.get();
	}
}
