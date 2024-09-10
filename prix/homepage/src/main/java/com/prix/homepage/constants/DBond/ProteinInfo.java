package com.prix.homepage.constants.DBond;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

@Data
@ToString
public class ProteinInfo extends Base {
	/*public ProteinInfo() {
		numberOfMatchedPeptides = -1;
	}*/

	ProteinInfo(String proteinName, String proteinDescription, String proteinSequence) {
		name = proteinName;
		description = proteinDescription;
		sequence = proteinSequence;
		indices = new int[0];
		offsets = new int[0];
		positions = new int[0];
		//numberOfMatchedPeptides = -1;
	}

	ProteinInfo(String proteinName, int index, int offset, int position) {
		name = proteinName;
		indices = new int[1];
		offsets = new int[1];
		positions = new int[1];
		indices[0] = index;
		offsets[0] = offset;
		positions[0] = position;
		//numberOfMatchedPeptides = -1;
	}

	public void add(int index, int offset, int position) {
		int length = 0;
		if (indices != null)
			length = indices.length;
		int[] newIndices = new int[length + 1];
		int[] newOffsets = new int[length + 1];
		int[] newPositions = new int[length + 1];
		if (length > 0)
		{
			System.arraycopy(indices, 0, newIndices, 0, indices.length);
			System.arraycopy(offsets, 0, newOffsets, 0, offsets.length);
			System.arraycopy(positions, 0, newPositions, 0, positions.length);
		}
		newIndices[length] = index;
		newOffsets[length] = offset;
		newPositions[length] = position;
		indices = newIndices;
		offsets = newOffsets;
		positions = newPositions;
	}

	public void setInfo(String proteinName, String proteinDescription, String proteinSequence) {
		name = proteinName;
		description = proteinDescription;
		sequence = proteinSequence;
	}

	public void read(Reader reader) {
		name = getToken(reader, ' ');
		description = getToken(reader);
		sequence = getToken(reader);
		int count = getInteger(reader);
		indices = new int[count];
		offsets = new int[count];
		positions = new int[count];
		for (int i = 0; i < count; i++)
		{
			indices[i] = getInteger(reader);
			offsets[i] = getInteger(reader);
			positions[i] = getInteger(reader);
		}
	}

	public String write() {
		String result = name + " " + description + "|" + sequence + "|" + indices.length + "|";
		for (int i = 0; i < indices.length; i++)
		{
			result += indices[i] + "|" + offsets[i] + "|" + positions[i] + "|";
		}
		return result;
	}

/*	public void calculateHits(ProteinSummary summary) {
		if (hits == null)
		{
			int len = 0;
			if (sequence != null)
				len = sequence.length();
			hits = new boolean[len];

			if (len > 0)
			{
				for (int i = 0; i < indices.length; i++)
				{
					SpectrumInfo spectrum = summary.getSpectrum(indices[i]);
					PeptideInfo peptide = spectrum.getPeptide(offsets[i]);
					int start = peptide.getStartPosition(positions[i]);
					int end = peptide.getEndPosition(positions[i]);
					if (start >= len)
						start = len - 1;
					if (end >= len)
						end = len - 1;
					for (int j = start; j <= end; j++)
						hits[j] = true;
				}
			}
		}
	}

	public void calculateNumberOfMatchedPeptides(ProteinSummary summary) {
		if (numberOfMatchedPeptides < 0)
		{
			ArrayList<String> peptideList = new ArrayList<String>();
			for (int i = 0; i < offsets.length; i++)
			{
				SpectrumInfo spectrum = summary.getSpectrum(indices[i]);
				PeptideInfo peptide = spectrum.getPeptide(offsets[i]);
				if (peptideList.indexOf(peptide.getName()) == -1)
					peptideList.add(peptide.getName());
			}
			numberOfMatchedPeptides = peptideList.size();
		}
	}
*/
	public void makePeptideLines(ProteinSummary summary, Modification[] mods, double minScore, boolean second, int proteinIndex, boolean useTag) {
		int size = summary.getNumberOfQueries();
		ArrayList<PeptideLine> peptideList = new ArrayList<PeptideLine>();
		//ArrayList<PeptideLine> secondPeptideList = new ArrayList<PeptideLine>();
		PeptideLine[][] localPeptides = new PeptideLine[size][];
		//PeptideLine[] localSecondPeptides = new PeptideLine[size];
		ProteinInfo[] proteins = summary.getProteins();

		for (int j = 0; j < indices.length; j++)
		{
			int index = indices[j];
			SpectrumInfo spectrum = summary.getSpectrum(index);
			PeptideInfo peptide = spectrum.getPeptide(offsets[j]);

			if ((peptide.getScore() < minScore) || (localPeptides[index] != null) && (localPeptides[index][0].getScore() > peptide.getScore()))
				continue;

/*			int pi = spectrum.getProteinIndex();
			if (pi >= 0)
			{
				ProteinInfo protein = summary.getProtein(pi);
				if (proteinIndex != pi)
				{
					PeptideLine line = protein.getPeptideLineBySpectrum(index);
					if (line.getScore() > peptide.getScore())
						continue;
					else if (line.getScore() < peptide.getScore())
						protein.deletePeptideLine(index);
				}
			}
*/
			boolean higherFound = false;
			for (int k = 0; k < proteins.length; k++)
			{
				ProteinInfo protein = proteins[k];
				if (proteinIndex != k && protein != null)
				{
					PeptideLine line = protein.getPeptideLineBySpectrum(index);
					if (line == null)
						continue;
					if (line.getScore() > peptide.getScore())
					{
						higherFound = true;
						break;
					}
					else if (line.getScore() < peptide.getScore())
						protein.deletePeptideLine(index);
				}
			}
			if (higherFound)
				continue;

			String mod = "";
			int[] modIndices = peptide.getModIndices();
			int[] modOffsets = peptide.getModOffsets();
			if (modIndices.length > 0)
			{
				boolean first = true;
				for (int k = 0; k < modIndices.length; k++)
				{
					if (!second || modOffsets[k] != 0)
					{
						if (!first)
							mod += "; ";
						first = false;
						mod += mods[modOffsets[k]].getType() + " (" + peptide.getName().charAt(modIndices[k]-1)+modIndices[k] + ")";
					}
				}
			}

			StringBuffer name = new StringBuffer(peptide.getName());
			name.insert(0, peptide.getNTerm(positions[j]));
			name.insert(1, '.');
			name.append('.');
			name.append(peptide.getCTerm(positions[j]));
			Arrays.sort(modIndices);
			if (useTag)
			{
				for (int k = modIndices.length - 1; k >= 0; k--)
				{
					if (second && modOffsets[k] == 0)
						name.insert(modIndices[k] + 2, "</u>*");
					else
						name.insert(modIndices[k] + 2, "</u>");
					name.insert(modIndices[k] + 1, "<u>");
				}
			}

			if (second && (localPeptides[index] != null) && (localPeptides[index][0].getScore() == peptide.getScore()))
				//localSecondPeptides[index] = new PeptideLine(index, spectrum.getWeight(), spectrum.getCharge(), peptide.getMassCalc(), peptide.getMassDelta(), peptide.getScore(), peptide.getStartPosition(positions[j]), peptide.getEndPosition(positions[j]), name.toString(), mod, proteinIndex);
				localPeptides[index][localPeptides[index].length - 1].setSecond(new PeptideLine(index, spectrum.getWeight(), spectrum.getCharge(), peptide.getMassCalc(), peptide.getMassDelta(), peptide.getScore(), peptide.getStartPosition(positions[j]), peptide.getEndPosition(positions[j]), name.toString(), mod, proteinIndex));
			else if ((localPeptides[index] != null) && (localPeptides[index][0].getScore() == peptide.getScore()) && (localPeptides[index][0].getPeptide().compareTo(name.toString()) == 0) && (localPeptides[index][0].getStart() != peptide.getStartPosition(positions[j]) || localPeptides[index][0].getEnd() != peptide.getEndPosition(positions[j])) || (localPeptides[index] == null))
			{
				int ind = 0;
				if (localPeptides[index] == null)
					localPeptides[index] = new PeptideLine[1];
				else
				{
					ind = localPeptides[index].length;
					PeptideLine[] lines = new PeptideLine[ind + 1];
					System.arraycopy(localPeptides[index], 0, lines, 0, ind);
					localPeptides[index] = lines;
				}
				localPeptides[index][ind] = new PeptideLine(index, spectrum.getWeight(), spectrum.getCharge(), peptide.getMassCalc(), peptide.getMassDelta(), peptide.getScore(), peptide.getStartPosition(positions[j]), peptide.getEndPosition(positions[j]), name.toString(), mod, proteinIndex);
				spectrum.setProteinIndex(proteinIndex);
			}
		}

		for (int j = 0; j < size; j++)
		{
			if (localPeptides[j] != null)
			{
				for (int k = 0; k < localPeptides[j].length; k++)
					peptideList.add(localPeptides[j][k]);
					//secondPeptideList.add(localSecondPeptides[j]);
			}
		}
		peptides = peptideList.toArray(new PeptideLine[0]);
		//secondPeptides = secondPeptideList.toArray(new PeptideLine[0]);
	}

	public void deletePeptideLine(int index) {
		int size = 0;
		for (int i = 0; i < peptides.length; i++)
		{
			if (peptides[i].getIndex() != index)
				size++;
		}
		PeptideLine[] newPeptides = new PeptideLine[size];
		size = 0;
		for (int i = 0; i < peptides.length; i++)
		{
			if (peptides[i].getIndex() != index)
				newPeptides[size++] = peptides[i];
		}
		peptides = newPeptides;
	}

	public boolean[] getCoverageCode() {
		int len = 0;
		if (sequence != null)
			len = sequence.length();
		boolean[] hits = new boolean[len];

		if (peptides != null && len > 0)
		{
			for (int i = 0; i < peptides.length; i++)
			{
			/*	int start = peptides[i].getStart();
				int end = peptides[i].getEnd();
				if (start >= len)
					start = len - 1;
				if (end >= len)
					end = len - 1;
				for (int j = start; j <= end; j++)
					hits[j] = true;//*/

				int start = peptides[i].getStart()-1;
				int end = peptides[i].getEnd();
				if ( start > len-1 )
					start = len - 1;
				if (end > len)
					end = len - 1;
				for (int j = start; j < end; j++)
					hits[j] = true;//*/

				if( peptides[i].getSecond() != null ){
					start = peptides[i].getSecond().getStart()-1;
					end = peptides[i].getSecond().getEnd();
					if ( start > len-1 )
						start = len - 1;
					if (end > len)
						end = len - 1;
					for (int j = start; j < end; j++)
						hits[j] = true;//*/
				}
			}
		}

		return hits;
	}

	public int getNumberOfMatchedPeptides() {
		int count = 0;
		if (peptides != null)
		{
			ArrayList<String> peptideList = new ArrayList<String>();
			for (int i = 0; i < peptides.length; i++)
			{
				String peptide = peptides[i].getPeptide().substring(2, peptides[i].getPeptide().length() - 2) + peptides[i].getModification();
				if (peptideList.indexOf(peptide) == -1)
					peptideList.add(peptide);
				PeptideLine subPeptide = peptides[i].getSecond();
				if (subPeptide != null)
				{
					peptide = subPeptide.getPeptide().substring(2, subPeptide.getPeptide().length() - 2) + subPeptide.getModification();
					if (peptideList.indexOf(peptide) == -1)
						peptideList.add(peptide);
				}
			}
			count = peptideList.size();
		}
		return count;
	}

	public PeptideLine[] getPeptideLines() {
		return peptides;
	}

	public PeptideLine getPeptideLineBySpectrum(int index) {
		if (peptides == null)
			return null;

		for (int i = 0; i < peptides.length; i++)
			if (peptides[i].getIndex() == index)
				return peptides[i];

		return null;
	}

	//public PeptideLine[] getSecondPeptideLines() {
	//	return secondPeptides;
	//}

	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getSequence() { return sequence; }
	public int[] getIndices() { return indices; }
	public int[] getOffsets() { return offsets; }
	public int[] getPositions() { return positions; }

	private String name;
	private String description;
	private String sequence;
	private int[] indices;
	private int[] offsets;
	private int[] positions;
	//private boolean[] hits;
	//private int numberOfMatchedPeptides;
	private PeptideLine[] peptides;
	//private PeptideLine[] secondPeptides;
}
