package com.torah.sinai.moses.torahandroid;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import com.torah.sinai.moses.torahandroid.engine.console.Methods;
import com.torah.sinai.moses.torahandroid.engine.hebrewLetters.HebrewLetters;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LastSearchClass;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.torah.sinai.moses.torahandroid.MainActivity.isTorahSearch;
import static java.lang.Integer.parseInt;

public class LongOperation extends AsyncTask<String, Map<String, String>, Void> {

    private static final String ID_LABEL_DPROGRESS = "Label_DProgress";
    private static final String ID_LABEL_COUNT_MATCH = "Label_Count_Match";
    private static final String ID_PROGRESS_UPDATE = "Progress_Update";

    @SuppressLint("StaticFieldLeak")
    private static LongOperation instance;

    private static int currentProgress;
    private static final int finalProgress_hardCoded = 5846;
    private static int finalProgress = finalProgress_hardCoded;
    private static boolean bool_finishOK=true;
    private SearchFragment.SearchModeEnum mSearchModeEnum;
    @SuppressLint("StaticFieldLeak")
    private View view;

    public static void setFinalProgress(int[] range) {
        if (range[1] == 0) {
            switch (MainActivity.getComboBox_DifferentSearch(ManageIO.fileMode.Line)) {
                case Line:
                case NoTevot:
                    finalProgress = finalProgress_hardCoded;
                    break;
                case Different:
                    finalProgress = ManageIO.countLinesOfFile(ManageIO.fileMode.Different);
                    break;
                case LastSearch:
                    finalProgress = LastSearchClass.getStoredSize();
                    break;
            }
        } else {
            finalProgress = range[1] - range[0];
        }
    }

    LongOperation(SearchFragment.SearchModeEnum mSMode, View v) {
        mSearchModeEnum = mSMode;
        view = v;
        instance = this;
    }


    private int getSpinner_Pos(int id) {
        Spinner spinner = view.findViewById(id);
        if (spinner == null) return -1;
        if (spinner.getVisibility() == View.GONE) return -1;
        return spinner.getSelectedItemPosition();
    }

    private String getEditText(int id) {
        try {
            EditText editText = view.findViewById(id);
            if (editText == null) return "";
            if (editText.getVisibility() == View.GONE) return "";
            return editText.getText().toString();
        } catch (Exception e) {
            return "";
        }
    }

    private boolean getCheckBox(int id, boolean def) {
        try {
            CheckBox checkbox = view.findViewById(id);
            if (checkbox == null) return def;
            if (checkbox.getVisibility() == View.GONE) return def;
            return checkbox.isChecked();
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        // TODO Auto-generated method stub
        String searchSTR1 = getEditText(R.id.edit_search)
                .replaceAll("%1", "יהוה").replaceAll("%2", "אלהים").trim();

        String searchSTR2 = getEditText(R.id.edit_search_2).replaceAll("%1", "יהוה").replaceAll("%2", "אלהים").trim();

        Boolean differentSearch = !isTorahSearch();
        if ((mSearchModeEnum != SearchFragment.SearchModeEnum.Print_Torah) &
                (mSearchModeEnum != SearchFragment.SearchModeEnum.Report_Count)){
            if ((searchSTR1 == null) || (searchSTR1.length() == 0)) {
                MainActivity.makeToast("חסר מילת חיפוש");
                bool_finishOK=false;
                return null;
            }
            if ((!differentSearch) && (mSearchModeEnum != SearchFragment.SearchModeEnum.Gimatria)
                    && !HebrewLetters.checkHebrew(searchSTR1)) {
                MainActivity.makeToast("ניתן להקליד בתיבת החיפוש רק אותיות עבריות ורווחים");
                bool_finishOK=false;
                return null;
            }
            if ((!differentSearch) && (mSearchModeEnum == SearchFragment.SearchModeEnum.Dilugim) && (searchSTR1.length() == 1)) {
                MainActivity.makeToast("נא להקליד יותר מאות אחת בתיבת החיפוש עבור החיפוש בדילוגים");
                bool_finishOK=false;
                return null;
            }
            if ((mSearchModeEnum != SearchFragment.SearchModeEnum.Calculate_Gimatria) && (differentSearch)) {
                MainActivity.makeToast("החיפוש לא נעשה בספר תורה"+"<br>"
                        +"חיפוש נעשה מקובץ - " + ToraApp.differentSearchFile + "<br>"
                        +"אם ברצונך לחפש בתורה -> תשנה \"קובץ משתמש\" לתורה ");
            }
            if (MainActivity.getComboBox_DifferentSearch(null) == ManageIO.fileMode.LastSearch) {
                switch (mSearchModeEnum) {
                    case Gimatria:
                    case Letters:
                    case Search:
                        MainActivity.makeToast("החיפוש לא נעשה בספר תורה"+"<br>"
                        + "חיפוש נעשה מתוצאות חיפוש קודם "+"<br>"
                        + "אם ברצונך לחפש בתורה -> תשנה \"חיפוש קודם\" לתורה ");
                        break;
                }
            }
        }
        switch (mSearchModeEnum) {
            case Gimatria:
            case Letters:
            case Search:
                SearchFragment.setBool_CanStore(true);
                break;
            default:
                SearchFragment.setBool_CanStore(false);
        }

        // args are used at the end
        // approximately line 197
        // in Method
        // Methods.arrayMethods.get
        Object[] args = {null};
        int selection = 0;
        try {
            switch (mSearchModeEnum) {
                case Search:
                    args = Arrays.copyOf(args, 7);
                    args[0] = searchSTR1;
                    args[1] = getCheckBox(R.id.checkbox_whole_word, false);
                    args[2] = getCheckBox(R.id.checkbox_sofiot, false);
                    args[3] = SearchFragment.get_searchRange(view);
                    args[4] = getCheckBox(R.id.checkbox_multi_search, false);
                    if (((boolean) args[4]) && (searchSTR2.length() > 0)) {
                        args[5] = searchSTR2;
                        if (!HebrewLetters.checkHebrew((String) args[5]) || (differentSearch)) {
                            MainActivity.makeToast("ניתן להקליד בתיבת החיפוש רק אותיות עבריות ורווחים");
                            bool_finishOK=false;
                            return null;
                        }
                        args[6] = (getSpinner_Pos(R.id.spinner_2) == 0);
                        SearchFragment.setSaveMode_search(getSpinner_Pos(R.id.spinner_2));
                        SearchFragment.setString_searchSTR2((String) args[5]);
                    }
                    SearchFragment.setBool_searchMultiple((boolean) args[4]);
                    selection = Methods.id_searchWords;
                    break;
                case Gimatria:
                    args = Arrays.copyOf(args, 5);
                    args[0] = searchSTR1;
                    args[1] = getCheckBox(R.id.checkbox_whole_word, false);
                    args[2] = getCheckBox(R.id.checkbox_sofiot, false);
                    args[3] = SearchFragment.get_searchRange(view);
                    // multiple whole words
                    args[4] = getCheckBox(R.id.checkbox_multi_search, false);
                    SearchFragment.setBool_gimatriaMultiple((Boolean) args[4]);
                    selection = Methods.id_searchGimatria;
                    break;
                case Calculate_Gimatria:
                    args = Arrays.copyOf(args, 2);
                    args[0] = searchSTR1;
                    args[1] = getCheckBox(R.id.checkbox_sofiot, false);
                    selection = Methods.id_calculateGimatria;
                    break;
                case Dilugim:
                    args = Arrays.copyOf(args, 8);
                    args[0] = searchSTR1;
                    args[1] = getCheckBox(R.id.checkbox_sofiot, false);
                    Boolean exitCode = false;
                    SearchFragment.setBool_reverseDilug(getCheckBox(R.id.checkbox_dilug_reverse, false));
                    //Output.printText("");
                    if (!StringUtils.isNumeric(getEditText(R.id.edit_dilug_min).trim())) {
                        MainActivity.makeToast("שדה 'דילוג מינימים' צריך להיות מספר");
                        exitCode = true;
                    }
                    if (!StringUtils.isNumeric(getEditText(R.id.edit_dilug_max).trim())) {
                        MainActivity.makeToast("שדה 'דילוג מקסימים' צריך להיות מספר");
                        exitCode = true;
                    }
                    if (!StringUtils.isNumeric(getEditText(R.id.edit_dilug_padding).trim())) {
                        MainActivity.makeToast("שדה 'מספר אותיות' צריך להיות מספר");
                        exitCode = true;
                    }
                    if (exitCode) {
                        bool_finishOK=false;
                        return null;
                    }
                    args[2] = getEditText(R.id.edit_dilug_min).trim();
                    args[3] = getEditText(R.id.edit_dilug_max).trim();
                    //args[4] is SearchStr2 in Original Program
                    args[4] = getEditText(R.id.edit_dilug_padding).trim();
                    SearchFragment.setString_padding_Dilug((String) args[4]);
                    args[5] = SearchFragment.get_searchRange(view);
                    args[6] = getCheckBox(R.id.checkbox_multi_search, false);
                    if (getSpinner_Pos(R.id.spinner_2) == 0) {
                        selection = Methods.id_searchDilugim;
                    } else {
                        args[7] = getSpinner_Pos(R.id.spinner_2);
                        selection = Methods.id_searchDilugWordPasuk;
                    }
                    SearchFragment.setSaveMode_dilugim(selection);
                    break;
                case Letters:
                    args = Arrays.copyOf(args, 13);
                    args[0] = searchSTR1;
                    args[1] = getCheckBox(R.id.checkbox_sofiot, false);
                    args[2] = SearchFragment.get_searchRange(view);
                    args[3] = getSpinner_Pos(R.id.spinner_2);
                    SearchFragment.setSaveMode_letter((int) args[3]);
                    // args[3] mode 0 = Words-Single
                    // mode 1 = Words-Multiple
                    // mode 2 = Psukim
                    args[4] = getCheckBox(R.id.checkbox_letters_order_1, false);
                    args[5] = getCheckBox(R.id.checkbox_first_1, false);
                    args[6] = getCheckBox(R.id.checkbox_last_1, false);
                    // 7 = exact spaces
                    args[7] = getCheckBox(R.id.checkbox_whole_word, false);
                    switch ((int) args[3]) {
                        case 2:
                        case 3:
                            SearchFragment.setBool_letter_exactSpaces((Boolean) args[7]);
                            break;
                    }
                    switch ((int) args[3]) {
                        case 1:
                        case 3:
                            // 8 = searchSTR2
                            args[8] = searchSTR2;
                            if (!HebrewLetters.checkHebrew((String) args[8]) || (differentSearch)) {
                                MainActivity.makeToast("ניתן להקליד בתיבת החיפוש רק אותיות עבריות");
                                bool_finishOK=false;
                                return null;
                            }
                            SearchFragment.setString_searchSTR2((String) args[8]);
                            args[9] = getCheckBox(R.id.checkbox_letters_order_2, false);
                            args[10] = getCheckBox(R.id.checkbox_first_2, false);
                            // 11 = last2
                            args[11] = getCheckBox(R.id.checkbox_last_2, false);
                            SearchFragment.setBool_letter_last2((Boolean) args[11]);
                            // 12 = sofiot
                            args[12] = getCheckBox(R.id.checkbox_sofiot_2, false);
                            break;
                    }
                    selection = Methods.id_searchLetters;
                    break;
                case Index:  //Originally called CountSearch
                    args = Arrays.copyOf(args, 5);
                    args[0] = searchSTR1;
                    args[1] = getCheckBox(R.id.checkbox_whole_word, false);
                    args[2] = getCheckBox(R.id.checkbox_sofiot, false);
                    args[3] = SearchFragment.get_searchRange(view);
                    args[4] = getEditText(R.id.edit_index_number).trim();
                    SearchFragment.setString_countIndex((String) args[4]);
                    selection = Methods.id_searchCount;
                    break;
                case Print_Torah:
                    args = Arrays.copyOf(args, 2);
                    args[0] = SearchFragment.get_searchRange(view);
                    args[1] = true;  // always mark
                    SearchFragment.setBool_placeInfo(true);
                    selection = Methods.id_printTorah;
                    break;
                case Report_Count:
                    args = SearchFragment.getReportArgs();
                    selection = Methods.id_ReportTorahCount;
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        try {
            if (selection >= 0) {
                DilugMatchLab.reset();
                MatchLab.reset();
                Methods.arrayMethods.get(selection).run(args);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        currentProgress = 0;
        SearchFragment.resetButton_storeSearch();
        showProgressBar(false, 0b11);
        SearchFragment.setMethodRunning(false, view);
        instance=null;
        view=null;
        MatchListFragment mFragment = MatchListFragment.getInstance();
        if (mFragment!=null){
            mFragment.update();
        }
        if ((bool_finishOK)&&
                (mSearchModeEnum!= SearchFragment.SearchModeEnum.Calculate_Gimatria)) {
            MainActivity.changeToResultTab();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Map<String, String>... values) {
        for (Map<String, String> map : values) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                switch (entry.getKey()) {
                    case ID_LABEL_COUNT_MATCH:
                        MainActivity.getInstance()
                                .mLabel_Progress_Count_Match
                                .setText(entry.getValue());
                        break;
                    case ID_LABEL_DPROGRESS:
                        MainActivity.getInstance()
                                .mLabel_Progress_dProgress
                                .setText(entry.getValue());
                        break;
                    case ID_PROGRESS_UPDATE:
                        MainActivity.getInstance()
                                .mProgressBar.setProgress(parseInt(entry.getValue()));
                }
            }
        }
    }

    public static void callProcess(int num) {
        callProcess(num, 1, 1, 1);
    }

    public static void callProcess(int num, int thisDilug, int minDilug, int maxDilug) {
        currentProgress = num;
        int factor = (int) (100 * (((float) currentProgress / finalProgress) + (thisDilug - minDilug))
                / (maxDilug - minDilug + 1));
        Map<String, String> map = new HashMap<String, String>();
        map.put(ID_PROGRESS_UPDATE, String.valueOf(factor));
        instance.publishProgress(map);
    }

    public static void showProgressBar(Boolean bool, int flag) {
        // 0b01 - progressBar and countLabel
        // 0b10 - label
        if ((flag & 0b01) == 0b01) {
            MainActivity.getInstance()
                    .mProgressBar
                    .setVisibility(bool ? View.VISIBLE : View.GONE);
            MainActivity.getInstance()
                    .mLabel_Progress_Count_Match
                    .setVisibility(bool ? View.VISIBLE : View.GONE);
        }
        if ((flag & 0b10) == 0b10) {
            MainActivity.getInstance()
                    .mLabel_Progress_dProgress
                    .setVisibility(bool ? View.VISIBLE : View.GONE);
        }
    }

    public static void setLabel_countMatch(String str) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(ID_LABEL_COUNT_MATCH, str);
        instance.publishProgress(map);
    }

    public static void setLabel_dProgress(String str) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(ID_LABEL_DPROGRESS, str);
        instance.publishProgress(map);

    }

}
