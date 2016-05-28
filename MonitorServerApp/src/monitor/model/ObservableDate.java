package monitor.model;

import javafx.beans.property.SimpleStringProperty;

public class ObservableDate {

	private SimpleStringProperty date;

	public ObservableDate(String date){
		this.date = new SimpleStringProperty(date);
	}

	public SimpleStringProperty getDateProperty(){
		return date;
	}

	public String getDate(){
		return date.get();
	}
}
