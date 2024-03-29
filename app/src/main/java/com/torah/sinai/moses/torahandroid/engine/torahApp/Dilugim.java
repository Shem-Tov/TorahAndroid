package com.torah.sinai.moses.torahandroid.engine.torahApp;

import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.torah.sinai.moses.torahandroid.DilugMatchLab;
import com.torah.sinai.moses.torahandroid.LongOperation;
import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;

import com.torah.sinai.moses.torahandroid.engine.hebrewLetters.HebrewLetters;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LineReport;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO.fileMode;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.TorahLine;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.StringAlignUtils;

public class Dilugim {
    private static Dilugim instance;

    public static Dilugim getInstance() {
        if (instance == null) {
            instance = new Dilugim();
        }
        return instance;
    }

    public class paddingResults {
        private StringBuilder myString;
        private int[] paddingCount;

        paddingResults(StringBuilder strB, int headPad, int tailPad) {
            myString = strB;
            paddingCount = new int[]{headPad, tailPad};
        }

        StringBuilder getMyString() {
            return myString;
        }

        int getPaddingHead() {
            return paddingCount[0];
        }

        int getPaddingTail() {
            return paddingCount[1];
        }

        ArrayList<Integer[]> getArrayListPadding() {
            ArrayList<Integer[]> aList = new ArrayList<Integer[]>();
            // converts int[] to Integer[] and adds to arrayList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                aList.add(IntStream.of(paddingCount).boxed().toArray(Integer[]::new));
            } else {
                //Java 6
                Integer[] newArray = ArrayUtils.toObject(paddingCount);
                aList.add(newArray);
            }
            return aList;
        }
    }

    public paddingResults readDilugExpandedResult(String searchSTR, int countChar, int dilug, int padding,
                                                  boolean differentSearch, boolean foundPaddingFile) {
        BufferedReader inputStream = null;
        StringBuilder str = null;
        int countJumps = 0, newpadding = 0;
        Boolean checkJumps = (differentSearch || (!foundPaddingFile));
        /*
         * if (differentSearch) { return new paddingResults(new StringBuilder(), 0, 0);
         * }
         */
        BufferedReader bReader;
        if (differentSearch) {
            bReader = ManageIO.getBufferedReader(ManageIO.fileMode.Different, false);
        } else if (!foundPaddingFile) {
            bReader = ManageIO.getBufferedReader(ManageIO.fileMode.Line, false);
        } else {
            bReader = ManageIO.getBufferedReader(ManageIO.fileMode.NoTevot, false);
        }

        if (bReader == null) {
            MainActivity.makeToast("לא הצליח לפתוח קובץ תורה");
            return new paddingResults(new StringBuilder(), 0, 0);
        }
        try {
            inputStream = bReader;
            @SuppressWarnings("unused")
            int markInt = 640000;
            // inputStream.mark(markInt);
            int c;
            int startChar = countChar - dilug * padding;
            newpadding = padding;
            while (startChar <= 0) {
                // if the padding is too large find the minimum position to read from.
                startChar += dilug;
                newpadding -= 1;
            }
            int maxJumps = padding + newpadding + searchSTR.length();
            str = new StringBuilder(maxJumps);
            if (startChar > 1) {
                if (!checkJumps) {
                    inputStream.skip(startChar - 1);
                } else {
                    int jumpCount = 1;
                    while (jumpCount < startChar) {
                        c = inputStream.read();
                        if ((c != 10) && (c != 32)) {
                            jumpCount++;
                        }
                    }
                }
            }
            while ((c = inputStream.read()) != -1) {
                if ((checkJumps) && ((c == 10) || (c == 32))) {
                    continue;
                }
                countJumps++;
                str.append((char) c);
                if (countJumps < maxJumps) {
                    try {
                        if (dilug > 0) {
                            if (!checkJumps) {
                                inputStream.skip(dilug - 1);
                            } else {
                                int jumpCount = 1;
                                while (jumpCount < dilug) {
                                    c = inputStream.read();
                                    if ((c != 10) && (c != 32)) {
                                        jumpCount++;
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            MainActivity.makeToast("Error with loading lines.txt");
            /*
             * } catch (NoSuchFieldException e) { e.printStackTrace();
             */
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new paddingResults(str, newpadding, newpadding + searchSTR.length());
    }

    public void searchWordsDilugim(Object[] args) {
        // String[][] results=null;
        BufferedReader inputStream = null;
        String searchConvert, sRegular = "", sReverse = "";
        String searchSTR;
        int[] searchRange;
        Boolean bool_sofiot, bool_reverseDilug;
        int minDilug;
        int maxDilug;
        int padding;
        int countAll = 0;
        // Table.getInstance(true).newTable(true);
        Boolean foundPaddingFile = true;
        Boolean differentMode = (MainActivity.getComboBox_DifferentSearch(null) == fileMode.Different);
        // FileWriter outputStream2 = null;
        BufferedReader bReader = ManageIO.getBufferedReader(
                MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line), false);
        if (bReader == null) {
            return;
        }
        BufferedReader tempReader = ManageIO.getBufferedReader(
                MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.NoTevot), false);
        if (tempReader != null) {
            try {
                tempReader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            foundPaddingFile = false;
        }

        try {
            if (args.length < 6) {
                throw new IllegalArgumentException("Missing Arguments in Dilugim.searchWordsDilugim");
            }
            searchSTR = ((String) args[0]);
            searchConvert = searchSTR.replace(" ", "");
            bool_sofiot = (args[1] != null) ? (Boolean) args[1] : true;
            minDilug = ((args[2] != null) && (StringUtils.isNumeric((String) args[2]))
                    && (((String) args[2]).length() > 0)) ? Integer.parseInt((String) args[2]) : 2;
            maxDilug = ((args[3] != null) && (StringUtils.isNumeric((String) args[3]))
                    && (((String) args[3]).length() > 0)) ? Integer.parseInt((String) args[3]) : 2;
            padding = ((args[4] != null) && (StringUtils.isNumeric((String) args[4]))
                    && (((String) args[4]).length() > 0)) ? Integer.parseInt((String) args[4]) : 0;
            searchRange = (args[5] != null) ? (int[]) (args[5]) : (new int[]{0, 0});
            bool_reverseDilug = (args[6] != null) ? (Boolean) args[6] : false;
            if (!bool_sofiot) {
                searchConvert = HebrewLetters.switchSofiotStr(searchConvert);
            }
            if (bool_reverseDilug) {
                sRegular = searchConvert;
                sReverse = new StringBuilder(searchConvert).reverse().toString();
            }
        } catch (ClassCastException e) {
            MainActivity.makeToast("casting exception...");
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        try {
            // System.out.println("Working Directory = " +
            // System.getProperty("user.dir"));
            final int markInt = 640000;
            // \u202A - Left to Right Formatting
            // \u202B - Right to Left Formatting
            // \u202C - Pop Directional Formatting
             //Header
            DilugMatchLab.newDilugSet();
            String str = "\u202B" + "חיפוש דילוגים";
            DilugMatchLab.addToCurrent(Output.markText(str, ColorClass.headerStyleHTML));
            str = "\u202B" + "מחפש" + " \"" + searchConvert + "\"...";
            DilugMatchLab.addToCurrent(Output.markText(str, ColorClass.headerStyleHTML));
            str = "\u202B" + "בין דילוג" + ToraApp.cSpace() + minDilug + " ו" + ToraApp.cSpace() + maxDilug + ".";
            DilugMatchLab.addToCurrent(Output.markText(str, ColorClass.headerStyleHTML));
            // Output.printText("");

            // End Header */

            Output.markText(searchConvert, ColorClass.headerStyleHTML);

            LongOperation.setLabel_countMatch("נמצא " + "0" + " פעמים");
            LongOperation.setFinalProgress(searchRange);

            // System.out.println(formatter.locale());

            for (int thisDilug = minDilug; thisDilug <= maxDilug; thisDilug++) {
                outer:
                for (int z = 0; z <= ((bool_reverseDilug) ? 1 : 0); z++) {
                    if (bool_reverseDilug) {
                        if (z == 0) {
                            searchConvert = sRegular;
                        } else {
                            searchConvert = sReverse;
                        }
                    }
                    ArrayList<LineReport> results = new ArrayList<LineReport>();
                    if (ToraApp.isGui()) {
                        LongOperation.setLabel_dProgress("דילוג " + thisDilug);
                    }
                    inputStream = ManageIO.getBufferedReader(
                            MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line), false);
                    inputStream.mark(markInt);
                    ArrayList<ArrayList<Integer[]>> resArray = new ArrayList<ArrayList<Integer[]>>();
                    int indexResArray = -1;
                    int charPOS = -1; // counts char position in line
                    int countLines = 1; // counts line in Tora File
                    int backup_countLines = 0; // backup for when reset buffer
                    int backup_charPOS = -1; // backup for when reset buffer
                    int backup_countChar = 0; // backup for when reset buffer
                    int searchIndex = 0; // index of letter in searchSTR which is being examined
                    int countChar = 0; // used to update progressbar and connect with Letter version of Torah for
                    // padding
                    int countCharOfFirst = 0;
                    int count = 0; // counts matches
                    int lastCharIndex = 0; // counts letters from last match
                    int c;
                    while ((c = inputStream.read()) != -1) {
                        if ((c == 10) || (c == 32)) {
                            if (c == 10) {
                                charPOS = -1;
                                countLines++;
                            } else {
                                charPOS++;
                            }
                            continue;
                        }
                        if ((searchRange[1] != 0)
                                && ((countLines <= searchRange[0]) || (countLines > searchRange[1]))) {
                            if (searchIndex != 0) {
                                searchIndex = 0;
                                inputStream.reset();
                                lastCharIndex = 0;
                                countLines = backup_countLines;
                                charPOS = backup_charPOS;
                                countChar = backup_countChar;
                            } else {
                                // Counts Char if did not reset
                                // and if char is not space or carriage return
                                // the previous continue makes sure it is not
                                // a space or carriage return
                                countChar++;
                            }
                            continue;
                        }
                        lastCharIndex = (searchIndex == 0) ? 0 : lastCharIndex + 1;
                        charPOS++;
                        countChar++;
                        if ((ToraApp.isGui()) && (countLines % 25 == 0)) {
                            LongOperation.callProcess(countLines, thisDilug, minDilug, maxDilug);
                        }
                        if ((searchIndex == 0) || (lastCharIndex % thisDilug == 0)) {
                            if (HebrewLetters.compareChar(searchConvert.charAt(searchIndex), (char) c, bool_sofiot)) {
                                lastCharIndex = 0;
                                if (searchIndex == 0) {
                                    inputStream.mark(markInt);
                                    backup_countLines = countLines;
                                    backup_charPOS = charPOS;
                                    backup_countChar = countChar;
                                    countCharOfFirst = countChar;
                                }
                                // This collects information of char
                                // positions of matches and organizes
                                // them where char positions in the
                                // same line will be in the same
                                // arraylist.
                                if ((resArray.size() == 0) || (resArray.get(indexResArray).get(0)[0] != countLines)) {
                                    // create new index
                                    resArray.add(new ArrayList<Integer[]>());
                                    indexResArray++;
                                    resArray.get(indexResArray).add(new Integer[]{countLines, 0});
                                }
                                // add to last index created
                                // add char Position
                                resArray.get(indexResArray).add(new Integer[]{charPOS, charPOS + 1});
                                searchIndex++;
                                if (searchIndex == searchConvert.length()) {
                                    count++;
                                    countAll++;
                                    if (ToraApp.isGui()) {
                                        LongOperation.setLabel_countMatch("נמצא " + countAll + " פעמים");
                                    }
                                    if (count == 1) {
                                        DilugMatchLab.newDilugSet();
                                        DilugMatchLab.addEmptyToCurrent();
                                        DilugMatchLab.addToCurrent(Output.markText(
                                                ("דילוג" + ToraApp.cSpace() + thisDilug + ((z == 1) ? "(הפוך)" : "")),
                                                ColorClass.headerStyleHTML));
                                    }
                                    // Must change size of resArray when adding more information
                                    int reportLineIndex = 0;
                                    paddingResults pReport;
                                    pReport = readDilugExpandedResult(searchSTR, countCharOfFirst, thisDilug, padding,
                                            differentMode, foundPaddingFile);
                                    LineReport lineReport = new LineReport(new String[]{String.valueOf(thisDilug),
                                            searchSTR, "", "", "", pReport.myString.toString()},
                                            pReport.getArrayListPadding());
                                    DilugMatchLab.newDilugSet();
                                    for (ArrayList<Integer[]> j : resArray) {
                                        String reportLine = "";
                                        for (int k = reportLineIndex; k < (reportLineIndex + j.size() - 1); k++) {
                                            if (k != reportLineIndex) {
                                                reportLine += ", ";
                                            }
                                            reportLine += searchConvert.substring(k, k + 1);
                                        }
                                        reportLineIndex += j.size() - 1;
                                        TorahLine tLine = Output.markMatchesFromArrayList(j,
                                                ColorClass.markupStyleHTML);
                                        ToraApp.perekBookInfo pBookInstance = ToraApp.findPerekBook(j.get(0)[0]);
                                        String tempStr1 = Output.markText(reportLine, ColorClass.markupStyleHTML);
                                        String bookInfo = Output.markText(
                                                StringAlignUtils.padRight(pBookInstance.getBookName(), 6) + " "
                                                        + pBookInstance.getPerekLetters() + ":"
                                                        + pBookInstance.getPasukLetters(),
                                                ColorClass.highlightStyleHTML[pBookInstance.getBookNumber()
                                                        % ColorClass.highlightStyleHTML.length]);
                                        String tempStr2 = StringAlignUtils.padRight(tempStr1 + ":  " + bookInfo, 32)
                                                + " =    ";
                                        String pasukLine = "";
                                        if (ToraApp.getGuiMode() == ToraApp.id_guiMode_Frame) {
                                            pasukLine = tLine.getLineHtml();
                                            tempStr2 += pasukLine;
                                        } else {
                                            tempStr2 += tLine.getLine();
                                        }
                                        //Output.printText(tempStr2);
                                        String somePsukim = Output.printSomePsukimHtml(j.get(0)[0], Output.padLines, pasukLine);
                                        //Output.printText("טקסט נוסף", 3, somePsukim);
                                        // Output.printTableRow(tempStr1, bookInfo, tLine.getLineHtml(), "", thisDilug);
                                        lineReport.add(
                                                new String[]{"", reportLine, pBookInstance.getBookName(),
                                                        pBookInstance.getPerekLetters(),
                                                        pBookInstance.getPasukLetters(), tLine.getLine()},
                                                new ArrayList<Integer[]>(j.subList(1, j.size())));
                                        DilugMatchLab.addToCurrent(reportLine, bookInfo, pasukLine, somePsukim);
                                    }
                                    String lineDilug = Output.markTextBounds(pReport.getMyString().toString(),
                                            pReport.getPaddingHead(), pReport.getPaddingTail(),
                                            ColorClass.markupStyleHTML);
                                    DilugMatchLab.addLineDilug(lineDilug);
                                    String treeString2 = ((foundPaddingFile) && (pReport.getMyString().length() > 0))
                                            ? "שורת הדילוג:" + ToraApp.cSpace(2)
                                            + lineDilug : "";
                                    //Output.printText(treeString2);
                                    //Output.printText("");
                                    results.add(lineReport);
                                    resArray = new ArrayList<ArrayList<Integer[]>>();
                                    indexResArray = -1;
                                    inputStream.reset();
                                    searchIndex = 0;
                                    countLines = backup_countLines;
                                    charPOS = backup_charPOS;
                                    countChar = backup_countChar;
                                    lastCharIndex = 0;
                                }
                            } else if (searchIndex != 0) {
                                resArray = new ArrayList<ArrayList<Integer[]>>();
                                indexResArray = -1;
                                searchIndex = 0;
                                inputStream.reset();
                                lastCharIndex = 0;
                                countLines = backup_countLines;
                                charPOS = backup_charPOS;
                                countChar = backup_countChar;
                            }
                        }
                        if ((ToraApp.isGui())
                                && (SearchFragment.getMethodCancelRequest())) {
                            maxDilug = thisDilug;
                            MainActivity.makeToast("\u202B" + "המשתמש הפסיק חיפוש באמצע");
                            // break is redundant, because for loop will end anyway because maxDilug has
                            // changed to current loop index
                            break outer;
                        }
                    }
                     //Sub Footer
                    DilugMatchLab.newDilugSet();
                    DilugMatchLab.addEmptyToCurrent();
                    DilugMatchLab.addToCurrent(Output.markText(
                            "\u202B" + "נמצא " + "\"" + searchConvert + "\"" + "\u00A0" + count
                                    + " פעמים" + " לדילוג" + ToraApp.cSpace() + thisDilug + ".",
                            ColorClass.footerStyleHTML));
                      // End of Sub Footer

                }
            }
             //Footer
            DilugMatchLab.newDilugSet();
            DilugMatchLab.addEmptyToCurrent();
            DilugMatchLab.addToCurrent(Output.markText(
                    "\u202B" + "נמצא " + "\"" + searchConvert + "\"" + "\u00A0" + countAll + " פעמים"
                            + " לדילוגים" + ToraApp.cSpace() + minDilug + " עד" + ToraApp.cSpace() + maxDilug + ".",
                    ColorClass.footerStyleHTML));
            DilugMatchLab.addToCurrent(Output.markText("\u202B" + "סיים חיפוש", ColorClass.footerStyleHTML));
            // End of Footer

        } catch (Exception e) {
            MainActivity.makeToast("Error with Dilug");
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
