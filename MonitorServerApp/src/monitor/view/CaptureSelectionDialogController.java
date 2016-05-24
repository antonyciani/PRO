package monitor.view;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.ObservableDate;
import monitor.model.PCInfoViewWrapper;

public class CaptureSelectionDialogController {

	private Database database;
	private Stage dialogStage;
	private ServerApp serverApp;
	private String selectedDate;
	private ObservableList<ObservableDate> list;

	@FXML
	private TableView<ObservableDate>  dateTable;
	@FXML
	private TableColumn<ObservableDate, String> dateColumn;


	@FXML
    private void initialize(){
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
		//Listen for selection changes and show the person details when changed.
        dateTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> setDate(newValue));
    }

	public void setDatabase(Database database){
		this.database = database;
		list = createObservableDate(database.getCaptures());
		dateTable.setItems(list);
	}

	public void setServerApp(ServerApp serverApp){
		this.serverApp = serverApp;
	}

	/**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
    	this.dialogStage = dialogStage;
    }

    private void setDate(ObservableDate date){
    	selectedDate = date.getDate();
    }

    public String getDate(){
    	return selectedDate;
    }

    private ObservableList<ObservableDate> createObservableDate(List<String> dates) {
		ObservableList<ObservableDate> tmp = FXCollections.observableArrayList();
		for(String date : dates){
			tmp.add(new ObservableDate(date));
		}
		return tmp;
	}

	@FXML
	public void handleSelect(){
		dialogStage.close();
	}

	/**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
    	selectedDate = "";
        dialogStage.close();
    }
}
