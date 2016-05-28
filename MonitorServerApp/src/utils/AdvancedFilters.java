package utils;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import monitor.model.PCInfoViewWrapper;
import monitor.model.ProgramViewWrapper;

/**
 * @author ROHRER Michaël
 *
 */
public class AdvancedFilters {

	private FilteredList<PCInfoViewWrapper> filteredList;

	/**
	 * @param observableList
	 */
	public AdvancedFilters(ObservableList<PCInfoViewWrapper> observableList) {
		this.filteredList = new FilteredList<>(observableList, p -> true);
	}

	/**
	 * @param os
	 * @param ramSize
	 * @param hddSize
	 * @param hddOcupRate
	 * @param installedProgram
	 */
	public void applyFilter(String os, String ramSize, String hddSize, String hddOcupRate, String installedProgram) {

		filteredList.setPredicate(pc -> {

			if ((os == null || os.isEmpty()) && (ramSize == null || ramSize.isEmpty())
					&& (hddSize == null || hddSize.isEmpty()) && (hddOcupRate == null || hddOcupRate.isEmpty())
					&& (installedProgram == null || installedProgram.isEmpty())) {
				return true;
			}

			if (!(os == null || os.isEmpty())) {
				if (!(pc.getOs().toLowerCase().contains(os.toLowerCase()))) {
					return false;
				}
			}

			if (!(ramSize == null || ramSize.isEmpty())) {
				if (!(pc.getRamSize() == Integer.valueOf(ramSize))) {
					return false;
				}
			}

			if (!(hddSize == null || hddSize.isEmpty())) {
				if (!(pc.getHdd().getTotalSize() == Integer.valueOf(hddSize))) {
					return false;
				}
			}

			if (!(hddOcupRate == null || hddOcupRate.isEmpty())) {

				double ocupRate = (100 / pc.getHdd().getTotalSize())
						* (pc.getHdd().getTotalSize() - pc.getHdd().getFreeSize());

				if (ocupRate < Integer.valueOf(hddOcupRate)) {
					return false;
				}
			}

			if (!(installedProgram == null || installedProgram.isEmpty())) {

				boolean contains;
				boolean containsAll;
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
	 * 
	 */
	public void clearFilter() {
		applyFilter(null, null, null, null, null);
	}

	/**
	 * @return
	 */
	public FilteredList<PCInfoViewWrapper> getFilteredList() {
		return filteredList;
	}
}
