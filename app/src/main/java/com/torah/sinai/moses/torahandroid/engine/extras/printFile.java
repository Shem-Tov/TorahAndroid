package com.torah.sinai.moses.torahandroid.engine.extras;

import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.MatchLab;
import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;
import com.torah.sinai.moses.torahandroid.engine.torahApp.TorahPlaceClass;

import java.io.BufferedReader;
import java.io.IOException;


public class printFile {

	public static void printTorah(Object[] args) {
		int[] searchRange;
		boolean bool_addInfo;
		try {
			searchRange = (args[0] != null) ? (int[]) args[0] : new int[] { 0, 0 };
			bool_addInfo = (args[1] != null) ? (Boolean) args[1] : true;
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			searchRange = new int[] {0,0};
			bool_addInfo = true;
		}
		int countLines = 0;
		//change to false if you do not want to print lastSearch
		BufferedReader br = ManageIO.getBufferedReader(ManageIO.fileMode.Line,true);
		if (br == null) {
			MainActivity.makeToast("לא הצליח לפתוח קובץ תורה");
			return;
		}
		try {
			//Output.printLine(2,4);

			// For some strange reason the Text
			// is being printed between these two
			// lines. printLine should be added to
			// the end of the function, but it
			// works better here.

			//if (!bool_addInfo) Output.printLine(2,4);
		    for (String line; (line = br.readLine()) != null;) {
				countLines++;
				if ((searchRange[1] != 0) && ((countLines <= searchRange[0]) || (countLines > searchRange[1]))) {
					continue;
				}
				MatchLab.newMatch();
				if (bool_addInfo) {
					TorahPlaceClass TorahPlace = ToraApp.checkStartBookParashaFromLineNum(countLines);
					if (TorahPlace.getName() != null) {
						//Output.printLine(2,4);
						MatchLab.addTitle(Output.markText(TorahPlace.getName(),ColorClass.highlightStyleHTML[0]));
					} else {
						MatchLab.addTitle("");
					}
					if (TorahPlace.getIsStartPerek()) {
						//Output.printLine(2,4);
					}
				} else {
					MatchLab.addTitle("");
				}
				MatchLab.addPosition((bool_addInfo) ? Output.markText(ToraApp.lookupTorahPositionFromLineNumber(countLines, false),ColorClass.highlightStyleHTML[2]):"");
				MatchLab.addPasuk(Output.markText(line, ColorClass.tooltipStyleHTML));
				MatchLab.addDetail("");
				if ((SearchFragment.getMethodCancelRequest())) {
					MainActivity.makeToast("\u202B" + "המשתמש הפסיק הדפסה באמצע");
					break;
				}
				// System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
