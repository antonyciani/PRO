package monitor.view;

import java.util.TreeMap;
import java.util.Map.Entry;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import monitor.ServerApp;
import monitor.database.Database;
import monitor.model.PCInfoViewWrapper;
import monitor.model.ProgramViewWrapper;

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
    private PieChart pieChart;
    @FXML
    private Label spaceLabel;
    @FXML
    private Label statusLabel;
    private ObservableList<PieChart.Data> pieChartData;

    @FXML
    private LineChart<String, Double> lineChart;
    @FXML
	private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private Label captureDateLabel;


    private ServerApp serverApp;
    private SimpleStringProperty currentDateView;
    private FilteredList<PCInfoViewWrapper> filteredList;
    private SortedList<PCInfoViewWrapper> sortedList;

    private Database database;

    public void init(ServerApp serverApp){
		this.serverApp = serverApp;
		database = serverApp.getDatabase();
		currentDateView = serverApp.getCurentDateView();
		captureDateLabel.textProperty().bind(currentDateView);
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
    		cpuFrequencyLabel.setText(Double.toString(newValue.getCpu().getFrequency()) + " GHz");
    		cpuNumbCorelLabel.setText(Integer.toString(newValue.getCpu().getNbCore()) + " Cores");
    		hddTotalSizeLabel.setText(Double.toString(newValue.getHdd().getTotalSize()) + " GB");
    		hddFreeSizeLabel.setText(Double.toString(newValue.getHdd().getFreeSize()) + " GB");
    		ramSizeLabel.setText(Long.toString(newValue.getRamSize()) + " GB");
    		if(newValue.getPrograms() != null){
    			installedProgrammsLabel.setText(Integer.toString(newValue.getPrograms().size()));
        	}
    		else{
    			installedProgrammsLabel.setText("0");
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

    	programTable.getItems().clear();

    	if(newValue != null){
    		programTable.setItems(newValue.getPrograms());
    		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    		versionColumn.setCellValueFactory(cellData -> cellData.getValue().versionProperty());
    	}
    }

    private void showLineChartDetails(PCInfoViewWrapper newValue){

    	if(newValue != null){

    		spaceLabel.setText("");
    		statusLabel.setText("");
    		lineChart.getData().clear();

        	//Récupère les infos de la base de donnée
        	TreeMap<String, Double> map = database.storageLoadRate(newValue);

        	//Ajout des données au graphique
        	Series<String, Double> series = new Series<>();
        	series.setName("Storage Load Rate");
        	for(Entry<String, Double> e : map.entrySet()){
    			series.getData().add(new Data<String, Double>(e.getKey(), e.getValue()));
    		}

        	lineChart.getData().add(series);
    	}
    	else {
    		lineChart.getData().clear();
    	}
    }

    private void showPieChartDetails(PCInfoViewWrapper newValue){
    	if(newValue != null){
    		pieChart.getData().clear();

    		double freeSpace = newValue.getHdd().getFreeSize();
    		double fullSpace = newValue.getHdd().getTotalSize() - newValue.getHdd().getFreeSize();

    		pieChartData = FXCollections.observableArrayList(
    				new PieChart.Data("Free Space", freeSpace),
    				new PieChart.Data("Full Space", fullSpace));


    		pieChart.setData(pieChartData);

    		for (final PieChart.Data data : pieChart.getData()) {
    		    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
    		        new EventHandler<MouseEvent>() {
    		            @Override
    		            public void handle(MouseEvent e) {
    		            	String tmp = String.format("%.1f%%", 100*data.getPieValue()/newValue.getHdd().getTotalSize()) ;
    		            	spaceLabel.setText(data.getName() + ": ");
    		            	statusLabel.setText(tmp);
    		             }
    		        });
    		}
    	}
    	else {
    		pieChart.getData().clear();
    	}
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
}
