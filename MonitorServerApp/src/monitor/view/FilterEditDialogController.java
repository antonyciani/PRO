package monitor.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import monitor.model.PCInfoViewWrapper;

public class FilterEditDialogController {

	@FXML
	TextField osField;
	@FXML
	TextField RAMSizeField;
	@FXML
	TextField HDDSizeField;
	@FXML
	TextField HDDOcupRateField;
	@FXML
	TextField InstalledProgramField;

	private Stage dialogStage;
	private ObservableList<PCInfoViewWrapper> pcList;


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
    public void setPcList(ObservableList<PCInfoViewWrapper> pcList){
    	this.pcList = pcList;
    }


    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {

    	//TODO Filter
    }

	/**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

}
