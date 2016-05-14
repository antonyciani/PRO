package utils;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import monitor.model.PCInfo;
import monitor.model.PCInfoViewWrapper;
import monitor.model.Program;
import monitor.model.ProgramViewWrapper;

public class AdvancedFilters {

	private FilteredList<PCInfoViewWrapper> filteredList;

	public AdvancedFilters(ObservableList<PCInfoViewWrapper> observableList){
		this.filteredList = new FilteredList<>(observableList, p -> true);
	}

    public void applyFilter(String os, String ramSize, String hddSize, String hddOcupRate, String installedProgram){

    	filteredList.setPredicate(pc -> {

    		if((os == null || os.isEmpty())
    				&& (ramSize == null || ramSize.isEmpty())
    				&& (hddSize == null || hddSize.isEmpty())
    				&& (hddOcupRate == null || hddOcupRate.isEmpty())
    				&& (installedProgram == null || installedProgram.isEmpty())){
    			return true;
    		}


    		if(!(os == null || os.isEmpty())){
    			if(!(pc.getOs().contains(os))){
    				return false;
    			}
    		}


    		if(!(ramSize == null || ramSize.isEmpty())){
    			if(!(pc.getRamSize() == Integer.valueOf(ramSize))){
    				return false;
    			}
    		}

    		if(!(hddSize == null || hddSize.isEmpty())){
    			if(!(pc.getHdd().getTotalSize() == Integer.valueOf(hddSize))){
    				return false;
    			}
    		}

    		if(!(hddOcupRate == null || hddOcupRate.isEmpty())){

    			double ocupRate = (100 / pc.getHdd().getTotalSize()) * (pc.getHdd().getTotalSize() - pc.getHdd().getFreeSize());

    			if(ocupRate < Integer.valueOf(hddOcupRate)){
    				return false;
    			}
    		}

    		if(!(installedProgram == null || installedProgram.isEmpty())){

    			boolean contains = false;

    			for(ProgramViewWrapper p : pc.getPrograms()){
    				if(p.getName().contains(installedProgram)){
        				contains = true;
        				break;
        			}
    			}
    			return contains;
    		}
    		return true;
    	});
    }

    public void clearFilter(){
    	applyFilter(null, null, null, null, null);
    }

    public FilteredList<PCInfoViewWrapper> getFilteredList(){
		return filteredList;
    }
}
