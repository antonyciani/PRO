package monitor.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.AdvancedFilters;

public class FilterEditDialogController {

	@FXML
	private TextField osField;
	@FXML
	private TextField ramSizeField;
	@FXML
	private TextField hddSizeField;
	@FXML
	private TextField hddOccupRateField;
	@FXML
	private TextField programField;

	private Stage dialogStage;

	private AdvancedFilters filter;

	/**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
    	this.dialogStage = dialogStage;
    }

    /**
     * Set the list which contain the pc to sort
     *
     * @param pcList
     */
    public void setAdvancedFilter(AdvancedFilters filter){
    	this.filter = filter;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleSearch() {

    	System.out.println(osField.getText());
    	System.out.println(ramSizeField.getText());
    	System.out.println(hddSizeField.getText());
    	System.out.println(hddOccupRateField.getText());
    	System.out.println(programField.getText());

    	filter.applyFilter(osField.getText(), ramSizeField.getText(), hddSizeField.getText(), hddOccupRateField.getText(), programField.getText());
    	dialogStage.close();

    }

	/**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
