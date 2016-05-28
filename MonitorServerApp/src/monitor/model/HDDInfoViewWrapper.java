package monitor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author CIANI Antony
 *
 */
public class HDDInfoViewWrapper {
	private DoubleProperty totalSize;
	private DoubleProperty freeSize;

	/**
	 * @param totalSize
	 * @param freeSize
	 */
	public HDDInfoViewWrapper(double totalSize, double freeSize) {
		this.totalSize = new SimpleDoubleProperty(totalSize);
		this.freeSize = new SimpleDoubleProperty(freeSize);
	}

	/**
	 * @param hdd
	 */
	public HDDInfoViewWrapper(HDDInfo hdd) {
		this.totalSize = new SimpleDoubleProperty(hdd.getTotalSize());
		this.freeSize = new SimpleDoubleProperty(hdd.getFreeSize());
	}

	/**
	 * @return
	 */
	public double getTotalSize() {
		return totalSize.get();
	}

	/**
	 * @return
	 */
	public DoubleProperty totalSizeProperty() {
		return totalSize;
	}

	/**
	 * @return
	 */
	public double getFreeSize() {
		return freeSize.get();
	}

	/**
	 * @return
	 */
	public DoubleProperty freeSizeProperty() {
		return freeSize;
	}
}
