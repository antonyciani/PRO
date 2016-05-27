package monitor.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProgramViewWrapper {
	StringProperty name;
	StringProperty version;
	StringProperty lastUpdate;

	public ProgramViewWrapper(String name, String version){
		this.name = new SimpleStringProperty(name);
		this.version = new SimpleStringProperty(version);

	}
	
	public ProgramViewWrapper(Program program){
		this.name = new SimpleStringProperty(program.getName());
		this.version = new SimpleStringProperty(program.getVersion());

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
}
