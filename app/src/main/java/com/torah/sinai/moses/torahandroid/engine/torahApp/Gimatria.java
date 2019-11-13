package com.torah.sinai.moses.torahandroid.engine.torahApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.torah.sinai.moses.torahandroid.LongOperation;
import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.MatchLab;
import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;

import com.torah.sinai.moses.torahandroid.engine.ioManagement.LastSearchClass;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LineReport;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO.fileMode;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.StringAlignUtils;

public class Gimatria {
    private static Gimatria instance;

    public static Gimatria getInstance() {
        if (instance == null) {
            instance = new Gimatria();
        }
        return instance;
    }

    private static char[] hLetters = {'א', 'ב', 'ג', 'ד', 'ה', 'ו', 'ז', 'ח', 'ט', 'י', 'כ', 'ל', 'מ', 'נ', 'ס', 'ע',
            'פ', 'צ', 'ק', 'ר', 'ש', 'ת'};
    private static char[] endLetters = {'ך', 'ם', 'ן', 'ף', 'ץ'};

    public static int calculateGimatriaLetter(char c, Boolean boolSofiot) {
        int index = new String(hLetters).indexOf(c);
        if (index > -1) {
            // =10^(ROUNDDOWN(A6 / 9))*(MOD(A6,9)+1)
            return (int) (Math.pow(10, index / 9) * ((index % 9) + 1));
        } else {
            index = new String(endLetters).indexOf(c);
            if (index > -1) {
                if (boolSofiot) {
                    return (index) * 100 + 500;
                } else {
                    int sum = 0;
                    switch (index) {
                        case 0:
                            sum = 20;
                            break;
                        case 1:
                            sum = 40;
                            break;
                        case 2:
                            sum = 50;
                            break;
                        case 3:
                            sum = 80;
                            break;
                        case 4:
                            sum = 90;
                    }
                    return sum;
                }
            }
        }
        return 0;
    }

    public static int calculateGimatria(String str) {
        return calculateGimatria(str, false);
    }

    public static int calculateGimatria(String str, Boolean boolSofiot) {
        // boolSofiot=false לא מתחשב בסופיות
        // boolSofiot=true גימטריה מיוחדת לסופיות
        int sumGimatria = 0;
        for (char c : str.toCharArray()) {
            sumGimatria += calculateGimatriaLetter(c, boolSofiot);
        }
        return sumGimatria;
    }

    public static void callCalculateGimatria(Object[] args) {
        MainActivity.makeLargeToast(String.valueOf(Gimatria.calculateGimatria((String) args[0], (Boolean) args[1])),180, -1,-1);
    }

    public void searchGimatria(Object[] args) {
        ArrayList<LineReport> results = new ArrayList<LineReport>();
        LastSearchClass searchRecord = new LastSearchClass();
        WordCounter wCounter = new WordCounter();
        BufferedReader inputStream = null;
        String searchSTR;
        int[] searchRange;
        boolean bool_wholeWords;
        boolean bool_sofiot, bool_multi;

        int countFileLines = -1;
        fileMode fMode = MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line);
        BufferedReader bReader = ManageIO.getBufferedReader(fMode, true);
        if (bReader == null) {
            MainActivity.makeToast("לא הצליח לפתוח קובץ תורה");
            return;
        }
        // FileWriter outputStream2 = null;
        try {
            if (args.length < 3) {
                throw new IllegalArgumentException("Missing Arguments in Gimatria.searchGimatria");
            }
            searchSTR = (String) args[0];
            bool_wholeWords = (Boolean) args[1];
            bool_sofiot = (Boolean) args[2];
            try {
                searchRange = (args[3] != null) ? (int[]) (args[3]) : (new int[]{0, 0});
            } catch (ArrayIndexOutOfBoundsException e) {
                searchRange = new int[]{0, 0};
            }
            // checks multiple whole words
            // only used if bool_wholeWords is true
            bool_multi = (args[4] != null) ? (Boolean) (args[4]) : false;

        } catch (ClassCastException e) {
            MainActivity.makeToast("casting exception...");
            e.printStackTrace();
            return;
        } catch (NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
        int searchGmt;
        int sumGimatria = 0;
        if (StringUtils.isNumeric(searchSTR)) {
            searchGmt = Integer.parseInt(searchSTR);
        } else {
            searchGmt = calculateGimatria(searchSTR, bool_sofiot);
        }
        if (searchGmt == 0) {
            MainActivity.makeToast("Can not search for a Gimatria of 0");
            return;
        }
        int countLines = 0;
        int count = 0;
        try {
            // System.out.println("Working Directory = " +
            // System.getProperty("user.dir"));
            inputStream = bReader;
            inputStream.mark(640000);
            count = 0;
//				outputStream.getBuffer().setLength(0);
            String line;
            // \u202A - Left to Right Formatting
            // \u202B - Right to Left Formatting
            // \u202C - Pop Directional Formatting

			/* // Header
			String str = "\u202B" + "חיפוש גימטריה";
			Output.printText(Output.markText(str, ColorClass.headerStyleHTML));
			str = "\u202B" + "מחפש גימטריה " + " \"" + searchGmt + "\"...";
			Output.printText(Output.markText(str, ColorClass.headerStyleHTML));
			if (bool_wholeWords) {
				Output.printText("\u202B" + Output.markText("חיפוש מילים שלמות", ColorClass.headerStyleHTML));
			} else {
				Output.printText("\u202B" + Output.markText("חיפוש צירופי אותיות", ColorClass.headerStyleHTML));
			}
			// Output.printText("");
			// End of Header
			 */

            if (ToraApp.isGui()) {
                LongOperation.setLabel_countMatch("נמצא " + "0" + " פעמים");
                LongOperation.setFinalProgress(searchRange);
                //Output.markText(String.valueOf(searchGmt), ColorClass.headerStyleHTML);
            } else {
                //Output.printText(StringAlignUtils.padRight("", str.length() + 4).replace(' ', '-'));
            }
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
                ArrayList<Integer[]> prevIndexes = null;
                if (bool_wholeWords) {
                    String[] splitStr = line.trim().split("\\s+");
                    int startWordIndex = -1;
                    for (String s : splitStr) {
                        // Do your stuff here
                        String foundWords = s;
                        startWordIndex++;
                        int counter = -1;
                        int countGimatria = 0;
                        do {
                            // finds multiple words in same pasuk, only if bool_multi is true
                            counter++;
                            String strtemp = splitStr[startWordIndex + counter];
                            countGimatria += calculateGimatria(strtemp, bool_sofiot);
                            if (counter > 0) {
                                foundWords += " " + strtemp;
                            }
                        } while ((countGimatria < searchGmt) && (bool_multi)
                                && (counter + startWordIndex < splitStr.length - 1));
                        if (searchGmt == countGimatria) {
                            count++;
                            if (ToraApp.isGui()) {
                                LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                            }
                            if (fMode == fileMode.LastSearch) {
                                prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                            }
                            wCounter.addWord(foundWords);
                            results.add(Output.printPasukInfoExtraIndexes(countLines, foundWords, line,
                                    ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords, prevIndexes));
                            searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                        }
                    }
                } else {
                    sumGimatria = 0;
                    int lineCountEnd = 0;
                    int lineCountStart = 0;
                    for (char ch : line.toCharArray()) {
                        lineCountEnd += 1;
                        sumGimatria += calculateGimatriaLetter(ch, bool_sofiot);
                        if (sumGimatria > searchGmt) {
                            while ((sumGimatria > searchGmt)
                                    || ((line.length() > lineCountStart) && (line.charAt(lineCountStart) == ' '))) {
                                sumGimatria -= calculateGimatriaLetter(line.charAt(lineCountStart), bool_sofiot);
                                lineCountStart += 1;
                            }
                        }
                        if ((sumGimatria == searchGmt)
                                && ((line.length() > lineCountEnd) && (line.charAt(lineCountEnd) != ' '))) {
                            count++;
                            if (ToraApp.isGui()) {
                                LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                            }
                            String s = line.substring(lineCountStart, lineCountEnd);
                            wCounter.addWord(s);
                            if (fMode == fileMode.LastSearch) {
                                prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                            }
                            results.add(Output.printPasukInfoExtraIndexes(countLines, s, line, ColorClass.markupStyleHTML,
                                    bool_sofiot, bool_wholeWords, prevIndexes));
                            searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                        }
                    }
                }
                if ((ToraApp.isGui()) && (SearchFragment.getMethodCancelRequest())) {
                    MainActivity.makeToast("\u202B" + "המשתמש הפסיק חיפוש באמצע");
                    // break is redundant, because for loop will end anyway because maxDilug has
                    // changed to current loop index
                    break;
                }
            }

            MatchLab.addEmpty();
            //Output.printText("");
            MatchLab.add("מילים שנמצאו", "", "לחץ כאן", wCounter.getWords());

            MatchLab.addEmpty();
            MatchLab.add("\u202B" + "נמצא " + "\"" + searchGmt, "\"" + "\u00A0" + count + " פעמים.", "", "");
            if ((ToraApp.isGui()) && (SearchFragment.getMethodCancelRequest())) {
                MainActivity.makeToast("\u202B" + "המשתמש הפסיק חיפוש באמצע");
                // break is redundant, because for loop will end anyway because maxDilug has
                // changed to current loop index
            }
        } catch (Exception e) {
            MainActivity.makeToast("Found Error at Line: " + countLines);
            e.printStackTrace();
        } finally {
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
