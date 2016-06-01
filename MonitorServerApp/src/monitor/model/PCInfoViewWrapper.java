package monitor.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Cette classe permet d'envelopper les informations relatives au PC dans des objets
 * observables. Ces objets permettent de notifier automatiquement la vue correspondante
 * si un champ venait à changer.
 * 
 * @author CIANI Antony
 *
 */
public class PCInfoViewWrapper {

	private StringProperty hostname;
	private StringProperty ipAddress;
	private StringProperty macAddress;
	private StringProperty os;
	private ObjectProperty<CPUInfo> cpu;
	private ObjectProperty<HDDInfo> hdd;
	private LongProperty ramSize;
	private ObservableList<ProgramViewWrapper> programs;

	/**
	 * @param hostname
	 * @param ipAddress
	 * @param macAddress
	 * @param os
	 * @param cpu
	 * @param hdd
	 * @param ramSize
	 * @param programs
	 */
	public PCInfoViewWrapper(String hostname, String ipAddress, String macAddress, String os, CPUInfo cpu, HDDInfo hdd,
			long ramSize, ObservableList<Program> programs) {
		this.hostname = new SimpleStringProperty(hostname);
		this.ipAddress = new SimpleStringProperty(ipAddress);
		this.macAddress = new SimpleStringProperty(macAddress);
		this.os = new SimpleStringProperty(os);
		this.cpu = new SimpleObjectProperty<CPUInfo>(cpu);
		this.hdd = new SimpleObjectProperty<HDDInfo>(hdd);
		this.ramSize = new SimpleLongProperty(ramSize);
		this.programs = FXCollections.observableArrayList();
		for (Program p : programs) {

			this.programs.add(new ProgramViewWrapper(p));

		}
	}

	/**
	 * @param pc
	 */
	public PCInfoViewWrapper(PCInfo pc) {
		this.hostname = new SimpleStringProperty(pc.getHostname());
		this.ipAddress = new SimpleStringProperty(pc.getIpAddress());
		this.macAddress = new SimpleStringProperty(pc.getMacAddress());
		this.os = new SimpleStringProperty(pc.getOs());
		this.cpu = new SimpleObjectProperty<CPUInfo>(pc.getCpu());
		this.hdd = new SimpleObjectProperty<HDDInfo>(pc.getHdd());
		this.ramSize = new SimpleLongProperty(pc.getRamSize());
		this.programs = FXCollections.observableArrayList();
		for (Program p : pc.getPrograms()) {

			this.programs.add(new ProgramViewWrapper(p));

		}
	}

	/**
	 * @return
	 */
	public String getHostname() {
		return hostname.get();
	}

	/**
	 * @return
	 */
	public StringProperty hostnameProperty() {
		return hostname;
	}

	/**
	 * @return
	 */
	public String getIpAddress() {
		return ipAddress.get();
	}

	/**
	 * @return
	 */
	public StringProperty ipAddressProperty() {
		return ipAddress;
	}

	/**
	 * @return
	 */
	public String getMacAddress() {
		return macAddress.get();
	}

	/**
	 * @return
	 */
	public StringProperty macAddressProperty() {
		return macAddress;
	}

	/**
	 * @return
	 */
	public String getOs() {
		return os.get();
	}

	/**
	 * @return
	 */
	public StringProperty osProperty() {
		return os;
	}

	/**
	 * @return
	 */
	public CPUInfo getCpu() {
		return cpu.getValue();
	}

	/**
	 * @return
	 */
	public ObjectProperty<CPUInfo> cpuProperty() {
		return cpu;
	}

	/**
	 * @return
	 */
	public HDDInfo getHdd() {
		return hdd.getValue();
	}

	/**
	 * @return
	 */
	public ObjectProperty<HDDInfo> hddProperty() {
		return hdd;
	}

	/**
	 * @return
	 */
	public long getRamSize() {
		return ramSize.get();
	}

	/**
	 * @return
	 */
	public LongProperty ramSizeProperty() {
		return ramSize;
	}

	/**
	 * @return
	 */
	public ObservableList<ProgramViewWrapper> getPrograms() {
		return programs;
	}

	/**
	 * @return
	 */
	public ObservableList<ProgramViewWrapper> programsProperties() {
		return programs;
	}
}
