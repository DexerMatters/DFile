package com.dfile.dbrt.dfile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class FileViewAdapter extends BaseAdapter {
    private File[] files;
    private File file;
    private Context context;
    private boolean willSelect=false;
    public FileViewAdapter(Context context, String path){
        this.context=context;
        file=new File(path);
        files=file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if(f1.isDirectory()&&f2.isFile())
                    return -1;
                if(f1.isFile()&&f2.isDirectory())
                    return 1;
                return f1.getName().compareTo(f2.getName());
            }
        });
    }
    public File getFileAt(int i){
        return files[i];
    }
    public File getCurrentDir(){
        return file;
    }
    @Override
    public int getCount() {
        int count=0;
        try {
            count=files.length;
        }catch (NullPointerException e){
            e.fillInStackTrace();
        }
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v=LayoutInflater.from(context).inflate(R.layout.file_view_bar,null);
        v.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Info.MAIN.get().dip2px(38)));
        TextView file_name=v.findViewById(R.id.file_name);
        ImageView file_icon=v.findViewById(R.id.file_icon);
        final CardView icon_card=v.findViewById(R.id.icon_card),
                name_card=v.findViewById(R.id.name_card);
        final LinearLayout file_bar=v.findViewById(R.id.file_bar);
        file_name.setText(files[i].getName());
        if(files[i].isDirectory())
            file_icon.setImageResource(R.drawable.folder);
        else
            file_icon.setImageResource(R.drawable.file);
        file_bar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if(!willSelect) {
                    if (files[i].isDirectory())
                        Info.MAIN.get().fvc.setDirPath(getCurrentDir().getPath() + '/' + files[i].getName());
                }else{
                    if(Info.SELECTED_FILE.indexOf(files[i])==-1) {
                        Log.d("long",files[i].getPath());
                        icon_card.setCardBackgroundColor(context.getColor(R.color.bar_selected));
                        name_card.setCardBackgroundColor(context.getColor(R.color.bar_selected));
                        Info.SELECTED_FILE.add(files[i]);
                        willSelect=true;
                    }else{
                        icon_card.setCardBackgroundColor(context.getColor(R.color.icon_background));
                        name_card.setCardBackgroundColor(context.getColor(R.color.bar));
                        Info.SELECTED_FILE.remove(files[i]);
                        if(Info.SELECTED_FILE.size()==0) {
                            Info.MAIN.get().tool_bar.setVisibility(View.GONE);
                            Info.MAIN.get().path.setFocusable(true);
                            willSelect=false;
                        }
                    }
                }
            }
        });
        file_bar.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View view) {
                if(Info.SELECTED_FILE.indexOf(files[i])==-1) {
                    Log.d("long",files[i].getPath());
                    icon_card.setCardBackgroundColor(context.getColor(R.color.bar_selected));
                    name_card.setCardBackgroundColor(context.getColor(R.color.bar_selected));
                    Info.SELECTED_FILE.add(files[i]);
                    Info.MAIN.get().tool_bar.setVisibility(View.VISIBLE);
                    Info.MAIN.get().path.setFocusable(false);
                    willSelect=true;
                }else{
                    icon_card.setCardBackgroundColor(context.getColor(R.color.icon_background));
                    name_card.setCardBackgroundColor(context.getColor(R.color.bar));
                    Info.SELECTED_FILE.remove(files[i]);
                    if(Info.SELECTED_FILE.size()==0) {
                        Info.MAIN.get().tool_bar.setVisibility(View.GONE);
                        Info.MAIN.get().path.setFocusable(true);
                        willSelect = false;
                    }
                }
                return true;
            }
        });
        return v;
    }

}
