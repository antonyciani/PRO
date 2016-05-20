package monitor.model;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
