package monitor.model;

import java.io.Serializable;

/**
 * Cette classe permet de stocker les informations relatives au disque dur.
 * 
 * @author CIANI Antony
 *
 */
@SuppressWarnings("serial")
public class HDDInfo implements Serializable {

	private double totalSize;
	private double freeSize;

	/**
	 * @param totalSize
	 * @param freeSize
	 */
	public HDDInfo(double totalSize, double freeSize) {
		this.totalSize = totalSize;
		this.freeSize = freeSize;
	}

	/**
	 * @return
	 */
	public double getTotalSize() {
		return totalSize;
	}

	/**
	 * @return
	 */
	public double getFreeSize() {
		return freeSize;
	}

	/**
	 * @param d
	 */
	public void setFreeSize(double d) {
		// TODO Auto-generated method stub
		freeSize = d;

	}

}
