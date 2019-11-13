package com.torah.sinai.moses.torahandroid.engine.extras;
import com.torah.sinai.moses.torahandroid.engine.ioManagement.Output;
import com.torah.sinai.moses.torahandroid.engine.torahApp.ToraApp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Random;

public class extraFunctions {
	public static int createRandomNumber(int max) {
		return createRandomNumber(0, max);
	}

	public static int createRandomNumber(int min, int max) {
		Random randomNum = new Random();
		return (min + randomNum.nextInt(max + 1 - min));
	}

	public static boolean logicalXOR(boolean x, boolean y) {
		return ((x || y) && !(x && y));
	}

}