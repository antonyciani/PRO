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
import monitor.model.Program;

public class ProgramStatisticsController {

	private Database database;
	private ServerApp serverApp;

	private String currentProgram;

	@FXML
	TableView<String> programTable;
	@FXML
	TableColumn<String, String> programColumn;
	@FXML
	BarChart<String, Integer> numberOfInstalledProgramsByVersion;

	@FXML
	BarChart<String, Integer> mostFrequentlyInstalledPrograms;

	@FXML
	private CategoryAxis xAxisA;

	@FXML
	private NumberAxis yAxisA;


	@FXML
	private CategoryAxis xAxisB;

	@FXML
	private NumberAxis yAxisB;

	@FXML
    private void initialize(){
		programColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
		//Listen for selection changes and show the person details when changed.
        programTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showNumberOfInstalledProgramsByVersionBarChart(newValue));

    }

	public void setDatabase(Database database){
		this.database = database;
		programTable.setItems(FXCollections.observableArrayList(database.getProgramsName(serverApp.getCurentPcView())));

	}

	public void setServerApp(ServerApp serverApp){
		this.serverApp = serverApp;
	}

	public void showStatistics(){
		showMostFrequentlyInstalledProgramsBarChart();
	}


	public void showMostFrequentlyInstalledProgramsBarChart(){
		HashMap<String, Integer> map = database.mostFrequentlyInstalledPrograms(10, serverApp.getCurentPcView());
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		series.setName("Programs");
		for(Entry<String, Integer> entry : map.entrySet()){
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}

		mostFrequentlyInstalledPrograms.getData().add(series);
	}

	public void showNumberOfInstalledProgramsByVersionBarChart(String newValue){
		currentProgram = newValue;
		numberOfInstalledProgramsByVersion.getData().clear();
        HashMap<String, Integer> map = database.nbProgramsInstalledByVersion(currentProgram, serverApp.getCurentPcView());
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Versions");
        for(Entry<String, Integer> entry : map.entrySet()){
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}

        numberOfInstalledProgramsByVersion.getData().add(series);
	}
/*
	private void setProgram(Program program){
		currentProgram = program.getName();
		showNumberOfInstalledProgramsByVersionBarChart(program);
	}*/
}
