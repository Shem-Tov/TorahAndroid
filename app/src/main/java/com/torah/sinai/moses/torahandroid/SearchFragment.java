package com.torah.sinai.moses.torahandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.text.HtmlCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.torah.sinai.moses.torahandroid.engine.ioManagement.LastSearchClass;

import org.apache.commons.lang3.StringUtils;

import static com.torah.sinai.moses.torahandroid.MainActivity.isTorahSearch;

public class SearchFragment extends Fragment {

    private static final String DIALOG_RANGE = "DialigRange";


    @SuppressLint("StaticFieldLeak")
    private static SearchFragment instance = null;

    public static SearchFragment getInstance() {
        if (instance == null) {
            instance = new SearchFragment();
        }
        return instance;
    }

    public static boolean getMethodCancelRequest() {
        return methodCancelRequest;
    }

    enum LettersModeEnum {
        One_Word,
        Multi_Words,
        Verses,
        Words_and_Verses
    }

    private LettersModeEnum letterMode;

    LettersModeEnum getLettersEnum(int value) {
        switch (value) {
            case 0:
                letterMode = LettersModeEnum.One_Word;
                break;
            case 1:
                letterMode = LettersModeEnum.Multi_Words;
                break;
            case 2:
                letterMode = LettersModeEnum.Verses;
                break;
            case 3:
                letterMode = LettersModeEnum.Words_and_Verses;
                break;
        }
        return letterMode;
    }

    public static SearchModeEnum getSearchModeEnum(int value) {
        switch (value) {
            case 0:
                SearchMode = SearchModeEnum.Search;
                break;
            case 1:
                SearchMode = SearchModeEnum.Gimatria;
                break;
            case 2:
                SearchMode = SearchModeEnum.Calculate_Gimatria;
                break;
            case 3:
                SearchMode = SearchModeEnum.Dilugim;
                break;
            case 4:
                SearchMode = SearchModeEnum.Letters;
                break;
            case 5:
                SearchMode = SearchModeEnum.Index;
                break;
            case 6:
                SearchMode = SearchModeEnum.Print_Torah;
                break;
            case 7:
                SearchMode = SearchModeEnum.Report_Count;
                break;
        }
        return SearchMode;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    public enum SearchModeEnum {
        Search,
        Gimatria,
        Calculate_Gimatria,
        Dilugim,
        Letters,
        Index,
        Print_Torah,
        Report_Count
    }

    public static SearchModeEnum SearchMode;
    //General
    public Spinner mSpinner_2;
    public Button mButton_Range;
    public Button mButton_Start_Search;
    public CheckBox mCB_Range;
    //Search_Search
    public LinearLayout mLinearLayout_Search_2;
    public CheckBox mCB_Multi;
    //Search Letters
    public LinearLayout mLinearLayout_Letters_2;
    public CheckBox mCB_Letters_Pasuk_Exact;
    public TextView mLabel_Search_1, mLabel_Search_2;
    //Dilug
    public EditText mDilug_Padding;
    //Index
    public EditText mIndex_Count;

    private ViewGroup mViewGroup;
    private View view = null;
    private SearchModeEnum mSearchModeEnum;

    protected View inflateNew(int resource) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(resource, mViewGroup, false);
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //loads AsyncTask
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mViewGroup = container;
        View rootView = null;
        Bundle bundle = getArguments();
        int searchMode = bundle.getInt(MainActivity.ARGS_ADAPTER);

        //rootView = initialize(SearchModeEnum.Search);
        if (searchMode >= 0) {
            mSearchModeEnum = getSearchModeEnum(searchMode);
            rootView = initialize(mSearchModeEnum);
            mButton_Start_Search = rootView.findViewById(R.id.button_search);
            switch (mSearchModeEnum) {
                case Search:
                case Index:
                case Dilugim:
                case Letters:
                case Gimatria:
                    mButton_Start_Search.setText(getText(R.string.button_search));
                    break;
                case Print_Torah:
                    mButton_Start_Search.setText(getText(R.string.button_print));
                    break;
                case Report_Count:
                    mButton_Start_Search.setText(getText(R.string.button_report));
                    break;
                case Calculate_Gimatria:
                    mButton_Start_Search.setText(getText(R.string.button_calculate));
            }
            //General - Must be after initialize();
            switch (mSearchModeEnum) {
                case Search:
                case Index:
                case Dilugim:
                case Letters:
                case Gimatria:
                case Print_Torah:
                    mButton_Range = rootView.findViewById(R.id.button_range);
                    mButton_Range.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager manager = getFragmentManager();
                            RangePickerFragment dialog = RangePickerFragment
                                    .newInstance(true, null);
                            dialog.setTargetFragment(SearchFragment.this, RangePickerFragment.REQUEST_RANGE);
                            dialog.show(manager, DIALOG_RANGE);
                         }
                    });
                    mCB_Range = rootView.findViewById(R.id.checkbox_range);
                    mCB_Range.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                setCB_Range_Text(searchRangeStringHTML, true);
                            } else {
                                setCB_Range_Text(getString(R.string.checkbox_range_all), false);
                            }
                        }
                    });
            }
            mDilug_Padding = rootView.findViewById(R.id.edit_dilug_padding);
            mIndex_Count = rootView.findViewById(R.id.edit_index_number);
            mButton_Start_Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!methodRunning) {
                        switch (mSearchModeEnum) {
                            case Dilugim:
                                if (StringUtils.isNumeric(mDilug_Padding.getText().toString())) {
                                    setString_padding_Dilug(mDilug_Padding.getText().toString());
                                }
                                LongOperation.showProgressBar(true, 0b11);
                                break;
                            case Index:
                                if (StringUtils.isNumeric(mIndex_Count.getText().toString())) {
                                    setString_countIndex(mIndex_Count.getText().toString());
                                }
                                LongOperation.showProgressBar(true, 0b01);
                                break;
                            case Letters:
                            case Search:
                            case Gimatria:
                                LongOperation.showProgressBar(true, 0b01);
                        }
                        if (mSearchModeEnum == SearchModeEnum.Report_Count) {
                            FragmentManager manager = getFragmentManager();
                            RangePickerFragment dialog = RangePickerFragment
                                    .newInstance(false,v.getRootView());
                            dialog.setTargetFragment(SearchFragment.this, RangePickerFragment.REQUEST_RANGE);
                            if (manager != null) {
                                dialog.show(manager, DIALOG_RANGE);
                            }
                        } else {
                            methodRunning = true;
                            mButton_Start_Search.setText(R.string.button_search_cancel);
                            new LongOperation(mSearchModeEnum, v.getRootView()).execute("");
                        }
                     } else {
                        methodCancelRequest = true;
                        mButton_Start_Search.setText(R.string.button_search_canceling);
                    }

                }
            });

        }
        return rootView;
    }

    public View initialize(SearchModeEnum sMode) {
        //Change SearchMode to change Activity
        View rootView;
        SearchMode = sMode;
        switch (SearchMode) {
            case Search:
                rootView = setSearchSearch();
                break;
            case Gimatria:
                rootView = setSearchGimatria();
                break;
            case Calculate_Gimatria:
                rootView = inflateNew(R.layout.activity_calculate_gimatria);
                break;
            case Dilugim:
                rootView = setSearchDilugim();
                break;
            case Letters:
                rootView = setSearchLetters();
                break;
            case Index:
                rootView = setSearchIndex();
                break;
            case Print_Torah:
                rootView = setPrintTorah();
                break;
            case Report_Count:
                rootView = inflateNew(R.layout.activity_report_count);
                break;
            default:
                rootView = null;
        }
        return rootView;
    }

    private View setSearchSearch() {
        View v = inflateNew(R.layout.activity_search_search);
        mCB_Multi = v.findViewById(R.id.checkbox_multi_search);
        mSpinner_2 = v.findViewById(R.id.spinner_2);
        mLinearLayout_Search_2 = v.findViewById(R.id.layout_search_2);
        setBy_CB_Multi(mCB_Multi.isChecked());
        mCB_Multi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SearchMode == SearchModeEnum.Search) {
                    setBy_CB_Multi(isChecked);
                }
            }
        });
        return v;
    }

    public static void resetButton_storeSearch() {
        bool_stored = false;
        if (LastSearchClass.getStoredSize() == -1) {
            //button_storeSearch.setBackground(ColorBG_textPane);
        } else {
            //button_storeSearch.setBackground(ColorBG_menu);
        }
    }

    //Used in setSearchSearch()
    private void setBy_CB_Multi(boolean isChecked) {
        if (isChecked) {
            mSpinner_2.setVisibility(View.VISIBLE);
            mLinearLayout_Search_2.setVisibility(View.VISIBLE);
        } else {
            mSpinner_2.setVisibility(View.GONE);
            mLinearLayout_Search_2.setVisibility(View.GONE);
        }
    }

    private View setSearchGimatria() {
        View v = inflateNew(R.layout.activity_search_gimatria);

        return v;
    }

    private View setPrintTorah() {
        View v = inflateNew(R.layout.activity_print_torah);

        return v;
    }

    private View setSearchDilugim() {
        View v = inflateNew(R.layout.activity_search_dilugim);
        if (true) {
            ((EditText) v.findViewById(R.id.edit_search)).setText("אדם");
            ((EditText) v.findViewById(R.id.edit_dilug_max)).setText("4");
            ((EditText) v.findViewById(R.id.edit_dilug_min)).setText("2");
            ((EditText) v.findViewById(R.id.edit_dilug_padding)).setText("20");
        }
        return v;
    }

    private View setSearchLetters() {
        View v = inflateNew(R.layout.activity_search_letters);
        mSpinner_2 = v.findViewById(R.id.spinner_2);
        mLinearLayout_Letters_2 = v.findViewById(R.id.layout_letters_2);
        mCB_Letters_Pasuk_Exact = v.findViewById(R.id.checkbox_letters_pasuk_exact_space);
        mLabel_Search_1 = v.findViewById(R.id.label_search_1);
        mLabel_Search_2 = v.findViewById(R.id.label_search_2);

        mSpinner_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (SearchMode == SearchModeEnum.Letters) {
                    switch (getLettersEnum(position)) {
                        case One_Word:
                            mCB_Letters_Pasuk_Exact.setVisibility(View.GONE);
                            mLinearLayout_Letters_2.setVisibility(View.GONE);
                            mLabel_Search_1.setText(R.string.label_letters_word);
                            break;
                        case Multi_Words:
                            mCB_Letters_Pasuk_Exact.setVisibility(View.GONE);
                            mLinearLayout_Letters_2.setVisibility(View.VISIBLE);
                            mLabel_Search_1.setText(R.string.label_letters_word);
                            mLabel_Search_2.setText(R.string.label_letters_word);
                            break;
                        case Verses:
                            mCB_Letters_Pasuk_Exact.setVisibility(View.VISIBLE);
                            mLinearLayout_Letters_2.setVisibility(View.GONE);
                            mLabel_Search_1.setText(R.string.label_letters_pasuk);
                            break;
                        case Words_and_Verses:
                            mCB_Letters_Pasuk_Exact.setVisibility(View.VISIBLE);
                            mLinearLayout_Letters_2.setVisibility(View.VISIBLE);
                            mLabel_Search_1.setText(R.string.label_letters_pasuk);
                            mLabel_Search_2.setText(R.string.label_letters_word);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return v;
    }

    private View setSearchIndex() {
        View v = inflateNew(R.layout.activity_search_index);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == RangePickerFragment.REQUEST_RANGE) {
            String str = data.getStringExtra(
                    RangePickerFragment.EXTRA_RANGE_STR);
            String strHTML = data.getStringExtra(
                    RangePickerFragment.EXTRA_RANGE_STR_HTML);
            int start = data.getIntExtra(
                    RangePickerFragment.EXTRA_RANGE_START, 0);
            int end = data.getIntExtra(
                    RangePickerFragment.EXTRA_RANGE_END, 0);
            setSearchRange(start, end, str, strHTML);
        }

        if (requestCode == RangePickerFragment.REQUEST_RANGE_FOR_REPORT) {
            reportArgs = data.getStringArrayExtra(RangePickerFragment.EXTRA_REPORT_ARGS);
        }
    }

    public static Object[] getReportArgs() {
        return reportArgs;
    }

    private static Object[] reportArgs;
    private static Boolean methodCancelRequest = false;
    private static Boolean methodRunning = false;
    private static String[] name_Replace1 = {"יהוה", "ה'"};

    public static void setMethodRunning(Boolean bool, View view) {
        methodRunning = bool;
        Button button = view.findViewById(R.id.button_search);
        if (bool) {
            button.setText(R.string.button_search_cancel);
        } else {
            button.setText(R.string.button_search);
            methodCancelRequest = false;
        }
    }

    public void setSearchRange(int start, int end, String str, String strHTML) {
        searchRange = new int[]{start, end};
        searchRangeString = str;
        searchRangeStringHTML = strHTML;
        setCB_Range_Text(strHTML, true);
        setCB_Range_Checked(true);
    }

    public void setCB_Range_Checked(boolean checked) {
        try {
            mCB_Range.setChecked(checked);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCB_Range_Text(String str, boolean isHTML) {
        try {
            if (isHTML) {
                mCB_Range.setText(HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                mCB_Range.setText(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getCB_Range_isChecked(View v) {
        try {
            return ((CheckBox) v.findViewById(R.id.checkbox_range)).isChecked();
        } catch (Exception e) {
            return false;
        }
    }

    public static int[] get_searchRange(View v) {
        // Functions only with Torah file, not with custom files
        if ((getCB_Range_isChecked(v)) && (isTorahSearch())) {
            return searchRange;
        } else {
            return new int[]{0, 0};
        }
    }

    public static String get_searchRangeText(View v) {
        // checks if frame exists

        if ((getCB_Range_isChecked(v))) {
            return searchRangeString;
        } else {
            return "";
        }
    }

    private static int[] searchRange = new int[]{0, 0};
    private static final String searchRangeAll = "הכול";
    private static String searchRangeString = searchRangeAll;
    private static String searchRangeStringHTML = searchRangeAll;


    public static void setSaveMode_search(int num) {
        savedMode_search = num;
    }

    public static void setSaveMode_letter(int num) {
        savedMode_letter = num;
    }

    public static void setSaveMode_dilugim(int num) {
        savedMode_dilugim = num;
    }

    private static int savedMode_search = 0;
    private static int savedMode_letter = 0;
    private static int savedMode_dilugim = 0;

    public static void setString_countIndex(String str) {
        savedString_countIndex = str;
    }

    public static void setString_padding_Dilug(String str) {
        savedString_padding_Dilug = str;
    }

    public static void setString_searchSTR2(String str) {
        savedString_searchSTR2 = str;
    }

    private static String savedString_countIndex = "";
    private static String savedString_padding_Dilug = "";
    private static String savedString_searchSTR2 = "";

    public static void setBool_placeInfo(Boolean bool) {
        bool_placeInfo = bool;
    }

    public static void setBool_searchMultiple(Boolean bool) {
        bool_searchMultiple = bool;
    }

    public static void setBool_gimatriaMultiple(Boolean bool) {
        bool_gimatriaMultiple = bool;
    }

    public static void setBool_reverseDilug(Boolean bool) {
        bool_dilugReversed = bool;
    }

    public static void setBool_letter_exactSpaces(Boolean bool) {
        bool_letters_exactSpaces = bool;
    }

    public static void setBool_letter_last2(Boolean bool) {
        bool_letters_last2 = bool;
    }

    public static void setBool_CanStore(Boolean bool) {
        bool_canStore = bool;
    }

    public static void setBool_Stored(Boolean bool) {
        bool_stored = bool;
    }

    private static Boolean bool_placeInfo = true;
    private static Boolean bool_dilugReversed = false;
    private static Boolean bool_searchMultiple = false;
    private static Boolean bool_gimatriaMultiple = false;
    private static Boolean bool_letters_last2 = false;
    private static Boolean bool_letters_exactSpaces = false;
    private static Boolean bool_stored = false;
    private static Boolean bool_canStore = false;

    public static Boolean getBool_stored() {
        return bool_stored;
    }

    public static Boolean getBool_canStore() {
        return bool_canStore;
    }
}

