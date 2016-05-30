package monitor.view;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import monitor.ServerApp;
import monitor.database.Database;
import utils.PdfGenerator;

public class CaptureSummaryWindowController {

	private Database database;
	private ServerApp serverApp;
	private Stage dialogStage;

	@FXML
	private PieChart nbCoreChart;
	@FXML
	private PieChart modelChart;
	@FXML
	private PieChart hddSizeChart;
	@FXML
	private PieChart ramSizeChart;

	@FXML
	private LineChart<String, Double> lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	@FXML
	BarChart<String, Integer> mostFrequentlyInstalledPrograms;
	@FXML
	private CategoryAxis xAxisA;
	@FXML
	private NumberAxis yAxisA;

	@FXML
	private void initialize() {

	}

	/**
	 * @param serverApp
	 * @param dialogStage
	 */
	public void init(ServerApp serverApp, Stage dialogStage) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		this.dialogStage = dialogStage;
		showNumberOfCoreStatistics();
		showModelStatistics();
		showHardDriveStatistics();
		showRamStatistics();
		showStatistics();
		showMostFrequentlyInstalledProgramsBarChart();
	}

	/**
	 *
	 */
	public void showNumberOfCoreStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<Integer, Integer> nbCore = database.nbPcByNbCores(serverApp.getCurentDateView().get());
		for (Entry<Integer, Integer> e : nbCore.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " Cores", e.getValue()));
		}

		nbCoreChart.setData(pieChartData);

	}

	/**
	 *
	 */
	public void showModelStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<String, Integer> constructor = database.nbPcByModel(serverApp.getCurentDateView().get());
		for (Entry<String, Integer> e : constructor.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
		}
		modelChart.setData(pieChartData);
	}

	/**
	 *
	 */
	public void showHardDriveStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<Float, Integer> hdd = database.nbPcByHddSize(serverApp.getCurentDateView().get());
		for (Entry<Float, Integer> e : hdd.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
		}
		hddSizeChart.setData(pieChartData);
	}

	/**
	 *
	 */
	public void showRamStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<Integer, Integer> ram = database.nbPcByRamSize(serverApp.getCurentDateView().get());
		for (Entry<Integer, Integer> e : ram.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
		}
		ramSizeChart.setData(pieChartData);
	}

	private void showStatistics() {
		lineChart.getData().clear();

		// Récupère les infos de la base de donnée
		TreeMap<String, Double> map = database.averageStorageLoadRate(serverApp.getCurentDateView().getValue());

		// Ajout des données au graphique

		XYChart.Series<String, Double> series = new XYChart.Series<>();
		series.setName("Average Storage Load Rate");

		for (Entry<String, Double> e : map.entrySet()) {
			series.getData().add(new XYChart.Data<String, Double>(e.getKey(), e.getValue()));
		}
		lineChart.getData().add(series);
	}

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
	@FXML
	public void handleExport(){
		FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        String docNameCapture = serverApp.getCurentDateView().get().replace(' ', '_').replace(':', '_');
        fileChooser.setInitialFileName(docNameCapture);
        File file = fileChooser.showSaveDialog(serverApp.getPrimaryStage());


        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".pdf")) {
                file = new File(file.getPath() + ".pdf");
            }
    		PdfGenerator.generatePdfCapture(file, serverApp.getPcInfo(), serverApp.getCurentDateView().get(), nbCoreChart, modelChart, hddSizeChart, ramSizeChart, lineChart, mostFrequentlyInstalledPrograms);
        }
		dialogStage.close();
	}
	@FXML
	public void handleCancel(){
		dialogStage.close();
	}
}

