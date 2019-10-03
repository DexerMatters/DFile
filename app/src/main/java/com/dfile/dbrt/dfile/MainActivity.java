package com.dfile.dbrt.dfile;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Info.MAIN=new WeakReference<>(this);

        ListView file_list;

        Info.FILELIST=new WeakReference<>(file_list =findViewById(R.id.fileList));
        path=findViewById(R.id.path);
        tool_bar=findViewById(R.id.tool_bar);
        fvc=new FileViewControl(file_list, Environment.getExternalStorageDirectory().getPath());
        file_list.setItemsCanFocus(true);

        ImageButton button_delete=findViewById(R.id.button_delete),
                button_copy=findViewById(R.id.button_copy),
                button_cut=findViewById(R.id.button_cut);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(File f : Info.SELECTED_FILE)
                    deleteAll(f);
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
