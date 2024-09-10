package com.prix.homepage.constants.DBond;

import java.io.StringWriter;
import java.io.Reader;

public class Base {
	final static protected char DELIMITER = '|';

	protected String getToken(Reader reader) {
		return getToken(reader, DELIMITER);
	}

	protected String getToken(Reader reader, char delimiter) {
		StringWriter writer = new StringWriter();
		try {
			char c = (char)reader.read();
			while (c != delimiter)
			{
				writer.write(c);
				c = (char)reader.read();
			}
		}
		catch (java.io.IOException e) {

		}
		return writer.toString();
	}

	protected int getInteger(Reader reader) {
		return Integer.parseInt(getToken(reader));
	}

	protected double getDouble(Reader reader) {
		return Double.parseDouble(getToken(reader));
	}
}
