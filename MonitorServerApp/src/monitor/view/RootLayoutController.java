package monitor.view;

import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import communication.SystemInfoRetrieverServer;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import monitor.ServerApp;
import monitor.database.Database;
import utils.PlatformExecutor;

/**
 * Cette classe joue le rôle de contrôleur. Elle contrôle les actions qu'il est possible d'effectuer
 * sur la barre de tâche. Entre autre, elle gère les menus suivantes:
 *  -Menu d'affichage des statistiques
 *  -Menu d'action sur les captures
 *  -Menu système
 *  -Menu about
 *
 * Cette classe est fortement liée à la vue principale puisque cette dernière est ratachée au centre
 * de l'objet que la classe contrôle.
 *
 * @author ROHRER Michaël
 * @author CIANI Antony
 */
public class RootLayoutController {

	//Permet la gestion des messages d'alertes
	private Alert refreshingAlert;
	private Database database;
	private ServerApp serverApp;

	//Référence à la capture courante
	private SimpleStringProperty currentDateView;

	/**
	 * Permet d'initialiser le contrôleur
	 *
	 * @param serverApp en référence à l'application principale, permet d'en utilisé ses méthodes
	 */
	public void init(ServerApp serverApp) {
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		this.currentDateView = serverApp.getCurentDateView();
	}

	/**
	 *
	 */
	@FXML
	public void handleAbout() {
		Alert about = new Alert(AlertType.INFORMATION);
		about.setTitle("Information");
		about.setHeaderText("SystemInfoVisualizer");
		about.setContentText("This program allows you to perform the monitoring of your Windows machines in your IT infrastructure.\n"
				+ "Authors:\n\n\tSTEINER Lucie,\n\tZHARKOVA Anastasia,\n\tCIANI Antony,\n\tSELIMI Dardan,\n\tROHRER Michaël");
		about.show();
	}

	/**
	 * Affiche la fenêtre permettant de visualiser les satistiques générales relatives
	 * à la capacité de stockage du parc informatique.
	 *
	 */
	@FXML
	public void handleStorageStatistic() {
		serverApp.showAverageStorageLoadDialog();
	}

	/**
	 * Affiche la fenêtre permettant de visualiser les satistiques générales relatives
	 * au parc informatique.
	 *
	 */
	@FXML
	public void handleParkStatistics() {
		serverApp.showGeneralStatistics();
	}

	/**
	 * Permet de quitter l'application
	 *
	 */
	@FXML
	public void handleExit() {
		System.exit(0);
	}

	/**
	 * Affiche la fenêtre permettant de visualiser les satistiques générales relatives aux
	 * programmes du parc informatique.
	 *
	 */
	@FXML
	public void handleProgramsStatistics() {
		serverApp.showProgramsStatistics();
	}

	/**
	 * Affiche la fenêtre permettant la sélection d'une capture et affiche la capture
	 * sélectionnée sur la scène principale.
	 *
	 */
	@FXML
	public void handleSelectCapture() {

		//Affiche la fenêtre de sélection de capture
		String date = serverApp.showCaptureSelectionDialog("Select Capture");

		//Si la date sélectionnée n'est pas vide, affiche la capture
		if (!date.equals("")) {
			currentDateView.setValue(date);
			serverApp.getPcInfo().clear();
			serverApp.getPcInfo().addAll(database.loadPCInfo(date));
		}
	}

	/**
	 * Affiche la fenêtre permettant la suppression d'une capture et supprime de
	 * la base de donnée la capture sélectionnée par l'utilisateur. Si la capture
	 * sélectionnée est la capture actuellement affichée, celle-ci disparaît de la
	 * vue principale.
	 *
	 */
	@FXML
	public void handleDeleteCapture() {

		//Affiche la fenêtre de sélection de capture
		String date = serverApp.showCaptureSelectionDialog("Delete Capture");

		//Si la date sélectionnée n'est pas vide
		if (!date.equals("")) {

			//Supprime la capture de la base de donnée
			serverApp.getDatabase().deleteCapture(date);

			//Si la capture supprimée est la capture actuellement affichée elle disparaît de la scène principale
			if (currentDateView.get().equals(date)) {
				currentDateView.setValue("");
				serverApp.getPcInfo().clear();
			}
		}
	}

	/**
	 * Permet de récupérer les information des différents pc du par informatique. Cette
	 * méthode est appelée lorsque l'utilisateur appuie sur le bouton "Refresh".
	 *
	 */
	@FXML
	public void handleRefreshCapture() {
		//Affiche la fenêtre d'allerte indiquant que la capture est en cours
		showRetrievingAlertDialog();

		//Permet d'éviter que l'application graphique freeze en encapsulant la récolte
		//d'information dans un thread créé pour l'ocasion.
		CompletableFuture.supplyAsync(() -> {

			SystemInfoRetrieverServer sirs = null;
			try {
				sirs = new SystemInfoRetrieverServer(serverApp.getUdpPort(),
						serverApp.getTcpPort(), serverApp.getMulticastGroupAddress());
				//Récolte d'information
				sirs.retrieveInfosFromClients();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			//Une liste comportant les différents PC capturés est retournée
			return sirs.getPcInfos();

			//Lorsque la tâche est terminèe
		}).whenCompleteAsync((list, ex) -> {
			if(!list.isEmpty()){

				//Enregistrement de la capture dans la base de donnée
				database.storePCs(list);

				//Affichage de la fenêtre d'alerte indiquant la fin de la capture
				showCompletedAlertDialog();

				//Chargement de la nouvelle capture pour affichage dans la vue principale
				serverApp.getPcInfo().clear();
				serverApp.getPcInfo().setAll(database.loadPCInfo(database.getLastCapture()));

				//Changement de la date de capture courante
				currentDateView.setValue(database.getLastCapture());
			}
			else{
				//Affichage de la fenêtre d'alerte indiquant qu'aucun PC n'a été trouvé
				showNoPCAlertDialog();
			}
		} , PlatformExecutor.instance);
	}

	/**
	 * Construit et affiche une fenêtre d'alerte indiquant qu'une capture est en cours.
	 *
	 */
	private void showRetrievingAlertDialog() {
		refreshingAlert = new Alert(AlertType.INFORMATION);
		refreshingAlert.setTitle("New Capture");
		refreshingAlert.setHeaderText("Retrieving PCs Information...");
		refreshingAlert.setContentText("You will be notified when it's done!");
		refreshingAlert.show();

	}

	/**
	 * Construit et affiche une fenêtre d'alerte indiquant qu'aucun PC n'a été détecté.
	 *
	 */
	private void showCompletedAlertDialog() {
		refreshingAlert.close();
		refreshingAlert = new Alert(AlertType.INFORMATION);
		refreshingAlert.setTitle("Capture completed");
		refreshingAlert.setHeaderText("Your capture is finished !");
		refreshingAlert.setContentText("The new capture is now available in the capture selection window !");
		refreshingAlert.show();
	}

	/**
	 * Construit et affiche une fenêtre d'alerte indiquant qu'aucun PC n'a été détecté.
	 *
	 */
	private void showNoPCAlertDialog() {
		refreshingAlert.close();
		refreshingAlert = new Alert(AlertType.WARNING);
		refreshingAlert.setTitle("Capture completed");
		refreshingAlert.setHeaderText("Your capture is finished !");
		refreshingAlert.setContentText("Unfortunately no PC's have been detected!");
		refreshingAlert.show();
	}

	/**
	 * Permet d'exporter un résumé des statistiques relatives au parc informatique au
	 * moment de la capture courante.
	 *
	 */
	@FXML
	private void handleExportStatistics(){
		if(!serverApp.getCurentDateView().get().equals("")){
			//Affiche la fenêtre permettant d'exporter les statistiques
			serverApp.showCaptureSummaryWindow(serverApp.getCurentDateView());
		}
	}
}
