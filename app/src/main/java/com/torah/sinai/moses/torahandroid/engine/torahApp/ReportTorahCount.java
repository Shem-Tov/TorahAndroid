package com.torah.sinai.moses.torahandroid.engine.torahApp;

import java.io.BufferedReader;
import java.io.IOException;

import com.torah.sinai.moses.torahandroid.LongOperation;
import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.MatchLab;
import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;
import com.torah.sinai.moses.torahandroid.engine.frame.Frame;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.HtmlGenerator;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.StringAlignUtils;

public class ReportTorahCount {
    private static ReportTorahCount instance;

    public static ReportTorahCount getInstance() {
        if (instance == null) {
            instance = new ReportTorahCount();
        }
        return instance;
    }

    private static char[] Letters = {'א', 'ב', 'ג', 'ד', 'ה', 'ו', 'ז', 'ח', 'ט', 'י', 'כ', 'ל', 'מ', 'נ', 'ס', 'ע',
            'פ', 'צ', 'ק', 'ר', 'ש', 'ת', 'ך', 'ם', 'ן', 'ף', 'ץ'};

    private static int getCharIndex(char ch) {
        int index = 0;
        for (char c : Letters) {
            if (ch == c) {
                break;
            }
            index++;
        }
        return index;
    }

    private static int getSofit(char ch) {
        switch (ch) {
            case 'כ':
                return 22;
            case 'מ':
                return 23;
            case 'נ':
                return 24;
            case 'פ':
                return 25;
            case 'צ':
                return 26;
        }
        return -1;
    }

    public void createReport(Object[] args) throws IOException {
        // String[][] results=null;
        BufferedReader inputStream = null;
        // StringWriter outputStream = null;
        int[] searchRange;
        String[] stringRange;
        // FileWriter outputStream2 = null;
        try {
            try {
                searchRange = (args[0] != null) ? (int[]) args[0] : new int[]{0, 0};
                stringRange = (args[1] != null) ? (String[]) args[1] : new String[]{"התחלה", "סוף", ""};
            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                searchRange = new int[]{0, 0};
                stringRange = new String[]{"התחלה", "סוף", ""};
            }
        } catch (ClassCastException e) {
            MainActivity.makeToast("casting exception...");
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
        int countLines = 0;
        int countLinesInRange = 0;
        int countLetters = 0;
        int countWords = 0;
        int[] countSpecLetters = new int[27];
        int[] countHWordSpecLetters = new int[27];
        int[] countTWordSpecLetters = new int[27];
        int[] countHPasukSpecLetters = new int[27];
        int[] countTPasukSpecLetters = new int[27];
        int[] lineMostLetters = new int[]{-1, -1}; // [0] = lineNum, [1] = letterCount
        int[] lineMostWords = new int[]{-1, -1}; // [0] = lineNum, [1] = wordCount
        int[] lineLeastLetters = new int[]{-1, Integer.MAX_VALUE};// [0] = lineNum, [1] = letterCount
        int[] lineLeastWords = new int[]{-1, Integer.MAX_VALUE}; // [0] = lineNum, [1] = wordCount
        int[] lineHighestGimatria = new int[]{-1, -1}; // [0] = lineNum, [1] = gimatriaSum
        int[] lineLowestGimatria = new int[]{-1, Integer.MAX_VALUE}; // [0] = lineNum, [1] = gimatriaSum
        int[] lineHighestGimatriaSofiot = new int[]{-1, -1}; // [0] = lineNum, [1] = gimatriaSum
        int[] wordMostLetters = new int[]{-1, -1, -1}; // [0] = lineNum, [1] = wordIndex, [2] = letterCount
        int[] wordLeastLetters = new int[]{-1, -1, Integer.MAX_VALUE}; // [0] = lineNum, [1] = wordIndex, [2] =
        // letterCount
        int[] wordHighestGimatria = new int[]{-1, -1, -1}; // [0] = lineNum, [1] = wordIndex, [2] = gimatriaSum
        int[] wordLowestGimatria = new int[]{-1, -1, Integer.MAX_VALUE}; // [0] = lineNum, [1] = wordIndex, [2] =
        // gimatriaSum
        int[] wordHighestGimatriaSofiot = new int[]{-1, -1, -1}; // [0] = lineNum, [1] = wordIndex, [2] = gimatriaSum

        BufferedReader bReader = ManageIO.getBufferedReader(
                MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line), false);
        if (bReader == null) {
            MainActivity.makeToast("לא הצליח לפתוח קובץ תורה");
            return;
        }
        try {
            // System.out.println("Working Directory = " +
            // System.getProperty("user.dir"));
            inputStream = bReader;
            String line = "";
            // inputStream.mark(640000);
            // \u202A - Left to Right Formatting
            // \u202B - Right to Left Formatting
            // \u202C - Pop Directional Formatting
            String str = "\u202B" + "דוח ספירה";

            MatchLab.add(wrapPAlign(Output.markText(str, ColorClass.headerStyleHTML)));
            MatchLab.addEmpty();
            // Output.printText("");
            // System.out.println(formatter.locale());
            LongOperation.setLabel_countMatch("נמצא " + "0" + " פעמים");
            LongOperation.setFinalProgress(searchRange);
            while ((line = inputStream.readLine()) != null) {
                countLines++;
                if ((searchRange[1] != 0) && ((countLines <= searchRange[0]) || (countLines > searchRange[1]))) {
                    continue;
                }
                if ((ToraApp.isGui()) && (countLines % 25 == 0)) {
                    LongOperation.callProcess(countLines);
                }
                String[] splitStr;
                countLinesInRange++;
                countHPasukSpecLetters[getCharIndex(line.charAt(0))]++;
                countTPasukSpecLetters[getCharIndex(line.charAt(line.length() - 1))]++;
                splitStr = line.split("\\s+");
                int countLineLetters = 0;
                int sumLineGimatria = 0;
                int sumLineGimatriaSofiot = 0;
                int countLineWords = splitStr.length;
                if (lineMostWords[1] < countLineWords) {
                    lineMostWords[0] = countLines;
                    lineMostWords[1] = countLineWords;
                }
                if (lineLeastWords[1] > countLineWords) {
                    lineLeastWords[0] = countLines;
                    lineLeastWords[1] = countLineWords;
                }
                int wordIndex = -1;
                for (String s : splitStr) {
                    wordIndex++;
                    int countWordLetters = s.length();
                    int sumWordGimatria = Gimatria.calculateGimatria(s, false);
                    int sumWordGimatriaSofiot = Gimatria.calculateGimatria(s, true);
                    sumLineGimatria += sumWordGimatria;
                    sumLineGimatriaSofiot += sumWordGimatriaSofiot;
                    countLineLetters += countWordLetters;
                    if (wordMostLetters[2] < countWordLetters) {
                        wordMostLetters[0] = countLines;
                        wordMostLetters[1] = wordIndex;
                        wordMostLetters[2] = countWordLetters;
                    }
                    if (wordLeastLetters[2] > countWordLetters) {
                        wordLeastLetters[0] = countLines;
                        wordLeastLetters[1] = wordIndex;
                        wordLeastLetters[2] = countWordLetters;
                    }
                    if (wordHighestGimatria[2] < sumWordGimatria) {
                        wordHighestGimatria[0] = countLines;
                        wordHighestGimatria[1] = wordIndex;
                        wordHighestGimatria[2] = sumWordGimatria;
                    }
                    if (wordLowestGimatria[2] > sumWordGimatria) {
                        wordLowestGimatria[0] = countLines;
                        wordLowestGimatria[1] = wordIndex;
                        wordLowestGimatria[2] = sumWordGimatria;
                    }
                    if (wordHighestGimatriaSofiot[2] < sumWordGimatriaSofiot) {
                        wordHighestGimatriaSofiot[0] = countLines;
                        wordHighestGimatriaSofiot[1] = wordIndex;
                        wordHighestGimatriaSofiot[2] = sumWordGimatriaSofiot;
                    }
                    countWords++;
                    countHWordSpecLetters[getCharIndex(s.charAt(0))]++;
                    countTWordSpecLetters[getCharIndex(s.charAt(s.length() - 1))]++;
                    for (char c : s.toCharArray()) {
                        countLetters++;
                        countSpecLetters[getCharIndex(c)]++;
                    }
                }
                if (lineMostLetters[1] < countLineLetters) {
                    lineMostLetters[0] = countLines;
                    lineMostLetters[1] = countLineLetters;
                }
                if (lineLeastLetters[1] > countLineLetters) {
                    lineLeastLetters[0] = countLines;
                    lineLeastLetters[1] = countLineLetters;
                }
                if (lineHighestGimatria[1] < sumLineGimatria) {
                    lineHighestGimatria[0] = countLines;
                    lineHighestGimatria[1] = sumLineGimatria;
                }
                if (lineLowestGimatria[1] > sumLineGimatria) {
                    lineLowestGimatria[0] = countLines;
                    lineLowestGimatria[1] = sumLineGimatria;
                }
                if (lineHighestGimatriaSofiot[1] < sumLineGimatriaSofiot) {
                    lineHighestGimatriaSofiot[0] = countLines;
                    lineHighestGimatriaSofiot[1] = sumLineGimatriaSofiot;
                }
                if ((ToraApp.isGui()) && (SearchFragment.getMethodCancelRequest())) {
                    MainActivity.makeToast("\u202B" + "המשתמש הפסיק חיפוש באמצע");
                    break;
                }
            }
        } catch (Exception e) {
            MainActivity.makeToast("Error with loading lines.txt");
            e.printStackTrace();
        } finally {
            String style = " style = \"padding: 8px;  border: 2px solid #ccc; text-align: right; "
                    + " font-family: Arial, Verdana, sans-serif;" + " color:		#"
                    + ColorClass.getRGBmainStyleHTML() + ";" + " font-size:	" + Frame.getFontSize() + "px;"
                    + " font-weight:bold" + "\"";
            // String tdstyle2 =" style = \"padding-top: 8px; padding-bottom: 8px;\"";
            String tdstyle = " style = \"padding: 8px; padding-left: 8px; padding-right: 8px;\"";
            String tdstyleMaxWidth = " style = \"padding: 8px; padding-left: 8px; padding-right: 8px;\"";
            //String tdstyleMaxWidth = " style = \"padding: 8px; padding-left: 8px; padding-right: 8px; max-width: 60px;\"";

            if (ToraApp.isGui()) {
                Frame.clearTextPane();
                MatchLab.addEmpty();
                MatchLab.add(wrapPAlign(Output.markText("טווח:", ColorClass.headerStyleHTML)
                        + Output.markText(stringRange[0], ColorClass.markupStyleHTML)
                        + Output.markText(" עד ", ColorClass.headerStyleHTML)
                        + Output.markText(stringRange[1], ColorClass.markupStyleHTML)
                        + Output.markText(stringRange[2], ColorClass.headerStyleHTML)));
                //Output.printLine(4, "#FFD700", 0);
            }
            // FirstTable
            int fontSizeBig = Frame.getFontSizeBig() + 1;
            int fontSizeBigger = fontSizeBig + 1;
            StringBuilder str = new StringBuilder();
            str.append("<Table border=\"1\"" + style + ">" + "<TR" + style + "><TH></TH><TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBigger, HtmlGenerator.mode_header)
                    + ">דוח כללי</TH></TR>" + "<TR" + style + "><TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header)
                    + ">מספר</TH></TR>");
            str.append("<TR" + style + "><td" + tdstyle + ">" + countLinesInRange + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר פסוקים" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyle + ">" + countWords + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר מילים" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyle + ">" + countLetters + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר אותיות" + "</Th></TR>");
            // str.append("<TR"+style+"><td"+tdstyle+">" + "" + "</td><Th>" + "" +
            // "</Th></TR>");
            str.append("</TABLE>");
            MatchLab.add(wrapPAlign(str.toString()));
            // SecondTable
            str = new StringBuilder();
            str.append("<Table border=\"1\"" + style + ">" + "<TR" + style
                    + "><TH></TH><TH></TH><TH></TH><TH></TH><TH></TH>" + "<TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBigger, HtmlGenerator.mode_header)
                    + ">דוח אותיות</TH></TR>" + "<TR" + style + ">" + "<TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header) + ">בסוף פסוק</TH>"
                    + "<TH" + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header)
                    + ">בראש פסוק</TH>" + "<TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header) + ">בסוף מילה</TH>"
                    + "<TH" + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header)
                    + ">בראש מילה</TH>" + "<TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header) + ">מספר</TH>"
                    + "<TH" + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header)
                    + ">אות</TH></TR>");
            int j = 0;
            for (int i = 0; i < Letters.length; i++) {
                // str.append("<TR><td"+style+">"+ i+ "</td><Td"+style+">" + Letters[i] +
                // "</Td></TR>");
                str.append("<TR" + style + "><td" + tdstyle + ">" + countTPasukSpecLetters[i]
                        + (((j = getSofit(Letters[i])) != -1)
                        ? " (" + (countTPasukSpecLetters[j] + countTPasukSpecLetters[i]) + ")"
                        : "")
                        + "</td><td" + tdstyle + ">" + countHPasukSpecLetters[i]
                        + (((j = getSofit(Letters[i])) != -1)
                        ? " (" + (countHPasukSpecLetters[j] + countHPasukSpecLetters[i]) + ")"
                        : "")
                        + "</td><td" + tdstyle + ">" + countTWordSpecLetters[i]
                        + (((j = getSofit(Letters[i])) != -1)
                        ? " (" + (countTWordSpecLetters[j] + countTWordSpecLetters[i]) + ")"
                        : "")
                        + "</td><td" + tdstyle + ">" + countHWordSpecLetters[i]
                        + (((j = getSofit(Letters[i])) != -1)
                        ? " (" + (countHWordSpecLetters[j] + countHWordSpecLetters[i]) + ")"
                        : "")
                        + "</td><td" + tdstyle + ">" + countSpecLetters[i]
                        + (((j = getSofit(Letters[i])) != -1) ? " (" + (countSpecLetters[j] + countSpecLetters[i]) + ")"
                        : "")
                        + "</td><Th" + HtmlGenerator.createFontSizeColorStyle(fontSizeBigger, HtmlGenerator.mode_markup)
                        + ">" + Letters[i] + "</Th></TR>");
            }
            str.append("</TABLE>");
            MatchLab.add(wrapPAlign(str.toString()));
            // Table3
            str = new StringBuilder();
            str.append("<Table border=\"1\"" + style + ">" + "<TR" + style + "><TH></TH><TH></TH><TH></TH><TH></TH><TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBigger, HtmlGenerator.mode_header)
                    + ">דוח שיאים</TH></TR>" + "<TR" + style + "><TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header) + ">כתוב</TH><TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header) + ">מילה</TH><TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header) + ">פסוק</TH><TH"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_header) + ">מספר</TH>"
                    + "</TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(lineLeastWords[0]) + "</td><td>"
                    + "</td><td>" + ToraApp.lookupTorahPositionFromLineNumber(lineLeastWords[0], true) + "</td><td>"
                    + lineLeastWords[1] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר מילים הנמוך ביותר בפסוק" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(lineMostWords[0]) + "</td><td>"
                    + "</td><td>" + ToraApp.lookupTorahPositionFromLineNumber(lineMostWords[0], true) + "</td><td>"
                    + lineMostWords[1] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר מילים הגבוה ביותר בפסוק" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(lineLeastLetters[0]) + "</td><td>"
                    + "</td><td>" + ToraApp.lookupTorahPositionFromLineNumber(lineLeastLetters[0], true) + "</td><td>"
                    + lineLeastLetters[1] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר אותיות הנמוך ביותר בפסוק" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(lineMostLetters[0]) + "</td><td>"
                    + "</td><td>" + ToraApp.lookupTorahPositionFromLineNumber(lineMostLetters[0], true) + "</td><td>"
                    + lineMostLetters[1] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר אותיות הגבוה ביותר בפסוק" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(wordLeastLetters[0]) + "</td><td>"
                    + ToraApp.getTorahWord(wordLeastLetters[0], wordLeastLetters[1]) + "</td><td>"
                    + ToraApp.lookupTorahPositionFromLineNumber(wordLeastLetters[0], true) + "</td><td>"
                    + wordLeastLetters[2] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר אותיות הנמוך ביותר במילה" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(wordMostLetters[0]) + "</td><td>"
                    + ToraApp.getTorahWord(wordMostLetters[0], wordMostLetters[1]) + "</td><td>"
                    + ToraApp.lookupTorahPositionFromLineNumber(wordMostLetters[0], true) + "</td><td>"
                    + wordMostLetters[2] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר אותיות הגבוה ביותר במילה" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(lineLowestGimatria[0])
                    + "</td><td>" + "</td><td>" + ToraApp.lookupTorahPositionFromLineNumber(lineLowestGimatria[0], true)
                    + "</td><td>" + lineLowestGimatria[1] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר גימטריה הנמוך ביותר בפסוק" + "</Th></TR>");
            str.append(
                    "<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(lineHighestGimatria[0]) + "</td><td>"
                            + "</td><td>" + ToraApp.lookupTorahPositionFromLineNumber(lineHighestGimatria[0], true)
                            + "</td><td>" + lineHighestGimatria[1] + "</td><Th"
                            + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                            + "מספר גימטריה הגבוה ביותר בפסוק" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(lineHighestGimatriaSofiot[0])
                    + "</td><td>" + "</td><td>"
                    + ToraApp.lookupTorahPositionFromLineNumber(lineHighestGimatriaSofiot[0], true) + "</td><td>"
                    + lineHighestGimatriaSofiot[1] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר גימטריה הגבוה ביותר בפסוק עם חישוב שונה לסופיות" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(wordLowestGimatria[0])
                    + "</td><td>" + ToraApp.getTorahWord(wordLowestGimatria[0], wordLowestGimatria[1]) + "</td><td>"
                    + ToraApp.lookupTorahPositionFromLineNumber(wordLowestGimatria[0], true) + "</td><td>"
                    + wordLowestGimatria[2] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר גימטריה הנמוך ביותר במילה" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(wordHighestGimatria[0])
                    + "</td><td>" + ToraApp.getTorahWord(wordHighestGimatria[0], wordHighestGimatria[1]) + "</td><td>"
                    + ToraApp.lookupTorahPositionFromLineNumber(wordHighestGimatria[0], true) + "</td><td>"
                    + wordHighestGimatria[2] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר גימטריה הגבוה ביותר במילה" + "</Th></TR>");
            str.append("<TR" + style + "><td" + tdstyleMaxWidth + ">" + ToraApp.getTorahLine(wordHighestGimatriaSofiot[0])
                    + "</td><td>" + ToraApp.getTorahWord(wordHighestGimatriaSofiot[0], wordHighestGimatriaSofiot[1])
                    + "</td><td>" + ToraApp.lookupTorahPositionFromLineNumber(wordHighestGimatriaSofiot[0], true)
                    + "</td><td>" + wordHighestGimatriaSofiot[2] + "</td><Th"
                    + HtmlGenerator.createFontSizeColorStyle(fontSizeBig, HtmlGenerator.mode_markup) + ">"
                    + "מספר גימטריה הגבוה ביותר במילה עם חישוב שונה לסופיות" + "</Th></TR>");
            // str.append("<TR"+style+"><td"+tdstyle+">" + "" + "</td><Th>" + "" +
            // "</Th></TR>");
            str.append("</TABLE>");
            MatchLab.add(wrapPAlign(str.toString()));
            MatchLab.addEmpty();
                //MatchLab.add(Output.markText("\u202B" + "סיים דוח", ColorClass.footerStyleHTML));
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private String wrapPAlign(String str) {
        String[] pAlign = new String[]{"<p align=\"right\">", "</p>"};
        return pAlign[0] + str + pAlign[1];
    }
}
