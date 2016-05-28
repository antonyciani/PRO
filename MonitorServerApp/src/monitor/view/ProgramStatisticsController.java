package monitor.view;

import java.util.HashMap;
import java.util.Map.Entry;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import monitor.ServerApp;
import monitor.database.Database;

/**
 * @author ROHRER Michaël
 * @author STEINER Lucie
 *
 */
public class ProgramStatisticsController {

	private Database database;
	private ServerApp serverApp;
	private String currentProgram;

	@FXML
	TableView<String> programTable;
	@FXML
	TableColumn<String, String> programColumn;

	@FXML
	BarChart<String, Integer> mostFrequentlyInstalledPrograms;
	@FXML
	private CategoryAxis xAxisA;
	@FXML
	private NumberAxis yAxisA;

	@FXML
	BarChart<String, Integer> numberOfInstalledProgramsByVersion;
	@FXML
	private CategoryAxis xAxisB;
	@FXML
	private NumberAxis yAxisB;

	@FXML
	private void initialize() {
		programColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
		// Listen for selection changes and show the person details when
		// changed.
		programTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showNumberOfInstalledProgramsByVersionBarChart(newValue));

	}

	/**
	 * @param serverApp
	 */
	public void init(ServerApp serverApp) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		programTable.setItems(
				FXCollections.observableArrayList(database.getProgramsName(serverApp.getCurentDateView().get())));
		showMostFrequentlyInstalledProgramsBarChart();
	}

	/**
	 * 
	 */
	public void showMostFrequentlyInstalledProgramsBarChart() {
		HashMap<String, Integer> map = database.mostFrequentlyInstalledPrograms(10,
				serverApp.getCurentDateView().get());
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		series.setName("Programs");
		for (Entry<String, Integer> entry : map.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}
		mostFrequentlyInstalledPrograms.getData().add(series);
	}

	/**
	 * @param newValue
	 */
	public void showNumberOfInstalledProgramsByVersionBarChart(String newValue) {
		currentProgram = newValue;
		numberOfInstalledProgramsByVersion.getData().clear();
		HashMap<String, Integer> map = database.nbProgramsInstalledByVersion(currentProgram,
				serverApp.getCurentDateView().get());
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		series.setName("Versions");
		for (Entry<String, Integer> entry : map.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}
		numberOfInstalledProgramsByVersion.getData().add(series);
	}
}
