package com.dfile.dbrt.dfile;

import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

import static com.dfile.dbrt.dfile.Info.*;

public class FileViewControl {
    ListView view;
    File file;
    public FileViewControl(ListView recyclerView,String path){
        view=recyclerView;
        file=new File(path);
        setDirPath(path);
    }
    public void setDirPath(final String path){
        if(new File(path).listFiles()==null){
            Toast.makeText(MAIN.get(),R.string.error,Toast.LENGTH_SHORT).show();
            return;
        }
        LinearLayout path_layout=MAIN.get().path;
        path_layout.removeAllViews();
        final String[] names=path.split("/");
        for(int i=0;i<names.length;i++) {
            TextView tv = new TextView(MAIN.get());
            tv.setBackgroundResource(R.drawable.path_border);
            LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(-2,-2);
            ll.setMargins(0,0,MAIN.get().dip2px(3),0);
            tv.setLayoutParams(ll);
            tv.setPadding(4,0,4,0);
            tv.setText(names[i].concat("/"));
            tv.setTextColor(MAIN.get().getResources().getColor(R.color.name_text));
            tv.setTextSize(MAIN.get().dip2px(4));
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
                view.setAdapter(new FileViewAdapter(MAIN.get(),path));
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        view.startAnimation(out);

    }
    public File getCurrentFile(){
        return file;
    }
    public void goParentPath(){
        file=file.getParentFile();
        setDirPath(file.getPath());
    }
}
