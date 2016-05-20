package monitor.view;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;
import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.PCInfoViewWrapper;

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
    private void initialize(){

    }

	public void setDatabase(Database database){
		this.database = database;
	}

	public void setServerApp(ServerApp serverApp){
		this.serverApp = serverApp;
	}

	public void showStatistics(){
		showNumberOfCoreStatistics();
		showConstructorStatistics();
		showHardDriveStatistics();
		showRamStatistics();
	}

	public void showNumberOfCoreStatistics(){
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<Integer, Integer> nbCore = database.nbPcByNbCores(serverApp.getCurentPcView());
		for(Entry<Integer, Integer> e : nbCore.entrySet()){
			pieChartData.add(new PieChart.Data(e.getKey() + " Cores", e.getValue()));
		}

		nbCoreChart.setData(pieChartData);
	}

	public void showConstructorStatistics(){
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<String, Integer> constructor = database.nbPcByConstructor(serverApp.getCurentPcView());
		for(Entry<String, Integer> e : constructor.entrySet()){
			pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
		}
		constructorChart.setData(pieChartData);
	}

	public void showHardDriveStatistics(){
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<Float, Integer> hdd = database.nbPcByHddSize(serverApp.getCurentPcView());
		for(Entry<Float, Integer> e : hdd.entrySet()){
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
		}
		hddSizeChart.setData(pieChartData);
	}

	public void showRamStatistics(){
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		HashMap<Integer, Integer> ram = database.nbPcByRamSize(serverApp.getCurentPcView());
		for(Entry<Integer, Integer> e : ram.entrySet()){
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
		}
		ramSizeChart.setData(pieChartData);
	}
}
