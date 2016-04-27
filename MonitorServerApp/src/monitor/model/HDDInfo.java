package monitor.model;

import java.io.Serializable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

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

}
