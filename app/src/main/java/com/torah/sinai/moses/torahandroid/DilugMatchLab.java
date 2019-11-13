package com.torah.sinai.moses.torahandroid;

import java.util.ArrayList;

public class DilugMatchLab {
    private static DilugMatchLab sMatchLab;

    private static ArrayList<ArrayList<Match>> mDilugMatches = new ArrayList<ArrayList<Match>>();
    private static ArrayList<String> mLineDilugSets = new ArrayList<>();
    private static int currentIndex=-1;
    private static int dilugSetIndex=-1;


    public static ArrayList<ArrayList<Match>> getDilugMatches() {
        return mDilugMatches;
    }

    public static ArrayList<String> getLineDilugSets() {
        return mLineDilugSets;
    }

    public static void addToCurrent(String title,String position
            , String pasuk, String detail) {
        Match match = new Match(title,pasuk
                ,position,detail);
        mDilugMatches.get(dilugSetIndex).add(match);
        currentIndex++;
    }
    public static void addEmptyToCurrent() {
        Match match = new Match("",""
                ,"","");
        mDilugMatches.get(dilugSetIndex).add(match);
        currentIndex++;
    }
    public static void addToCurrent(String... strings) {
        Match match = new Match("","","","");
        if ((strings!=null) && (strings.length!=0)){
            switch (strings.length) {
                case 4:
                    match.setDetail(strings[3]);
                case 3:
                    match.setPasuk(strings[2]);
                case 2:
                    match.setPosition(strings[1]);
                case 1:
                    match.setTitle(strings[0]);
            }
        }
        mDilugMatches.get(dilugSetIndex).add(match);
        currentIndex++;
    }

    public static void addLineDilug(String string){
        //while (mLineDilugSets.size()<dilugSetIndex){
        //    mLineDilugSets.add("");
        //}
        mLineDilugSets.set(dilugSetIndex,string);
    }

    public static void addTitle(String title) {
        mDilugMatches.get(dilugSetIndex).get(currentIndex).setTitle(title);
    }
    public static void addPasuk(String pasuk) {
        mDilugMatches.get(dilugSetIndex).get(currentIndex).setPasuk(pasuk);
    }
    public static void addPosition(String position) {
        mDilugMatches.get(dilugSetIndex).get(currentIndex).setPosition(position);
    }
    public static void addDetail(String detail) {
        mDilugMatches.get(dilugSetIndex).get(currentIndex).setDetail(detail);
    }

    public static void newDilugSet() {
        dilugSetIndex++;
        currentIndex=-1;
        mDilugMatches.add(new ArrayList<Match>());
        mLineDilugSets.add("");
    }

    public static void newDilugMatch() {
        mDilugMatches.get(dilugSetIndex).add(new Match());
        currentIndex++;
    }

    public static void reset(){
        mDilugMatches.clear();
        currentIndex=-1;
        dilugSetIndex=-1;
    }
}
