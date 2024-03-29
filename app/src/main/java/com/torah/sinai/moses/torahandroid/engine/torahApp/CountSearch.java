package com.torah.sinai.moses.torahandroid.engine.torahApp;

import com.torah.sinai.moses.torahandroid.LongOperation;
import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.MatchLab;
import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;
import com.torah.sinai.moses.torahandroid.engine.frame.Frame;
import com.torah.sinai.moses.torahandroid.engine.hebrewLetters.HebrewLetters;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.StringAlignUtils;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class CountSearch {
	private static CountSearch instance;

	public static CountSearch getInstance() {
		if (instance == null) {
			instance = new CountSearch();
		}
		return instance;
	}

	public void searchByCount(Object[] args) {
		// String[][] results=null;
		BufferedReader inputStream = null;
		BufferedReader inputStream2 = null;
		// StringWriter outputStream = null;
		String searchSTR;
		String searchConvert;
		int[] searchRange;
		boolean bool_wholeWords;
		boolean bool_sofiot;
		int searchIndex = 1;
		// FileWriter outputStream2 = null;
		try {
			if (args.length < 3) {
				throw new IllegalArgumentException("Missing Arguments in CountSearch.searchByCount");
			}
			searchSTR = ((String) args[0]);
			bool_wholeWords = (args[1] != null) ? (Boolean) args[1] : true;
			if (bool_wholeWords) {
				searchSTR = searchSTR.trim();
			}
			bool_sofiot = (args[2] != null) ? (Boolean) args[2] : true;
			searchConvert = (!bool_sofiot) ? HebrewLetters.switchSofiotStr(searchSTR) : searchSTR;
			searchRange = (args[3] != null) ? (int[]) (args[3]) : (new int[] { 0, 0 });
			searchIndex = ((args[4] != null) && (StringUtils.isNumeric((String) args[4]))
					&& (((String) args[4]).length() > 0)) ? Integer.parseInt((String) args[4]) : 1;
		} catch (ClassCastException e) {
			MainActivity.makeToast("casting exception...");
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}
		int countLines = 0;
		int count = 0;

		BufferedReader bReader = ManageIO.getBufferedReader(
				MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line),false);
		if (bReader == null) {
			MainActivity.makeToast("לא הצליח לפתוח קובץ תורה");
			return;
		}
		try {
			// System.out.println("Working Directory = " +
			// System.getProperty("user.dir"));
			inputStream = bReader;
			String line = "";
			String line2 = "";
			int searchSTRinLine2 = 0;
			if ((!bool_wholeWords) && (searchConvert.contains(" "))) {
				inputStream2 = ManageIO.getBufferedReader(
						MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line),false);
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

			 //Header
			String str = "\u202B" + "חיפוש בתורה לפי מספר הופעה";
			MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
			str = "\u202B" + "מחפש" + " \"" + searchSTR + "\"...";
			MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
			str = "\u202B" + ((bool_wholeWords) ? "חיפוש מילים שלמות" : "חיפוש צירופי אותיות");
			MatchLab.add(Output.markText(str, ColorClass.headerStyleHTML));
			MatchLab.addEmpty();

			Output.markText(searchSTR, ColorClass.headerStyleHTML);

			// System.out.println(formatter.locale());

			//End of Header */

			if (ToraApp.isGui()) {
				LongOperation.setLabel_countMatch("");
				LongOperation.setFinalProgress(searchRange);
			}
			outerloop: while ((line = inputStream.readLine()) != null) {
				countLines++;
				if ((searchRange[1] != 0) && ((countLines <= searchRange[0]) || (countLines > searchRange[1]))) {
					continue;
				}
				if ((ToraApp.isGui()) && (countLines % 25 == 0)) {
					LongOperation.callProcess(countLines);
				}
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
					int countIndex = 0;
					for (String s : splitStr) {
						// Do your stuff here
						if (s.equals(searchConvert)) {
							count++;
							if (count == searchIndex) {
								// printPasukInfo gets the Pasuk Info, prints to screen and sends back array to
								// fill results array
								MatchLab.add("נמצא בפעם ה: "+searchIndex,"","","");
								Output.printPasukInfo(countLines, searchSTR, line, ColorClass.markupStyleHTML,
										bool_sofiot, bool_wholeWords, countIndex);
								break outerloop;
							}
							countIndex++;
						}
					}
				} else {
					String combineConvertedLines = "";
					if (searchSTRinLine2 > 0) {
						line2 = (inputStream2.readLine());
						if (line2 != null) {
							line2 = line2.substring(0, searchSTRinLine2);
							combineConvertedLines = ((!bool_sofiot) ? HebrewLetters.switchSofiotStr(line + " " + line2)
									: (line + " " + line2));
						} else {
							combineConvertedLines = ((!bool_sofiot) ? HebrewLetters.switchSofiotStr(line) : line);
						}
					} else {
						combineConvertedLines = ((!bool_sofiot) ? HebrewLetters.switchSofiotStr(line) : line);
					}
					if (combineConvertedLines.contains(searchConvert)) {
						boolean foundInLine2 = false;
						if (searchSTRinLine2 > 0) {
							if ((combineConvertedLines.indexOf(searchConvert) + searchConvert.length()) > line
									.length()) {
								foundInLine2 = true;
							}
						}
						int countMatch = StringUtils.countMatches(combineConvertedLines, searchConvert);
						count = count + countMatch;
						if (count >= searchIndex) {
							// printPasukInfo gets the Pasuk Info, prints to screen and sends back array to
							// fill results array
							MatchLab.add("נמצא בפעם ה: "+searchIndex,"","","");
							Output.printPasukInfo(countLines, searchSTR, ((foundInLine2) ? (line + " " + line2) : line),
									ColorClass.markupStyleHTML, bool_sofiot, bool_wholeWords,((countMatch-(count-searchIndex))-1));
							break outerloop;
						}
					}
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
			 // Footer
			// End of Footer */

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
