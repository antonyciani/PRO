package monitor.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class PCInfo {

	private StringProperty hostname;
	private StringProperty ipAddress;
	private StringProperty macAddress;
	private StringProperty os;
	private ObjectProperty<CPUInfo> cpu;
	private ObjectProperty<HDDInfo> hdd;
	private LongProperty ramSize;
	private ObservableList<Program> programs;

	public PCInfo(String hostname, String ipAddress, String macAddress, String os, CPUInfo cpu, HDDInfo hdd, long ramSize, ObservableList<Program> programs){
		this.hostname = new SimpleStringProperty(hostname);
		this.ipAddress = new SimpleStringProperty(ipAddress);
		this.macAddress = new SimpleStringProperty(macAddress);
		this.os = new SimpleStringProperty(os);
		this.cpu = new SimpleObjectProperty<CPUInfo>(cpu);
		this.hdd = new SimpleObjectProperty<HDDInfo>(hdd);
		this.ramSize = new SimpleLongProperty(ramSize);
		this.programs = programs;
	}

	public String getHostname() {
		return hostname.get();
	}

	public StringProperty hostnameProperty() {
		return hostname;
	}

	public String getIpAddress() {
		return ipAddress.get();
	}

	public StringProperty ipAddressProperty() {
		return ipAddress;
	}

	public String getMacAddress() {
		return macAddress.get();
	}

	public StringProperty macAddressProperty() {
		return macAddress;
	}

	public String getOs() {
		return os.get();
	}

	public StringProperty osProperty() {
		return os;
	}

	public CPUInfo getCpu() {
		return cpu.getValue();
	}

	public ObjectProperty<CPUInfo> cpuProperty() {
		return cpu;
	}

	public HDDInfo getHdd() {
		return hdd.getValue();
	}

	public ObjectProperty<HDDInfo> hddProperty() {
		return hdd;
	}

	public long getRamSize() {
		return ramSize.get();
	}

	public LongProperty ramSizeProperty() {
		return ramSize;
	}

	public ObservableList<Program> getPrograms() {
		return programs;
	}
}
