package com.prix.homepage.constants.DBond;

public class PeptideLine {
	public PeptideLine(int i, double m, int c, double mc, double md, double sc, int s, int e, String p, String mo, int pi) {
		index = i;
		mass = m;
		charge = c;
		mwCalc = mc;
		mwDelta = md;
		score = sc;
		start = s;
		end = e;
		peptide = p;
		modification = mo;
		proteinIndex = pi;
		second = null;
	}

	public int getIndex() { return index; }
	public double getMass() { return mass; }
	public int getCharge() { return charge; }
	public double getMWCalc() { return mwCalc; }
	public double getMWDelta() { return mwDelta; }
	public double getScore() { return score; }
	public int getStart() { return start; }
	public int getEnd() { return end; }
	public String getPeptide() { return peptide; }
	public String getModification() { return modification; }
	public int getProteinIndex() { return proteinIndex; }
	public PeptideLine getSecond() { return second; }
	public void setSecond(PeptideLine line) { second = line; }

	private int index;
	private double mass;
	private int charge;
	private double mwCalc;
	private double mwDelta;
	private double score;
	private int start;
	private int end;
	private String peptide;
	private String modification;
	private int proteinIndex;
	private PeptideLine second;
}
