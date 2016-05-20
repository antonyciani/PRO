package monitor.view;

import java.util.HashMap;
import java.util.Map.Entry;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import monitor.ServerApp;
import monitor.database.Database;

public class AverageStorageLoadStatisticDialogController {
	private Database database;
	private ServerApp serverApp;

	@FXML
	private LineChart lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

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
		lineChart.getData().clear();

    	//Récupère les infos de la base de donnée
    	HashMap<String, Double> map = database.averageFreeHardDriveSizeRate();

    	//Ajout des données au graphique

    	XYChart.Series series = new XYChart.Series();

    	for(Entry<String, Double> e : map.entrySet()){
			series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
		}

    	lineChart.getData().add(series);
	}
}
