package utils;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import monitor.model.PCInfoViewWrapper;
import monitor.model.ProgramViewWrapper;

/**
 * Cette classe permet de filtrer une liste de PCInfoViewWrapper selon les critères suivants:
 * l'os, la taille de la mémoire, la capacité de stockage, le taux d'occupation de la mémoire,
 * et les différents programmes installés. Le filtre est aplique sous forme de "et logique",
 * ainsi les éléments présent dans la liste après qpplication du filtre seront ceux qui auront
 * satisfait à tous les champs conditionnels du filtre.
 *
 * @author ROHRER Michaël
 */
public class AdvancedFilters {

	//La liste contenent les éléments filtrés
	private FilteredList<PCInfoViewWrapper> filteredList;

	/**
	 * Constructeur: AdvancedFilters
	 *
	 * @param observableList
	 */
	public AdvancedFilters(ObservableList<PCInfoViewWrapper> observableList) {
		this.filteredList = new FilteredList<>(observableList, p -> true);
	}

	/**
	 * Applique le filtre sur la liste de pc donnée dans le constructeur.
	 *
	 * @param os, l'os du pc.
	 * @param ramSize, la taille de la mémoire du pc.
	 * @param hddSize, la capacité de stockage du pc.
	 * @param hddOcupRate, le taux d'occupation du disque dur du pc.
	 * @param installedProgram, le(s) programme(s) installés sur le pc.
	 */
	public void applyFilter(String os, String ramSize, String hddSize, String hddOcupRate, String installedProgram) {

		//Un pc doit passé tous les testes ci-dessous pour être sélectionné

		filteredList.setPredicate(pc -> {

			//Si tous les champs sont vides alors le pc est sélectionné
			if ((os == null || os.isEmpty())
					&& (ramSize == null || ramSize.isEmpty())
					&& (hddSize == null || hddSize.isEmpty()) && (hddOcupRate == null || hddOcupRate.isEmpty())
					&& (installedProgram == null || installedProgram.isEmpty())) {
				return true;
			}

			//le pc n'est pas sélectionné si son os ne correspond pas au champ os
			if (!(os == null || os.isEmpty())) {
				if (!(pc.getOs().toLowerCase().contains(os.toLowerCase()))) {
					return false;
				}
			}

			//le pc n'est pas sélectionné si sa taille de mémoire vive ne ne correspond pas au champ ramSize
			if (!(ramSize == null || ramSize.isEmpty())) {
				if (!(pc.getRamSize() == Integer.valueOf(ramSize))) {
					return false;
				}
			}

			//le pc n'est pas sélectionné si sa capacité de stockage ne correspond pas au champ hddSize
			if (!(hddSize == null || hddSize.isEmpty())) {
				if (!(pc.getHdd().getTotalSize() == Integer.valueOf(hddSize))) {
					return false;
				}
			}

			//le pc n'est pas sélectionné si le taux d'occupation de son disque dure est plus petit que hddhddOcupRate
			if (!(hddOcupRate == null || hddOcupRate.isEmpty())) {

				double ocupRate = (pc.getHdd().getTotalSize() - pc.getHdd().getFreeSize()) / pc.getHdd().getTotalSize();

				if (ocupRate < Double.valueOf(hddOcupRate)) {
					return false;
				}
			}

			//le pc est séléctionné si tôt que tous les programmes mentionnés par l'utilisateur y sont installé
			if (!(installedProgram == null || installedProgram.isEmpty())) {

				boolean contains;
				boolean containsAll;

				//Récupère les différents programmes passé en paramètre par l'utilisateur
				String[] progs = installedProgram.split(":");

				containsAll = true;
				for (String prog : progs) {
					contains = false;
					for (ProgramViewWrapper p : pc.getPrograms()) {
						if (p.getName().toLowerCase().equals(prog.toLowerCase())) {
							contains = true;
							break;
						}
					}
					containsAll &= contains;
				}
				return containsAll;
			}
			return true;
		});
	}

	/**
	 * Efface le filtre appliqué à la liste de pc, tous les pcs sont alors présents dans la liste filtrée
	 */
	public void clearFilter() {
		applyFilter(null, null, null, null, null);
	}

	/**
	 * Permet de récupérer la liste de pc filtrée
	 *
	 * @return la liste de pc sur la quel a été apliqué le filtre
	 */
	public FilteredList<PCInfoViewWrapper> getFilteredList() {
		return filteredList;
	}
}
