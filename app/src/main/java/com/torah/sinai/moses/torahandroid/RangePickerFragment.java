package com.torah.sinai.moses.torahandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;

import java.util.ArrayList;
import java.util.Arrays;


public class RangePickerFragment extends DialogFragment {
    public static final String EXTRA_RANGE_START =
            "com.torah.sinai.moses.torahandroid.range_start";
    public static final String EXTRA_RANGE_END =
            "com.torah.sinai.moses.torahandroid.range_end";
    public static final String EXTRA_RANGE_STR =
            "com.torah.sinai.moses.torahandroid.range_str";
    public static final String EXTRA_RANGE_STR_HTML =
            "com.torah.sinai.moses.torahandroid.range_str_html";
    public static final String EXTRA_REPORT_ARGS =
            "com.torah.sinai.moses.torahandroid.report_args";
    public static final int REQUEST_RANGE = 0;
    public static final int REQUEST_RANGE_FOR_REPORT = 1;

    private static final String ARG_SET_RANGE = "setRange";

    private CheckBox mCB_range_1, mCB_range_2;
    private Spinner mSpinner_Book_1, mSpinner_Perek_1;
    private Spinner mSpinner_Book_2, mSpinner_Perek_2;
    private Spinner mSpinner_Pasuk_1, mSpinner_Pasuk_2;
    private static View targetView;

    public static RangePickerFragment newInstance(boolean setRange, View view) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_SET_RANGE, setRange);
        targetView = view;
        RangePickerFragment fragment = new RangePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Date date = (Date) getArguments().getSerializable(ARG_RANGE);
        Boolean setRange = getArguments().getBoolean(ARG_SET_RANGE);

        final ViewGroup nullParent = null;
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_range, nullParent);

        mCB_range_1 = v.findViewById(R.id.dialog_range_cb1);
        mCB_range_2 = v.findViewById(R.id.dialog_range_cb2);
        mSpinner_Book_1 = v.findViewById(R.id.dialog_range_spinner1_a);
        mSpinner_Perek_1 = v.findViewById(R.id.dialog_range_spinner1_b);
        mSpinner_Book_2 = v.findViewById(R.id.dialog_range_spinner2_a);
        mSpinner_Perek_2 = v.findViewById(R.id.dialog_range_spinner2_b);
        mSpinner_Pasuk_1 = v.findViewById(R.id.dialog_range_spinner1_c);
        mSpinner_Pasuk_2 = v.findViewById(R.id.dialog_range_spinner2_c);

        relistComboPerek(mSpinner_Perek_1,mSpinner_Book_1.getSelectedItemPosition());
        relistComboPerek(mSpinner_Perek_2,mSpinner_Book_2.getSelectedItemPosition());
        relistComboPasuk(mSpinner_Pasuk_1, mSpinner_Book_1.getSelectedItemPosition()
                , mSpinner_Perek_1.getSelectedItemPosition());
        relistComboPasuk(mSpinner_Pasuk_2, mSpinner_Book_2.getSelectedItemPosition()
                , mSpinner_Perek_2.getSelectedItemPosition());
        //avoid trigger on initialization
        avoidTrigger();

        if (setRange) {
            mSpinner_Pasuk_1.setVisibility(View.GONE);
            mSpinner_Pasuk_2.setVisibility(View.GONE);
        }

        mSpinner_Book_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!mCB_range_1.isChecked()) {
                    Spinner spinner = (Spinner) parentView;
                    if (spinner.getSelectedItemPosition() == 5) {
                        mSpinner_Perek_1.setVisibility(View.GONE);
                        if (!setRange)
                            mSpinner_Pasuk_1.setVisibility(View.GONE);
                    } else {
                        mSpinner_Perek_1.setVisibility(View.VISIBLE);
                        relistComboPerek(mSpinner_Perek_1, spinner.getSelectedItemPosition());
                        if (!setRange) {
                            mSpinner_Pasuk_1.setVisibility(View.VISIBLE);
                            relistComboPasuk(mSpinner_Pasuk_1, spinner.getSelectedItemPosition(), mSpinner_Perek_1.getSelectedItemPosition());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        mSpinner_Book_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!mCB_range_2.isChecked()) {
                    Spinner spinner = (Spinner) parentView;
                    if (spinner.getSelectedItemPosition() == 5) {
                        mSpinner_Perek_2.setVisibility(View.GONE);
                        if (!setRange)
                            mSpinner_Pasuk_2.setVisibility(View.GONE);
                    } else {
                        mSpinner_Perek_2.setVisibility(View.VISIBLE);
                        relistComboPerek(mSpinner_Perek_2, spinner.getSelectedItemPosition());
                        if (!setRange) {
                            mSpinner_Pasuk_2.setVisibility(View.VISIBLE);
                            relistComboPasuk(mSpinner_Pasuk_2, spinner.getSelectedItemPosition(),
                                    mSpinner_Perek_2.getSelectedItemPosition());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        mSpinner_Perek_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                if ((!mCB_range_1.isChecked()) && (!setRange)) {
                    Spinner spinner = (Spinner) parentView;
                    relistComboPasuk(mSpinner_Pasuk_1, mSpinner_Book_1.getSelectedItemPosition()
                            , spinner.getSelectedItemPosition());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        mSpinner_Perek_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                if ((!mCB_range_2.isChecked()) && (!setRange)) {
                    Spinner spinner = (Spinner) parentView;
                    relistComboPasuk(mSpinner_Pasuk_2, mSpinner_Book_2.getSelectedItemPosition()
                            , spinner.getSelectedItemPosition());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        mCB_range_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                   @Override
                                                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                       CheckBox cb = (CheckBox) buttonView;
                                                       if (isChecked) {
                                                           cb.setText(R.string.range_picker_checkbox_parasha);
                                                           mSpinner_Perek_1.setVisibility(View.GONE);
                                                           if (!setRange)
                                                               mSpinner_Pasuk_1.setVisibility(View.GONE);
                                                           String[] spinnerArray = ToraApp.getParashaNames();
                                                           ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                                                           mSpinner_Book_1.setAdapter(spinnerArrayAdapter);
                                                       } else {
                                                           cb.setText(R.string.range_picker_checkbox_chapter);
                                                           mSpinner_Perek_1.setVisibility(View.VISIBLE);
                                                           ArrayList<String> spinnerArray = new ArrayList<String> (Arrays.asList(ToraApp.getBookNames()));
                                                           spinnerArray.add(getString(R.string.range_picker_spinner_end));
                                                           ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                                                           mSpinner_Book_1.setAdapter(spinnerArrayAdapter);
                                                           relistComboPerek(mSpinner_Perek_1, mSpinner_Book_1.getSelectedItemPosition());
                                                           if (!setRange) {
                                                               mSpinner_Pasuk_1.setVisibility(View.VISIBLE);
                                                               relistComboPasuk(mSpinner_Pasuk_1, mSpinner_Book_1.getSelectedItemPosition()
                                                                       , mSpinner_Perek_1.getSelectedItemPosition());
                                                           }
                                                       }
                                                   }
                                               }
        );

         mCB_range_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              CheckBox cb = (CheckBox) buttonView;
              if (isChecked) {
                  cb.setText(R.string.range_picker_checkbox_parasha);
                  mSpinner_Perek_2.setVisibility(View.GONE);
                  mSpinner_Pasuk_2.setVisibility(View.GONE);
                  String[] spinnerArray = ToraApp.getParashaNames();
                  ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                  mSpinner_Book_2.setAdapter(spinnerArrayAdapter);
              } else {
                  cb.setText(R.string.range_picker_checkbox_chapter);
                  mSpinner_Perek_2.setVisibility(View.VISIBLE);
                  ArrayList<String> spinnerArray = new ArrayList<String> (Arrays.asList(ToraApp.getBookNames()));
                  spinnerArray.add(getString(R.string.range_picker_spinner_end));
                  ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                  mSpinner_Book_2.setAdapter(spinnerArrayAdapter);
                  relistComboPerek(mSpinner_Perek_2, mSpinner_Book_2.getSelectedItemPosition());
                  if (!setRange) {
                      mSpinner_Pasuk_2.setVisibility(View.VISIBLE);
                      relistComboPasuk(mSpinner_Pasuk_2
                              , mSpinner_Book_2.getSelectedItemPosition()
                      ,mSpinner_Perek_2.getSelectedItemPosition());
                  }
              }
          }
        }
        );

        AlertDialog.Builder dialog =  new AlertDialog.Builder(getActivity())//,R.style.AlertDialogStyle)
                .setView(v)
                .setTitle(R.string.range_picker_title)
                .setPositiveButton(R.string.range_picker_button_edit,
                   new DialogInterface.OnClickListener() {
                @Override
                public void onClick (DialogInterface dialog,int which){

                    try {
                        //setRange = true for all searches, =false for ReportTorahCount
                        if (setRange) {
                            int start = 0, end = 0;
                            String strRange1 = "", strRange2 = "";
                            if (mCB_range_1.isChecked()) {
                                start = ToraApp.lookupLineNumberFromParasha(mSpinner_Book_1.getSelectedItemPosition());
                                strRange1 = "פר' " + mSpinner_Book_1.getSelectedItem().toString();
                            } else {
                                start = ToraApp.lookupLineNumberFromPerek(mSpinner_Book_1.getSelectedItemPosition(),
                                        mSpinner_Perek_1.getSelectedItemPosition());
                                strRange1 = mSpinner_Book_1.getSelectedItem().toString() + " "
                                        + mSpinner_Perek_1.getSelectedItem().toString();
                            }
                            if (mCB_range_2.isChecked()) {
                                end = ToraApp.lookupLineNumberFromParasha(mSpinner_Book_2.getSelectedItemPosition());
                                // Missing the Book, will be added conditionally below
                                strRange2 = "פר' " + mSpinner_Book_2.getSelectedItem().toString();
                            } else {
                                end = ToraApp.lookupLineNumberFromPerek(mSpinner_Book_2.getSelectedItemPosition(),
                                        mSpinner_Perek_2.getSelectedItemPosition());
                                if (mSpinner_Book_2.getSelectedItemPosition() < 5) {
                                    strRange2 = mSpinner_Perek_2.getSelectedItem().toString();
                                }
                            }
                            if (end - start <= 0) {
                                errorMessageDialog(v, getString(R.string.range_error_title)
                                        , getString(R.string.range_error_message));
                                //JOptionPane.showMessageDialog(getInstance(setRange), "טווח לא תקין", "שגיאה",
                                //        JOptionPane.ERROR_MESSAGE);
                            } else {
                                String strHTML = "<html>" + strRange1 + " : ";
                                if ((!mCB_range_2.isChecked()
                                        && (mSpinner_Book_1.getSelectedItemPosition() != mSpinner_Book_2.getSelectedItemPosition()))
                                        || (!mCB_range_2.isChecked() && mCB_range_1.isChecked())) {
                                    // Book added conditionally
                                    strHTML += "<br>" + mSpinner_Book_2.getSelectedItem().toString() + " ";
                                } else if (mCB_range_2.isChecked()) {
                                    strHTML += "<br>";
                                }
                                String str = "טווח חיפוש: \n" + strRange1 + " - ";
                                if (mCB_range_2.isChecked()) {
                                    str += strRange2;
                                } else {
                                    str += mSpinner_Book_2.getSelectedItem().toString();
                                    if (mSpinner_Book_2.getSelectedItemPosition() < 5) {
                                        str += " " + mSpinner_Perek_2.getSelectedItem().toString();
                                    }
                                }
                                strHTML += strRange2 + "</html>";
                                sendResult(Activity.RESULT_OK, new RangeClass(start, end, str, strHTML));
                                //Frame.setSearchRange(start, end, str, strHTML);
                            }
                        } else {
                            int start = 0, end = 0;
                            String startStr = "", endStr = "", end2Str = "";
                            if (mCB_range_1.isChecked()) {
                                start = ToraApp.lookupLineNumberFromParasha(mSpinner_Book_1.getSelectedItemPosition());
                                startStr = "פרשת " + mSpinner_Book_1.getSelectedItem().toString();
                            } else {
                                start = ToraApp.lookupLineNumberFromPerek(mSpinner_Book_1.getSelectedItemPosition(),
                                        mSpinner_Perek_1.getSelectedItemPosition(), mSpinner_Pasuk_1.getSelectedItemPosition());
                                startStr = mSpinner_Book_1.getSelectedItem().toString() + " "
                                        + mSpinner_Perek_1.getSelectedItem().toString() + ":"
                                        + mSpinner_Pasuk_1.getSelectedItem().toString();
                            }
                            if (mCB_range_2.isChecked()) {
                                end = ToraApp.lookupLineNumberFromParasha(mSpinner_Book_2.getSelectedItemPosition());
                                endStr = "פרשת " + mSpinner_Book_2.getSelectedItem().toString();
                                end2Str = " (לא כולל)";
                                // Missing the Book, will be added conditionally below
                            } else {
                                end = ToraApp.lookupLineNumberFromPerek(mSpinner_Book_2.getSelectedItemPosition(),
                                        mSpinner_Perek_2.getSelectedItemPosition(), mSpinner_Pasuk_2.getSelectedItemPosition());
                                if (mSpinner_Book_2.getSelectedItemPosition() == 5) {
                                    endStr = "הסוף";
                                } else {
                                    endStr = mSpinner_Book_2.getSelectedItem().toString() + " "
                                            + mSpinner_Perek_2.getSelectedItem().toString() + ":"
                                            + mSpinner_Pasuk_2.getSelectedItem().toString();
                                    end2Str = " (לא כולל)";
                                }
                            }
                            if (end - start <= 0) {
                                errorMessageDialog(v, getString(R.string.range_error_title)
                                        , getString(R.string.range_error_message));
                                //JOptionPane.showMessageDialog(getInstance(setRange), "טווח לא תקין", "שגיאה",
                                //        JOptionPane.ERROR_MESSAGE);
                            } else {
                                Object[] args = new Object[2];
                                args[0] = new int[]{start, end};
                                args[1] = new String[]{startStr, endStr, end2Str};
                                sendResultForReport(Activity.RESULT_OK, args);
                                new LongOperation(SearchFragment.SearchModeEnum.Report_Count, targetView).execute("");

                                //dispose();
                            }
                        }
                    }
                    finally {
                        targetView = null;
                        dialog.dismiss();
                    }

                }

            })
                .setNegativeButton(R.string.range_picker_button_cancel, new DialogInterface.OnClickListener() {
                public void onClick (DialogInterface dialog,int id){
                    dialog.cancel();
                }
            });

            return dialog.create();

    }

        private void sendResult ( int resultCode, RangeClass rClass){
            if (getTargetFragment() == null) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_RANGE_START, rClass.getStart());
            intent.putExtra(EXTRA_RANGE_END, rClass.getEnd());
            intent.putExtra(EXTRA_RANGE_STR, rClass.getStr());
            intent.putExtra(EXTRA_RANGE_STR_HTML, rClass.getStrHTML());
            getTargetFragment()
                    .onActivityResult(REQUEST_RANGE,
                            resultCode, intent);
        }

    private void sendResultForReport ( int resultCode, Object[] args){
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_REPORT_ARGS, args);
        getTargetFragment()
                .onActivityResult(REQUEST_RANGE_FOR_REPORT,
                        resultCode, intent);
    }

        public void errorMessageDialog (View v, String title, String message){
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            builder.setTitle(title);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.range_picker_button_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void relistComboPerek (Spinner spinner,int bookNum){
            ArrayList<String> spinnerArray = new ArrayList<String>();
            spinnerArray = ToraApp.getPerekArray(bookNum);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);
        }

        private void relistComboPasuk (Spinner spinner,int bookNum, int perekNum){
            ArrayList<String> spinnerArray = new ArrayList<String>();
            spinnerArray = ToraApp.getPasukArray(bookNum, perekNum);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);
         }

        private void avoidTrigger(){
            //avoid triggering listener on initialization
            mSpinner_Book_1.setSelection(0, false);
            mSpinner_Book_2.setSelection(0, false);
            mSpinner_Perek_1.setSelection(0, false);
            mSpinner_Perek_2.setSelection(0, false);
            mSpinner_Pasuk_1.setSelection(0, false);
            mSpinner_Pasuk_2.setSelection(0, false);
        }
    }
