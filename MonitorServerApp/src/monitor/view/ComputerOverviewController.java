package monitor.view;

import java.io.IOException;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;

import com.mysql.jdbc.PingTarget;

import communication.SystemInfoRetrieverProtocol;
import communication.SystemInfoRetrieverServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.PCInfo;
import monitor.model.PCInfoViewWrapper;
import monitor.model.Program;
import monitor.model.ProgramViewWrapper;
import utils.AdvancedFilters;
import utils.PlatformExecutor;

public class ComputerOverviewController {

	@FXML
    private TextField filterField;

	@FXML
	private TableView<PCInfoViewWrapper>  pcTable;
	@FXML
	private TableColumn<PCInfoViewWrapper, String> hostnameColumn;
	@FXML
	private TableColumn<PCInfoViewWrapper, String> ipAddressColumn;


	@FXML
	private TableView<ProgramViewWrapper> programTable;
	@FXML
	private TableColumn<ProgramViewWrapper, String> nameColumn;
	@FXML
	private TableColumn<ProgramViewWrapper, String> versionColumn;
	@FXML
	private TableColumn<ProgramViewWrapper, String> lastUpdateColumn;


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


    @FXML
    private PieChart chart;
    @FXML
    private Label status;
    private ObservableList<PieChart.Data> pieChartData;

    @FXML
    private LineChart<LocalDateTime, Double> lineChart;
    @FXML
	private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;


    private AdvancedFilters filter;
    private ServerApp serverApp;
    private FilteredList<PCInfoViewWrapper> filteredList;
    private FilteredList<PCInfoViewWrapper> filteredListCopy;

    private Database db;

    public void setServerApp(ServerApp serverApp){
		this.serverApp = serverApp;
		db = this.serverApp.getDatabase();
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		filteredList = new FilteredList<>(serverApp.getPcInfo(), p -> true);

		filter = new AdvancedFilters(serverApp.getPcInfo());

		// 3. Wrap the FilteredList in a SortedList.
        SortedList<PCInfoViewWrapper> sortedList = new SortedList<>(filteredList);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedList.comparatorProperty().bind(pcTable.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        pcTable.setItems(sortedList);
    }

    @FXML
    private void initialize(){
    	hostnameColumn.setCellValueFactory(cellData -> cellData.getValue().hostnameProperty());
    	ipAddressColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty());

    	//Clear person details.
        showPcDetails(null);

    	//Listen for selection changes and show the person details when changed.
        pcTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPcDetails(newValue));
        pcTable.getSelectionModel().selectedItemProperty().addListener(
        		(observable, oldValue, newValue) -> showPieChartDetails(newValue));
        pcTable.getSelectionModel().selectedItemProperty().addListener(
        		(observable, oldValue, newValue) -> showProgrammsDetails(newValue));
        pcTable.getSelectionModel().selectedItemProperty().addListener(
        		(observable, oldValue, newValue) -> showLineChartDetails(newValue));

        //FilteredList<PCInfo> filteredList; = new FilteredList<>(pcList, p -> true);
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {

        	filteredList.setPredicate(pc -> {
        		// If filter text is empty, display all persons.
        		if(newValue == null || newValue.isEmpty()){
        			return true;
        		}
        		if(pc.getHostname().toLowerCase().contains(newValue.toLowerCase())){
        			return true;
        		}
        		else if(pc.getIpAddress().contains(newValue)){
        			return true;
        		}
        		return false;
        	});
        });
    }

    private void showPcDetails(PCInfoViewWrapper newValue){
    	if(newValue != null){
    		hostnameLabel.setText(newValue.getHostname());
    		ipAddressLabel.setText(newValue.getIpAddress());
    		macAddressLabel.setText(newValue.getMacAddress());
    		osLabel.setText(newValue.getOs());
    		cpuConstructorLabel.setText(newValue.getCpu().getConstructor());
    		cpuModelLabel.setText(newValue.getCpu().getModel());
    		cpuFrequencyLabel.setText(Double.toString(newValue.getCpu().getFrequency()));
    		cpuNumbCorelLabel.setText(Integer.toString(newValue.getCpu().getNbCore()));
    		hddTotalSizeLabel.setText(Double.toString(newValue.getHdd().getTotalSize()));
    		hddFreeSizeLabel.setText(Double.toString(newValue.getHdd().getFreeSize()));
    		ramSizeLabel.setText(Long.toString(newValue.getRamSize()));
    		if(newValue.getPrograms() != null){
    			installedProgrammsLabel.setText(Integer.toString(newValue.getPrograms().size()));
        	}
    		else{
    			installedProgrammsLabel.setText("");
    		}
    	}

    	else{
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

    private void showProgrammsDetails(PCInfoViewWrapper newValue){
    	if(newValue != null){
    		programTable.setItems(newValue.getPrograms());
    		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    		versionColumn.setCellValueFactory(cellData -> cellData.getValue().versionProperty());
    		lastUpdateColumn.setCellValueFactory(cellData -> cellData.getValue().lastUpdateProperty());
    	}
    }

    private void showLineChartDetails(PCInfoViewWrapper newValue){

    	lineChart.getData().clear();

    	//Récupère les infos de la base de donnée
    	HashMap<String, Double> map = db.freeHardDriveSizeRate(newValue);

    	//Ajout des données au graphique

    	XYChart.Series series = new XYChart.Series();

    	for(Entry<String, Double> e : map.entrySet()){
			series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
		}

    	lineChart.getData().add(series);
    }

    private void showPieChartDetails(PCInfoViewWrapper newValue){
    	if(newValue != null){

    		double availableSpace = newValue.getHdd().getFreeSize();
    		double unavailableSpace = newValue.getHdd().getTotalSize() - newValue.getHdd().getFreeSize();

    		pieChartData = FXCollections.observableArrayList(
    				new PieChart.Data("Available", availableSpace),
    				new PieChart.Data("Unavailable", unavailableSpace));

    		chart.setData(pieChartData);

    		for (final PieChart.Data data : chart.getData()) {
    		    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
    		        new EventHandler<MouseEvent>() {
    		            @Override public void handle(MouseEvent e) {
    		            	String tmp = String.valueOf((data.getPieValue() / (newValue.getHdd().getTotalSize() / 100)));
    		            	int i = tmp.indexOf(".");
    		                status.setText(tmp.substring(0, i + 3) + "%");
    		             }
    		        });
    		}
    	}
    }

    private void showFilterEditDialog(){
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ServerApp.class.getResource("view/FilterEditDialog.fxml"));
			AnchorPane filterEditDialog = (AnchorPane) loader.load();

			Stage filterStage = new Stage();
			filterStage.setTitle("Filter");
			filterStage.initModality(Modality.WINDOW_MODAL);
			filterStage.initOwner(serverApp.getPrimaryStage());
			Scene scene = new Scene(filterEditDialog);
			filterStage.setScene(scene);

			FilterEditDialogController controller = loader.getController();
			controller.setDialogStage(filterStage);

			controller.setAdvancedFilter(filter);

            // Show the dialog and wait until the user closes it
            filterStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @FXML
    public void handleCaptureDate(){
    	String date = serverApp.showCaptureSelectionDialog();

    	if(!date.equals("")){
    		serverApp.setCurentPcView(date);
        	serverApp.getPcInfo().clear();
    		serverApp.getPcInfo().addAll(serverApp.getDatabase().loadPCInfo(date));
    	}
    }

    @FXML
    public void handleDeleteCapture(){
    	String date = serverApp.showCaptureSelectionDialog();
    	serverApp.getDatabase().deleteCapture(date);
    }

    @FXML
	public void handleAdvancedFilter(){
    	filterField.clear();
    	filteredListCopy = filteredList;
		showFilterEditDialog();
		filteredList = filter.getFilteredList();

		// 3. Wrap the FilteredList in a SortedList.
        SortedList<PCInfoViewWrapper> sortedList = new SortedList<>(filteredList);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedList.comparatorProperty().bind(pcTable.comparatorProperty());

		pcTable.setItems(sortedList);
	}

    @FXML
    public void handleClearFilter(){
    	filteredList = filteredListCopy;
    	filter.clearFilter();
    	pcTable.setItems(filteredList);
    }

    @FXML
    public void handleRefresh(){


//    	ObservableList<PCInfoViewWrapper> pcData = serverApp.getPcInfo();
//    	pcData.clear();

       //games = FXCollections.observableArrayList();

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
			db.storePCs(list);
			serverApp.setCurentPcView(db.getLastCapture());
		}, PlatformExecutor.instance);

		


		/*for(PCInfo pc : pcInfos){
			System.out.println(pc.getHostname());
			System.out.println(pc.getIpAddress());
			System.out.println(pc.getMacAddress());
			System.out.println(pc.getOs());
			System.out.println(pc.getRamSize());
			System.out.println(pc.getCpu().getConstructor());
			System.out.println(pc.getCpu().getModel());
			System.out.println(pc.getHdd().getFreeSize());

			pcData.add(new PCInfoViewWrapper(pc));
		}*/

    }

}
