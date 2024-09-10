package com.prix.homepage.constants.DBond;

import java.io.Reader;

public class PeptideInfo extends Base {
	public void read(Reader reader) {
		peptideName = getToken(reader);
		score = getDouble(reader);
		int n = getInteger(reader);
		modIndices = new int[n];
		modOffsets = new int[n];
		for (int j = 0; j < n; j++)
		{
			modIndices[j] = getInteger(reader);
			modOffsets[j] = getInteger(reader);
		}
		massCalc = getDouble(reader);
		massDelta = getDouble(reader);
		n = getInteger(reader);
		proteinIndices = new int[n];
		starts = new int[n];
		ends = new int[n];
		nterms = new char[n];
		cterms = new char[n];
		for (int j = 0; j < n; j++)
		{
			/*proteinNames[j] = getToken(reader);
			while (true)
			{
				String s = getToken(reader);
				try {
					Integer start = new Integer(s);
					starts[j] = start.intValue();
					break;
				} catch (Exception e) {
					proteinNames[j] += DELIMITER + s;
				}
			}*/
			proteinIndices[j] = getInteger(reader);
			starts[j] = getInteger(reader);
			ends[j] = getInteger(reader);
			nterms[j] = getToken(reader).charAt(0);
			cterms[j] = getToken(reader).charAt(0);
		}
	}

	public String write() {
		String result = peptideName + "|" + score + "|" + modIndices.length + "|";
		for (int j = 0; j < modIndices.length; j++)
		{
			result += modIndices[j] + "|" + modOffsets[j] + "|";
		}
		result += massCalc + "|" + massDelta + "|" + proteinIndices.length + "|";
		for (int j = 0; j < proteinIndices.length; j++)
		{
			result += proteinIndices[j] + "|" + starts[j] + "|" + ends[j] + "|" + nterms[j] + "|" + cterms[j] + "|";
		}
		return result;
	}

	public String getName() { return peptideName; }
	public double getScore() { return score; }
	public int[] getModIndices() { return modIndices; }
	public int[] getModOffsets() { return modOffsets; }
	public double getMassCalc() { return massCalc; }
	public double getMassDelta() { return massDelta; }
	public int getProteinCount() { return proteinIndices.length; }
	public int getProteinIndex(int index) { return proteinIndices[index]; }
	public int getStartPosition(int index) { return starts[index]; }
	public int getEndPosition(int index) { return ends[index]; }
	public char getNTerm(int index) { return nterms[index]; }
	public char getCTerm(int index) { return cterms[index]; }

	private String peptideName;
	private double score;
	private int[] modIndices;
	private int[] modOffsets;
	private double massCalc;
	private double massDelta;
	private int[] proteinIndices;
	private int[] starts;
	private int[] ends;
	private char[] nterms;
	private char[] cterms;
}
