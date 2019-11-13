package com.torah.sinai.moses.torahandroid.engine.torahApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.torah.sinai.moses.torahandroid.LongOperation;
import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.MatchLab;
import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.extras.extraFunctions;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;

import com.torah.sinai.moses.torahandroid.engine.frame.Frame;
import com.torah.sinai.moses.torahandroid.engine.hebrewLetters.HebrewLetters;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LastSearchClass;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LineReport;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO.fileMode;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.StringAlignUtils;

public class ToraSearch {
    private static ToraSearch instance;

    public static ToraSearch getInstance() {
        if (instance == null) {
            instance = new ToraSearch();
        }
        return instance;
    }

    public void searchWords(Object[] args) {
        //Log.d("SEARCH","Started",new Exception());
        ArrayList<LineReport> results = new ArrayList<LineReport>();
        LastSearchClass searchRecord = new LastSearchClass();
        // String[][] results=null;
        BufferedReader inputStream = null;
        BufferedReader inputStream2 = null;
        // StringWriter outputStream = null;
        String searchSTR;
        String searchConvert;
        int[] searchRange;
        boolean bool_wholeWords;
        boolean bool_sofiot;
        boolean bool_multiSearch;
        boolean bool_multiMustFindBoth = true;
        String searchSTR2 = "", searchConvert2 = "";
        // FileWriter outputStream2 = null;
        try {
            if (args.length < 3) {
                throw new IllegalArgumentException("Missing Arguments in ToraSearch.searchWords");
            }
            searchSTR = ((String) args[0]);
            bool_wholeWords = (args[1] != null) ? (Boolean) args[1] : true;
            if (bool_wholeWords) {
                searchSTR = searchSTR.trim();
            }
            bool_sofiot = (args[2] != null) ? (Boolean) args[2] : true;
            searchConvert = (!bool_sofiot) ? HebrewLetters.switchSofiotStr(searchSTR) : searchSTR;
            searchRange = (args[3] != null) ? (int[]) (args[3]) : (new int[]{0, 0});
            bool_multiSearch = (args[4] != null) ? (Boolean) args[4] : false;
            if (bool_multiSearch) {
                searchSTR2 = ((String) args[5]);
                if (bool_wholeWords) {
                    searchSTR2 = searchSTR2.trim();
                }
                searchConvert2 = (!bool_sofiot) ? HebrewLetters.switchSofiotStr(searchSTR2) : searchSTR2;
                bool_multiMustFindBoth = (args[6] != null) ? (Boolean) args[6] : true;
            }
        } catch (ClassCastException e) {
            MainActivity.makeToast("casting exception...");
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
        int countPsukim = 0;
        int countLines = 0;
        int count = 0;

        int countFileLines = -1;
        fileMode fMode = MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line);
        BufferedReader bReader = ManageIO.getBufferedReader(fMode, true);
        if (bReader == null) {
            MainActivity.makeToast("לא הצליח לפתוח קובץ");
            return;
        }
        try {
            // System.out.println("Working Directory = " +
            // System.getProperty("user.dir"));
            inputStream = bReader;
            String line = "";
            String line2 = "";
            int searchSTRinLine2 = 0;
            if ((!bool_wholeWords) && (searchConvert.contains(" ") && (fMode != fileMode.LastSearch))) {
                inputStream2 = ManageIO.getBufferedReader(MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line),
                        true);
                searchSTRinLine2 = searchConvert.length() - searchConvert.indexOf(' ');
                // inputStream2.mark(640000);
                line2 = inputStream2.readLine();
            }
            // inputStream.mark(640000);
            count = 0;
//				outputStream.getBuffer().setLength(0);
            // \u202A - Left to Right Formatting
            // \u202B - Right to Left Formatting
            // \u202C - Pop Directional Formatting

            //This is the Header
            String str = "\u202B" + "חיפוש בתורה";
            MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
            str = "\u202B" + "מחפש" + " \"" + searchSTR + "\"";
            if (bool_multiSearch) {
                str += " | \"" + searchSTR2 + "\"";
            }
            str += "...";
            MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
            str = "\u202B" + ((bool_wholeWords) ? "חיפוש מילים שלמות" : "חיפוש צירופי אותיות");
            MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
            // Output.printText("");

            String tempStr = searchSTR;
            if (bool_multiSearch) {
                tempStr += " | " + searchSTR2;
            }
            Output.markText(tempStr, ColorClass.headerStyleHTML);

            //End of Header */

            // System.out.println(formatter.locale());
            if (ToraApp.isGui()) {
                LongOperation.setLabel_countMatch("נמצא " + "0" + " פעמים");
                LongOperation.setFinalProgress(searchRange);
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
                    if (searchSTR.contains(" ")) {
                        if (ToraApp.isGui()) {
                            Frame.clearTextPane();
                        }
                        MainActivity.makeToast("לא ניתן לעשות חיפוש לפי מילים ליותר ממילה אחת, תעשו חיפוש לפי אותיות");
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        return;
                    }
                    String[] splitStr;
                    if (!bool_sofiot) {
                        splitStr = HebrewLetters.switchSofiotStr(line).trim().split("\\s+");
                    } else {
                        splitStr = line.trim().split("\\s+");
                    }
                    Boolean found1 = false, found2 = false;
                    for (String s : splitStr) {
                        if (bool_multiSearch) {
                            if (s.equals(searchConvert) && (!found1)) {
                                found1 = true;
                            } else if (s.equals(searchConvert2)) {
                                found2 = true;
                            }
                        }
                        if (((bool_multiSearch) && (found1) && (found2))
                                || ((bool_multiSearch) && (!bool_multiMustFindBoth) && ((found1) || (found2)))
                                || ((!bool_multiSearch) && (s.equals(searchConvert)))) {
                            count++;
                            if (ToraApp.isGui()) {
                                LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                            }
                            // printPasukInfo gets the Pasuk Info, prints to screen and sends back array to
                            // fill results array
                            if (fMode == fileMode.LastSearch) {
                                prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                            }
                            if (bool_multiSearch) {
                                if ((found1) && (found2)) {
                                    results.add(Output.printPasukInfoExtraIndexes(countLines, searchSTR, line,
                                            ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords, bool_sofiot,
                                            searchSTR2, prevIndexes));
                                } else if (found1) {
                                    results.add(Output.printPasukInfoExtraIndexes(countLines, searchSTR, line,
                                            ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords, prevIndexes));
                                } else { // then (found2)
                                    results.add(Output.printPasukInfoExtraIndexes(countLines, searchSTR2, line,
                                            ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords, prevIndexes));
                                }
                                break;
                            } else {
                                results.add(Output.printPasukInfoExtraIndexes(countLines, searchSTR, line,
                                        ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords, prevIndexes));
                            }
                            searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                        }
                    }
                } else {
                    String combineConvertedLines = "";
                    if (searchSTRinLine2 > 0) {
                        line2 = (inputStream2.readLine());
                        if (line2 != null) {
                            int numChars = line2.indexOf(' ', searchSTRinLine2 - 1);
                            //if could not find space, then use complete string
                            if (numChars != -1) {
                                line2 = line2.substring(0, numChars);
                            }
                            combineConvertedLines = ((!bool_sofiot) ? HebrewLetters.switchSofiotStr(line + " " + line2)
                                    : (line + " " + line2));
                        } else {
                            combineConvertedLines = ((!bool_sofiot) ? HebrewLetters.switchSofiotStr(line) : line);
                        }
                    } else {
                        combineConvertedLines = ((!bool_sofiot) ? HebrewLetters.switchSofiotStr(line) : line);
                    }
                    boolean found1 = (combineConvertedLines.contains(searchConvert));
                    boolean found2 = ((bool_multiSearch) && (combineConvertedLines.contains(searchConvert2)));
                    if ((found1) || ((found2) && (!bool_multiMustFindBoth))) {

                        if ((!bool_multiSearch)
                                || ((extraFunctions.logicalXOR(found1, found2)) && (!bool_multiMustFindBoth))) {
                            boolean foundInLine2 = false;
                            if (searchSTRinLine2 > 0) {
                                if (found1) {
                                    if ((combineConvertedLines.lastIndexOf(searchConvert)
                                            + searchConvert.length()) > line.length()) {
                                        foundInLine2 = true;
                                    }
                                } else {
                                    if ((combineConvertedLines.lastIndexOf(searchConvert2)
                                            + searchConvert2.length()) > line.length()) {
                                        foundInLine2 = true;
                                    }
                                }
                            }
                            int countMatch;
                            countMatch = StringUtils.countMatches(combineConvertedLines,
                                    (found1) ? searchConvert : searchConvert2);
                            count = count + countMatch;
                            if (ToraApp.isGui()) {
                                LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                            }
                            countPsukim++;
                            // printPasukInfo gets the Pasuk Info, prints to screen and sends back array to
                            // fill results array
                            if (fMode == fileMode.LastSearch) {
                                prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                            }
                            results.add(Output.printPasukInfoExtraIndexes(countLines, (found1) ? searchSTR : searchSTR2,
                                    ((foundInLine2) ? (line + " " + line2) : line), ColorClass.markupStyleHTML,
                                    bool_sofiot, bool_wholeWords, prevIndexes));
                            searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                        } else if ((combineConvertedLines.contains(searchConvert2))) {
                            if ((searchConvert2.contains(searchConvert)) || (searchConvert.contains(searchConvert2))) {
                                if ((combineConvertedLines.indexOf(searchConvert,
                                        combineConvertedLines.indexOf(searchConvert2) + 1) != -1)
                                        || (combineConvertedLines.indexOf(searchConvert2,
                                        combineConvertedLines.indexOf(searchConvert) + 1) != -1)) {
                                    boolean foundInLine2 = false;
                                    if (searchSTRinLine2 > 0) {
                                        if ((combineConvertedLines.lastIndexOf(searchConvert)
                                                + searchConvert.length()) > line.length()) {
                                            foundInLine2 = true;
                                        }
                                    }
                                    count++;
                                    if (ToraApp.isGui()) {
                                        LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                                    }
                                    countPsukim++;
                                    // printPasukInfo gets the Pasuk Info, prints to screen and sends back array to
                                    // fill results array
                                    if (fMode == fileMode.LastSearch) {
                                        prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                                    }
                                    results.add(Output.printPasukInfoExtraIndexes(countLines, searchSTR,
                                            ((foundInLine2) ? (line + " " + line2) : line),
                                            ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords, bool_sofiot,
                                            searchSTR2, prevIndexes));
                                    searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                                }
                            } else {
                                boolean foundInLine2 = false;
                                if (searchSTRinLine2 > 0) {
                                    if ((combineConvertedLines.lastIndexOf(searchConvert)
                                            + searchConvert.length()) > line.length()) {
                                        foundInLine2 = true;
                                    }
                                }
                                count++;
                                if (ToraApp.isGui()) {
                                    LongOperation.setLabel_countMatch("נמצא " + count + " פעמים");
                                }
                                countPsukim++;
                                // printPasukInfo gets the Pasuk Info, prints to screen and sends back array to
                                // fill results array
                                if (fMode == fileMode.LastSearch) {
                                    prevIndexes = LastSearchClass.getStoredLineIndexes(countFileLines);
                                }
                                results.add(Output.printPasukInfoExtraIndexes(countLines, searchSTR,
                                        ((foundInLine2) ? (line + " " + line2) : line),
                                        ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords, bool_sofiot,
                                        searchSTR2, prevIndexes));
                                searchRecord.add(countLines, results.get(results.size() - 1).getResults().get(0));
                            }
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
            MainActivity.makeToast("Error with loading lines.txt");
            e.printStackTrace();
        } finally {
            //footer
            MatchLab.addEmpty();
            MatchLab.add(
                    Output.markText(
                            "\u202B" + "נמצא " + "\"" + searchSTR + "\"" + "\u00A0" + count + " פעמים"
                                    + ((bool_wholeWords) ? "."
                                    : (" ב" + "\u00A0" + countPsukim + " פסוקים.")),
                            ColorClass.footerStyleHTML));

            //End of Footer */
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
