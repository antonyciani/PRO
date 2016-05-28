package monitor.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class HDDInfo implements Serializable{

	private double totalSize;
	private double freeSize;

	public HDDInfo(double totalSize, double freeSize){
		this.totalSize = totalSize;
		this.freeSize = freeSize;
	}

	public double getTotalSize() {
		return totalSize;
	}

	public double getFreeSize() {
		return freeSize;
	}

	public void setFreeSize(double d) {
		// TODO Auto-generated method stub
		freeSize = d;

	}

}
