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
	 * Constructeur, construit l'objet à partir des infos relatives au PC
	 *
	 * @param hostname, le nom d'hôte du PC
	 * @param ipAddress, l'adresse IP du PC
	 * @param macAddress, l'adresse MAC du PC
	 * @param os, l'OS du PC
	 * @param cpu, les informations relatives au CPU du PC
	 * @param hdd, les informations relatives au disque dure
	 * @param ramSize, la taille de la mémoire RAM
	 * @param programs, les informations relatives aux programmes installés sur le PC
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
	 * Constructeur, construit l'objet à partir de l'objet PCInfo
	 *
	 * @param pc, l'objet contenant les infos relative au PC
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
	 * @return le nom d'hôte
	 */
	public String getHostname() {
		return hostname.get();
	}

	/**
	 * @return le nom d'hôte sous forme d'objet observable
	 */
	public StringProperty hostnameProperty() {
		return hostname;
	}

	/**
	 * @return l'adresse IP
	 */
	public String getIpAddress() {
		return ipAddress.get();
	}

	/**
	 * @return l'adresse IP sous forme d'objet observable
	 */
	public StringProperty ipAddressProperty() {
		return ipAddress;
	}

	/**
	 * @return l'adresse MAC
	 */
	public String getMacAddress() {
		return macAddress.get();
	}

	/**
	 * @return l'adresse MAC sous forme d'objet observable
	 */
	public StringProperty macAddressProperty() {
		return macAddress;
	}

	/**
	 * @return l'os
	 */
	public String getOs() {
		return os.get();
	}

	/**
	 * @return l'os sous forme d'objet observable
	 */
	public StringProperty osProperty() {
		return os;
	}

	/**
	 * @return les informations relatives au CPU
	 */
	public CPUInfo getCpu() {
		return cpu.getValue();
	}

	/**
	 * @return les informations relatives au CPU sous forme d'objet observable
	 */
	public ObjectProperty<CPUInfo> cpuProperty() {
		return cpu;
	}

	/**
	 * @return les informations relatives au disque dure
	 */
	public HDDInfo getHdd() {
		return hdd.getValue();
	}

	/**
	 * @return les informations relatives au disque dure sous forme d'objet observable
	 */
	public ObjectProperty<HDDInfo> hddProperty() {
		return hdd;
	}

	/**
	 * @return la taille de la mémoire RAM
	 */
	public long getRamSize() {
		return ramSize.get();
	}

	/**
	 * @return la taille de la mémoire RAM sous forme d'objet observable
	 */
	public LongProperty ramSizeProperty() {
		return ramSize;
	}

	/**
	 * @return les informations relatives aux programmes installés sur le PC
	 */
	public ObservableList<ProgramViewWrapper> getPrograms() {
		return programs;
	}
}
