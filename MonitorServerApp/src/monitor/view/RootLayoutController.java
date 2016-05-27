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

public class RootLayoutController {

	private Alert alert;
	private Database database;
	private ServerApp serverApp;
	private SimpleStringProperty currentDateView;

	public void init(ServerApp serverApp){
		this.serverApp = serverApp;
		this.database = serverApp.getDatabase();
		this.currentDateView = serverApp.getCurentDateView();
	}

	@FXML
	public void handleAbout(){

	}

	@FXML
	public void handleStorageStatistic(){
		serverApp.showAverageStorageLoadDialog();
	}

	@FXML
	public void handleParkStatistics(){
		serverApp.showGeneralStatistics();
	}

	@FXML
	public void handleExit(){
		System.exit(0);
	}

	@FXML
	public void handleProgramsStatistics(){
		serverApp.showProgramsStatistics();
	}

	@FXML
    public void handleSelectCapture(){
    	String date = serverApp.showCaptureSelectionDialog("Select Capture");
    	if(!date.equals("")){
    		currentDateView.setValue(date);
        	serverApp.getPcInfo().clear();
    		serverApp.getPcInfo().addAll(database.loadPCInfo(date));
    	}
    }

	@FXML
    public void handleDeleteCapture(){
    	String date = serverApp.showCaptureSelectionDialog("Delete Capture");
    	if(!date.equals("")){
    		if(!currentDateView.get().equals(date)){
    			serverApp.getDatabase().deleteCapture(date);
    		}
    		else{
    			currentDateView.setValue("");
    			serverApp.getPcInfo().clear();
    		}
    	}
    }

	@FXML
    public void handleRefreshCapture(){
    	showRetrivingAlertDialog();
    	CompletableFuture.supplyAsync(() -> {
    		SystemInfoRetrieverServer sirs = null;
    		try {
    			sirs = new SystemInfoRetrieverServer(SystemInfoRetrieverProtocol.UDP_PORT, SystemInfoRetrieverProtocol.TCP_PORT);
    			sirs.retrieveInfosFromClients();
    		} catch (SocketException e) {
    			e.printStackTrace();
    		}
			return sirs.getPcInfos();
		}).whenCompleteAsync((list, ex) -> {
			alert.close();
			database.storePCs(list);
			showCompletedAlertDialog();
			serverApp.getPcInfo().clear();
			serverApp.getPcInfo().setAll(database.loadPCInfo(database.getLastCapture()));
			currentDateView.setValue(database.getLastCapture());
		}, PlatformExecutor.instance);
    }

	private void showRetrivingAlertDialog(){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Alert");
    	alert.setHeaderText("Retriving PCs Information...");
    	alert.show();
    }

	private void showCompletedAlertDialog(){
		Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Alert");
    	alert.setHeaderText("Retriving PCs Information Completed !");
    	alert.setContentText("The new capture is now available on the main window !");
    	alert.show();
	}
}
