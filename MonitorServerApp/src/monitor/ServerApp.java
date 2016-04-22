package monitor;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import monitor.model.CPUInfo;
import monitor.model.HDDInfo;
import monitor.model.PCInfo;
import monitor.model.Program;
import monitor.view.ComputerOverviewController;
import monitor.view.FilterEditDialogController;
import monitor.view.RootLayoutController;
import utils.SystemInfoRecuperator;

public class ServerApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<PCInfo> pcData = FXCollections.observableArrayList();

	public ServerApp(){
		String hostname = "MichaelPc";
		String ipAddress = "192.168.1.10";
		String macAddress = "5E:FF:56:A2:AF:15";
		String os = "Windows 10";
		CPUInfo cpu = new CPUInfo("Intell", "i7", 2.7, 4);
		HDDInfo hdd = new HDDInfo(500.8, 209.4);
		int ramSize = 16;
		ObservableList<Program> programs = FXCollections.observableArrayList();
		programs.add(new Program("ls", "1.2", "10.2.1990"));
		programs.add(new Program("cat", "2.3", "12.03.2000"));

		pcData.add(new PCInfo(hostname, ipAddress, macAddress, os, cpu, hdd, ramSize, programs));

		hostname = "LuciePc";
		ipAddress = "192.168.1.11";
		macAddress = "5E:FF:56:A2:AF:30";
		os = "Windows 7";
		cpu = new CPUInfo("Intel", "i5", 2.3, 4);
		hdd = new HDDInfo(500.8, 400.4);
		ramSize = 8;

		pcData.add(new PCInfo(hostname, ipAddress, macAddress, os, cpu, hdd, ramSize, programs));
		pcData.add(SystemInfoRecuperator.retrievePCInfo());
	}

	public ObservableList<PCInfo> getPcInfo() {
        return pcData;
    }

	public Stage getPrimaryStage(){
		return primaryStage;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MonitorAppServer");

		initRootLayout();
		showComputerOverview();

	}

	public void initRootLayout(){
		try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setServerApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void showComputerOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerApp.class.getResource("view/ComputerOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Give the controller access to the main app.
            ComputerOverviewController controller = loader.getController();
            controller.setServerApp(this);

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void showFilterEditDialog(){
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/FilterEditDialog.fxml"));
			AnchorPane filterEditDialog = (AnchorPane) loader.load();

			Stage filterStage = new Stage();
			filterStage.setTitle("Filter");
			filterStage.initModality(Modality.WINDOW_MODAL);
			filterStage.initOwner(primaryStage);
			Scene scene = new Scene(filterEditDialog);
			filterStage.setScene(scene);

			FilterEditDialogController controller = loader.getController();
			controller.setDialogStage(filterStage);

			controller.setPcList(pcData);

            // Show the dialog and wait until the user closes it
            filterStage.showAndWait();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
