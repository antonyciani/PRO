package monitor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class HDDInfo {
	private DoubleProperty totalSize;
	private DoubleProperty freeSize;

	public HDDInfo(double totalSize, double freeSize){
		this.totalSize = new SimpleDoubleProperty(totalSize);
		this.freeSize = new SimpleDoubleProperty(freeSize);
	}

	public double getTotalSize() {
		return totalSize.get();
	}

	public DoubleProperty totalSizeProperty(){
		return totalSize;
	}

	public double getFreeSize() {
		return freeSize.get();
	}

	public DoubleProperty freeSizeProperty(){
		return freeSize;
	}
}
