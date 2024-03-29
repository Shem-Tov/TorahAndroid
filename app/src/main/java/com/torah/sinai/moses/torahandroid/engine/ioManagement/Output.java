package com.torah.sinai.moses.torahandroid.engine.ioManagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.MatchLab;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;

import com.torah.sinai.moses.torahandroid.engine.frame.Frame;
import com.torah.sinai.moses.torahandroid.engine.hebrewLetters.HebrewLetters;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.HtmlGenerator;
import com.torah.sinai.moses.torahandroid.engine.stringFormat.StringAlignUtils;
import com.torah.sinai.moses.torahandroid.engine.torahApp.LetterCounter;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;

import static com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO.readBufferReaderLineX;

public class Output {
	public final static int padLines =2;

	public static String r2l() {
		// changes console to right to left
		// add at start of every line
		return "\u202B";
	}
	
	public static TorahLine markMatchesFromArrayList(ArrayList<Integer[]> indexes,
			HtmlGenerator htmlFormat) {
		// Does not mark, if in console mode
		String line = "";
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			try (BufferedReader bReader2 = ManageIO.getBufferedReader(
					MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line),false);
                 Stream<String> lines = bReader2.lines()) {
				// Recieves words of Pasuk
				line = lines.skip(indexes.get(0)[0] - 1).findFirst().get();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			line = readBufferReaderLineX(ManageIO.getBufferedReader(
					MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line),false),
					indexes.get(0)[0] - 1);
		}

		if (!ToraApp.isGui()) {
			return new TorahLine(line, null);
		}
		//remove first index
		indexes = new ArrayList<Integer[]>( indexes.subList(1,indexes.size()));
		return new TorahLine(line, createLineHtml(line, indexes, htmlFormat));
	}

	public static LineHtmlReport markMatchesInLine(String line, String searchSTR,
			HtmlGenerator htmlFormat, Boolean bool_sofiot1, Boolean bool_wholeWords) {
		return markMatchesInLine(line, searchSTR, htmlFormat, bool_sofiot1, bool_wholeWords, -1, false, null, null);
	}

	public static LineHtmlReport markMatchesInLine(String line, String searchSTR,
			HtmlGenerator htmlFormat, Boolean bool_sofiot1, Boolean bool_wholeWords,
			Boolean bool_sofiot2,String searchSTR2) {
		return markMatchesInLine(line, searchSTR, htmlFormat, bool_sofiot1, bool_wholeWords, -1, bool_sofiot2, searchSTR2, null);
	}

	public static LineHtmlReport markMatchesInLine(String line, String searchSTR,
			HtmlGenerator htmlFormat, Boolean bool_sofiot1, Boolean bool_wholeWords,
			int index) {
		return markMatchesInLine(line, searchSTR, htmlFormat, bool_sofiot1, bool_wholeWords, index, false, null, null);
	}

	public static LineHtmlReport markMatchesInLine(String line, String searchSTR,
			HtmlGenerator htmlFormat, Boolean bool_sofiot1, Boolean bool_wholeWords, int index,
			Boolean bool_sofiot2, String searchSTR2, ArrayList<Integer[]> addIndexes) {

		// Does not mark, if in console mode
		String lineHtml = "";
		String lineConvert1=ToraApp.getHebrew(line, bool_sofiot1);
		String searchConvert1=ToraApp.getHebrew(searchSTR, bool_sofiot1);
		String searchConvert2 = "";
		String lineConvert2 = "";
		if (searchSTR2 != null) {
				searchConvert2 = ToraApp.getHebrew(searchSTR2, bool_sofiot2);
				lineConvert2 = ToraApp.getHebrew(line, bool_sofiot2);
		}
		ArrayList<Integer[]> indexes = new ArrayList<Integer[]>();
		ArrayList<Integer[]> indexes2 = new ArrayList<Integer[]>();

		int myIndex;
		int tempIndex = lineConvert1.indexOf(searchConvert1);
		indexes.add(new Integer[] { tempIndex, tempIndex + searchConvert1.length() });
		try {
			if (indexes.get(0)[0] == -1) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		int newIndex = 0;
		int startPOS = indexes.get(0)[0];
		if (bool_wholeWords) {
			// checks for spaces before and after word
			boolean cancel = false;
			if ((indexes.get(0)[0] > 0) && (lineConvert1.charAt(indexes.get(0)[0] - 1) != ' ')) {
				cancel = true;
			}
			if (((indexes.get(0)[1] < lineConvert1.length()) && (lineConvert1.charAt(indexes.get(0)[1]) != ' '))) {
				cancel = true;
			}
			if (cancel) {
				startPOS += 1;
				indexes.remove(0);
			}
		}
		// find all occurences of searchSTR in Line and Color them
		while ((newIndex = lineConvert1.indexOf(searchConvert1, startPOS + 1)) != -1) {
			if (bool_wholeWords) {
				// checks for spaces before and after word
				Boolean cancel = false;
				if ((newIndex > 0) && (lineConvert1.charAt(newIndex - 1) != ' ')) {
					cancel = true;
				}
				if (((newIndex + searchConvert1.length() < lineConvert1.length())
						&& (lineConvert1.charAt(newIndex + searchConvert1.length()) != ' '))) {
					cancel = true;
				}
				if (!cancel) {
					startPOS = newIndex;
					indexes.add(new Integer[] { newIndex, newIndex + searchConvert1.length() });
				} else {
					startPOS += 1;
				}
			} else {
				startPOS = newIndex;
				indexes.add(new Integer[] { newIndex, newIndex + searchConvert1.length() });
			}
		}
		if (index != -1) {
			myIndex = indexes.get(index)[0];
			indexes = new ArrayList<Integer[]>();
			indexes.add(new Integer[] { myIndex, myIndex + searchConvert1.length() });
		}
		//combine indexes if addIndexes not null
		if (addIndexes != null) {
			indexes = mergeMarkIndexes(indexes, addIndexes);
		}
		// Search Word 2
		int myIndex2;
		if (searchSTR2 != null) {
			tempIndex = lineConvert2.indexOf(searchConvert2);
			indexes2.add(new Integer[] { tempIndex, tempIndex + searchConvert2.length() });
			try {
				if (indexes2.get(0)[0] == -1) {
					throw new IllegalArgumentException();
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			int newIndex2 = 0;
			int startPOS2 = indexes2.get(0)[0];
			if (bool_wholeWords) {
				// checks for spaces before and after word
				boolean cancel = false;
				if ((indexes2.get(0)[0] > 0) && (lineConvert2.charAt(indexes2.get(0)[0] - 1) != ' ')) {
					cancel = true;
				}
				if (((indexes2.get(0)[1] < lineConvert2.length()) && (lineConvert2.charAt(indexes2.get(0)[1]) != ' '))) {
					cancel = true;
				}
				if (cancel) {
					startPOS2 += 1;
					indexes2.remove(0);
				}
			}
			// find all occurences of searchSTR in Line and Color them
			while ((newIndex2 = lineConvert2.indexOf(searchConvert2, startPOS2 + 1)) != -1) {
				if (bool_wholeWords) {
					// checks for spaces before and after word
					Boolean cancel = false;
					if ((newIndex2 > 0) && (lineConvert2.charAt(newIndex2 - 1) != ' ')) {
						cancel = true;
					}
					if (((newIndex2 + searchConvert2.length() < lineConvert2.length())
							&& (lineConvert2.charAt(newIndex2 + searchConvert2.length()) != ' '))) {
						cancel = true;
					}
					if (!cancel) {
						startPOS2 = newIndex2;
						indexes2.add(new Integer[] { newIndex2, newIndex2 + searchConvert2.length() });
					} else {
						startPOS2 += 1;
					}
				} else {
					startPOS2 = newIndex2;
					indexes2.add(new Integer[] { newIndex2, newIndex2 + searchConvert2.length() });
				}
			}
			if (index != -1) {
				myIndex2 = indexes2.get(index)[0];
				indexes2 = new ArrayList<Integer[]>();
				indexes2.add(new Integer[] { myIndex2, myIndex2 + searchConvert2.length() });
			}

			//merge markIndexes
			indexes = mergeMarkIndexes(indexes, indexes2);
		}

		int lastIndex = 0;
		for (Integer[] thisIndex : indexes) {

			// Boolean wasSpace = false;
			String tempStr = "";
			if (thisIndex[0] > 0) {
				tempStr = line.substring(lastIndex, thisIndex[0]);
				/*
				 * if ((tempStr.length() >= 1) && (tempStr.charAt(tempStr.length() - 1) == ' '))
				 * { wasSpace = true; // removes whitespace from the end tempStr =
				 * tempStr.replaceFirst("\\s++$", ""); }
				 */
			}

			// lineHtml += tempStr + ((wasSpace) ? ToraApp.cSpace() : "") +
			// htmlFormat.getHtml(0)
			// + line.substring(thisIndex, STRLength + thisIndex) + htmlFormat.getHtml(1);

			lineHtml += tempStr + htmlFormat.getHtml(0) + line.substring(thisIndex[0], thisIndex[1])
					+ htmlFormat.getHtml(1);

			lastIndex = thisIndex[1];
		}
		lineHtml += line.substring(lastIndex);
		return new LineHtmlReport(lineHtml,line, indexes);
	}

	public static ArrayList<Integer[]> mergeMarkIndexes(ArrayList<Integer[]> indexes,ArrayList<Integer[]> indexes2){
		ArrayList<Integer[]> indexes3 = new ArrayList<Integer[]>();
		//merge markIndexes
		int countIndex = 0;
		int countIndex2 = 0;
		while (true) {
			if ((countIndex < indexes.size()) && (countIndex2 < indexes2.size())) {
				if (indexes.get(countIndex)[0] <= indexes2.get(countIndex2)[0]) {
					if (indexes.get(countIndex)[1] >= indexes2.get(countIndex2)[0]) {
						if (indexes.get(countIndex)[1] < indexes2.get(countIndex2)[1]) {
							indexes3.add(
									new Integer[] { indexes.get(countIndex)[0], indexes2.get(countIndex2)[1] });
							countIndex++;
							countIndex2++;
						} else {
							indexes3.add(new Integer[] { indexes.get(countIndex)[0], indexes.get(countIndex)[1] });
							countIndex++;
							countIndex2++;
						}
					} else {
						indexes3.add(new Integer[] { indexes.get(countIndex)[0], indexes.get(countIndex)[1] });
						countIndex++;
					}
				} else {
					if (indexes2.get(countIndex2)[1] >= indexes.get(countIndex)[0]) {
						if (indexes2.get(countIndex2)[1] < indexes.get(countIndex)[1]) {
							indexes3.add(
									new Integer[] { indexes2.get(countIndex2)[0], indexes.get(countIndex)[1] });
							countIndex++;
							countIndex2++;
						} else {
							indexes3.add(
									new Integer[] { indexes2.get(countIndex2)[0], indexes2.get(countIndex2)[1] });
							countIndex++;
							countIndex2++;
						}
					} else {
						indexes3.add(new Integer[] { indexes2.get(countIndex2)[0], indexes2.get(countIndex2)[1] });
						countIndex2++;
					}
				}
			} else {
				int lastIndex3 = indexes3.size()-1;
				if (countIndex < indexes.size()) {
					if (indexes.get(countIndex)[0]<=indexes3.get(lastIndex3)[1]) {
						if (indexes.get(countIndex)[1]>indexes3.get(lastIndex3)[1]) {
							indexes3.set(lastIndex3,new Integer[] {indexes3.get(lastIndex3)[0],indexes.get(countIndex)[1]});
						}
					} else {
						indexes3.add(new Integer[] { indexes.get(countIndex)[0], indexes.get(countIndex)[1] });
					}
					countIndex++;
				} else if (countIndex2 < indexes2.size()) {
					if (indexes2.get(countIndex2)[0]<=indexes3.get(lastIndex3)[1]) {
						if (indexes2.get(countIndex2)[1]>indexes3.get(lastIndex3)[1]) {
							indexes3.set(lastIndex3,new Integer[] {indexes3.get(lastIndex3)[0],indexes2.get(countIndex2)[1]});
						}
					} else {
						indexes3.add(new Integer[] { indexes2.get(countIndex2)[0], indexes2.get(countIndex2)[1] });
					}
					countIndex2++;
				} else {
					break;
				}
			}
		}
		return indexes3;
	}
	
	private static ArrayList<Integer[]> markWordIndex(String line, int[] wordsIndex) {
		ArrayList<Integer[]> indexes2 = new ArrayList<Integer[]>();
		String[] splitStr = line.trim().split("\\s+");

		int countLetters=0;
		int countWords=0;
		int countMatches=0;
		outer:
		for (String s:splitStr) {
			for (int i:wordsIndex) {
				if (i==countWords) {
					indexes2.add(new Integer[] {countLetters,countLetters+s.length()});
					countMatches++;
					if (countMatches==wordsIndex.length) {
						break outer;
					}
				}
			}
			countLetters += s.length()+1;
			countWords++;
		}
		return indexes2;
	}
	

	public static String markText(String str, HtmlGenerator markupStyle) {
		return markText(str, markupStyle, false);
	}

	public static String markText(String str, HtmlGenerator markupStyle, Boolean htmlTag) {
		// Does not mark, if in console mode
		if (!ToraApp.isGui()) {
			return str;
		}
		String html = (markupStyle.getHtml(0) + str + markupStyle.getHtml(1));
		if (htmlTag) {
			html = "<html>" + html + "</html>";
		}
		return html;
	}

	public static LineHtmlReport markTextOrderedLettersInPasuk(String searchSTR, String line, Boolean bool_sofiot,
			Boolean first, Boolean last, HtmlGenerator htmlFormat, int... wordsIndex) {
		//searchSTR - will find letters ordered but scattered, and will mark first appearance
		//wordsIndex - other words that should be marked.
		String lineConvert = ToraApp.getHebrew(line, bool_sofiot);
		String searchConvert = ToraApp.getHebrew(searchSTR, bool_sofiot);
		int oldIndex = 0;
		ArrayList<Integer[]> indexes = new ArrayList<Integer[]>();
		int indexCounter = 0;
		//find scattered ordered letters
		for (char ch : searchConvert.toCharArray()) {
			if ((last) && (indexCounter == (searchConvert.length() - 1))) {
				//if searchConvert has only one letter
				if ((first) && (searchConvert.length()==1)) {
					//mark first
					indexes.add(new Integer[] { 0, 1 });
				}
				//mark last
				int tempIndex = lineConvert.length()-1;
				indexes.add(new Integer[] { tempIndex, tempIndex + 1 });
			} else {
				int tempIndex = lineConvert.indexOf(ch, oldIndex);
				indexes.add(new Integer[] { tempIndex, tempIndex + 1 });
			}
			if (indexes.get(indexCounter)[0] == -1) {
				return new LineHtmlReport(line, line, null);
			} else {
				oldIndex = indexes.get(indexCounter++)[0]+1;
			}
		}
		//find words to mark
		if ((wordsIndex!=null) && (wordsIndex.length>0)) {
			//merge markIndexes
			indexes = mergeMarkIndexes(indexes, markWordIndex(line,wordsIndex));
		}
		
		//start marking
		return new LineHtmlReport(createLineHtml(line, indexes, htmlFormat), line, indexes);
	}

	public static LineHtmlReport markTextUnOrderedLettersInPasuk(String searchSTR, String line, Boolean bool_sofiot,
			Boolean first, Boolean last, HtmlGenerator htmlFormat, int... wordsIndex) {
		//searchSTR - will find letters unOrdered but scattered, and will mark first appearance
		//wordsIndex - other words that should be marked.
		String lineConvert = ToraApp.getHebrew(line, bool_sofiot);
		String searchConvert = ToraApp.getHebrew(searchSTR, bool_sofiot);
		//find scattered unOrdered letters
		//create letterMap
		LetterCounter lCounter = new LetterCounter();
		int indexCounter = 0;
		for (char ch : searchConvert.toCharArray()) {
			if 	(((!first) || (indexCounter!=0)) && ((!last) || (indexCounter != searchConvert.length()-1))) {
				lCounter.addChar(ch);					
			}
			indexCounter++;
		}
		
		ArrayList<Integer[]> indexes = new ArrayList<Integer[]>();
		indexCounter = 0;
		for (char ch : lineConvert.toCharArray()) {
			if (((first) && (indexCounter==0)) || 
			((last) && (indexCounter == lineConvert.length()-1))) {
				indexes.add(new Integer[] {indexCounter,indexCounter+1});
			} else {
				if (lCounter.findAndRemoveChar(ch)) {
					indexes.add(new Integer[] {indexCounter,indexCounter+1});
				}
			}
			indexCounter++;
		}
		//find words to mark
		if ((wordsIndex!=null) && (wordsIndex.length>0)) {
			//merge markIndexes
			indexes = mergeMarkIndexes(indexes, markWordIndex(line,wordsIndex));
		}
		
		//start marking
		return new LineHtmlReport(createLineHtml(line, indexes, htmlFormat), line, indexes);
	}

	private static String createLineHtml(String line, ArrayList<Integer[]> indexes, HtmlGenerator htmlFormat) {
		String lineHtml = "";
		int lastIndex = 0;
		for (Integer[] thisIndex : indexes) {
			String tempStr = "";
			if (thisIndex[0] > 0) {
				tempStr = line.substring(lastIndex, thisIndex[0]);
			}
			lineHtml += tempStr + htmlFormat.getHtml(0) + line.substring(thisIndex[0], thisIndex[1])
					+ htmlFormat.getHtml(1);

			lastIndex = thisIndex[1];
		}
		lineHtml += line.substring(lastIndex);
		return lineHtml;
	}
	
	public static String printSomePsukimHtml(int lineNum, int padLines, String lineText) {
		if (!ToraApp.isGui() || !Frame.getCheckBox_Tooltip()) {
			return "";
		}
		String line = "";
		try (BufferedReader bReader = ManageIO.getBufferedReader(
				MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line),false)){
			int firstLine = Math.max(0, lineNum - padLines - 1);
			for (int i = 0; i < firstLine; i++) {
				bReader.readLine();
			}
			for (int i = 0; i < (padLines * 2 + 1); i++) {
				if (i > 0) {
					line += "<br>";
				}
				String str="";
				if (i==padLines) {
					str += "-> "+"* ";
					if (lineText==""){
						str += bReader.readLine();
					} else {
						str += lineText;
						bReader.readLine();
					}
				} else {
					str += "* " + bReader.readLine();
				}
				line += str;
			}

			// Recieves words of Pasuk
		} catch (

		IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	public static String markTextBounds(String str, int startMark, int finishMark, HtmlGenerator markupStyle) {
		// Does not mark, if in console mode
		if (!ToraApp.isGui()) {
			return str;
		}
		String[] strArray = new String[] { "", "", "" };
		strArray[0] = str.substring(0, startMark);
		strArray[1] = markupStyle.getHtml(0) + str.substring(startMark, finishMark) + markupStyle.getHtml(1);
		strArray[2] = str.substring(finishMark);
		return strArray[0] + strArray[1] + strArray[2];
	}

	public static LineReport printPasukInfo(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords) throws NoSuchFieldException {
		return printPasukInfoExtraIndexes(countLines, searchSTR, line, markupStyle, bool_sofiot1, bool_wholeWords, -1, false,null,null);
	}

	public static LineReport printPasukInfo(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords, Boolean bool_sofiot2, String searchSTR2) throws NoSuchFieldException {
		return printPasukInfoExtraIndexes(countLines, searchSTR, line, markupStyle, bool_sofiot1, bool_wholeWords, -1, bool_sofiot2, searchSTR2, null);
	}

	public static LineReport printPasukInfo(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords, int index) throws NoSuchFieldException {
		return printPasukInfoExtraIndexes(countLines, searchSTR, line, markupStyle, bool_sofiot1, bool_wholeWords, -1, false, null, null);
	}
	
	public static LineReport printPasukInfo(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords, int index, Boolean bool_sofiot2, String searchSTR2) throws NoSuchFieldException {
		return printPasukInfoExtraIndexes(countLines, searchSTR, line, markupStyle, bool_sofiot1, bool_wholeWords, index, bool_sofiot2, searchSTR2, null);
	}
	
		public static LineReport printPasukInfoExtraIndexes(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords, ArrayList<Integer[]> indexes) throws NoSuchFieldException {
		return printPasukInfoExtraIndexes(countLines, searchSTR, line, markupStyle, bool_sofiot1, bool_wholeWords, -1, false,null, indexes);
	}

	public static LineReport printPasukInfoExtraIndexes(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords, Boolean bool_sofiot2, String searchSTR2, ArrayList<Integer[]> indexes) throws NoSuchFieldException {
		return printPasukInfoExtraIndexes(countLines, searchSTR, line, markupStyle, bool_sofiot1, bool_wholeWords, -1, bool_sofiot2, searchSTR2, indexes);
	}

	public static LineReport printPasukInfoExtraIndexes(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords, int index, ArrayList<Integer[]> indexes) throws NoSuchFieldException {
		return printPasukInfoExtraIndexes(countLines, searchSTR, line, markupStyle, bool_sofiot1, bool_wholeWords, -1, false, null, indexes);
	}

	public static LineReport printPasukInfoExtraIndexes(int countLines, String searchSTR, String line, HtmlGenerator markupStyle,
			Boolean bool_sofiot1, Boolean bool_wholeWords, int index, Boolean bool_sofiot2, String searchSTR2,ArrayList<Integer[]> indexes) throws NoSuchFieldException {
		ToraApp.perekBookInfo pBookInstance = ToraApp.findPerekBook(countLines);
		LineHtmlReport lineHtmlReport = new LineHtmlReport("","", new ArrayList<Integer[]>());
		String posText="";
		String searchText="";
		String tooltip="";
		try {
			String tempStr1 = "\u202B" + "\"" + markText(searchSTR, markupStyle) + "\" ";
			if (searchSTR2 != null) {
				tempStr1 += " | \"" + markText(searchSTR2, markupStyle) + "\" ";
			}
			searchText = tempStr1;
			tempStr1 += "נמצא ב";
			posText +=  markText(
					StringAlignUtils.padRight(pBookInstance.getBookName(), 6) + " " + pBookInstance.getPerekLetters()
							+ ":" + pBookInstance.getPasukLetters(),
					ColorClass.highlightStyleHTML[pBookInstance.getBookNumber()
							% ColorClass.highlightStyleHTML.length]);
			tempStr1+=posText;
			// Output.printText(StringAlignUtils.padRight(tempStr1, 32) + " = " + line);
			if (searchSTR2 != null) {
				lineHtmlReport = markMatchesInLine(line, searchSTR, markupStyle, bool_sofiot1, bool_wholeWords, index,
						bool_sofiot2, searchSTR2, indexes);
			} else {
				lineHtmlReport = markMatchesInLine(line, searchSTR, markupStyle, bool_sofiot1, bool_wholeWords, index, false, null, indexes);
			}
			String outputText = StringAlignUtils.padRight(tempStr1, 32) + " =    " + lineHtmlReport.getLineHtml(ToraApp.isGui());
			//printText(outputText, 0);
			tooltip = printSomePsukimHtml(countLines, padLines, lineHtmlReport.getLineHtml(ToraApp.isGui()));
			//printText("טקסט נוסף",3,tooltip);
		} catch (Exception e) {
			System.out.println("Error at line: " + countLines);
			e.printStackTrace();
		}
		String[] results = new String[] { searchSTR, pBookInstance.getBookName(), pBookInstance.getPerekLetters(),
				pBookInstance.getPasukLetters(), line };
		MatchLab.add(searchText,posText,lineHtmlReport.getLineHtml(ToraApp.isGui())
				,tooltip);
		return new LineReport(results, lineHtmlReport.getIndexes());
	}

	/*
	public static void printText(String text, String... tooltipText) {
		printText(text, (byte) 0, tooltipText);
	}

	public static void printText(String text, int mode, String... tooltipText) {
		printText(text, (byte) mode, tooltipText);
	}

	public static void printText(String text, byte mode, String... tooltipText) {
		// mode 0 = regular
		// mode 1 = attention
		// mode 2 = silence on GUI
		// mode 3 = label
		// mode 4 = print Torah
		switch (ToraApp.getGuiMode()) {
			case ToraApp.id_guiMode_Frame: // GUI Mode
				switch (mode) {
					case 0:
					case 1:
					case 4:
						Frame.appendText(text, mode);
						break;
					case 3:
						Frame.appendText(text, mode, tooltipText);
						// util.format is not needed because document is html and auto wraps text
						// Frame.appendText(util.format(text), mode);
						break;
					case 2:
						break;
				}
				break;
		}
	}

	 */
}
