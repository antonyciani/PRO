package monitor.view;

import java.util.HashMap;
import java.util.Map.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

	@FXML
	private Label nbCoresLabel;
	@FXML
	private Label nbCoresPercentLabel;
	@FXML
	private Label modelLabel;
	@FXML
	private Label modelPercentLabel;
	@FXML
	private Label hddSizeLabel;
	@FXML
	private Label hddSizePercentLabel;
	@FXML
	private Label ramLabel;
	@FXML
	private Label ramPercentLabel;


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
	 * Permet d'initialiser le controleur et génère les graphiques.
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
	 * Génère un graphique en camambert permettant de visualiser les différents types de processeurs du
	 * parc informatique en fonction de leur nombre de coeur.
	 *
	 */
	public void showNumberOfCoreStatistics() {
		int tot = 0;
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<Integer, Integer> nbCore = database.nbPcByNbCores(serverApp.getCurentDateView().get());

		//Formatage des données
		for (Entry<Integer, Integer> e : nbCore.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " Cores", e.getValue()));
			tot += e.getValue();
		}

		//Assignation des données au graphique
		nbCoreChart.setData(pieChartData);

		//Ajout d'un listener permettant de connaître le pourcentage d'une partie du graphique lorsque l'utilisateur
		//clique dessus
		final int total = tot;
		for (final PieChart.Data data : nbCoreChart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					String tmp = String.format("%.1f%%",
							100 * data.getPieValue() / total);
					nbCoresLabel.setText(data.getName() + ":");
					nbCoresPercentLabel.setText(tmp);
				}
			});
		}
	}

	/**
	 * Génère un graphique en camambert permettant de visualiser les différents modèles de processeurs du
	 * parc informatique.
	 *
	 */
	public void showModelStatistics() {
		int tot = 0;
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<String, Integer> constructor = database.nbPcByModel(serverApp.getCurentDateView().get());
		//Formatage des données
		for (Entry<String, Integer> e : constructor.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
			tot += e.getValue();
		}
		//Assignation des données au graphique
		modelChart.setData(pieChartData);

		//Ajout d'un listener permettant de connaître le pourcentage d'une partie du graphique lorsque l'utilisateur
		//clique dessus
		final int total = tot;
		for (final PieChart.Data data : modelChart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					String tmp = String.format("%.1f%%",
							100 * data.getPieValue() / total);
					modelLabel.setText(data.getName() + ":");
					modelPercentLabel.setText(tmp);
				}
			});
		}
	}

	/**
	 * Génère un graphique en camambert permettant de visualiser les différents tailles de disque dur du
	 * parc informatique.
	 *
	 */
	public void showHardDriveStatistics() {
		int tot = 0;
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<Float, Integer> hdd = database.nbPcByHddSize(serverApp.getCurentDateView().get());

		//Formatage des données
		for (Entry<Float, Integer> e : hdd.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
			tot += e.getValue();
		}
		//Assignation des données au graphique
		hddSizeChart.setData(pieChartData);

		//Ajout d'un listener permettant de connaître le pourcentage d'une partie du graphique lorsque l'utilisateur
		//clique dessus
		final int total = tot;
		for (final PieChart.Data data : hddSizeChart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					String tmp = String.format("%.1f%%",
							100 * data.getPieValue() / total);
					hddSizeLabel.setText(data.getName() + ":");
					hddSizePercentLabel.setText(tmp);
				}
			});
		}
	}

	/**
	 * Génère un graphique en camambert permettant de visualiser les différents tailles de mémoire vives du
	 * parc informatique.
	 *
	 */
	public void showRamStatistics() {
		int tot = 0;
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		//Récupération des données dans la base de donnée
		HashMap<Integer, Integer> ram = database.nbPcByRamSize(serverApp.getCurentDateView().get());

		//Formatage des données
		for (Entry<Integer, Integer> e : ram.entrySet()) {
			pieChartData.add(new PieChart.Data(e.getKey() + " GB", e.getValue()));
			tot += e.getValue();
		}
		//Assignation des données au graphique
		ramSizeChart.setData(pieChartData);

		//Ajout d'un listener permettant de connaître le pourcentage d'une partie du graphique lorsque l'utilisateur
		//clique dessus
		final int total = tot;
		for (final PieChart.Data data : ramSizeChart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					String tmp = String.format("%.1f%%",
							100 * data.getPieValue() / total);
					ramLabel.setText(data.getName() + ":");
					ramPercentLabel.setText(tmp);
				}
			});
		}
	}
}
