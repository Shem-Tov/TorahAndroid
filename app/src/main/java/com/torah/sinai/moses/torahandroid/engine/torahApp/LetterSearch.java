package com.torah.sinai.moses.torahandroid.engine.torahApp;

import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.torah.sinai.moses.torahandroid.LongOperation;
import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.MatchLab;
import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;

import com.torah.sinai.moses.torahandroid.engine.frame.Frame;
import com.torah.sinai.moses.torahandroid.engine.hebrewLetters.HebrewLetters;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LastSearchClass;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LineHtmlReport;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LineReport;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO.fileMode;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.StringAlignUtils;

public class LetterSearch {
    private static LetterSearch instance;

    public static LetterSearch getInstance() {
        if (instance == null) {
            instance = new LetterSearch();
        }
        return instance;
    }

    static public Map<Character, Integer> toMap(String searchSTR) {
        ArrayList<Character> lst = new ArrayList<Character>();
        for (char c : searchSTR.toCharArray()) {
            lst.add(c);
        }
        // I do not remeber, but I think this function
        // counts all unique characters in searchSTR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return lst.stream().collect(HashMap::new, (map, str) -> {
                if (!map.containsKey(str)) {
                    map.put(str, 1);
                } else {
                    map.put(str, map.get(str) + 1);
                }
            }, HashMap::putAll);
        } else {
            return characterCount_OLD(searchSTR);
        }
    }

    static HashMap<Character, Integer> characterCount_OLD(String inputString) {
        // Creating a HashMap containing char
        // as a key and occurrences as  a value
        HashMap<Character, Integer> charCountMap
                = new HashMap<Character, Integer>();

        // Converting given string to char array

        char[] strArray = inputString.toCharArray();

        // checking each char of strArray
        for (char c : strArray) {
            if (charCountMap.containsKey(c)) {

                // If char is present in charCountMap,
                // incrementing it's count by 1
                charCountMap.put(c, charCountMap.get(c) + 1);
            } else {

                // If char is not present in charCountMap,
                // putting this char to charCountMap with 1 as it's value
                charCountMap.put(c, 1);
            }
        }

        // Printing the charCountMap
		/*
		for (Map.Entry entry : charCountMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		*/
        return charCountMap;
    }

    private Boolean containsLetters(String pLine, Map<Character, Integer> searchMap) {
        Map<Character, Integer> pLineMap = toMap(pLine);
        Boolean foundMatch = true;
        for (Character key : searchMap.keySet()) {
            if ((!pLineMap.containsKey(key)) || (searchMap.get(key) > pLineMap.get(key))) {
                foundMatch = false;
                break;
            }
        }

        return foundMatch;
    }

    private Boolean containsOrderOfLetters(String line, String searchSTR, Boolean exactSpaces) {
        Boolean found = true;
        int oldIndex = 0;
        int index = -1;
        int countCh = -1;
        for (char ch : searchSTR.toCharArray()) {
            countCh++;
            index = line.indexOf(ch, oldIndex);
            if (index == -1) {
                found = false;
                break;
            } else {
                // if exactSpaces, do if not a space and not first char
                if ((exactSpaces) && (countCh > 0) && (ch != ' ')) {
                    if (line.indexOf(' ', oldIndex) < index) {
                        found = false;
                        break;
                    }
                }
                oldIndex = index + 1;
            }
        }
        return found;
    }

    public void searchForLetters(Object[] args) {
        ArrayList<LineReport> results = new ArrayList<LineReport>();
        LastSearchClass searchRecord = new LastSearchClass();
        // String[][] results=null;
        BufferedReader inputStream = null;
        String searchSTR, searchSTR2 = "";
        String searchConvert, searchConvert2 = "";
        // modePsukim - false = checks letters in words
        // true = checks letters in psukim
        int mode = 0;
        // mode = 0 Words - Single
        // mode = 1 Words - Multiple
        // mode = 2 Psukim
        int[] searchRange;
        boolean bool_sofiot1 = false, bool_sofiot2 = false;
        boolean bool_keepOrder1 = false, bool_keepOrder2 = false;
        boolean bool_first1 = false, bool_last1 = false;
        boolean bool_first2 = false, bool_last2 = false;
        boolean bool_exact_spaces = false;
        // FileWriter outputStream2 = null;
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Missing Arguments in LetterSearch.searchForLetters");
            }
            searchSTR = (String) args[0];
            bool_sofiot1 = (args[1] != null) ? (Boolean) args[1] : true;
            searchConvert = (!bool_sofiot1) ? HebrewLetters.switchSofiotStr(searchSTR) : searchSTR;
            searchRange = (args[2] != null) ? (int[]) (args[2]) : (new int[]{0, 0});
            mode = (args[3] != null) ? (int) args[3] : 0;
            bool_keepOrder1 = (args[4] != null) ? (Boolean) args[4] : false;
            bool_first1 = (args[5] != null) ? (Boolean) args[5] : false;
            bool_last1 = (args[6] != null) ? (Boolean) args[6] : false;
            bool_exact_spaces = (args[7] != null) ? (Boolean) args[7] : false;
            if ((mode == 1) || (mode == 3)) {
                searchSTR2 = (String) args[8];
                bool_keepOrder2 = (args[9] != null) ? (Boolean) args[9] : false;
                bool_first2 = (args[10] != null) ? (Boolean) args[10] : false;
                bool_last2 = (args[11] != null) ? (Boolean) args[11] : false;
                bool_sofiot2 = (args[12] != null) ? (Boolean) args[12] : true;
                searchConvert2 = (!bool_sofiot2) ? HebrewLetters.switchSofiotStr(searchSTR2) : searchSTR2;
                if (searchSTR2.contains(" ")) {
                    if (ToraApp.isGui()) {
                        Frame.clearTextPane();
                    }
                    MainActivity.makeToast("מילה 2 - לא ניתן לחפש עם רווחים");
                    return;
                }
            }
            if ((mode == 0) || (mode == 1)) {
                if (searchSTR.contains(" ")) {
                    if (ToraApp.isGui()) {
                        Frame.clearTextPane();
                    }
                    MainActivity.makeToast("מילה 1 - לא ניתן לחפש עם רווחים");
                    return;
                }
            }
        } catch (ClassCastException e) {
            MainActivity.makeToast("casting exception...");
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        Map<Character, Integer> searchMap = toMap(searchConvert);
        Map<Character, Integer> searchMap2 = toMap(searchConvert2);
        WordCounter wCounter = new WordCounter();
        int countLines = 0;
        int count = 0;

        int countFileLines = -1;
        fileMode fMode = MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line);
        BufferedReader bReader = ManageIO.getBufferedReader(
                fMode, true);
        if (bReader == null) {
            MainActivity.makeToast("לא הצליח לפתוח קובץ תורה");
            return;
        }
        try {
            // System.out.println("Working Directory = " +
            // System.getProperty("user.dir"));
            inputStream = bReader;
            // outputStream2 = new FileWriter("/myText.txt", false);
            inputStream.mark(640000);
            count = 0;
//				outputStream.getBuffer().setLength(0);
            String line;
            // \u202A - Left to Right Formatting
            // \u202B - Right to Left Formatting
            // \u202C - Pop Directional Formatting
            String str = "\u202B" + "חיפוש אותיות";
            MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
            str = "\u202B" + "מחפש" + " \"" + searchSTR + ((mode == 1) ? (" | " + searchSTR2) : "") + "\"...";
            MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
            // Output.printText("");

            Output.markText(searchSTR + ((mode == 1) ? (" | " + searchSTR2) : ""),
                    ColorClass.headerStyleHTML);
            LongOperation.setLabel_countMatch("נמצא " + "0" + " פעמים");
            LongOperation.setFinalProgress(searchRange);
            // System.out.println(formatter.locale());
            while ((line = inputStream.readLine()) != null) {
                switch (fMode) {
                    case LastSearch:
                        countFileLines++;
                        countLines = LastSearchClass.getStoredLineNum(countFileLines);
                        break;
                    default:
                        countLines++;
                }

                if ((searchRange[1] != 0) && ((countLines <= searchRange[0]) || (countLines > searchRange[1]))) {
                    continue;
                }
                if ((ToraApp.isGui()) && (countLines % 25 == 0)) {
                    LongOperation.callProcess(countLines);
                }

                String[] splitStr;
                switch (mode) {
                    case 2:
                    case 3:
                        splitStr = new String[1];
                        splitStr[0] = line.trim();
                        break;
                    default:
                        bool_exact_spaces = false;
                        splitStr = line.trim().split("\\s+");
                }
                for (int i = 0; i < splitStr.length; i++) {
                    String s1 = splitStr[i];
                    // Do your stuff here
                    String sConvert;
                    sConvert = ToraApp.getHebrew(s1, bool_sofiot1);
                    if (((bool_first1) && (searchConvert.charAt(0) != sConvert.charAt(0)))
                            || ((bool_last1) && (!searchConvert.substring(searchConvert.length() - 1)
                            .equals(sConvert.substring(sConvert.length() - 1))))) {
                        continue;
                    }
                    // checks first last letters only of first text
                    // when mode is not 1 (word-multi)
                    if (((!bool_keepOrder1) && (containsLetters(sConvert, searchMap))) || ((bool_keepOrder1)
                            && containsOrderOfLetters(sConvert, searchConvert, bool_exact_spaces))) {
                        // Do nothing because first and last letters do not match
                        // printPasukInfo gets the Pasuk Info, prints to screen and sends back array to
                        // fill results array
                        ArrayList<Integer[]> prevIndexes = null;
                        switch (mode) {
                            case 0:
                                count++;
                                if (ToraApp.isGui()) {
                                    LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                                }
                                wCounter.addWord(s1);
                                if (fMode == fileMode.LastSearch) {
                                    prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                                }
                                results.add(Output.printPasukInfoExtraIndexes(countLines, s1, line, ColorClass.markupStyleHTML,
                                        bool_sofiot1, true, prevIndexes));
                                searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                                break;
                            case 1:
                            case 3:
                                String[] splitStr2;
                                splitStr2 = line.trim().split("\\s+");
                                for (int j = 0; j < splitStr2.length; j++) {
                                    String s2 = splitStr2[j];
                                    // Do your stuff here
                                    String sConvert2;
                                    sConvert2 = ToraApp.getHebrew(s2, bool_sofiot2);
                                    if (((bool_first2) && (searchConvert2.charAt(0) != sConvert2.charAt(0)))
                                            || ((bool_last2) && (!searchConvert2.substring(searchConvert2.length() - 1)
                                            .equals(sConvert2.substring(sConvert2.length() - 1))))) {
                                        continue;
                                    }
                                    if (((!bool_keepOrder2) && (containsLetters(sConvert2, searchMap2)))
                                            || ((bool_keepOrder2)
                                            && containsOrderOfLetters(sConvert2, searchConvert2, false))) {
                                        count++;
                                        if (ToraApp.isGui()) {
                                            LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                                        }
                                        wCounter.addWord(s2);
                                        switch (mode) {
                                            case 1:
                                                wCounter.addWord(s1);
                                                if (fMode == fileMode.LastSearch) {
                                                    prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                                                }
                                                results.add(Output.printPasukInfoExtraIndexes(countLines, s1, line,
                                                        ColorClass.markupStyleHTML, bool_sofiot1, true, bool_sofiot2,
                                                        s2, prevIndexes));
                                                searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                                                break;
                                            case 3:
                                                LineHtmlReport lineHtmlReport = new LineHtmlReport(null, null, null);
                                                if (bool_keepOrder1) {
                                                    lineHtmlReport = Output.markTextOrderedLettersInPasuk(searchSTR, line,
                                                            bool_sofiot1, bool_first1, bool_last1,
                                                            ColorClass.markupStyleHTML, j);
                                                } else {
                                                    lineHtmlReport = Output.markTextUnOrderedLettersInPasuk(searchSTR, line,
                                                            bool_sofiot1, bool_first1, bool_last1,
                                                            ColorClass.markupStyleHTML, j);
                                                }
                                                if (fMode == fileMode.LastSearch) {
                                                    prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                                                }
                                                results.add(ToraApp.returnBookInfoExtraIndexes(searchSTR, countLines, lineHtmlReport, prevIndexes));
                                                searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                                                break;
                                        }

                                    }
                                }
                                break;
                            case 2:
                                count++;
                                if (ToraApp.isGui()) {
                                    LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                                }
                                LineHtmlReport lineHtmlReport = new LineHtmlReport(null, null, null);
                                if (bool_keepOrder1) {
                                    lineHtmlReport = Output.markTextOrderedLettersInPasuk(searchSTR, line, bool_sofiot1,
                                            bool_first1, bool_last1, ColorClass.markupStyleHTML);
                                } else {
                                    lineHtmlReport = Output.markTextUnOrderedLettersInPasuk(searchSTR, line, bool_sofiot1,
                                            bool_first1, bool_last1, ColorClass.markupStyleHTML);
                                }
                                if (fMode == fileMode.LastSearch) {
                                    prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                                }
                                // returnBookInfoExtraIndexes also prints to screen
                                results.add(ToraApp.returnBookInfoExtraIndexes(searchSTR, countLines, lineHtmlReport, prevIndexes));
                                searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                                break;
                        }
                    }
                }
                if ((ToraApp.isGui()) && (SearchFragment.getMethodCancelRequest())) {
                    MainActivity.makeToast("\u202B" + "המשתמש הפסיק חיפוש באמצע");
                    break;
                }
            }
        } catch (

                Exception e) {
            MainActivity.makeToast("Error with at line " + countLines);
            e.printStackTrace();
        } finally {
            MatchLab.addEmpty();
            //Output.printText("");
            MatchLab.add("מילים שנמצאו","","לחץ כאן",wCounter.getWords());

            MatchLab.addEmpty();

            MatchLab.add(Output.markText(
                    "\u202B" + "נמצא " + "\"" + searchSTR + "\"" + "\u00A0" + count + " פעמים" + ".",
                    ColorClass.footerStyleHTML));
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
