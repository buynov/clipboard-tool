package com.buynov.clipboard;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public class MappingHandler {

	public static final String MAPPING_FILE_NAME="cyr_to_lat_mapping.properties";
	
	private static Properties mappings = new Properties();
	
	static {
		try {
			mappings.load(MappingHandler.class.getResourceAsStream(MAPPING_FILE_NAME));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static Collection<String> getCharactersForReplacement() {
		return mappings.stringPropertyNames();
	}
	
	public static String getReplacementForCharacter(String s) {
		return mappings.getProperty(s);
	}
}
