package monitor.view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import monitor.ServerApp;
import monitor.model.ObservableDate;

/**
 * Cette classe joue le rôle de contrôleur. Elle contrôle une fenêtre graphique permettant
 * d'afficher les différentes captures réalisées au cours du temps et d'en sélectionner une.
 * Pour se faire elle utilise la base de donnée de l'application principale.
 *
 * @author ROHRER Michaël
 */
public class CaptureSelectionDialogController {

	//la scène correspondante à la fenêtre de dialogue
	private Stage dialogStage;

	//Une référence à l'application principale
	@SuppressWarnings("unused")
	private ServerApp serverApp;

	//Permet de retenir la date sélectionnée
	private String selectedDate;

	//Contient les différentes dates de captures retournée par la base de donnée
	private ObservableList<ObservableDate> list;

	//Correspond aux tableau d'affichage des différentes dates
	@FXML
	private TableView<ObservableDate> dateTable;
	@FXML
	private TableColumn<ObservableDate, String> dateColumn;

	/**
	 * Utilisé par le xml loader pour initialiser les objets déclarés dans le fichier xml.
	 * Configure le contenu du tableau de date et l'action à effectuer lors du clique d'un
	 * utilisateur.
	 */
	@FXML
	private void initialize() {
		//Configure le contenu des cellules ici les dates de captures
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());

		//Ajoute un listener sur les cellules
		//Ainsi à chaque clique sur une cellule, la fonction setDate sera appelée
		dateTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> setDate(newValue));
	}

	/**
	 * Permet d'initialiser le contrôleur.
	 *
	 * @param serverApp
	 * @param dialogStage
	 */
	public void init(ServerApp serverApp, Stage dialogStage) {
		this.serverApp = serverApp;
		this.dialogStage = dialogStage;
		this.selectedDate = "";

		//Récupère la liste de date de la base de donnée
		list = createObservableDate(serverApp.getDatabase().getCaptures());

		//Insère le contenu de la liste dans le tableau
		dateTable.setItems(list);

		//Configure le comportement de l'application lorsque l'utilisateur ferme la fenêtre avec la croix
		dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				//Appel de la fonction d'annulation
				handleCancel();
			}
		});
	}

	/**
	 * Permet de retenir la date sélectionnée, est appelée par le listener lorsqu'un utilisateur clique sur une cellule
	 *
	 * @param date, la date séléctonnée
	 */
	private void setDate(ObservableDate date) {
		selectedDate = date.getDate();
	}

	/**
	 * Permet de récupérer la date de capture sélectionnée
	 *
	 * @return la date sélectionnée
	 */
	public String getDate() {
		return selectedDate;
	}

	/**
	 * Permet de convertir une liste de date au format String en liste Observable de date au format ObservableDate
	 *
	 * @param dates, la liste de date au format String
	 * @return Une liste de date observable du type ObservableDate
	 */
	private ObservableList<ObservableDate> createObservableDate(List<String> dates) {
		ObservableList<ObservableDate> tmp = FXCollections.observableArrayList();
		for (String date : dates) {
			tmp.add(new ObservableDate(date));
		}
		return tmp;
	}

	/**
	 * Ferme la fenêtre de dialogue, cette fonction est appelée lorsque l'utilisateur clique sur select.
	 */
	@FXML
	public void handleSelect() {
		//Fermeture de la fenêtre graphique
		dialogStage.close();
	}

	/**
	 * Ferme la fenêtre de dialogue, et set la date de capture avec un champ vide, cette fonction est appelée
	 * lorsque l'utilisateur clique sur cancel.
	 */
	@FXML
	private void handleCancel() {
		selectedDate = "";
		//Fermeture de la fenêtre graphique
		dialogStage.close();
	}
}
