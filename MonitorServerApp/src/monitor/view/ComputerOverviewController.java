package monitor.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import monitor.ServerApp;
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


    private ServerApp serverApp;
    private FilteredList<PCInfoViewWrapper> filteredList;


    public void setServerApp(ServerApp serverApp){
		this.serverApp = serverApp;
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		filteredList = new FilteredList<>(serverApp.getPcInfo(), p -> true);
		// 3. Wrap the FilteredList in a SortedList.
        SortedList<PCInfoViewWrapper> sortedList = new SortedList<>(filteredList);

     // 4. Bind the SortedList comparator to the TableView comparator.
        sortedList.comparatorProperty().bind(pcTable.comparatorProperty());

     // 5. Add sorted (and filtered) data to the table.
        pcTable.setItems(sortedList);
		// Add observable list data to the table
		//pcTable.setItems(serverApp.getPcInfo());
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

    private void showPcDetails(PCInfoViewWrapper pc){
    	if(pc != null){
    		hostnameLabel.setText(pc.getHostname());
    		ipAddressLabel.setText(pc.getIpAddress());
    		macAddressLabel.setText(pc.getMacAddress());
    		osLabel.setText(pc.getOs());
    		cpuConstructorLabel.setText(pc.getCpu().getConstructor());
    		cpuModelLabel.setText(pc.getCpu().getModel());
    		cpuFrequencyLabel.setText(Double.toString(pc.getCpu().getFrequency()));
    		cpuNumbCorelLabel.setText(Integer.toString(pc.getCpu().getNbCore()));
    		hddTotalSizeLabel.setText(Double.toString(pc.getHdd().getTotalSize()));
    		hddFreeSizeLabel.setText(Double.toString(pc.getHdd().getFreeSize()));
    		ramSizeLabel.setText(Long.toString(pc.getRamSize()));
    		if(pc.getPrograms() != null){
    			installedProgrammsLabel.setText(Integer.toString(pc.getPrograms().size()));
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

    private void showProgrammsDetails(PCInfoViewWrapper pc){
    	if(pc != null){
    		programTable.setItems(pc.getPrograms());
    		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    		versionColumn.setCellValueFactory(cellData -> cellData.getValue().versionProperty());
    		lastUpdateColumn.setCellValueFactory(cellData -> cellData.getValue().lastUpdateProperty());
    	}
    }

    private void showPieChartDetails(PCInfoViewWrapper pc){
    	if(pc != null){

    		double availableSpace = pc.getHdd().getFreeSize();
    		double unavailableSpace = pc.getHdd().getTotalSize() - pc.getHdd().getFreeSize();

    		pieChartData = FXCollections.observableArrayList(
    				new PieChart.Data("Available", availableSpace),
    				new PieChart.Data("Unavailable", unavailableSpace));

    		chart.setData(pieChartData);

    		for (final PieChart.Data data : chart.getData()) {
    		    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
    		        new EventHandler<MouseEvent>() {
    		            @Override public void handle(MouseEvent e) {
    		            	String tmp = String.valueOf((data.getPieValue() / (pc.getHdd().getTotalSize() / 100)));
    		            	int i = tmp.indexOf(".");
    		                status.setText(tmp.substring(0, i + 3) + "%");
    		             }
    		        });
    		}
    	}
    }

    @FXML
	public void handleAdvancedFilter(){
		serverApp.showFilterEditDialog();
	}
}
