package monitor;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

import communication.SystemInfoRetrieverProtocol;
import communication.SystemInfoRetrieverServer;
import javafx.application.Application;
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


public class ServerApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;


	private Database database;
	private AdvancedFilters filters;

	private String curentPcView;
	private ObservableList<PCInfoViewWrapper> pcData = FXCollections.observableArrayList();

	public ServerApp(){
		filters = new AdvancedFilters(pcData);
		database = new Database("jdbc:mysql://localhost:3306/inventory", "root", "1234");
		database.connect();
	}

	public ObservableList<PCInfoViewWrapper> getPcInfo() {
        return pcData;
    }

	public Stage getPrimaryStage(){
		return primaryStage;
	}

	public Database getDatabase(){
		return database;
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

	public void showGeneralStatistics(){

		try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerApp.class.getResource("view/GeneralStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("General Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the persons into the controller.
            GeneralStatisticsController controller = loader.getController();
            controller.setServerApp(this);
            controller.setDatabase(database);
            controller.showStatistics();

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public String showCaptureSelectionDialog(){

		try {
			//Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/CaptureSelectionDialog.fxml"));
			AnchorPane captureSelectionDialog = (AnchorPane) loader.load();

			Stage captureStage = new Stage();
			captureStage.setTitle("Capture Selection");
			captureStage.initModality(Modality.WINDOW_MODAL);
			captureStage.initOwner(primaryStage);
			Scene scene = new Scene(captureSelectionDialog);
			captureStage.setScene(scene);


			CaptureSelectionDialogController controller = loader.getController();
			controller.setServerApp(this);
			controller.setDatabase(database);
			controller.setDialogStage(captureStage);

			captureStage.showAndWait();
			return controller.getDate();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public void showAverageStorageLoadDialog(){
		try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerApp.class.getResource("view/AverageStorageLoadStatisticDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Average Storage Load");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the persons into the controller.
            AverageStorageLoadStatisticDialogController controller = loader.getController();
            controller.setServerApp(this);
            controller.setDatabase(database);
            controller.showStatistics();

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void setCurentPcView(String date){
		curentPcView = date;
		pcData.clear();
		pcData.addAll(database.loadPCInfo(curentPcView));

	}

	public String getCurentPcView(){
		return curentPcView;
	}

	public void showProgramsStatistics(){

		try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ServerApp.class.getResource("view/ProgramStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Program Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the persons into the controller.
            ProgramStatisticsController controller = loader.getController();
            controller.setServerApp(this);
            controller.setDatabase(database);
            controller.showStatistics();

            dialogStage.show();

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

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Filter");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(filterEditDialog);
			dialogStage.setScene(scene);

			FilterEditDialogController controller = loader.getController();
			controller.setServerApp(this);
			controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public AdvancedFilters getAdvancedFilters(){
		return filters;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
