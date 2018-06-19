package com.example.bios90.voicedemo10;

import android.app.Application;

public class GlobalHelper extends Application
{

    public static final String START = "STARTSERVICE";
    public static final String STOP = "STOPSERVICE";

    boolean work;

    public boolean isWork()
    {
        return work;
    }

    public void setWork(boolean work)
    {
        this.work = work;
    }
}
