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

/**
 * Cette classe joue le rôle de contrôleur. Elle permet de visualiser  les
 * statistiques correspondant au taux moyen d'utilisation de l'ensemble de
 * l'espace de stockage du parc informatique de l'entreprise.
 *
 * @author ROHRER Michaël
 */
public class AverageStorageLoadStatisticDialogController {
	//La base de donnée
	private Database database;

	//La référence à l'application principale
	private ServerApp serverApp;

	//Les objets indispensables à la conception du graphique
	@FXML
	private LineChart<String, Double> lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	/**
	 * Utilisé par le xml loader pour initialiser les objets déclarés dans le fichier xml
	 * 
	 */
	@FXML
	private void initialize() {}

	/**
	 * Permet d'initialiser le contrôleur et génère le graphique.
	 *
	 * @param serverApp, en référence à l'application principale, permet d'en utilisé ses méthodes
	 */
	public void init(ServerApp serverApp) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();

		//Génération du graphique
		showStatistics();
	}

	/**
	 * Génère le graphique correspondant à l'évolution du taux de chargement selon la capacité totale
	 * de stockage du parc informatique.
	 *
	 */
	private void showStatistics() {
		lineChart.getData().clear();

		// Récupère les infos de la base de donnée
		TreeMap<String, Double> map = database.averageStorageLoadRate(serverApp.getCurentDateView().getValue());

		//Crée les axes du graphique
		XYChart.Series<String, Double> series = new XYChart.Series<>();
		series.setName("Average Storage Load Rate");

		//Lie les données aux différents axes
		for (Entry<String, Double> e : map.entrySet()) {
			series.getData().add(new XYChart.Data<String, Double>(e.getKey(), e.getValue()));
		}
		//Ajout des données au graphique
		lineChart.getData().add(series);
	}
}
