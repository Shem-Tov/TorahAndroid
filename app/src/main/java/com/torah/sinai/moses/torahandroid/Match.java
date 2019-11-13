package com.torah.sinai.moses.torahandroid;

import java.util.UUID;

public class Match {
    private UUID mId;
    private String mTitle;
    private String mPasuk;
    private String mPosition;
    private String mDetail;

    public Match() {
        //Generate unique identifier
        mId = UUID.randomUUID();
    }

    public Match(String title,String pasuk
            , String position, String detail) {
        //Generate unique identifier
        mId = UUID.randomUUID();
        mTitle = title;
        mPasuk = pasuk;
        mPosition = position;
        mDetail = detail;
    }

    public UUID getId() {
        return mId;
    }

    public String getPasuk() {
        return mPasuk;
    }

    public void setPasuk(String pasuk) {
        mPasuk = pasuk;
    }

    public String getPosition() {
        return mPosition;
    }

    public void setPosition(String position) {
        mPosition = position;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        mDetail = detail;
    }
}
