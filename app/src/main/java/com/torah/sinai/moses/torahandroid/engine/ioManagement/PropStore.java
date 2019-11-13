package com.torah.sinai.moses.torahandroid.engine.ioManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.R;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;

public class PropStore {
	private static final String fileName = ToraApp.resourceFolder + "config.properties";
	public static Map<String, String> map = new HashMap<String, String>();
	public static final String searchWord = "searchWord";
	public static final String searchWord2 = "searchWord2";
	public static final String bool_wholeWord = "bool_wholeWord";
	public static final String searchGmt = "searchGmt";
	public static final String bool_gimatriaSofiot = "bool_gimatriaSofiot";
	public static final String bool_countPsukim = "count_psukim";
	public static final String minDilug = "minDilug";
	public static final String maxDilug = "maxDilug";
	public static final String paddingDilug = "paddingDilug";
	public static final String countSearchIndex = "countSearchIndex";
	public static final String subTorahTablesFile = "subTorahTables";
	public static final String subTorahLineFile = "subTorahLineFile";
	public static final String subTorahLettersFile = "subTorahLettersFile";
	public static final String differentSearchFile = "differentSearchFile";
	public static final String bool_TorahTooltip = "bool_TorahTooltip";
	public static final String bgColor = "bgColor";
	public static final String mainHtmlColor = "mainHtmlColor";
	public static final String markupHtmlColor = "markupHtmlColor";
	public static final String bool_createExcel = "createExcel";
	public static final String bool_createTree = "createTree";
	public static final String bool_createDocument = "createDocument";
	public static final String dataFolder = "dataFolder";
	public static final String bool_letterOrder1 = "letterOrder1";
	public static final String bool_letterOrder2 = "letterOrder2";
	public static final String bool_first1 = "first1";
	public static final String bool_first2 = "first2";
	public static final String bool_last1 = "last1";
	public static final String bool_last2 = "last2";
	public static final String mode_main_number = "mainMode";
	public static final String mode_sub_letter = "subModeLetter";
	public static final String mode_sub_search = "subModeSearch";
	public static final String mode_sub_dilugim = "subModeDilugim";
	public static final String bool_search_Multi = "searchMulti";
	public static final String fontSize = "fontSize";
	public static final String frameHeight = "frameHeight";
	public static final String frameWidth = "frameWidth";
	public static final String splitPaneDivide = "splitPaneDivide";
	
	public static void addNotNull(String key, String value) {
		if (value != null) {
			map.put(key, value);
		}
	}

	public static void store() {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			File outputFile;
			try {
				outputFile = new File(fileName);
				outputFile.createNewFile(); // if file already exists will do nothing
				output = new FileOutputStream(outputFile);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// set the properties value
			prop.putAll(map);

			// save properties to project root folder
			// prop.putAll(map);
			prop.store(output, null);
		} catch (IOException | NullPointerException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void load() {

		Properties prop = new Properties();
		InputStreamReader input = null;
		for (int i = 1; i < 2; i++) {
			try {
				if (i == 1) {
					InputStream inputStream = MainActivity.getInstance()
							.getApplicationContext().getResources().openRawResource(R.raw.config);

					//file = new File(ClassLoader.getSystemResource(fileName).toURI());
					//input = new InputStreamReader(PropStore.class.getClassLoader().getResourceAsStream(fileName));
					map = readPropertiesFileAsMap(inputStream, "=");
					// get the property value and print it out
					// System.out.println(prop.getProperty("database"));
				}
			} catch (IOException | IllegalArgumentException | NullPointerException ex) {
				
				if (i == 1) {
					MainActivity.makeToast("Could not open config file");
					// ex.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads a "properties" file, and returns it as a Map
	 * (a collection of key/value pairs).
	 *
	 * @param fileStream  The properties InputStream to read.
	 * @param delimiter The string (or character) that separates the key
	 *                  from the value in the properties file.
	 * @return The Map that contains the key/value pairs.
	 * @throws Exception
	 */
	public static Map<String, String> readPropertiesFileAsMap(InputStream fileStream, String delimiter)
			throws Exception
	{
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
		String line;
		while ((line = reader.readLine()) != null)
		{
			if (line.trim().length()==0) continue;
			if (line.charAt(0)=='#') continue;
			// assumption here is that proper lines are like "String : http://xxx.yyy.zzz/foo/bar",
			// and the ":" is the delimiter
			int delimPosition = line.indexOf(delimiter);
			String key = line.substring(0, delimPosition-1).trim();
			String value = line.substring(delimPosition+1).trim();
			map.put(key, value);
		}
		reader.close();
		return map;
	}
}
