package com.torah.sinai.moses.torahandroid.engine.console;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.torah.sinai.moses.torahandroid.engine.extras.extraFunctions;
import com.torah.sinai.moses.torahandroid.engine.extras.printFile;
import com.torah.sinai.moses.torahandroid.engine.torahApp.CountSearch;
import com.torah.sinai.moses.torahandroid.engine.torahApp.DilugWordPasuk;
import com.torah.sinai.moses.torahandroid.engine.torahApp.Dilugim;
import com.torah.sinai.moses.torahandroid.engine.torahApp.Gimatria;
import com.torah.sinai.moses.torahandroid.engine.torahApp.LetterSearch;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ReportTorahCount;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraSearch;

public class Methods {
	public interface MethodRunner {
		void run(Object... args) throws IOException;
	}

	public static final int id_searchWords=1;
	public static final int id_searchGimatria=2;
	public static final int id_calculateGimatria=3;
	public static final int id_searchDilugim=4;
	public static final int id_searchDilugWordPasuk=10;
	public static final int id_searchLetters=5;
	public static final int id_searchCount=6;
	public static final int id_settings=7;
	public static final int id_printTorah=8;
	public static final int id_ReportTorahCount=9;
	
	// Add method references here
	public static Map<Integer,MethodRunner> arrayMethods = new HashMap<Integer,MethodRunner>();
	private static Map<Integer,String> arrayMethodTitle = new TreeMap<Integer,String>();
	//private static List<Integer> arrayMethodID = new ArrayList<>();
	public static void arrayMethodCreator() throws IOException {
		getArrayMethodTitles().put(id_searchWords,"חיפוש מילים");
		arrayMethods.put(id_searchWords,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				ToraSearch tora = ToraSearch.getInstance();
				tora.searchWords(args);
			}
		});
		getArrayMethodTitles().put(id_searchGimatria,"חיפוש גימטריה");
		arrayMethods.put(id_searchGimatria,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				Gimatria gim = Gimatria.getInstance();
				gim.searchGimatria(args);
			}
		});
		getArrayMethodTitles().put(id_calculateGimatria,"חישוב גימטריה");
		arrayMethods.put(id_calculateGimatria,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				Gimatria.callCalculateGimatria(args);
			}
		});
		getArrayMethodTitles().put(id_searchDilugim,"חיפוש דילוגים");
		arrayMethods.put(id_searchDilugim,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				Dilugim dilug = Dilugim.getInstance();
				dilug.searchWordsDilugim(args);
			}
		});
		//getArrayMethodTitle().put(id_searchDilugWordPasuk,"חיפוש דילוגים");
		arrayMethods.put(id_searchDilugWordPasuk,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				DilugWordPasuk dilug = DilugWordPasuk.getInstance();
				dilug.searchDilugWordPasuk(args);
			}
		});
		getArrayMethodTitles().put(id_searchLetters,"חיפוש אותיות");
		arrayMethods.put(id_searchLetters,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				LetterSearch letter = LetterSearch.getInstance();
				letter.searchForLetters(args);
			}
		});
		getArrayMethodTitles().put(id_searchCount,"חיפוש הופעה ספציפית בתורה");
		arrayMethods.put(id_searchCount,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				CountSearch cSearch = CountSearch.getInstance();
				cSearch.searchByCount(args);
			}
		});
		getArrayMethodTitles().put(id_settings,"אפשרויות");
		arrayMethods.put(id_settings,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				Menu.menuSettings();
			}
		});
		getArrayMethodTitles().put(id_printTorah,"הדפסת תורה");
		arrayMethods.put(id_printTorah,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				printFile.printTorah(args);
			}
		});
		//getArrayMethodTitle().put(id_ReportTorahCount,"דוח ספירה בתורה");
		arrayMethods.put(id_ReportTorahCount,new MethodRunner() {
			@Override
			public void run(Object... args) throws IOException {
				ReportTorahCount tCount = ReportTorahCount.getInstance();
				tCount.createReport(args);
			}
		});
	}
	public static Map<Integer,String> getArrayMethodTitles() {
		return arrayMethodTitle;
	}
	public static String getArrayMethodTitleString(int num) {
		if (arrayMethodTitle.get(num)!=null) {
			return arrayMethodTitle.get(num);
		} else {
			return "";
		}
	}
}
