package com.torah.sinai.moses.torahandroid.engine.ioManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import com.torah.sinai.moses.torahandroid.MainActivity;
import com.torah.sinai.moses.torahandroid.R;
import com.torah.sinai.moses.torahandroid.engine.frame.Frame;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;

public class ManageIO {

	public static File checkFile(String fileName1, String fileName2) {
		File file = null;
		try {
			file = new File(ClassLoader.getSystemResource(fileName1).toURI());
		} catch (Exception e) {
			// safe to ignore
		}
		if ((file == null) || (!file.exists())) {
			try {
				file = new File(fileName2);
			} catch (Exception e) {
				// safe to ignore
			}
			if ((file == null) || (!file.exists())) {
				// throw new IOException("Could not find file for TorahLetters");
				Frame.clearTextPane();
				MainActivity.makeToast("Could not find file for TorahLetters");
				return null;
			}
		}
		return file;
	}

	public enum fileMode {Line,NoTevot,LastSearch,Different}

    public static BufferedReader getBufferedReader(fileMode mode, Boolean allowLastSearch) {
		int rawId_file=0;
		BufferedReader bReader = null;
		String fileName1="", fileName2="";
		switch (mode) {
		case LastSearch:
			if (allowLastSearch) {
				rawId_file=-1;
				return LastSearchClass.getStoredBufferedReader();
			}
			//no break;
		case Line:
			fileName1 = ToraApp.ToraLineFile;
			fileName2 = ToraApp.subTorahLineFile;
			rawId_file = R.raw.lines;
			break;
		case NoTevot:
			fileName1 = ToraApp.ToraLetterFile;
			fileName2 = ToraApp.subTorahLetterFile;
			rawId_file = R.raw.no_tevot;
			break;
		case Different:
			fileName2 = ToraApp.differentSearchFile;
			rawId_file = -1;
			break;
		}

		try {
			if (fileName1.equals("")) {
				throw new Exception();
			}
			
			/*
			  public static void load(File f) throws IOException {
				    FileInputStream fis = new FileInputStream(f);
				    FileChannel fc = fis.getChannel();

				    MappedByteBuffer mmb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				   
				    byte[] buffer = new byte[(int)fc.size()];
				    mmb.get(buffer);
				   
				    fis.close();

				    BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));

				    for (String line = in.readLine(); line != null; line = in.readLine()) {
				      // do your processing here...
				    }

				    in.close();
				  }
			*/

			InputStream inputStream = MainActivity.getInstance()
					.getApplicationContext().getResources().openRawResource(rawId_file);
			bReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		} catch (Exception e) {
			try {
				File file = new File(fileName2);
				bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			} catch (NullPointerException | FileNotFoundException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
				if (ToraApp.isGui()) {
					Frame.clearTextPane();
				}
				MainActivity.makeToast("Could not find file for TorahLetters");
				return null;
			}
			return bReader;
			// safe to ignore
		}
		return bReader;
	}

	public static int countLinesOfFile(fileMode mode) {
		BufferedReader inputStream = ManageIO.getBufferedReader(mode,false);
		int countLines=0; 
		try {
				while ((inputStream.readLine()) != null) {
					countLines++;
				}
		} catch (IOException e) {
			e.printStackTrace();
	    } finally {
	    	try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		return countLines;
	}
	
	public static PushbackReader getPushbackReader(int rawId_file1, String fileName2) {
		PushbackReader bReader = null;
		try {
			InputStream inputStream = MainActivity.getInstance()
					.getApplicationContext().getResources().openRawResource(rawId_file1);
			bReader = new PushbackReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		} catch (Exception e) {
			try {
				File file = new File(fileName2);
				bReader = new PushbackReader(new FileReader(file));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
				if (ToraApp.isGui()) {
					Frame.clearTextPane();
				}
				MainActivity.makeToast("Could not find file for TorahLetters");
				return null;
			}
			return bReader;
			// safe to ignore
		}
		return bReader;
	}

	public static String readBufferReaderLineX(BufferedReader bReader, int lineNum){
		try {
			int i=0;
			while (i++<lineNum) {
				bReader.readLine();
			}
				return bReader.readLine();
			// Recieves words of Pasuk
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
