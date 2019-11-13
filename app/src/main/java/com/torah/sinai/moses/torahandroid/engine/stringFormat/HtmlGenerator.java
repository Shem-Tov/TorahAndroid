package com.torah.sinai.moses.torahandroid.engine.stringFormat;

import com.torah.sinai.moses.torahandroid.engine.frame.ColorClass;

public class HtmlGenerator {
	String[] htmlCode;
	final int flag_RGB_Size = 0b0100;
	final int flag_bold = 0b1000;
	//These four work in group
	//final int flag_noAlign = 0b0000; //is being implemented without need of separate definition
	final int flag_AlignGroup = 0b0011;
	final int flag_rAlign = 0b0001; 
	final int flag_cAlign = 0b0010;
	final int flag_lAlign = 0b0011;
	
	public HtmlGenerator(int flag) {
		this(0, 0, 0, 0, flag, 100);
		// shadePercent 100 is Original color
		if ((flag & flag_RGB_Size)==flag_RGB_Size) {
			throw new IllegalArgumentException("StringFormatting.HtmlGenerator - This flag is reserved for Color and Size parameters, but none were passed");
		}
	}

	public HtmlGenerator(int size, int r, int g, int b, int flag, int shadePercent) {
		//flag 0b111 - setAll
		//flag 0b100 - setRGBandSize
		//flag 0b010 - bold
		//flag 0b001 - rAlign
		//flag 0b1000 - cAlign
		// shadePercent 100 is Original color
		String[] strRGBSize = setRGBandSizeHTMLStringShader(size,r,g,b, shadePercent);
		htmlCode = 	new String[]
				{((checkFlag(flag,flag_rAlign,flag_AlignGroup))? rAlign[0]:"")
				  + ((checkFlag(flag,flag_cAlign,flag_AlignGroup))? cAlign[0]:"")
				  + ((checkFlag(flag,flag_lAlign,flag_AlignGroup))? lAlign[0]:"")
				  + ((checkFlag(flag,flag_RGB_Size))? strRGBSize[0]:"")
				  + ((checkFlag(flag,flag_bold))? bold[0]:""),
				  ((checkFlag(flag,flag_bold))? bold[1]:"")
				  + ((checkFlag(flag,flag_RGB_Size))? strRGBSize[1]:"")
				  + ((checkFlag(flag,flag_lAlign,flag_AlignGroup))? lAlign[1]:"")
				  + ((checkFlag(flag,flag_cAlign,flag_AlignGroup))? cAlign[1]:"")
				  + ((checkFlag(flag,flag_rAlign,flag_AlignGroup))?rAlign[1]:"")};
	}

	private Boolean checkFlag(int flags, int flag) {
		return checkFlag(flags,flag,flag);
	}
	
	private Boolean checkFlag(int flags, int flag, int flagGroup) {
		return ((flags & flagGroup)==flag);
	}
	
	public String getHtml(int mode) {
		//mode 0 - Html Header
		//mode 1 - Html Tail
		return htmlCode[mode];
	}
	
	final static String[] rAlign = new String[] {"<p align=\"right\">","</p>"};
	final static String[] cAlign = new String[] {"<p align=\"center\">","</p>"};
	final static String[] lAlign = new String[] {"<p align=\"left\">","</p>"};

	public static String[] setRGBHtmlString(int r, int g, int b) {
		return setRGBandSizeHTMLStringShader(0,r,g,b,100);
	}
	public static String[] setRGBandSizeHTMLString(int size, int r, int g, int b) {
		//size 0 = default size
		return setRGBandSizeHTMLStringShader(size,r,g,b,100);
	}

	public static String[] setRGBandSizeHTMLStringShader(int size,int r, int g, int b, int shadePercent) {
		// shadePercent 100 is Original color
		String str = "";
	    str += String.format("%02X", (int)(r*((float)shadePercent/100)));
		str += String.format("%02X", (int)(g*((float)shadePercent/100)));
		str += String.format("%02X", (int)(b*((float)shadePercent/100)));

		return new String[] {"<font "+((size>0)?("size=\""+size+"\""):"")+ "color=\"#"+str+"\">","</font>"};
	}

	final static String[] bold = new String[] {"<b>","</b>"};
	
	public static String[] setAll_Html_B_Size_RGB_Align_Right(int size, int r, int g, int b, int shadePercent) {
		String[] strRGBSize = setRGBandSizeHTMLStringShader(size,r,g,b, shadePercent);
		return new String[] {rAlign[0]+strRGBSize[0]+bold[0],bold[1]+strRGBSize[1]+rAlign[1]};
	}

	public static final int mode_main=0;
	public static final int mode_header=1;
	public static final int mode_markup=2;
	
	public static String createFontSizeColorStyle(int size,int mode) {
		return createFontSizeColorStyle(size,mode,55);
	}
	
	//can be added after an html tag
	//use mode_main, mode_header, mode_markup for color scheme
	public static String createFontSizeColorStyle(int size,int mode, int shadePercent) {
		String styleHeader =" style = \"padding: 8px; padding-left: 8px; padding-right: 8px;"+ 
				" text-align: right; font-family: Arial, Verdana, sans-serif;" + 
				" font-weight: bold;"+
				" font-size:	"+(size)+"px;"+
				" color:		#";

		switch (mode) {
		case mode_main:
			styleHeader+=ColorClass.getRGBmainStyleHTML(shadePercent)+";";
			break;
		case mode_header:
			styleHeader+=ColorClass.getRGBheaderStyleHTML(shadePercent)+";";
			break;
		case mode_markup:
			styleHeader+=ColorClass.getRGBmarkupStyleHTML(shadePercent)+";";
			break;
		}
		styleHeader += "\"";
		return styleHeader;
	}
	//can be added after an html tag
	//use mode_main, mode_header, mode_markup for color scheme
}
