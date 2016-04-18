
package com.ravelsoftware.ravtech.graphics;

public class SortingLayer {

	public String name;

	public SortingLayer (String layerName) {
		name = layerName;
	}

	public SortingLayer () {
	}

	@Override
	public String toString () {
		return name;
	}
}
