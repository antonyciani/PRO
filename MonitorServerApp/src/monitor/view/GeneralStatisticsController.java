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
 * Cette classe joue le rôle de contrôleur. Elle gère l'affichage d'une fenêtre graphique
 * comportant les statistiques suivantes:
 * 	-Nombre de coeur de processeur différents dans le parc
 * 	-Différents modèles de processeurs
 *  -Différentes capacité de stockage
 *  -Différentes tailles de mémoires
 *  Les graphiques sont formés à partir de la capture courante, et représentent donc l'état
 *  du parc informatique courant. Les graphiques générés sont des graphiques en camambert.
 *
 * @author ROHRER Michaël
 */
public class GeneralStatisticsController {

	private Database database;
	private ServerApp serverApp;

	//Nombre de coeur de processeur différents dans le parc
	@FXML
	private PieChart nbCoreChart;
	//Différents modèles de processeurs
	@FXML
	private PieChart modelChart;
	//Différentes capacité de stockage
	@FXML
	private PieChart hddSizeChart;
	//Différentes tailles de mémoires
	@FXML
	private PieChart ramSizeChart;

	/**
	 * Utilisé par le xml loader pour initialiser les objets déclarés dans le fichier xml
	 *
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * Permet d'initialiser le contrôleur et génère les graphiques.
	 *
	 * @param serverApp, en référence à l'application principale, permet d'en utilisé ses méthodes
	 */
	public void init(ServerApp serverApp) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		showNumberOfCoreStatistics();
		showModelStatistics();
		showHardDriveStatistics();
		showRamStatistics();
		//TestPDF.generatePdfCapture(serverApp.getCurentDateView().get(), nbCoreChart, modelChart, hddSizeChart, ramSizeChart);
	}

	/**
	 * Génère un graphique en camembert permettant de visualiser les différents types de processeurs du
	 * parc informatique en fonction de leur nombre de coeur.
	 *
	 */
	public void showNumberOfCoreStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<Integer, Integer> nbCore = database.nbPcByNbCores(serverApp.getCurentDateView().get());

		//Formatage des données
		for (Entry<Integer, Integer> e : nbCore.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " Cores", e.getValue()));
		}
		//Assignation des données au graphique
		nbCoreChart.setData(pieChartData);

	}

	/**
	 * Génère un graphique en camembert permettant de visualiser les différents modèles de processeurs du
	 * parc informatique.
	 *
	 */
	public void showModelStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<String, Integer> constructor = database.nbPcByModel(serverApp.getCurentDateView().get());
		//Formatage des données
		for (Entry<String, Integer> e : constructor.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
		}
		//Assignation des données au graphique
		modelChart.setData(pieChartData);
	}

	/**
	 * Génère un graphique en camembert permettant de visualiser les différents tailles de disque dur du
	 * parc informatique.
	 *
	 */
	public void showHardDriveStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<Float, Integer> hdd = database.nbPcByHddSize(serverApp.getCurentDateView().get());

		//Formatage des données
		for (Entry<Float, Integer> e : hdd.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
		}
		//Assignation des données au graphique
		hddSizeChart.setData(pieChartData);
	}

	/**
	 * Génère un graphique en camembert permettant de visualiser les différents tailles de mémoire vives du
	 * parc informatique.
	 *
	 */
	public void showRamStatistics() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<Integer, Integer> ram = database.nbPcByRamSize(serverApp.getCurentDateView().get());

		//Formatage des données
		for (Entry<Integer, Integer> e : ram.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
		}
		//Assignation des données au graphique
		ramSizeChart.setData(pieChartData);
	}
}
