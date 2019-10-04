package com.dfile.dbrt.dfile;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    public FileViewControl fvc;
    public LinearLayout path;
    public LinearLayout tool_bar;
    public TextView info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Info.MAIN=new WeakReference<>(this);

        final ListView file_list;

        Info.FILELIST=new WeakReference<>(file_list =findViewById(R.id.fileList));
        path=findViewById(R.id.path);
        tool_bar=findViewById(R.id.tool_bar);
        info=findViewById(R.id.file_info);
        fvc=new FileViewControl(file_list, Environment.getExternalStorageDirectory().getPath());
        file_list.setItemsCanFocus(true);
        file_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Info.SCROLL_Y=i;
                Log.d("onScroll",""+i);
            }
        });
        ImageButton button_delete=findViewById(R.id.button_delete),
                button_copy=findViewById(R.id.button_copy),
                button_cut=findViewById(R.id.button_cut);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(File f : Info.SELECTED_FILE)
                    deleteAll(f);
                fvc.update();
                Info.SELECTED_FILE.clear();
            }
        });
    }
    public static void deleteAll(File file) {
        if(file.isFile()||file.list().length==0) {
            file.delete();
        }else {
            File files[] = file.listFiles();
            for(File f :files) {
                deleteAll(f);
                f.delete();
            }
        }
    }
    public int dip2px(float dpValue) {
        Context context=MainActivity.this;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
