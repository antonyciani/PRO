package monitor.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProgramViewWrapper {
	StringProperty name;
	StringProperty version;
	StringProperty lastUpdate;

	public ProgramViewWrapper(String name, String version, String lastUpdate){
		this.name = new SimpleStringProperty(name);
		this.version = new SimpleStringProperty(version);
		this.lastUpdate = new SimpleStringProperty(lastUpdate);
	}
	
	public ProgramViewWrapper(Program program){
		this.name = new SimpleStringProperty(program.getName());
		this.version = new SimpleStringProperty(program.getVersion());
		this.lastUpdate = new SimpleStringProperty(program.getLastUpdate());
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty(){
		return name;
	}

	public String getVersion() {
		return version.get();
	}

	public StringProperty versionProperty(){
		return version;
	}

	public String getLastUpdate() {
		return lastUpdate.get();
	}

	public StringProperty lastUpdateProperty(){
		return lastUpdate;
	}
}
