package com.dfile.dbrt.dfile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Info {
    public static WeakReference<MainActivity> MAIN;
    public static WeakReference<ListView> FILELIST;
    public static ArrayList<File> SELECTED_FILE=new ArrayList<>();
}
