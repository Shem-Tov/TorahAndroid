package com.torah.sinai.moses.torahandroid;

import java.util.ArrayList;


public class MatchLab {
    private static MatchLab sMatchLab;

    private static ArrayList<Match> mMatches = new ArrayList<>();
    private static int index=-1;

    public static ArrayList<Match> getMatches() {
        return mMatches;
    }

    public static void add(String title,String position
            , String pasuk, String detail) {
        Match match = new Match(title,pasuk
                ,position,detail);
        mMatches.add(match);
        index++;
    }

    public static void add(String... strings) {
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
        mMatches.add(match);
        index++;
    }

    public static void addEmpty() {
        Match match = new Match("",""
                ,"","");
        mMatches.add(match);
        index++;
    }

    public static void addTitle(String title) {
        mMatches.get(index).setTitle(title);
    }
    public static void addPasuk(String pasuk) {
        mMatches.get(index).setPasuk(pasuk);
    }
    public static void addPosition(String position) {
        mMatches.get(index).setPosition(position);
    }
    public static void addDetail(String detail) {
        mMatches.get(index).setDetail(detail);
    }
    public static void newMatch() {
        mMatches.add(new Match());
        index++;
    }

    public static void reset(){
        mMatches.clear();
        index=-1;
    }
}
