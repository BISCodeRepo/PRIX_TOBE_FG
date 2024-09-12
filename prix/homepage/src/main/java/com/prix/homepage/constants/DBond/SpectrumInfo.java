package com.prix.homepage.constants.DBond;

import java.io.Reader;

public class SpectrumInfo extends Base {
	public SpectrumInfo() {
		proteinIndex = -1;
	}

	public void read(Reader reader) {
		id = getInteger(reader);
		title = getToken(reader);
		weight = getDouble(reader);
		charge = getInteger(reader);
		time = getDouble(reader);
		spectrumPosition = getInteger(reader);
		int count = getInteger(reader);
		peptides = new PeptideInfo[count];
		for (int i = 0; i < count; i++)
		{
			peptides[i] = new PeptideInfo();
			peptides[i].read(reader);
		}
	}

	public String write() {
		String result = id + "|" + title + "|" + weight + "|" + charge + "|" + time + "|" + spectrumPosition + "|" + peptides.length + "|";
		for (int i = 0; i < peptides.length; i++)
		{
			result += peptides[i].write();
		}
		return result;
	}

	public int getId() { return id; }
	public String getTitle() { return title; }
	public double getWeight() { return weight; }
	public int getCharge() { return charge; }
	public double getRTtime() { return time; }
	public int getSpectrumPosition() { return spectrumPosition; }
	public PeptideInfo[] getPeptides() { return peptides; }
	public PeptideInfo getPeptide(int index) { return peptides[index]; }
	public int getProteinIndex() { return proteinIndex; }

	public void setProteinIndex(int index) { proteinIndex = index; }

	private int id;
	private String title;
	private double weight;
	private int charge;
	private double time;
	private int spectrumPosition;
	private PeptideInfo[] peptides;
	private int proteinIndex;
}
