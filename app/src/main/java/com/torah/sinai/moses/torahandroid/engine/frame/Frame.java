package com.torah.sinai.moses.torahandroid.engine.frame;


import com.torah.sinai.moses.torahandroid.SearchFragment;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.ManageIO;

public class Frame {
    static int textHtmlSize = 5;
    public static final int lineHeaderSize = 5;
    // searchRange, has two line numbers to search through in the Torah
    // if {0,0} then search through ALL

    private static final int fontSize_hardCoded = 16;
    private static final int fontSizeBig_hardCoded = fontSize_hardCoded + 2;
    private static final int fontSizeSmall_hardCoded = fontSize_hardCoded - 2;
    private static final int fontSizeSmaller_hardCoded = fontSize_hardCoded - 3;

    private static int fontSize = fontSize_hardCoded;
    private static int fontSizeBig = getFontSize() + 2;
    private static int fontSizeSmall = getFontSize() - 2;
    private static int fontSizeSmaller = getFontSize() - 3;


    /*
    public void initValues() {
        try {
            comboBox_main.setSelectedIndex(Integer.parseInt(PropStore.map.get(PropStore.mode_main_number)));
        } catch (Exception e) {

        }
        try {
            setSaveMode_search(Integer.parseInt(PropStore.map.get(PropStore.mode_sub_search)));
            setSaveMode_letter(Integer.parseInt(PropStore.map.get(PropStore.mode_sub_letter)));
            setSaveMode_dilugim(Integer.parseInt(PropStore.map.get(PropStore.mode_sub_dilugim)));
            setBool_searchMultiple(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_search_Multi)));
        } catch (Exception e) {

        }
        try {
            switch (getComboBox_main()) {
                case combo_strSearch:
                    checkBox_searchMultiple.setSelected(bool_searchMultiple);
                    break;
            }
        } catch (Exception e) {

        }
        textField_Search.setText(PropStore.map.get(PropStore.searchWord));
        setString_searchSTR2(PropStore.map.get(PropStore.searchWord2));
        setString_countIndex(PropStore.map.get(PropStore.countSearchIndex));
        setString_padding_Dilug(PropStore.map.get(PropStore.paddingDilug));
        textField_dilugMin.setText(PropStore.map.get(PropStore.minDilug));
        textField_dilugMax.setText(PropStore.map.get(PropStore.maxDilug));
        ToraApp.subTorahTableFile = PropStore.map.get(PropStore.subTorahTablesFile);
        ToraApp.subTorahLineFile = PropStore.map.get(PropStore.subTorahLineFile);
        ToraApp.subTorahLetterFile = PropStore.map.get(PropStore.subTorahLettersFile);
        ToraApp.differentSearchFile = PropStore.map.get(PropStore.differentSearchFile);
        if ((ToraApp.differentSearchFile != null) && (ToraApp.differentSearchFile.length() > 0)) {
            comboBox_DifferentSearch
                    .setToolTipText(Output.markText(ToraApp.differentSearchFile, ColorClass.headerStyleHTML, true));
        } else {
            comboBox_DifferentSearch.setToolTipText(Output.markText(
                    "להגדרת קובץ לחיפוש -> הגדרות<br> -> קבצים -> קובץ אחר לחיפוש", ColorClass.headerStyleHTML, true));
        }
        String dataFolder = PropStore.map.get(PropStore.dataFolder);
        if ((dataFolder != null) && (dataFolder.length() > 0)) {
            ExcelFunctions.setData_Folder_Location(dataFolder);
        }
        try {
            customBGColor = new Color(Integer.parseInt(PropStore.map.get(PropStore.bgColor)));
        } catch (Exception e) {
            customBGColor = ColorBG_Panel;
        }
        try {
            setFontSize(Integer.parseInt(PropStore.map.get(PropStore.fontSize)));
        } catch (Exception e) {
            setFontSize(fontSize_hardCoded);
        }

        try {
            Color temp = new Color(Integer.parseInt(PropStore.map.get(PropStore.mainHtmlColor)));
            ColorClass.color_mainStyleHTML[0] = temp.getRed();
            ColorClass.color_mainStyleHTML[1] = temp.getGreen();
            ColorClass.color_mainStyleHTML[2] = temp.getBlue();
            ColorClass.mainStyleHTML = new stringFormat.HtmlGenerator(textHtmlSize, ColorClass.color_mainStyleHTML[0],
                    ColorClass.color_mainStyleHTML[1], ColorClass.color_mainStyleHTML[2], 0b1101);
            // public static StringFormatting.HtmlGenerator markupStyleHTML = new
            // StringFormatting.HtmlGenerator(textHtmlSize+1, 93, 192, 179,0b100);
        } catch (Exception e) {
            ColorClass.color_mainStyleHTML = ColorClass.color_mainStyleHTML_hardCoded.clone();
        }

        try {
            Color temp = new Color(Integer.parseInt(PropStore.map.get(PropStore.markupHtmlColor)));
            ColorClass.color_markupStyleHTML[0] = temp.getRed();
            ColorClass.color_markupStyleHTML[1] = temp.getGreen();
            ColorClass.color_markupStyleHTML[2] = temp.getBlue();
            ColorClass.markupStyleHTML = new stringFormat.HtmlGenerator(textHtmlSize + 1,
                    ColorClass.color_markupStyleHTML[0], ColorClass.color_markupStyleHTML[1],
                    ColorClass.color_markupStyleHTML[2], 0b100);
        } catch (Exception e) {
            ColorClass.color_markupStyleHTML = ColorClass.color_markupStyleHTML_hardCoded.clone();
        }
        checkBox_letterOrder1.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_letterOrder1)));
        checkBox_letterOrder2.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_letterOrder2)));
        checkBox_first1.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_first1)));
        checkBox_first2.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_first2)));
        checkBox_last1.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_last1)));
        bool_letters_last2 = Boolean.parseBoolean(PropStore.map.get(PropStore.bool_last2));
        checkBox_gimatriaSofiot.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_gimatriaSofiot)));
        checkBox_wholeWord.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_wholeWord)));
        checkBox_countPsukim.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_countPsukim)));
        checkBox_countPsukim.setText(
                ((checkBox_countPsukim.isSelected()) ? checkBox_countPsukim_true : checkBox_countPsukim_false));
        if (PropStore.map.get(PropStore.bool_createDocument) != null) {
            checkbox_createDocument.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_createDocument)));
        } else {
            checkbox_createDocument.setSelected(true);
        }
        if (PropStore.map.get(PropStore.bool_createTree) != null) {
            checkbox_createTree.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_createTree)));
        } else {
            checkbox_createTree.setSelected(true);
        }
        checkbox_createExcel.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_createExcel)));
        checkBox_TooltipOption.setSelected(Boolean.parseBoolean(PropStore.map.get(PropStore.bool_TorahTooltip)));
        textPane.setVisible(checkbox_createDocument.isSelected());
        tree.setVisible(checkbox_createTree.isSelected());
        try {
            int height = Integer.parseInt(PropStore.map.get(PropStore.frameHeight));
            int width = Integer.parseInt(PropStore.map.get(PropStore.frameWidth));
            int splitLocation = Integer.parseInt(PropStore.map.get(PropStore.splitPaneDivide));
            this.setSize(width,height);
            splitPane.setDividerLocation(splitLocation);
            //System.out.println("Width : "+width+" - Actual: "+this.getSize().width);
            //System.out.println("Height: "+height+" - Actual: "+this.getSize().height);
            //System.out.println("Split Location: "+splitLocation+" - Actual: "+splitPane.getDividerLocation());
        } catch (Exception e) {
        }
    }
    */
    /*
    public static void saveValues() {
        switch (getComboBox_main()) {
            case combo_strSearch:
                if (checkBox_searchMultiple.isSelected()) {
                    setSaveMode_search(getComboBox_sub_Index());
                }
                break;
            case combo_strLetterSearch:
                setSaveMode_letter(getComboBox_sub_Index());
                break;
            case combo_strDilugim:
                setSaveMode_dilugim(getComboBox_sub_Index());
                break;
        }
        PropStore.addNotNull(PropStore.mode_main_number, String.valueOf(comboBox_main.getSelectedIndex()));
        PropStore.addNotNull(PropStore.mode_sub_dilugim, String.valueOf(savedMode_dilugim));
        PropStore.addNotNull(PropStore.mode_sub_letter, String.valueOf(savedMode_letter));
        PropStore.addNotNull(PropStore.mode_sub_search, String.valueOf(savedMode_search));
        PropStore.addNotNull(PropStore.searchWord, textField_Search.getText());
        PropStore.addNotNull(PropStore.minDilug, textField_dilugMin.getText());
        PropStore.addNotNull(PropStore.maxDilug, textField_dilugMax.getText());
        PropStore.addNotNull(PropStore.paddingDilug, savedString_padding_Dilug);
        PropStore.addNotNull(PropStore.countSearchIndex, savedString_countIndex);
        PropStore.addNotNull(PropStore.searchWord2, savedString_searchSTR2);
        PropStore.addNotNull(PropStore.subTorahTablesFile, ToraApp.subTorahTableFile);
        PropStore.addNotNull(PropStore.subTorahLineFile, ToraApp.subTorahLineFile);
        PropStore.addNotNull(PropStore.subTorahLettersFile, ToraApp.subTorahLetterFile);
        PropStore.addNotNull(PropStore.bgColor, String.valueOf(customBGColor.getRGB()));
        PropStore.addNotNull(PropStore.mainHtmlColor, String.valueOf(new Color(ColorClass.color_mainStyleHTML[0],
                ColorClass.color_mainStyleHTML[1], ColorClass.color_mainStyleHTML[2]).getRGB()));
        PropStore.addNotNull(PropStore.markupHtmlColor, String.valueOf(new Color(ColorClass.color_markupStyleHTML[0],
                ColorClass.color_markupStyleHTML[1], ColorClass.color_markupStyleHTML[2]).getRGB()));
        PropStore.addNotNull(PropStore.fontSize, String.valueOf(getFontSize()));
        PropStore.addNotNull(PropStore.bool_gimatriaSofiot, String.valueOf(checkBox_gimatriaSofiot.isSelected()));
        PropStore.addNotNull(PropStore.bool_wholeWord, String.valueOf(checkBox_wholeWord.isSelected()));
        PropStore.addNotNull(PropStore.bool_countPsukim, String.valueOf(checkBox_countPsukim.isSelected()));
        PropStore.addNotNull(PropStore.bool_createDocument, String.valueOf(checkbox_createDocument.isSelected()));
        PropStore.addNotNull(PropStore.bool_createExcel, String.valueOf(checkbox_createExcel.isSelected()));
        PropStore.addNotNull(PropStore.bool_createTree, String.valueOf(checkbox_createTree.isSelected()));
        PropStore.addNotNull(PropStore.bool_letterOrder1, String.valueOf(checkBox_letterOrder1.isSelected()));
        PropStore.addNotNull(PropStore.bool_letterOrder2, String.valueOf(checkBox_letterOrder2.isSelected()));
        PropStore.addNotNull(PropStore.bool_first1, String.valueOf(checkBox_first1.isSelected()));
        PropStore.addNotNull(PropStore.bool_first2, String.valueOf(checkBox_first2.isSelected()));
        PropStore.addNotNull(PropStore.bool_last1, String.valueOf(checkBox_last1.isSelected()));
        PropStore.addNotNull(PropStore.bool_last2, String.valueOf(bool_letters_last2));
        PropStore.addNotNull(PropStore.bool_TorahTooltip, String.valueOf(checkBox_TooltipOption.isSelected()));
        PropStore.store();
    }
    */
    /*
    private static void saveFrameSize() {
        PropStore.addNotNull(PropStore.fontSize, String.valueOf(getFontSize()));
        PropStore.addNotNull(PropStore.frameHeight, String.valueOf(frame_instance.getBounds().height));
        PropStore.addNotNull(PropStore.frameWidth, String.valueOf(frame_instance.getBounds().width));
        PropStore.addNotNull(PropStore.splitPaneDivide, String.valueOf(splitPane.getDividerLocation()));
        PropStore.store();
    }
    */

    public static int getFontSize() {
        return fontSize;
    }

    public static void setFontSize(int fontSize) {
        Frame.fontSize = fontSize;
    }

    public static int getFontSizeBig() {
        return fontSizeBig;
    }

    public static void setFontSizeBig(int fontSizeBig) {
        Frame.fontSizeBig = fontSizeBig;
    }

    static int getTextHtmlSize() {
        return textHtmlSize;
    }


    public static void clearTextPane() {

    }
    public static boolean getCheckbox_createExcel() {
        return false;
    }

    public static boolean getCheckBox_Tooltip() {
        return true;
    }

    public static void appendText(String text, byte mode){

    }

    public static void appendText(String text, byte mode, String... toolTipText){

    }


    public static void setButtonEnabled(boolean bool){

    }
}
