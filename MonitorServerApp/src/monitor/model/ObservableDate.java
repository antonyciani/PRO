package monitor.model;

import javafx.beans.property.SimpleStringProperty;

/**
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
