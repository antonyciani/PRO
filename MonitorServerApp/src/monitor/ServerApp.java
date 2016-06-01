package monitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import communication.SystemInfoRetrieverProtocol;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import monitor.database.Database;
import monitor.model.*;
import monitor.view.*;
import utils.AdvancedFilters;

/**
 * Cette classe est la classe de base de l'application serverApp, c'est elle qui initialise et
 * lance l'interface graphique. Elle implémente les différentes méthodes permettant d'afficher
 * les fenêtres de l'application. Elle comporte également différents objets qui seront
 * utilisés par les controleurs correspondant pour réaliser leur tâches.
 *
 * @author ROHRER Michaël
 * @author CIANI Antony
 */
public class ServerApp extends Application {

	//Permet la gestion de la fenètre principale
	private Stage primaryStage;
	private BorderPane rootLayout;

	//Permet la gestion de la base de donnée
	private Database database;

	//Permet la gestion des filtres avancés
	private AdvancedFilters filters;

	//Contient la date de la capture courante
	private SimpleStringProperty currentDateView;

	//Contient les informations relatives aux différents PC du parc informatique
	private ObservableList<PCInfoViewWrapper> pcData = FXCollections.observableArrayList();

	private static final String confFilename = "app.properties";
	private String dbAddress;
	private String dbUsername;
	private String dbPassword;
	private int udpPort;
	private int tcpPort;
	private String multicastGroupAddress;

	/**
	 * Constructeur ServerApp:
	 * Instanciation des différents objets nécessaires à l'application et effectur la connection
	 * à la base de donnée.
	 *
	 */
	public ServerApp() {

		Properties prop = new Properties();

        //Récupération des informations de configuration à partir du fichier de configuration
        try {
        	//Chargement du fichier de configuration
            BufferedReader br = new BufferedReader(new FileReader(this.confFilename));
            prop.load(br);

            //Récupération des informations de configuration
            dbAddress = prop.getProperty("dbaddress");
            dbUsername = prop.getProperty("dbusername");
            dbPassword = prop.getProperty("dbpassword");

            udpPort = Integer.parseInt(prop.getProperty("udpport"));
            tcpPort = Integer.parseInt(prop.getProperty("tcpport"));
            multicastGroupAddress = prop.getProperty("multicastaddress");

        } catch (IOException ex) {
            System.out.println("app.properties couldn't be loaded, please check it is present in the same folder as the application");
            System.exit(1);
        }

        //Instanciation des différents objets utilisés par l'application
		filters = new AdvancedFilters(pcData);
		currentDateView = new SimpleStringProperty("");
		database = new Database(dbAddress, dbUsername, dbPassword);
		database.connect();
	}

	/**
	 * @return Une référence sur une instance de la classe AdvancedFilter
	 */
	public AdvancedFilters getAdvancedFilters() {
		return filters;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("SystemInfoVisualizer");

		//Initialisation de la scène principale
		initRootLayout();

		//Initialisation du contenu de la scène principale
		showComputerOverview();
	}

	/**
	 * Initialise la fenêtre principale.
	 *
	 */
	public void initRootLayout() {
		try {
			//Charge les données xml et construit la fenêtre correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			RootLayoutController controller = loader.getController();
			controller.init(this);

			//Affiche la scène comportant la feneêtre créée
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialise le contenu de la fenêtre principale en plaçant la vue correspondante
	 * au centre de cette dernière.
	 *
	 */
	public void showComputerOverview() {
		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/ComputerOverview.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			ComputerOverviewController controller = loader.getController();
			controller.init(this);

			// Place la vue au centre de la fenêtre principale.
			rootLayout.setCenter(page);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Affiche la fenêtre comportant les statistiques générales du parc informatique.
	 *
	 */
	public void showGeneralStatistics() {

		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/GeneralStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Construit la fenêtre popup qui présentera les différentes statistiques
			Stage dialogStage = new Stage();
			dialogStage.setTitle("General Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			GeneralStatisticsController controller = loader.getController();
			controller.init(this);

			//Affiche la fenêtre
			dialogStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Affiche la fenêtre permettant la selection d'une date de capture.
	 *
	 * @param title, le titre a afficher par la fenêtre de sélection
	 * @return la date de la capture sélectionnée sous forme d'un "String"
	 */
	public String showCaptureSelectionDialog(String title) {

		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/CaptureSelectionDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Construit la fenêtre popup qui affichera les différentes dates pouvant être sélectionnées
			Stage dialogStage = new Stage();
			dialogStage.setTitle(title);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			CaptureSelectionDialogController controller = loader.getController();
			controller.init(this, dialogStage);

			//Affiche la fenêtre de dialogue
			dialogStage.showAndWait();

			//Retourne la date sélectionnée
			return controller.getDate();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Affiche la fenêtre permettant de visualiser les statistiques correspondant aux taux d'utilisation
	 * global de l'ensemble des disques dures du parc informatique.
	 *
	 */
	public void showAverageStorageLoadDialog() {
		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/AverageStorageLoadStatisticDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Construit la fenêtre popup qui affichera les statistiques correspondantes
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Average Storage Load");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			AverageStorageLoadStatisticDialogController controller = loader.getController();
			controller.init(this);

			//Affiche la fenêtre
			dialogStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 *	Affiche la fenêtre permettant de consulter les différentes statistiques réalisées sur l'ensemble des
	 * 	programmes installés dans le parc informatique.
	 */
	public void showProgramsStatistics() {

		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/ProgramStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Construit la fenêtre popup qui affichera les statistiques correspondantes
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Program Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			ProgramStatisticsController controller = loader.getController();
			controller.init(this);

			//Affiche la fenêtre
			dialogStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Affiche la fenêtre de dialogue permettant d'éditer des filtres avancés
	 *
	 */
	public void showFilterEditDialog() {
		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/FilterEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Construit la fenêtre popup qui affichera la fenêtre d'édition de filtres
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Filter");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			FilterEditDialogController controller = loader.getController();
			controller.init(this, dialogStage);

			//Affiche la fenêtre de dialogue et attend que l'utilisateur la ferme
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Affiche une fenêtre récapitulative des différentes statistiques pour le PC séléctionné
	 *
	 * @param Le PC séléctionné
	 */
	public void showPcSummaryWindow(PCInfoViewWrapper pc) {
		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/PcSummaryWindow.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Construit la fenêtre popup dans la quelle sera affiché les différents graphiques
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Create PDF");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			PcSummaryWindowController controller = loader.getController();
			controller.init(this, dialogStage, pc);

			//Affiche la fenêtre de dialogue et attend que l'utilisateur la ferme
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Affiche une fenêtre récapitulative des différentes statistiques pour l'ensemble des PC
	 * d'une capture séléctionnée
	 *
	 * @param la date de la capture courante
	 */
	public void showCaptureSummaryWindow(SimpleStringProperty currentDateView) {
		try {
			//Charge les données xml et construit la vue correspondante
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/CaptureSummaryWindow.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			//Construit la fenêtre popup dans la quelle sera affiché les différents graphiques
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Create PDF");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//Donne à l'application principale l'accèes au controleur de la fenêtre correspondante
			CaptureSummaryWindowController controller = loader.getController();
			controller.init(this, dialogStage);

			//Affiche la fenêtre de dialogue et attend que l'utilisateur la ferme
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet de récupérer la liste de PC.
	 *
	 * @return Une référence sur la liste de PC contenant les informations de la capture courante.
	 */
	public ObservableList<PCInfoViewWrapper> getPcInfo() {
		return pcData;
	}

	/**
	 * Permet de récupérer la scène principale.
	 *
	 * @return Une référence sur la scène principale.
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Permet de récupérer la base de donnée.
	 *
	 * @return Une référence sur la base de donnée.
	 */
	public Database getDatabase() {
		return database;
	}

	/**
	 * Permet de récupérer le numéro de port UDP utilisé par l'application
	 *
	 * @return le numéro de port UDP
	 */
	public int getUdpPort() {
		return udpPort;
	}

	/**
	 * Permet de récupérer le numéro de port TCP utilisé par l'application
	 *
	 * @return le numéro de port TCP
	 */
	public int getTcpPort() {
		return tcpPort;
	}

	/**
	 * Permet de récupérer l'adresse multicaste de groupe
	 *
	 * @return l'adresse multicaste de groupe
	 */
	public String getMulticastGroupAddress() {
		return multicastGroupAddress;
	}

	/**
	 * Permet de récupérer la date de la capture courante
	 *
	 * @return La date de la capture courante sous forme d'un SimpleStringProperty (valeur observable)
	 */
	public SimpleStringProperty getCurentDateView() {
		return currentDateView;
	}

	/**
	 * Lance l'application graphique
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}



}
