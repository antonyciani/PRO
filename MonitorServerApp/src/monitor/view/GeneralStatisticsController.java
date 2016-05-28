package monitor.view;

import java.util.HashMap;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import monitor.ServerApp;
import monitor.database.Database;

/**
 * @author ROHRER Michaël
 *
 */
public class GeneralStatisticsController {

	private Database database;
	private ServerApp serverApp;

	@FXML
	private PieChart nbCoreChart;
	@FXML
	private PieChart constructorChart;
	@FXML
	private PieChart hddSizeChart;
	@FXML
	private PieChart ramSizeChart;

	@FXML
	private void initialize() {

	}

	/**
	 * @param serverApp
	 */
	public void init(ServerApp serverApp) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		showNumberOfCoreStatistics();
		showConstructorStatistics();
		showHardDriveStatistics();
		showRamStatistics();
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
	public void showConstructorStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<String, Integer> constructor = database.nbPcByConstructor(serverApp.getCurentDateView().get());
		for (Entry<String, Integer> e : constructor.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
		}
		constructorChart.setData(pieChartData);
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
}
