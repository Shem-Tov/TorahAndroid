package com.torah.sinai.moses.torahandroid;

public class RangeClass {
    private int start, end;
    private String str, strHTML;

     public RangeClass(int iStart, int iEnd,
                           String iStr, String iStrHTML){
        start = iStart;
        end = iEnd;
        str = iStr;
        strHTML = iStrHTML;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getStr() {
        return str;
    }

    public String getStrHTML() {
        return strHTML;
    }

}
