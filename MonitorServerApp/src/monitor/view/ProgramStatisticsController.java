package monitor.view;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

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
 * Cette classe joue le rôle de contrôleur. Elle gère l'affichage d'une fenêtre graphique
 * comportant des statistiques portant sur les programmes du parc informatique, à savoir:
 * 	-Un graphique représentant une sélection des programmes les plus installés dans le parc.
 *  -Un graphique représentant le nombre de programmes installés par version.
 *
 * @author ROHRER Michaël
 * @author STEINER Lucie
 */
public class ProgramStatisticsController {

	private Database database;
	private ServerApp serverApp;
	private String currentProgram;

	//Table permettant la sélection du programme pour le graphique présentant le nombre de
	//programmes installés par version
	@FXML
	TableView<String> programTable;
	@FXML
	TableColumn<String, String> programColumn;

	//Graphique représentant une sélection des programmes les plus installés dans le parc
	@FXML
	BarChart<String, Integer> mostFrequentlyInstalledPrograms;
	@FXML
	private CategoryAxis xAxisA;
	@FXML
	private NumberAxis yAxisA;

	//Graphique représentant le nombre de programmes installés par version
	@FXML
	BarChart<String, Integer> numberOfInstalledProgramsByVersion;
	@FXML
	private CategoryAxis xAxisB;
	@FXML
	private NumberAxis yAxisB;

	/**
	 * Utilisé par le xml loader pour initialiser les objets déclarés dans le fichier xml
	 *
	 */
	@FXML
	private void initialize() {

		//Configure les valeurs de la table de programmes
		programColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

		//Ajoute un listener sur les cellules de la table pour récupérer le programme sélectionné
		//Appel de la méthode permettant de générer le graphique correspondant
		programTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showNumberOfInstalledProgramsByVersionBarChart(newValue));

	}

	/**
	 * Permet d'initialiser le contrôleur et génère les graphiques.
	 *
	 * @param serverApp, en référence à l'application principale, permet d'en utilisé ses méthodes
	 */
	public void init(ServerApp serverApp) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();

		//Récupère la liste de programme dans la base de donnée et l'assigne à la table
		programTable.setItems(
				FXCollections.observableArrayList(database.getProgramsName(serverApp.getCurentDateView().get())));

		//Génère le graphique représentant les programmes les plus installés
		showMostFrequentlyInstalledProgramsBarChart();
	}

	/**
	 * Génère un graphique représentant une série de programmes les plus installés dans le parc
	 * informatique.
	 */
	public void showMostFrequentlyInstalledProgramsBarChart() {

		//Récupération des données dans la base de donnée
		HashMap<String, Integer> map = database.mostFrequentlyInstalledPrograms(10,
				serverApp.getCurentDateView().get());

		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		series.setName("Programs");

		//Formatage des données
		for (Entry<String, Integer> entry : map.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}
		//Assignation des données au graphique
		mostFrequentlyInstalledPrograms.getData().add(series);
	}

	/**
	 * Génère un graphique permettant de visualiser pour un programme donné, le nombre de pc ayant
	 * ses différentes versions installées.
	 *
	 * @param newValue, le programme sélectionné par l'utilisateur
	 */
	public void showNumberOfInstalledProgramsByVersionBarChart(String newValue) {
		currentProgram = newValue;
		numberOfInstalledProgramsByVersion.getData().clear();

		//Récupération des données dans la base de donnée
		TreeMap<String, Integer> map = database.nbProgramsInstalledByVersion(currentProgram,
				serverApp.getCurentDateView().get());
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		series.setName("Versions");

		//Formatage des données
		for (Entry<String, Integer> entry : map.entrySet()) {
			series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
		}
		//Assignation des données au graphique
		numberOfInstalledProgramsByVersion.getData().add(series);
	}
}
