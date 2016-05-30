package monitor.view;

import java.util.TreeMap;
import java.io.File;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.PCInfoViewWrapper;
import utils.PdfGenerator;

public class PcSummaryWindowController {


	@FXML
	private PieChart pieChart;
	private ObservableList<PieChart.Data> pieChartData;
	@FXML
	private LineChart<String, Double> lineChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;

	private Stage dialogStage;
	private ServerApp serverApp;
	private Database database;
	private PCInfoViewWrapper pc;

	@FXML
	public void initialize(){

	}

	public void init(ServerApp serverApp, Stage dialogStage, PCInfoViewWrapper pc){
		this.serverApp = serverApp;
		this.dialogStage = dialogStage;
		this.database = serverApp.getDatabase();
		this.pc = pc;
		showPieChartDetails(pc);
		showLineChartDetails(pc);
	}


	/**
	 * @param newValue
	 */
	private void showLineChartDetails(PCInfoViewWrapper newValue) {

		if (newValue != null) {
			lineChart.getData().clear();

			// Récupère les infos de la base de donnée
			TreeMap<String, Double> map = database.storageLoadRate(newValue, serverApp.getCurentDateView().get());

			// Ajout des données au graphique
			Series<String, Double> series = new Series<>();
			series.setName("Storage Load Rate");
			for (Entry<String, Double> e : map.entrySet()) {
				series.getData().add(new Data<String, Double>(e.getKey(), e.getValue()));
			}

			lineChart.getData().add(series);
		} else {
			lineChart.getData().clear();
		}
	}


	/**
	 * @param newValue
	 */
	private void showPieChartDetails(PCInfoViewWrapper newValue) {
		if (newValue != null) {
			pieChart.getData().clear();
			//A supprimer une fois probl�me regl�
			pieChart.setAnimated(false);
			double freeSpace = newValue.getHdd().getFreeSize();
			double fullSpace = newValue.getHdd().getTotalSize() - newValue.getHdd().getFreeSize();

			pieChartData = FXCollections.observableArrayList(new PieChart.Data("Free Space", freeSpace),
					new PieChart.Data("Full Space", fullSpace));

			pieChart.setData(pieChartData);

			//TestPDF.generatePdfMachine(newValue, currentDateView.getValue(), pieChart);

		} else {
			pieChart.getData().clear();
		}
	}


	@FXML
	public void handleExport(){

		FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        String docNameCapture = serverApp.getCurentDateView().get().replace(' ', '_').replace(':', '_');
        fileChooser.setInitialFileName(pc.getHostname()+"_"+docNameCapture);
        File file = fileChooser.showSaveDialog(serverApp.getPrimaryStage());


        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".pdf")) {
                file = new File(file.getPath() + ".pdf");
            }
    		PdfGenerator.generatePdfMachine(file, pc, serverApp.getCurentDateView().get(), pieChart, lineChart);
        }

		//ouvrir fenetre pour choisir le nom et l'emplacement



		dialogStage.close();
	}


	@FXML
	public void handleCancel(){
		dialogStage.close();
	}
}
