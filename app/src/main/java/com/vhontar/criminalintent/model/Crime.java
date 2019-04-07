package com.vhontar.criminalintent.model;

import java.util.UUID;

public class Crime {


    private UUID mId;
    private String mTitle;

    public Crime() {
        mId = UUID.randomUUID();
    }

    public Crime(String title) {
        mId = UUID.randomUUID();
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
