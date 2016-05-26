package monitor.view;

import javafx.fxml.FXML;
import monitor.ServerApp;

public class RootLayoutController {

	private ServerApp serverApp;

	public void init(ServerApp serverApp){
		this.serverApp = serverApp;
	}

	@FXML
	public void handleArchive(){

	}

	@FXML
	public void handleAverageLoadStorage(){
		serverApp.showAverageStorageLoadDialog();
	}

	@FXML
	public void handleGeneralStatistics(){
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
}
