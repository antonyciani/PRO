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

/**
 * Cette classe joue le rôle de contrôleur. Elle gère l'affichage d'une fenêtre graphique
 * comportant un résumé des statistiques portant sur l'ensemble du parc informatique. Ce
 * contrôleur gêne également l'exportation de ces données au format PDF.
 *
 * @author Michael Rohrer
 * @author Lucie Steiner
 */
public class CaptureSummaryWindowController {

	private Database database;
	private ServerApp serverApp;
	private Stage dialogStage;


	//Les différents graphiques afficher et exporter

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

	//Le graphique indiquant le taux de remplissage moyen de l'ensemble des pc du parc informatique
	@FXML
	private LineChart<String, Double> lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	//Le graphique sur le quel figure les programmes les plus installés
	@FXML
	BarChart<String, Integer> mostFrequentlyInstalledPrograms;
	@FXML
	private CategoryAxis xAxisA;
	@FXML
	private NumberAxis yAxisA;

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
	 * @param dialogStage la scène de la fenêtre graphique (permet sa fermeture)
	 */
	public void init(ServerApp serverApp, Stage dialogStage) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		this.dialogStage = dialogStage;

		//Génération des différents graphiques
		showNumberOfCoreStatistics();
		showModelStatistics();
		showHardDriveStatistics();
		showRamStatistics();
		showAverageStorageLoadRateStatistics();
		showMostFrequentlyInstalledProgramsBarChart();
	}

	/**
	 * Génère le graphique en camembert permettant de visualiser les différents types de processeurs du
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
	 * Génère le graphique en camambert permettant de visualiser les différents modèles de processeurs du
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
	 * Génère le graphique en camambert permettant de visualiser les différents tailles de disque dur du
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
	 * Génère le graphique en camembert permettant de visualiser les différents tailles de mémoire vives du
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

	/**
	 * Génère le graphique en ligne permettant de visualiser l'évolution du taux de chargement de l'espace
	 * de stockage totale du parc informatique.
	 *
	 */
	public void showAverageStorageLoadRateStatistics() {
		lineChart.getData().clear();

		//Récupération des données dans la base de donnée
		TreeMap<String, Double> map = database.averageStorageLoadRate(serverApp.getCurentDateView().getValue());

		XYChart.Series<String, Double> series = new XYChart.Series<>();
		series.setName("Average Storage Load Rate");

		//Formatage des données
		for (Entry<String, Double> e : map.entrySet()) {
			series.getData().add(new XYChart.Data<String, Double>(e.getKey(), e.getValue()));
		}
		//Assignation des données au graphique
		lineChart.getData().add(series);
	}

	/**
	 * Génère le graphique en barre permettant de visualiser les programmes les plus fréquemment
	 * installés dans le parc informatique.
	 *
	 */
	public void showMostFrequentlyInstalledProgramsBarChart() {
		//Récupération des données dans la base de donnée
		HashMap<String, Integer> map = database.mostFrequentlyInstalledPrograms(10, serverApp.getCurentDateView().get());

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
	 * Permet la génération du document pdf comportant un résumé des différents graphiques.
	 * Cette méthode est appelée dès que l'utilisateur appuie sur le bouton "Export".
	 *
	 */
	@FXML
	public void handleExport(){
		FileChooser fileChooser = new FileChooser();

        //Définition du filtre d'extension
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        //Affiche la fenêtre de dialogue demandant ou sauver le document
        String docNameCapture = serverApp.getCurentDateView().get().replace(' ', '_').replace(':', '_');
        fileChooser.setInitialFileName(docNameCapture);
        File file = fileChooser.showSaveDialog(serverApp.getPrimaryStage());


        if (file != null) {
            //Permet de s'assurer que l'extension est correcte
            if (!file.getPath().endsWith(".pdf")) {
                file = new File(file.getPath() + ".pdf");
            }
            //Génération du pdf
    		PdfGenerator.generatePdfCapture(file, serverApp.getPcInfo(), serverApp.getCurentDateView().get(), nbCoreChart, modelChart, hddSizeChart, ramSizeChart, lineChart, mostFrequentlyInstalledPrograms);
        }
        //Fermeture de la fenêtre
		dialogStage.close();
	}

	/**
	 * Permet la fermeture de la fenêtre si l'utilisateur clique sur cancel
	 *
	 */
	@FXML
	public void handleCancel(){
		//Fermeture de la fenêtre
		dialogStage.close();
	}
}

