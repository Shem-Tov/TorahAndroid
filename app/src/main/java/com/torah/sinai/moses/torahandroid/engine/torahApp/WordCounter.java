package com.torah.sinai.moses.torahandroid.engine.torahApp;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;


public class WordCounter {

	final Integer ONE = 1;
	Hashtable<String, Integer> map = new Hashtable<String, Integer>();
	
	public WordCounter() {
		//Hashtable<String, Integer> map = new Hashtable<String, Integer>();
	}
	
	public String getWords() {
		// Gets keys
		Enumeration<String> e = map.keys();
        String str="";
		// Alphabetizes keys
		List<String> list = Collections.list(e);
        Collections.sort(list);
        for (String key:list) {
        	str +=key + " : " + map.get(key)+"<br>";
        }
        return str;
	}

	public void addWord(String word) {
		String wordTrim = word.trim();
		Object obj = this.map.get(wordTrim);
		if (obj == null) {
			map.put(wordTrim, ONE);
		} else {
			int i = ((Integer) obj).intValue() + 1;
			map.put(wordTrim, i);
		}
	}
}
