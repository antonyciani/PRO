package monitor.view;

import java.net.SocketException;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.PCInfoViewWrapper;
import monitor.model.ProgramViewWrapper;
import communication.SystemInfoRetrieverProtocol;
import communication.SystemInfoRetrieverServer;
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
    private LineChart<String, Double> lineChart;
    @FXML
	private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private Alert alert;


    private ServerApp serverApp;
    private FilteredList<PCInfoViewWrapper> filteredList;
    private SortedList<PCInfoViewWrapper> sortedList;

    private Database db;

    public void setServerApp(ServerApp serverApp){
		this.serverApp = serverApp;
		db = this.serverApp.getDatabase();
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		filteredList = new FilteredList<>(serverApp.getPcInfo(), p -> true);

		// 3. Wrap the FilteredList in a SortedList.
        sortedList = new SortedList<>(filteredList);
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

    	if(newValue != null){

    		lineChart.getData().clear();

        	//Récupère les infos de la base de donnée
        	TreeMap<String, Double> map = db.storageLoadRate(newValue);

        	//Ajout des données au graphique
        	Series<String, Double> series = new Series<>();
        	series.setName("Storage Load Rate");
        	for(Entry<String, Double> e : map.entrySet()){
    			series.getData().add(new Data<String, Double>(e.getKey(), e.getValue()));
    		}

        	lineChart.getData().add(series);
    	}
    }

    private void showPieChartDetails(PCInfoViewWrapper newValue){
    	if(newValue != null){
    		chart.getData().clear();

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
    	else {
    		chart.getData().clear();
    	}
    }

    private void showAlertDialog(){
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Alert");
    	alert.setHeaderText("Retriving PCs Information...");
    	alert.show();
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
    	FilteredList<PCInfoViewWrapper> advancedFilteredList;
    	filterField.clear();
    	//filteredListCopy = filteredList;
		serverApp.showFilterEditDialog();
		advancedFilteredList = serverApp.getAdvancedFilters().getFilteredList();

		// 3. Wrap the FilteredList in a SortedList.
        SortedList<PCInfoViewWrapper> sortedList = new SortedList<>(advancedFilteredList);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedList.comparatorProperty().bind(pcTable.comparatorProperty());
		pcTable.setItems(sortedList);
	}

    @FXML
    public void handleClearFilter(){
    	serverApp.getAdvancedFilters().clearFilter();
    	// 3. Wrap the FilteredList in a SortedList.
        SortedList<PCInfoViewWrapper> sortedList = new SortedList<>(filteredList);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedList.comparatorProperty().bind(pcTable.comparatorProperty());
    	pcTable.setItems(sortedList);
    }

    @FXML
    public void handleRefresh(){
    	showAlertDialog();
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
			alert.close();
		}, PlatformExecutor.instance);
    }
}
