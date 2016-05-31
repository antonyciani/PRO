package monitor.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import monitor.ServerApp;

/**
 * Cette classe fait office de contrôleur. Elle gère la fenêtre graphique permettant
 * à un utilisateur d'éditer les filtres avancés.
 *
 * @author ROHRER Michaël
 */
public class FilterEditDialogController {

	//Champ permettant de filtrer par os
	@FXML
	private TextField osField;
	//Champ permettant de filtrer par taille de mémoire
	@FXML
	private TextField ramSizeField;
	//Champ permettant de filtrer par taille de disque dur
	@FXML
	private TextField hddSizeField;
	//Champ permettant de filtrer par taux d'occupation du disque dure
	@FXML
	private TextField hddOccupRateField;
	//Champ permettant de filtrer par programmes installés
	@FXML
	private TextField programField;

	private Stage dialogStage;
	private ServerApp serverApp;

	/**
	 * Permet d'initialiser le contrôleur
	 *
	 * @param serverApp, la référence à l'applicaton principale
	 * @param dialogStage, une référence sur la scène correspondant à la fenêtre graphique
	 */
	public void init(ServerApp serverApp, Stage dialogStage) {
		this.serverApp = serverApp;
		this.dialogStage = dialogStage;
	}

	/**
	 * Appelée lorsque l'utilisateur clique sur ok, cette fonction applique le filtre sur les
	 * données.
	 *
	 */
	@FXML
	private void handleSearch() {

		//Application du filtre avancé
		serverApp.getAdvancedFilters().applyFilter(osField.getText(), ramSizeField.getText(), hddSizeField.getText(),
				hddOccupRateField.getText(), programField.getText());
		//Fermetur de la fenêtre
		dialogStage.close();

	}

	/**
	 * Appelée lorsque l'utilisateur clique sur cancel.
	 *
	 */
	@FXML
	private void handleCancel() {
		//Fermetur de la fenêtre
		dialogStage.close();
	}
}
