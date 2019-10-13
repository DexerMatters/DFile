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
import java.util.Objects;

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
    @RequiresApi(api = Build.VERSION_CODES.M)
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
        else {
            int     image=R.drawable.image,
                    text=R.drawable.file_text,
                    video=R.drawable.video,
                    music=R.drawable.music,
                    compress=R.drawable.zip,
                    script=R.drawable.script,
                    apk=R.drawable.pack;
            int r;
            if(files[i].getName().lastIndexOf(".")!=-1) {
                switch (files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).toLowerCase()) {
                    case "png":
                        r = image;break;
                    case "jpg":
                        r = image;break;
                    case "jpeg":
                        r = image;break;
                    case "gif":
                        r = image;break;
                    case "icon":
                        r = image;break;
                    case "txt":
                        r = text;break;
                    case "word":
                        r = text;break;
                    case "md":
                        r = text;break;
                    case "css":
                        r = text;break;
                    case "json":
                        r = text;break;
                    case "htm":
                        r = text;break;
                    case "html":
                        r = text;break;
                    case "xml":
                        r = text;break;
                    case "iml":
                        r = text;break;
                    case "js":
                        r = script;break;
                    case "php":
                        r = script;break;
                    case "vue":
                        r = script;break;
                    case "c":
                        r = script;break;
                    case "java":
                        r = script;break;
                    case "cpp":
                        r = script;break;
                    case "zip":
                        r = compress;break;
                    case "rar":
                        r = compress;break;
                    case "7z":
                        r = compress;break;
                    case "tar":
                        r = compress;break;
                    case "jar":
                        r = compress;break;
                    case "ogg":
                        r = music;break;
                    case "mp3":
                        r = music;break;
                    case "wav":
                        r = music;break;
                    case "mp4":
                        r = video;break;
                    case "avi":
                        r = video;break;
                    case "apk":
                        r = apk;break;
                    default:
                        r = R.drawable.file;break;
                }
            }else r=R.drawable.file;
            file_icon.setImageResource(r);
        }
        if(Info.SELECTED_FILE.contains(files[i])){
            icon_card.setCardBackgroundColor(context.getColor(R.color.bar_selected));
            name_card.setCardBackgroundColor(context.getColor(R.color.bar_selected));
        }
        file_bar.setOnClickListener(new View.OnClickListener() {
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
                    if(Info.SELECTED_FILE.size()==1)
                        Info.MAIN.get().button_info.setVisibility(View.VISIBLE);
                    else Info.MAIN.get().button_info.setVisibility(View.GONE);
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
                if(Info.SELECTED_FILE.size()==1)
                    Info.MAIN.get().button_info.setVisibility(View.VISIBLE);
                else Info.MAIN.get().button_info.setVisibility(View.GONE);
                return true;
            }
        });
        return v;
    }

}
