package com.torah.sinai.moses.torahandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.LastSearchClass;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;
import com.torah.sinai.moses.torahandroid.main.SectionsStatePagerAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooser;

import static com.torah.sinai.moses.torahandroid.SearchFragment.SearchMode;
import static com.torah.sinai.moses.torahandroid.SearchFragment.getSearchModeEnum;
import static com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp.starter;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static MainActivity instance;
    private static SearchFragment.SearchModeEnum searchMode;
    public static String ARGS_ADAPTER = "searchMode";

    public static MainActivity getInstance() {
        return instance;
    }

    public Spinner mSpinner_main;
    Spinner mSpinner_input;
    Button mButton_Store_Search;
    SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    ProgressBar mProgressBar;
    TextView mLabel_Progress_Count_Match;
    TextView mLabel_Progress_dProgress;
    private static int mSpinner_mode;

    public static boolean isTorahSearch() {
        try {
            switch (mSpinner_mode) {
                case 0:
                    // Regular Torah Search
                case 1:
                    // Torah Search from Last Search
                    return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public static ManageIO.fileMode getComboBox_DifferentSearch(ManageIO.fileMode fMode) {
        try {
            switch (mSpinner_mode) {
                case 0:
                    return fMode;
                case 1:
                    return ManageIO.fileMode.LastSearch;
                case 2:
                    return ManageIO.fileMode.Different;
            }
        } catch (Exception e) {
            return fMode;
        }
        return fMode;
    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        SearchFragment mSearchFragment;
        mSearchFragment = SearchFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_ADAPTER, 0);
        searchMode = SearchFragment.SearchModeEnum.Search;
        mSearchFragment.setArguments(bundle);
        mSectionsStatePagerAdapter.addFragment(mSearchFragment, getString(R.string.tab_text_1), 0);
        Bundle thisBundle = new Bundle();
        thisBundle.putInt(ARGS_ADAPTER, 0);
        MatchListFragment mListFragment = new MatchListFragment();
        mListFragment.setArguments(thisBundle);
        mSectionsStatePagerAdapter.addFragment(mListFragment, getString(R.string.tab_text_2), 1);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(mSectionsStatePagerAdapter);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        try {
            starter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        mSpinner_main = findViewById(R.id.spinner_main);
        ArrayAdapter spinnerArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_spinner_main, R.layout.spinner_item);

        mSpinner_main.setAdapter(spinnerArrayAdapter);
        mSpinner_main.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SearchFragment mSearchFragment;
                mSearchFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(ARGS_ADAPTER, position);
                mSearchFragment.setArguments(bundle);
                if (getSearchModeEnum(position) != SearchFragment.SearchModeEnum.Calculate_Gimatria) {
                    if (getSearchModeEnum(position) == SearchFragment.SearchModeEnum.Dilugim) {
                        if (getSearchModeEnum(position) != searchMode) {
                            Bundle thisBundle = new Bundle();
                            searchMode = getSearchModeEnum(position);
                            thisBundle.putInt(ARGS_ADAPTER, position);
                            MatchListFragment mListFragment = new MatchListFragment();
                            mListFragment.setArguments(thisBundle);
                            mSectionsStatePagerAdapter.removeFragment(1);
                            mSectionsStatePagerAdapter.addFragment(mListFragment, getString(R.string.tab_text_2), 1);
                        }
                    } else if (getSearchModeEnum(position) == SearchFragment.SearchModeEnum.Report_Count) {
                        if (getSearchModeEnum(position) != searchMode) {
                            Bundle thisBundle = new Bundle();
                            searchMode = getSearchModeEnum(position);
                            thisBundle.putInt(ARGS_ADAPTER, position);
                            MatchListFragment mListFragment = new MatchListFragment();
                            mListFragment.setArguments(thisBundle);
                            mSectionsStatePagerAdapter.removeFragment(1);
                            mSectionsStatePagerAdapter.addFragment(mListFragment, getString(R.string.tab_text_2), 1);
                        }
                    } else {
                        if ((searchMode == SearchFragment.SearchModeEnum.Report_Count)
                                || (searchMode == SearchFragment.SearchModeEnum.Dilugim)) {
                            Bundle thisBundle = new Bundle();
                            searchMode = getSearchModeEnum(position);
                            thisBundle.putInt(ARGS_ADAPTER, position);
                            MatchListFragment mListFragment = new MatchListFragment();
                            mListFragment.setArguments(thisBundle);
                            mSectionsStatePagerAdapter.removeFragment(1);
                            mSectionsStatePagerAdapter.addFragment(mListFragment, getString(R.string.tab_text_2), 1);
                        }
                    }
                }
                mSectionsStatePagerAdapter.removeFragment(0);
                mSectionsStatePagerAdapter.addFragment(mSearchFragment, getString(R.string.tab_text_1), 0);
                mSectionsStatePagerAdapter.notifyDataSetChanged();
                MainActivity.changeToSearchTab();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        mSpinner_input = findViewById(R.id.spinner_input_method);
        mSpinner_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mSpinner_mode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        mButton_Store_Search = findViewById(R.id.button_store_search);
        mButton_Store_Search.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                storeFileChooser();
                return true;
            }
        });
        mButton_Store_Search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SearchFragment.getBool_canStore()) {
                    if (!SearchFragment.getBool_stored()) {
                        try {
                            SearchFragment.setBool_Stored(LastSearchClass.getInstance().storeCurrent());
                            mSpinner_input.setSelection(1);
                        } catch (NullPointerException e) {

                        }
                        if (SearchFragment.getBool_stored()) {
                            if (android.os.Build.VERSION.SDK_INT >= 23) {
                                mButton_Store_Search.setBackgroundColor(ContextCompat.getColor(getApplication(), R.color.colorBG_comboBox_main));
                                // Do something for lollipop and above versions
                            } else {
                                mButton_Store_Search.setBackgroundColor(getResources().getColor(R.color.colorBG_comboBox_main));
                                // do something for phones running an SDK before lollipop
                            }
                        }
                    } else {
                        storeFileChooser();
                    }
                } else {

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("הודעה")
                            .setMessage("ניתן לשמור רק חיפוש רגיל / אותיות / גימטריה")
                            .setPositiveButton(R.string.range_picker_button_ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }
            }
        });

        mProgressBar =findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.setMax(100);
        mLabel_Progress_Count_Match =

                findViewById(R.id.label_progress_count_match);
        mLabel_Progress_Count_Match.setVisibility(View.GONE);
        mLabel_Progress_dProgress =

                findViewById(R.id.label_progress_dprogress);
        mLabel_Progress_dProgress.setVisibility(View.GONE);


 /*
        //Creates a clickable button on screen

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Place this in activity_main.xml

        <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="@dimen/fab_margin"
        app:srcCompat="@android:drawable/ic_dialog_email" />

  */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private void storeFileChooser() {
        // TODO Auto-generated method stub
        String extension = LastSearchClass.lastSearchFileExtension_HardCoded.substring(1); // remove dot (.)
       /*
        FileChooser.Builder builder = new FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER, new FileChooser.ChooserListener() {
            @Override
            public void onSelect(String path) {
                String[] selectedFilePaths = path.split(FileChooser.FILE_NAMES_SEPARATOR);
                File filename = new File(selectedFilePaths[0]);
                try {
                    LastSearchClass.load(filename);
                    mSpinner_input.setSelection(1);
                } catch (Exception e) {
                    Toast toast = new Toast(MainActivity.this);
                    toast.makeText(MainActivity.this, getString(R.string.toast_error_load_file), Toast.LENGTH_SHORT).show();
                }
                Log.i("File Name", selectedFilePaths[0]);

                // Do whatever you want to do with selected files
            }
        })
                .setMultipleFileSelectionEnabled(true)
                .setFileFormats(new String[]{".txt"})
                .setListItemsTextColor(R.color.colorPrimary)
                .setPreviousDirectoryButtonIcon(R.drawable.ic_prev_dir)
                .setDirectoryIcon(R.drawable.ic_directory)
                .setFileIcon(R.drawable.ic_file);
        try {
            FileChooser fileChooserFragment = builder.build();
        } catch (ExternalStorageNotAvailableException e) {
            e.printStackTrace();
        }
        */
    }

    public static void changeToResultTab() {
        TabLayout tabhost = (TabLayout) instance.findViewById(R.id.tabs);
        tabhost.getTabAt(1).select();
    }

    public static void changeToSearchTab() {
        TabLayout tabhost = (TabLayout) instance.findViewById(R.id.tabs);
        tabhost.getTabAt(0).select();
    }

    /*
      public static void makeToast(String message) {
       instance.runOnUiThread(new Runnable() {
           public void run() {
               Toast.makeText(instance, message, Toast.LENGTH_SHORT).show();
           }
       });
   }*/
    public static void makeToast(String message) {
        makeLargeToast(message, 36, Color.BLACK, ColorClass.getColorCode(ColorClass.color_attentionHTML));
    }

    public static void makeLargeToast(String message, int size, int textColor, int backgroundColor) {
        instance.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(instance, message, Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) toast.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
                if (backgroundColor != -1) {
                    group.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN);
                }
                if (textColor != -1) {
                    messageTextView.setTextColor(textColor);
                }
                toast.show();
            }
        });
    }


}