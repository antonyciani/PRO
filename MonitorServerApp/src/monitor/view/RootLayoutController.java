package monitor.view;

import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import communication.SystemInfoRetrieverProtocol;
import communication.SystemInfoRetrieverServer;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import monitor.ServerApp;
import monitor.database.Database;
import utils.PlatformExecutor;

/**
 * @author ROHRER Michaël
 * @author CIANI Antony
 *
 */
public class RootLayoutController {

	private Alert refreshingAlert;
	private Database database;
	private ServerApp serverApp;
	private SimpleStringProperty currentDateView;

	/**
	 * @param serverApp
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

	}

	/**
	 * 
	 */
	@FXML
	public void handleStorageStatistic() {
		serverApp.showAverageStorageLoadDialog();
	}

	/**
	 * 
	 */
	@FXML
	public void handleParkStatistics() {
		serverApp.showGeneralStatistics();
	}

	/**
	 * 
	 */
	@FXML
	public void handleExit() {
		System.exit(0);
	}

	/**
	 * 
	 */
	@FXML
	public void handleProgramsStatistics() {
		serverApp.showProgramsStatistics();
	}

	/**
	 * 
	 */
	@FXML
	public void handleSelectCapture() {
		String date = serverApp.showCaptureSelectionDialog("Select Capture");
		if (!date.equals("")) {
			currentDateView.setValue(date);
			serverApp.getPcInfo().clear();
			serverApp.getPcInfo().addAll(database.loadPCInfo(date));
		}
	}

	/**
	 * 
	 */
	@FXML
	public void handleDeleteCapture() {
		String date = serverApp.showCaptureSelectionDialog("Delete Capture");
		if (!date.equals("")) {
			if (!currentDateView.get().equals(date)) {
				serverApp.getDatabase().deleteCapture(date);
			} else {
				currentDateView.setValue("");
				serverApp.getPcInfo().clear();
			}
		}
	}

	/**
	 * 
	 */
	@FXML
	public void handleRefreshCapture() {
		showRetrievingAlertDialog();
		CompletableFuture.supplyAsync(() -> {
			SystemInfoRetrieverServer sirs = null;
			try {
				sirs = new SystemInfoRetrieverServer(SystemInfoRetrieverProtocol.UDP_PORT,
						SystemInfoRetrieverProtocol.TCP_PORT);
				sirs.retrieveInfosFromClients();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			return sirs.getPcInfos();
		}).whenCompleteAsync((list, ex) -> {
			System.out.println("COUCOU");
			System.out.println("COUCOU2");
			database.storePCs(list);
			System.out.println("COUCOU3");
			showCompletedAlertDialog();
			serverApp.getPcInfo().clear();
			serverApp.getPcInfo().setAll(database.loadPCInfo(database.getLastCapture()));
			currentDateView.setValue(database.getLastCapture());
		} , PlatformExecutor.instance);
	}

	/**
	 * 
	 */
	private void showRetrievingAlertDialog() {
		refreshingAlert = new Alert(AlertType.INFORMATION);
		refreshingAlert.setTitle("New Capture");
		refreshingAlert.setHeaderText("Retrieving PCs Information...");
		refreshingAlert.setContentText("You will be notified when it's done!");
		// refreshingAlert.getButtonTypes().clear();
		refreshingAlert.show();

	}

	/**
	 * 
	 */
	private void showCompletedAlertDialog() {
		refreshingAlert.close();
		refreshingAlert = new Alert(AlertType.INFORMATION);
		refreshingAlert.setTitle("New Capure Available");
		refreshingAlert.setHeaderText("Your capture is completed !");
		refreshingAlert.setContentText("The new capture is now available in the capture selection window !");
		refreshingAlert.show();
	}
}
