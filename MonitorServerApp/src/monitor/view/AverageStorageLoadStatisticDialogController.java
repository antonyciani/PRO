package monitor.view;

import java.util.TreeMap;
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
	@SuppressWarnings("unused")
	private ServerApp serverApp;

	@FXML
	private LineChart<String, Double> lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	@FXML
	private void initialize(){

	}

	public void init(ServerApp serverApp){
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		showStatistics();
	}

	private void showStatistics(){
		lineChart.getData().clear();

    	//Récupère les infos de la base de donnée
    	TreeMap<String, Double> map = database.averageStorageLoadRate();

    	//Ajout des données au graphique

    	XYChart.Series<String, Double> series = new XYChart.Series<>();
    	series.setName("Average Storage Load Rate");

    	for(Entry<String, Double> e : map.entrySet()){
			series.getData().add(new XYChart.Data<String, Double>(e.getKey(), e.getValue()));
		}
    	lineChart.getData().add(series);
	}
}
