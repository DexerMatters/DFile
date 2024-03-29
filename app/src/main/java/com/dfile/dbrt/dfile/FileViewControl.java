package com.dfile.dbrt.dfile;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;

import static com.dfile.dbrt.dfile.Info.*;

public class FileViewControl {
    ListView view;
    File file;
    FileViewAdapter fva;
    public FileViewControl(ListView view,String path){
        this.view=view;
        file=new File(path);
        setDirPath(path);
    }
    public File getCurrentFile(){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<MAIN.get().path.getChildCount();i++){
            TextView t= (TextView) MAIN.get().path.getChildAt(i);
            sb.append(t.getText()+"/");
        }
        sb.setLength(sb.length()-1);
        return new File(sb.toString());
    }
    public void update(){
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                int y=view.getScrollY();
                Log.d("scroll_x",""+y);
                view.setAdapter(fva=new FileViewAdapter(MAIN.get(),file.getPath()));
                view.scrollTo(0,y);
            }
        },10);
    }
    public void setDirPath(final String path){
        if(new File(path).listFiles()==null){
            Toast.makeText(MAIN.get(),R.string.error,Toast.LENGTH_SHORT).show();
            return;
        }
        TextView info=MAIN.get().info;
        LinearLayout path_layout=MAIN.get().path;
        long total=file.getTotalSpace();
        long used=file.getUsableSpace();
        String pr_str=null;

        if(file.canRead()) pr_str="r";
        if(file.canWrite()) pr_str="w";
        if(file.canWrite()&&file.canRead()) pr_str="r/w";

        path_layout.removeAllViews();
        final String[] names=path.split("/");
        for(int i=0;i<names.length;i++) {
            TextView tv = new TextView(MAIN.get());
            tv.setBackgroundResource(R.drawable.path_border);
            LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(-2,-2);
            ll.setMargins(0,0,MAIN.get().dip2px(1.5f),0);
            tv.setLayoutParams(ll);
            tv.setPadding(4,0,4,0);
            tv.setText(names[i].concat("/"));
            tv.setTextColor(MAIN.get().getResources().getColor(R.color.name_text));
            tv.setTextSize(MAIN.get().dip2px(4.5f));
            final int i_ = i;
            tv.setOnClickListener(new View.OnClickListener() {
                String[] strs=Arrays.copyOfRange(names,0, i_+1);
                @Override
                public void onClick(View view) {
                    StringBuilder res=new StringBuilder();
                    for (int i=0;i<strs.length;i++)
                        res.append(strs[i]+"/");
                    res.setLength(res.length()-1);
                    if(i_!=names.length-1)
                        setDirPath(res.toString());
                }
            });
            path_layout.addView(tv);
        }
        final HorizontalScrollView scrollView=Info.MAIN.get().findViewById(R.id.scroll);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollBy(scrollView.getMaxScrollAmount(),0);
            }
        },10);
        Animation out=AnimationUtils.makeOutAnimation(MAIN.get(),false);
        final Animation in=AnimationUtils.makeInAnimation(MAIN.get(),false);
        out.setDuration(130);
        in.setDuration(130);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.startAnimation(in);
                view.setAdapter(fva=new FileViewAdapter(MAIN.get(),path));
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        view.startAnimation(out);
        file=new File(path);
        info.setText(String.format(MAIN.get().getString(R.string.info),toReadableSpace(used),toReadableSpace(total),file.list().length,pr_str));
        TOTAL_ITEMS=file.list().length;
    }
    public static String toReadableSpace(long b){
        String s=null;
        DecimalFormat df=new DecimalFormat("#.0");
        if(b<1024) s=b+(b==1?" Byte":" Bytes");
        if(b>=1024&&b<1024*1024) s=df.format(b/1024f)+" KB";
        if(b>=1024*1024&&b<1024*1024*1024) s=df.format(b/1024f/1024f)+" MB";
        if(b>=1024*1024*1024) s=df.format(b/1024f/1024f/1024f)+" GB";
        return s;
    }
    public static String shortenString(String str,int max){
        if(str.length()>max)
            return "..."+str.substring(str.length()-max-3);
        return str;
    };
}
