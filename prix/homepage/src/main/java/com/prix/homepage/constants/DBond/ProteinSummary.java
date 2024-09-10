package com.prix.homepage.constants.DBond;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;


@ToString
@Slf4j
@Data
public class
ProteinSummary extends Base {
	public static void main(String[] args) throws IOException {
		ProteinSummary summary = new ProteinSummary();
//		File file = new File("ms_4_1630028173643.prix");
		File file = new File("ms_128_1637088585736.prix");
		InputStream is = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(is);
		summary.read(reader);

		System.out.println("Date: " + summary.getDate());
		System.out.println("User: " + summary.getUserName());
		System.out.println("Title: " + summary.getTitle());
		for(int i=0; i<summary.getSpectrums().length; i++) {
			System.out.println(summary.getSpectrum(i).write());
		}
		
		
		file = new File("Lens.fasta");
		is = new FileInputStream(file);
		summary.readProtein(is);
	}

	public void read(String fileName) {
		read(fileName, true);
	}

	public void read(String fileName, boolean useDatabase) {
		try {
			FileReader reader = new FileReader(fileName);
			readHeader(reader);
			//numberOfQueries = getInteger(reader);
			int count = getInteger(reader);
			spectrums = new SpectrumInfo[count];
			for (int i = 0; i < count; i++)
			{
				spectrums[i] = new SpectrumInfo();
				spectrums[i].read(reader);
			}
			if (runMODMap)
			{
				count = getInteger(reader);
				modMaps = new int[count][];
				for (int i = 0; i < count; i++)
				{
					modMaps[i] = new int[numModMapColumns];
					for (int j = 0; j < numModMapColumns; j++)
						modMaps[i][j] = getInteger(reader);
				}
			}
			reader.close();
			readProtein(useDatabase);
		}
		catch (java.io.FileNotFoundException e) {
		}
		catch (IOException e) {
		}
	}

	public void read(Reader reader) {
		try {
			readHeader(reader);
			int count = getInteger(reader);
			spectrums = new SpectrumInfo[count];
			for (int i = 0; i < count; i++)
			{
				spectrums[i] = new SpectrumInfo();
				spectrums[i].read(reader);
			}
			if (runMODMap)
			{
				count = getInteger(reader);
				modMaps = new int[count][];
				for (int i = 0; i < count; i++)
				{
					modMaps[i] = new int[numModMapColumns];
					for (int j = 0; j < numModMapColumns; j++)
						modMaps[i][j] = getInteger(reader);
				}
			}
			if (targetDecoyed)
			{
				count = getInteger(reader);
				decoyScores = new double[count];
				decoyHits = new int[count];
				for (int i = 0; i < count; i++)
				{
					decoyScores[i] = getDouble(reader);
					decoyHits[i] = getInteger(reader);
				}
			}
			reader.close();
		}
		catch (java.io.FileNotFoundException e) {
			System.out.println("ProteinSummary.java is a problem");
		}
		catch (IOException e) {
			System.out.println("ProteinSummary.java is a problem");
		}
	}

	public void readHeader(Reader reader) {
		engineName = getToken(reader);
		version = getToken(reader);
		double versionNumber = Double.parseDouble(version);
		date = getToken(reader);
		userName = getToken(reader);
		title = getToken(reader);
		fileName = getToken(reader);
		filePath = getToken(reader);
		fileType = getToken(reader);
		instrumentName = getToken(reader);
		if (versionNumber >= 2.05) {
			msResolution = getToken(reader);
			msmsResolution = getToken(reader);
		}
		databaseName = getToken(reader);
		databasePath = getToken(reader);
		numProteins = getInteger(reader);
		numResidues = getInteger(reader);
		targetDecoyed = (getInteger(reader) == 1);
		decoyFilePath = getToken(reader);
		decoyIndicator = getToken(reader);
		enzymeName = getToken(reader);
		maxMissedCleavages = getInteger(reader);
		minTerminiNumber = getInteger(reader);
		proteinMassMin = getDouble(reader);
		proteinMassMax = getDouble(reader);
		peptideTolerance = getDouble(reader);
		ptUnit = getToken(reader);
		fragmentTolerance = getDouble(reader);
		ftUnit = getToken(reader);
		if (versionNumber >= 2.05) {
			cysteinAlkylation = (getInteger(reader) == 1);
			alkylationName = getToken(reader);
			alkylationMass = getDouble(reader);
		}
		runMODMap = (getInteger(reader) == 1);
		pathMODMap = getToken(reader);
		if (versionNumber >= 2.05) {
			multiStage = (getInteger(reader) == 1);
			searchProgramPath = getToken(reader);
		}
		int count = getInteger(reader);
		aminoAcids = new AminoAcid[count];
		for (int i = 0; i < count; i++)
		{
			aminoAcids[i] = new AminoAcid();
			aminoAcids[i].read(reader);
		}
		count = getInteger(reader);
		fixedModifications = new Modification[count];
		for (int i = 0; i < count; i++)
		{
			fixedModifications[i] = new Modification();
			fixedModifications[i].read(reader);
		}
		count = getInteger(reader);
		modifications = new Modification[count];
		for (int i = 0; i < count; i++)
		{
			modifications[i] = new Modification();
			modifications[i].read(reader);
		}
		
	}

	public void readProtein(boolean useDatabase)
	{
		FileInputStream fsProtein = null;
		if (useDatabase)
		{
			try {
				fsProtein = new FileInputStream (databaseName);
			} catch (java.io.FileNotFoundException e) {
				
			}
		}
		readProtein(fsProtein);
	}

	public void readProtein(InputStream is) {
		try {
			ArrayList<Integer> proteinIndexList = new ArrayList<Integer>();
			ArrayList<ProteinInfo> proteinList = new ArrayList<ProteinInfo>();

			int max = 0;

			// construct protein info
			for (int i = 0; i < spectrums.length; i++)
			{
				PeptideInfo[] peptides = spectrums[i].getPeptides();
				for (int j = 0; j < peptides.length; j++)
				{
					int size = peptides[j].getProteinCount();
					for (int k = 0; k < size; k++)
					{
						int index;
						int value = peptides[j].getProteinIndex(k);
						Integer valueInt = value;
						if ((index = proteinIndexList.indexOf(valueInt)) == -1)
						{
							if (max < value)
								max = value;
							proteinIndexList.add(valueInt);
							ProteinInfo info = new ProteinInfo("", i, j, k);
							log.info("add Protein = {}",info);
							proteinList.add(info);
						}
						else
						{
							ProteinInfo info = proteinList.get(index);
							info.add(i, j, k);
							log.info("add Protein2 = {}",info);
							proteinList.set(index, info);
						}
					}
				}
			}
			if (is != null)
			{
				log.info("not null");
				int size = is.available();
				StringWriter tempWriter = new StringWriter();
				boolean isKey = false;
				String key = "", desc = "";
				int count = 0;
				for (int i = 0; i < size; i++)
				{
					char c = (char)is.read();
					if (c != '\n' && c != '\r')
					{
						if (c == '>' && !isKey)
						{
							if (key.length() > 0)
							{
							//	System.out.println("[" + count + "] " + key + " : " + desc);
								int index = proteinIndexList.indexOf(count);
								if (index >= 0)
								{
									ProteinInfo info = proteinList.get(index);
									info.setInfo(key, desc, tempWriter.toString());
									proteinList.set(index, info);
								}
								tempWriter = new StringWriter();
							}
							isKey = true;
							key = "";
							desc = "";
							count++;
						}
						else
						{
							if (isKey && c == ' ' && key.length() == 0)
							{
								key = tempWriter.toString();
								tempWriter = new StringWriter();
							}
							else if( isKey || Character.isLetter(c) )
								tempWriter.write(c);
						}
					}
					else if (isKey)
					{
						if (key.length() == 0)
							key = tempWriter.toString();
						else
							desc = tempWriter.toString();
						//if (key.charAt(key.length() - 1) == '\n');
						//	key = key.substring(0, key.length() - 1);
						isKey = false;
						tempWriter = new StringWriter();
					}
				}
//				System.out.println("[" + count + "] " + key + " : " + desc);
				int index = proteinIndexList.indexOf(count++);
				if (index >= 0)
				{
					ProteinInfo info = proteinList.get(index);
					info.setInfo(key, desc, tempWriter.toString());
					proteinList.set(index, info);
				}
				is.close();

				max = count;				
			}

			proteins = new ProteinInfo[max];
			for (int i = 0; i < proteinList.size(); i++)
			{
				proteins[proteinIndexList.get(i).intValue()] = proteinList.get(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			proteins = new ProteinInfo[0];
		}
	}

	public String getEngineName() { return engineName; }
	public String getVersion() { return version; }
	public String getDate() { return date; }
	public String getUserName() { return userName; }
	public String getTitle() { return title; }
	public String getFileName() { return fileName; }
	public String getFilePath() { return filePath; }
	public String getFileType() { return fileType; }
	public String getInstrumentName() { return instrumentName; }
	public String getMSResolution() { return msResolution; }
	public String getMSMSResolution() { return msmsResolution; }
	public String getDatabaseName() { return databaseName; }
	public String getDatabasePath() { return databasePath; }
	public int getNumberOfUserProteins() { return numProteins; }
	public int getNumberOfUserResidues() { return numResidues; }
	public boolean isTargetDecoyed() { return targetDecoyed; }
	public String getDecoyFilePath() { return decoyFilePath; }
	public String getDecoyIndicator() { return decoyIndicator; }
	public String getEnzymeName() { return enzymeName; }
	public int getMaxMissedCleavages() { return maxMissedCleavages; }
	public int getMinTerminiNumber() { return minTerminiNumber; }
	public double getProteinMassMin() { return proteinMassMin; }
	public double getProteinMassMax() { return proteinMassMax; }
	public double getPeptideTolerance() { return peptideTolerance; }
	public String getPTUnit() { return ptUnit; }
	public double getFragmentTolerance() { return fragmentTolerance; }
	public String getFTUnit() { return ftUnit; }
	public boolean cysteinAlkylated() { return cysteinAlkylation; }
	public String getAlkylationName() { return alkylationName; }
	public double getAlkylationMass() { return alkylationMass; }
	public boolean isMODMapRun() { return runMODMap; }
	public String getMODMapPath() { return pathMODMap; }
	public boolean isMultiStage() { return multiStage; }
	public String getSearchProgramPath() { return searchProgramPath; }
	public int getNumberOfQueries() { return spectrums.length; }
	public AminoAcid[] getAminoAcids() { return aminoAcids; }
	public Modification[] getFixedModifications() { return fixedModifications; }
	public Modification[] getModifications() { return modifications; }
	public SpectrumInfo[] getSpectrums() { return spectrums; }
	public SpectrumInfo getSpectrum(int index) { return spectrums[index]; }
	public SpectrumInfo getSpectrumByID(int id) {
		for (int i = 0; i < spectrums.length; i++)
			if (spectrums[i].getId() == id)
				return spectrums[i];
		return null;
	}
	public ProteinInfo[] getProteins() { return proteins; }
	public int getNumberOfProteins() { return proteins.length; }
	public ProteinInfo getProtein(int index) { return proteins[index]; }
	public int getModMapCount() { return (modMaps == null) ? 0 : modMaps.length; }
	public int[] getModMap(int index) { return (modMaps == null) ? null : modMaps[index]; }
	public int[][] getModMaps() { return modMaps; }
	public double[] getDecoyScores() { return decoyScores; }
	public int[] getDecoyHits() { return decoyHits; }

	private String engineName;
	private String version;
	private String date;
	private String userName;
	private String title;
	private String fileName;
	private String filePath;
	private String fileType;
	private String instrumentName;
	private String msResolution;
	private String msmsResolution;
	private String databaseName;
	private String databasePath;
	private int numProteins;
	private int numResidues;
	private boolean targetDecoyed;
	private String decoyFilePath;
	private String decoyIndicator;
	private String enzymeName;
	private int maxMissedCleavages;
	private int minTerminiNumber;
	private double proteinMassMin;
	private double proteinMassMax;
	private double peptideTolerance;
	private String ptUnit;
	private double fragmentTolerance;
	private String ftUnit;
	private boolean cysteinAlkylation;
	private String alkylationName;
	private double alkylationMass;
	private boolean runMODMap;
	private String pathMODMap;
	private boolean multiStage;
	private String searchProgramPath;
	private AminoAcid[] aminoAcids;
	private Modification[] fixedModifications;
	private Modification[] modifications;
	//private int numberOfQueries;
	private SpectrumInfo[] spectrums;
	private ProteinInfo[] proteins;
	private int[][] modMaps;
	private double[] decoyScores;
	private int[] decoyHits;

	final private int numModMapColumns = 22;
}
