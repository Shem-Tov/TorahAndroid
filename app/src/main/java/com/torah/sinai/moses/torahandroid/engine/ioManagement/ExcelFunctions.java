package com.torah.sinai.moses.torahandroid.engine.ioManagement;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.engine.frame.Frame;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ExcelFunctions {
    public static Boolean tableLoaded = false;
    public static final int id_torahLine = 4;


    // End Excel Tables

    private static final String DATA_FOLDER_LOCATION_HARDCODED = "./Data/";
    private static String DATA_FOLDER_LOCATION = DATA_FOLDER_LOCATION_HARDCODED;

     public static String getData_Folder_Location() {
        return DATA_FOLDER_LOCATION;
    }

     public static String[][] readBookTableXLS(int rawId_File, int sheetNUM, int startColumn, int startRow,
                                              int endColumn, int endRow) {
        String[][] data = null;
        Workbook workbook = null;
        DataFormatter dataFormatter = new DataFormatter();
        try {
            InputStream excelFile = MainActivity.getInstance()
                    .getApplicationContext().getResources().openRawResource(rawId_File);

            workbook = new HSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(sheetNUM);
            Iterator<Row> iterator = datatypeSheet.iterator();
            data = new String[endColumn - startColumn][endRow - startRow];
            int i = 0;
            int j = 0;
            Row currentRow;
            while (j < startRow) {
                currentRow = iterator.next();
                j++;
            }
            while (iterator.hasNext()) {
                currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                i = startColumn;
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    data[i++ - startColumn][(j) - startRow] = dataFormatter.formatCellValue(currentCell);
                    if (i >= (endColumn)) {
                        break;
                    }
                }
                j++;
                if (j >= (endRow)) {
                    break;
                }
            }
            tableLoaded = true;
            if (ToraApp.isGui()) {
                Frame.clearTextPane();
                Frame.setButtonEnabled(true);
            }
            //MainActivity.makeToast("Imported XLS");
        } catch (IOException | NullPointerException e) {
            if (ToraApp.isGui()) {
                Frame.clearTextPane();
            }
            MainActivity.makeToast("Error importing from EXCEL Sheet"+"<br>"
                    +"Program can not work without torah_tables Excel file");
            tableLoaded = false;
            try {
                Frame.setButtonEnabled(false);
            } catch (NullPointerException ex) {
                // safe to ignore
            }
            // e.printStackTrace();
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {

            }
        }

        return data;
    }

 }
