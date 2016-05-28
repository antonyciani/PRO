package monitor.view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import monitor.ServerApp;
import monitor.model.ObservableDate;

/**
 * @author ROHRER Michaël
 *
 */
public class CaptureSelectionDialogController {

	private Stage dialogStage;
	@SuppressWarnings("unused")
	private ServerApp serverApp;
	private String selectedDate;
	private ObservableList<ObservableDate> list;

	@FXML
	private TableView<ObservableDate> dateTable;
	@FXML
	private TableColumn<ObservableDate, String> dateColumn;

	@FXML
	private void initialize() {
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
		// Listen for selection changes and show the person details when
		// changed.
		dateTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> setDate(newValue));
	}

	/**
	 * @param serverApp
	 * @param dialogStage
	 */
	public void init(ServerApp serverApp, Stage dialogStage) {
		this.serverApp = serverApp;
		this.dialogStage = dialogStage;
		list = createObservableDate(serverApp.getDatabase().getCaptures());
		dateTable.setItems(list);
		dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				handleCancel();
			}
		});
	}

	/**
	 * @param date
	 */
	private void setDate(ObservableDate date) {
		selectedDate = date.getDate();
	}

	/**
	 * @return
	 */
	public String getDate() {
		return selectedDate;
	}

	/**
	 * @param dates
	 * @return
	 */
	private ObservableList<ObservableDate> createObservableDate(List<String> dates) {
		ObservableList<ObservableDate> tmp = FXCollections.observableArrayList();
		for (String date : dates) {
			tmp.add(new ObservableDate(date));
		}
		return tmp;
	}

	/**
	 * 
	 */
	@FXML
	public void handleSelect() {
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
