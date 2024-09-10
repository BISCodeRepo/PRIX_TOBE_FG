package com.prix.homepage.constants.DBond;

import java.io.Reader;

public class AminoAcid extends Base {
	public void read(Reader reader) {
		acid = getToken(reader);
		mass = getDouble(reader);
	}

	public String getAcid() { return acid; }
	public double getMass() { return mass; }

	private String acid;
	private double mass;
}
