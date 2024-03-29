package com.torah.sinai.moses.torahandroid.engine.frame;

import android.graphics.Color;

import com.torah.sinai.moses.torahandroid.engine.stringFormat.HtmlGenerator;

public class ColorClass {
	public static final int[] color_mainStyleHTML_hardCoded = new int[] { 128, 88, 180 };

	public static final int[] color_markupStyleHTML_hardCoded = new int[] { 255, 218, 185 };
	public static final int[] color_tooltipStyleHTML_hardCoded = new int[] { 128, 88, 180 };
	public static final int[][] color_highlightStyleHTML_hardCoded = new int[][] { { 135, 206, 250 }, { 166, 220, 225 },
			{ 155, 215, 240 } };
	public static final int[] color_attentionHTML = new int[] { 255,197,150 };
	public static final int[] color_headerStyleHTML = new int[] { 58, 124, 240 };
	public static final int[] color_footerStyleHTML = new int[] { 255, 144, 180 };
	static int[] color_mainStyleHTML = color_mainStyleHTML_hardCoded.clone();
	static int[] color_tooltipStyleHTML = color_tooltipStyleHTML_hardCoded.clone();
	static int[] color_markupStyleHTML = color_markupStyleHTML_hardCoded.clone();
	static int[][] color_highlightStyleHTML = color_highlightStyleHTML_hardCoded.clone();

	// shadePercent 100 is Original color
	public static HtmlGenerator tooltipStyleHTML = new HtmlGenerator(Frame.getTextHtmlSize(),
			color_tooltipStyleHTML_hardCoded[0], color_tooltipStyleHTML_hardCoded[1], color_tooltipStyleHTML_hardCoded[2],
			0b100, 100);
	public static HtmlGenerator markupStyleHTML = new HtmlGenerator(Frame.getTextHtmlSize(),
			color_markupStyleHTML_hardCoded[0], color_markupStyleHTML_hardCoded[1], color_markupStyleHTML_hardCoded[2],
			0b100, 80);
	public static HtmlGenerator headerStyleHTML = new HtmlGenerator(Frame.getTextHtmlSize(), color_headerStyleHTML[0],
			color_headerStyleHTML[1], color_headerStyleHTML[2], 0b100, 100);
	public static HtmlGenerator footerStyleHTML = new HtmlGenerator(0, color_footerStyleHTML[0],
			color_footerStyleHTML[1], color_footerStyleHTML[2], 0b100, 100);
	public static HtmlGenerator[] highlightStyleHTML = new HtmlGenerator[] {
			new HtmlGenerator(Frame.getTextHtmlSize(), color_highlightStyleHTML[0][0], color_highlightStyleHTML[0][1],
					color_highlightStyleHTML[0][2], 0b100, 100),
			new HtmlGenerator(Frame.getTextHtmlSize(), color_highlightStyleHTML[1][0], color_highlightStyleHTML[1][1],
					color_highlightStyleHTML[1][2], 0b100, 100),
			new HtmlGenerator(Frame.getTextHtmlSize(), color_highlightStyleHTML[2][0], color_highlightStyleHTML[2][1],
					color_highlightStyleHTML[2][2], 0b100, 100) };
	// JTextPane Formatting

	public static String getRGBmainStyleHTML() {
		return getRGBmainStyleHTML(100);
	}
	
	public static String getRGBmainStyleHTML(int shadePercent) {
		String str = "";
		for (int i = 0; i < 3; i++) {
			str += String.format("%02X", (int)(color_mainStyleHTML[i]*((float)shadePercent/100)));
		}
		return str;
	}

	public static int getColorCode(int[] rgb){
		return getColorCode(255,rgb);
	}

	public static int getColorCode(int a, int[] rgb){
		return Color.argb(a, rgb[0], rgb[1], rgb[2]);
	}

	public static String getRGBmarkupStyleHTML() {
		return getRGBmarkupStyleHTML(100);
	}

	public static String getRGBmarkupStyleHTML(int shadePercent) {
		String str = "";
		for (int i = 0; i < 3; i++) {
			str += String.format("%02X", (int)(color_markupStyleHTML[i]*((float)shadePercent/100)));
		}
		return str;
	}

	public static String getRGBheaderStyleHTML() {
		return getRGBheaderStyleHTML(100);
	}

	public static String getRGBheaderStyleHTML(int shadePercent) {
		String str = "";
		for (int i = 0; i < 3; i++) {
			str += String.format("%02X", (int)(color_headerStyleHTML[i]*((float)shadePercent/100)));
		}
		return str;
	}

	public static String getRGBhighlightStyleHTML() {
		return getRGBhighlightStyleHTML(100);
	}

	public static String getRGBhighlightStyleHTML(int shadePercent) {
		String str = "";
		for (int i = 0; i < 3; i++) {
			str += String.format("%02X", (int)(color_highlightStyleHTML[0][i] * ((float)shadePercent / 100)));
		}
		return str;
	}

	public static String getRGBtooltipStyleHTML() {
		return getRGBtooltipStyleHTML(100);
	}

	public static String getRGBtooltipStyleHTML(int shadePercent) {
		String str = "";
		for (int i = 0; i < 3; i++) {
			str += String.format("%02X", (int)(color_tooltipStyleHTML[i] * ((float)shadePercent / 100)));
		}
		return str;
	}

	
	public void changeTintOfMarkupStyle(int percent) {
		for (int i = 0; i < 3; i++) {
			color_markupStyleHTML[i] = (255 - color_markupStyleHTML[i]) * percent;
		}
	}

}
