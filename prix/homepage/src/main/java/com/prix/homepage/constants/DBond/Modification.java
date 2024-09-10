package com.prix.homepage.constants.DBond;

import java.io.Reader;

public class Modification extends Base {
	public void read(Reader reader) {
		type = getToken(reader);
		position = getToken(reader);
		mass = getDouble(reader);
	}

	public String getType() { return type; }
	public String getPosition() { return position; }
	public double getMass() { return mass; }

	private String type;
	private String position;
	private double mass;
}