package monitor.view;

import java.util.TreeMap;
import java.util.Map.Entry;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.PCInfoViewWrapper;
import monitor.model.ProgramViewWrapper;

/**
 * Cette classe joue le rôle de contrôleur. Elle gère les actions suivantes:
 * 	-Affichage des pc d'une capture et des informations correspondates.
 * 	-Affichage des programmes installés sur la machine sélectionnée.
 *  -Application de filtres standard et avancés pour effectuer des recherches.
 *  -Affichage de différents graphiques.
 *
 * @author ROHRER Michaël
 */
public class ComputerOverviewController {

	//Le champ de saisie à partir du quel est apliqué le filtre principal
	@FXML
	private TextField filterField;

	//Permet l'affichage des pc dans une table
	@FXML
	private TableView<PCInfoViewWrapper> pcTable;
	@FXML
	private TableColumn<PCInfoViewWrapper, String> hostnameColumn;
	@FXML
	private TableColumn<PCInfoViewWrapper, String> ipAddressColumn;

	//Permet l'affichage des programmes dans une table
	@FXML
	private TableView<ProgramViewWrapper> programTable;
	@FXML
	private TableColumn<ProgramViewWrapper, String> nameColumn;
	@FXML
	private TableColumn<ProgramViewWrapper, String> versionColumn;

	//Les champs d'affichage des information relative au pc sélectionné
	@FXML
	private Label hostnameLabel;
	@FXML
	private Label ipAddressLabel;
	@FXML
	private Label macAddressLabel;
	@FXML
	private Label osLabel;
	@FXML
	private Label cpuConstructorLabel;
	@FXML
	private Label cpuModelLabel;
	@FXML
	private Label cpuFrequencyLabel;
	@FXML
	private Label cpuNumbCorelLabel;
	@FXML
	private Label hddTotalSizeLabel;
	@FXML
	private Label hddFreeSizeLabel;
	@FXML
	private Label ramSizeLabel;
	@FXML
	private Label installedProgrammsLabel;

	//Champs utilisés par le graphique en camembert comportant les infos sur la capacité de stockage
	@FXML
	private PieChart pieChart;
	@FXML
	private Label spaceLabel;
	@FXML
	private Label statusLabel;
	private ObservableList<PieChart.Data> pieChartData;

	//Champ utilisé par le graphique linéaire représentant l'évolution du taux de chargement du disc dur
	@FXML
	private LineChart<String, Double> lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML

	//Permet d'afficher la capture courante
	private Label captureDateLabel;

	//Référence à l'application principale
	private ServerApp serverApp;

	//Contient la capture courante
	private SimpleStringProperty currentDateView;

	//Permet de gérer le filtre principal
	private FilteredList<PCInfoViewWrapper> filteredList;
	private SortedList<PCInfoViewWrapper> sortedList;

	//Référance sur la base de donée
	private Database database;

	/**
	 * Utilisé par le xml loader pour initialiser les objets déclarés dans le fichier xml.
	 * 	-Configure le contenu des cellules du tableau d'affichage des pc.
	 * 	-Configure des listener et les méthodes à appeler lorsque l'utilisateur sélectionne un pc.
	 * 	-Définit le prédicat permettant de filtré la liste de pc.
	 */
	@FXML
	private void initialize() {

		//Configure le texte qui apparaît lorsque la table est vide
		pcTable.setPlaceholder(new Label("PC list is empty"));
		programTable.setPlaceholder(new Label("Installed programs list is empty"));

		//Configure le contenu des cellules du tableau d'affichage des pc
		hostnameColumn.setCellValueFactory(cellData -> cellData.getValue().hostnameProperty());
		ipAddressColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());

		//Efface le contenu des labels contenant les information relative au pc
		showPcDetails(null);

		//Affiche les détails du pc sélectionné
		pcTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showPcDetails(newValue));

		//Affiche le graphique en camambert correspondant au pc sélectionné
		pcTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showPieChartDetails(newValue));

		//Affiche les programmes installés sur le pc sélectionné
		pcTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showProgrammsDetails(newValue));

		//Affiche le graphique linéaire correspondant au pc sélectionné
		pcTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showLineChartDetails(newValue));

		// Message lorsque la liste des PC est vide
		pcTable.setPlaceholder(new Label("PC list is empty"));

		//Configure le prédicat à appliquer lorsque le champ text du filtre est modifié

		filterField.textProperty().addListener((observable, oldValue, newValue) -> {

			//Règle du filtre
			filteredList.setPredicate(pc -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				if (pc.getHostname().toLowerCase().contains(newValue.toLowerCase())) {
					return true;
				} else if (pc.getIpAddress().contains(newValue)) {
					return true;
				}
				return false;
			});
		});
	}

	/**
	 * Permet d'initialiser le contrôleur
	 *
	 * @param serverApp, la référence à l'applicaton principale
	 */
	public void init(ServerApp serverApp) {
		//Initialise les champs généraux
		this.serverApp = serverApp;
		database = serverApp.getDatabase();
		currentDateView = serverApp.getCurentDateView();

		//Lie le label affichant la date courante à l'objet observable correspondant
		captureDateLabel.textProperty().bind(currentDateView);

		//Enveloppe la liste de pc dans une liste filtrée et configure le prédicat de sélection des pc
		filteredList = new FilteredList<>(serverApp.getPcInfo(), p -> true);

		//Enveloppe la liste filtrée dans une liste triée
		sortedList = new SortedList<>(filteredList);

		//Lie le comparateur de la liste triée au comparateur du tableau
		sortedList.comparatorProperty().bind(pcTable.comparatorProperty());

		//Ajoute les données filtrées et triées au tableau
		pcTable.setItems(sortedList);
	}



	/**
	 * Permet de remplir les chams correspondant aux info du pc sélectionné.
	 *
	 * @param newValue, le pc sélectionné de type PCInfoViewWrapper
	 */
	private void showPcDetails(PCInfoViewWrapper newValue) {
		if (newValue != null) {
			hostnameLabel.setText(newValue.getHostname());
			ipAddressLabel.setText(newValue.getIpAddress());
			macAddressLabel.setText(newValue.getMacAddress());
			osLabel.setText(newValue.getOs());
			cpuConstructorLabel.setText(newValue.getCpu().getConstructor());
			cpuModelLabel.setText(newValue.getCpu().getModel());
			cpuFrequencyLabel.setText(Double.toString(newValue.getCpu().getFrequency()) + " GHz");
			cpuNumbCorelLabel.setText(Integer.toString(newValue.getCpu().getNbCore()) + " Cores");
			hddTotalSizeLabel.setText(Double.toString(newValue.getHdd().getTotalSize()) + " GB");
			hddFreeSizeLabel.setText(Double.toString(newValue.getHdd().getFreeSize()) + " GB");
			ramSizeLabel.setText(Long.toString(newValue.getRamSize()) + " GB");
			if (newValue.getPrograms() != null) {
				installedProgrammsLabel.setText(Integer.toString(newValue.getPrograms().size()));
			} else {
				installedProgrammsLabel.setText("0");
			}
		}
		else {
			hostnameLabel.setText("");
			ipAddressLabel.setText("");
			macAddressLabel.setText("");
			osLabel.setText("");
			cpuConstructorLabel.setText("");
			cpuModelLabel.setText("");
			cpuFrequencyLabel.setText("");
			cpuNumbCorelLabel.setText("");
			hddTotalSizeLabel.setText("");
			hddFreeSizeLabel.setText("");
			ramSizeLabel.setText("");
			installedProgrammsLabel.setText("");
		}
	}

	/**
	 * Permet d'afficher la liste de programmes installés sur le pc sélectionné
	 *
	 * @param newValue
	 */
	private void showProgrammsDetails(PCInfoViewWrapper newValue) {

		if (newValue != null) {
			//Configure les valeurs des cellules de la table
			nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
			versionColumn.setCellValueFactory(cellData -> cellData.getValue().versionProperty());
			//Ajoute la liste de programme à la table
			programTable.setItems(newValue.getPrograms());

		}
		else{
			//Efface le contenu de la table
			programTable.getItems().clear();
		}
	}

	/**
	 *
	 *
	 * @param newValue
	 */
	private void showLineChartDetails(PCInfoViewWrapper newValue) {

		if (newValue != null) {

			//Efface les éventuelles anciennes données
			spaceLabel.setText("");
			statusLabel.setText("");
			lineChart.getData().clear();

			//Récupère les infos de la base de donnée
			TreeMap<String, Double> map = database.storageLoadRate(newValue);


			Series<String, Double> series = new Series<>();
			series.setName("Storage Load Rate");

			//Ajout des données au graphique
			for (Entry<String, Double> e : map.entrySet()) {
				series.getData().add(new Data<String, Double>(e.getKey(), e.getValue()));
			}
			lineChart.getData().add(series);
		}
		else {
			//Efface les données de l'ancien graphique
			lineChart.getData().clear();
		}
	}

	/**
	 * Affiche le graphique en camambert permettant de visualiser le taux d'occupation
	 * du disc dur.
	 *
	 * @param newValue
	 */
	private void showPieChartDetails(PCInfoViewWrapper newValue) {

		if (newValue != null) {

			//Efface les éventuelles anciennes données
			pieChart.getData().clear();

			//Récupération des valeurs a placer dans le graphique
			double freeSpace = newValue.getHdd().getFreeSize();
			double fullSpace = newValue.getHdd().getTotalSize() - newValue.getHdd().getFreeSize();

			//Ajout de ces données au graphique
			pieChartData = FXCollections.observableArrayList(new PieChart.Data("Free Space", freeSpace),
					new PieChart.Data("Full Space", fullSpace));
			pieChart.setData(pieChartData);

			//Ajout d'un listener, lorsque l'utilisateur clique sur une partie du graphique le pourcentage correspondant s'affiche
			for (final PieChart.Data data : pieChart.getData()) {
				data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						//Définit la précision à un chiffre après la virgule
						String tmp = String.format("%.1f%%", 100 * data.getPieValue() / newValue.getHdd().getTotalSize());

						//Affiche les informations dans les champs correspondants
						spaceLabel.setText(data.getName() + ":");
						statusLabel.setText(tmp + " %");
					}
				});
			}
		} else {
			pieChart.getData().clear();
		}
	}

	/**
	 * Affiche la fenêtre de dialogue correspondant à l'édition de filtres avancés.
	 */
	@FXML
	public void handleAdvancedFilter() {
		FilteredList<PCInfoViewWrapper> advancedFilteredList;

		//Efface le champ d'édition du filtre standard
		filterField.clear();

		//Affichage de la fenêtre d'édition
		serverApp.showFilterEditDialog();

		//Récupère la liste filtrée
		advancedFilteredList = serverApp.getAdvancedFilters().getFilteredList();

		//Enveloppe la liste filtrée dans une liste triée
		SortedList<PCInfoViewWrapper> sortedList = new SortedList<>(advancedFilteredList);

		//Lie le comparateur de la liste triée à celui de la table
		sortedList.comparatorProperty().bind(pcTable.comparatorProperty());

		//défini la liste à afficher dans la table
		pcTable.setItems(sortedList);
	}

	/**
	 * Efface le filtre avancé appliqué à la liste de pc.
	 */
	@FXML
	public void handleClearFilter() {
		//Efface les filtres
		serverApp.getAdvancedFilters().clearFilter();

		//Réenveloppe la liste filtrée dans une liste triée
		SortedList<PCInfoViewWrapper> sortedList = new SortedList<>(filteredList);

		//Lie le comparateur de la liste triée à celui de la table
		sortedList.comparatorProperty().bind(pcTable.comparatorProperty());

		//Défini la liste à afficher dans la table
		pcTable.setItems(sortedList);
	}
}
