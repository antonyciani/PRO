package monitor.view;

import java.util.TreeMap;
import java.io.File;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.PCInfoViewWrapper;
import utils.PdfGenerator;

/**
 * Cette classe joue le rôle de contrôleur. Elle gère l'affichage d'une fenêtre graphique
 * comportant un résumé des statistiques portant sur un pc sélectionné. Ce contrôleur gêre
 * également l'exportation de ces données au format PDF.
 *
 * @author Michael Rohrer
 * @author Lucie Steiner
 */
public class PcSummaryWindowController {


	//Concerne le graphique du taux de remplissage du disque dure
	@FXML
	private PieChart pieChart;
	private ObservableList<PieChart.Data> pieChartData;

	//Concerne le graphique représentant l'évolution du taux de chargement du disc dure
	@FXML
	private LineChart<String, Double> lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	private Stage dialogStage;
	private ServerApp serverApp;
	private Database database;

	//Le pc sur le quel sont basées ces statistiques
	private PCInfoViewWrapper pc;

	/**
	 * Utilisé par le xml loader pour initialiser les objets déclarés dans le fichier xml
	 */
	@FXML
	public void initialize(){

	}

	/**
	 * Permet d'initialiser le controleur et génère les graphiques.
	 *
	 * @param serverApp, en référence à l'application principale, permet d'en utilisé ses méthodes
	 * @param dialogStage la scène de la fenêtre graphique (permet sa fermeture)
	 */
	public void init(ServerApp serverApp, Stage dialogStage, PCInfoViewWrapper pc){
		this.serverApp = serverApp;
		this.dialogStage = dialogStage;
		this.database = serverApp.getDatabase();
		this.pc = pc;
		showPieChartDetails(pc);
		showLineChartDetails(pc);
	}


	/**
	 * Génère le graphique correspondant à l'évolution du remplissage de l'espace disque du
	 * pc au cours du temps.
	 *
	 * @param newValue, le pc sélectionné
	 */
	private void showLineChartDetails(PCInfoViewWrapper newValue) {

		if (newValue != null) {

			//Efface les éventuelles anciennes données
			lineChart.getData().clear();

			//Récupération des données dans la base de donnée
			TreeMap<String, Double> map = database.storageLoadRate(newValue,
					serverApp.getCurentDateView().get());

			Series<String, Double> series = new Series<>();
			series.setName("Storage Load Rate");

			//Formatage des données
			for (Entry<String, Double> e : map.entrySet()) {
				series.getData().add(new Data<String, Double>(e.getKey(), e.getValue()));
			}
			//Assignation des données au graphique
			lineChart.getData().add(series);
		} else {
			lineChart.getData().clear();
		}
	}


	/**
	 * Génère le graphique en camambert permettant de visualiser le taux de remplissage du disque dure.
	 *
	 * @param newValue
	 */
	private void showPieChartDetails(PCInfoViewWrapper newValue) {
		if (newValue != null) {

			//Efface les éventuelles anciennes données
			pieChart.getData().clear();

			//Récupération des données
			double freeSpace = newValue.getHdd().getFreeSize();
			double fullSpace = newValue.getHdd().getTotalSize() - newValue.getHdd().getFreeSize();

			//Formatage des données
			pieChartData = FXCollections.observableArrayList(new PieChart.Data("Free Space", freeSpace),
					new PieChart.Data("Full Space", fullSpace));

			//Assignation des données au graphique
			pieChart.setData(pieChartData);
		} else {
			pieChart.getData().clear();
		}
	}


	/**
	 * Permet la génération du doccument pdf comportant un résumé des différents graphiques.
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
        fileChooser.setInitialFileName(pc.getHostname()+"_"+docNameCapture);
        File file = fileChooser.showSaveDialog(serverApp.getPrimaryStage());


        if (file != null) {
        	//Permet de s'assurer que l'extension est correcte
            if (!file.getPath().endsWith(".pdf")) {
                file = new File(file.getPath() + ".pdf");
            }
          //Génération du pdf
    		PdfGenerator.generatePdfMachine(file, pc,
    				serverApp.getCurentDateView().get(), pieChart, lineChart);
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
