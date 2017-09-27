package com.gwong.thereisaplace.data;

public class StationInfo {
	private String name;
	private float x;
	private float y;
	private String line[];
	private String surroundingStation[];
	private int offset = 0;

	public StationInfo(String name, float x, float y, String line, String surroundingStation) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.line = line.split("\\|");
		this.surroundingStation = surroundingStation.split("\\|");
	}

	public StationInfo(String name, float x, float y, String line, String surroundingStation, int offset) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.line = line.split("\\|");
		this.surroundingStation = surroundingStation.split("\\|");
		this.offset = offset;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public String[] getLine() {
		return line;
	}

	public void setLine(String[] line) {
		this.line = line;
	}

	public String[] getSurroundingStation() {
		return surroundingStation;
	}

	public void setSurroundingStation(String[] surroundingStation) {
		this.surroundingStation = surroundingStation;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
