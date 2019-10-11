package com.dfile.dbrt.dfile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public FileViewControl fvc;
    public LinearLayout path;
    public LinearLayout tool_bar;
    public TextView info;
    public ImageButton button_delete,
            button_copy,
            button_cut,
            button_info,
            button_rename;
    private PopupWindow popupWindow,loadPopupWindow;
    private ProgressBar pb;
    private int mode=0;
    private Thread task_solve_thr;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Info.MAIN=new WeakReference<>(this);

        final ListView file_list;
        final CardView button_create=findViewById(R.id.button_create),
                button_paste=findViewById(R.id.button_paste);

        Info.FILELIST=new WeakReference<>(file_list =findViewById(R.id.fileList));
        path=findViewById(R.id.path);
        tool_bar=findViewById(R.id.tool_bar);
        info=findViewById(R.id.file_info);
        fvc=new FileViewControl(file_list, Environment.getExternalStorageDirectory().getPath());
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow=showCreateFilePopupWindow();
            }
        });

        button_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(File f:Info.SELECTED_FILE){
                    Log.d("copiedFiles",fvc.getCurrentFile().getPath()+"/"+f.getName());
                    if(f.isDirectory())
                        dirCopy(f.getAbsolutePath(),fvc.getCurrentFile().getPath()+"/"+f.getName());
                    if(f.isFile())
                        fileCopy(f.getAbsolutePath(),fvc.getCurrentFile().getPath()+"/"+f.getName());
                }
                if(mode==1){
                    for(File f:Info.SELECTED_FILE)
                        deleteAll(f);
                }
                fvc.update();
                button_paste.setVisibility(View.GONE);

                Info.SELECTED_FILE.clear();
            }
        });
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
        button_delete=findViewById(R.id.button_delete);
        button_copy=findViewById(R.id.button_copy);
        button_cut=findViewById(R.id.button_cut);
        button_info=findViewById(R.id.button_info);
        button_rename=findViewById(R.id.button_rename);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(File f : Info.SELECTED_FILE) {
                    total=0;
                    getFileCount(total,f);
                    deleteAll(f);
                }
                fvc.update();
                Info.SELECTED_FILE.clear();
                tool_bar.setVisibility(View.GONE);

            }
        });
        button_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode=0;
                button_paste.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"复制成功",Toast.LENGTH_SHORT).show();
                tool_bar.setVisibility(View.GONE);
                fvc.update();
            }
        });
        button_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode=1;
                button_paste.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"剪切成功",Toast.LENGTH_SHORT).show();
                tool_bar.setVisibility(View.GONE);
                fvc.update();
            }
        });
        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoPopupWindow(fvc.getCurrentFile());

            }
        });
    }
    public void getFile(ArrayList<File> fs, File file){
        File[] files=file.listFiles();
        if(file.isDirectory())
            for(int i=0;i<files.length;i++){
                File f=files[i];
                if(f.isFile())
                    fs.add(f);
                else
                    getFile(fs,f);
            }
        else fs.add(file);
    }
    public void getFileCount(int count,File file){
        File[] files=file.listFiles();
        if(file.isDirectory())
            for(int i=0;i<files.length;i++){
                File f=files[i];
                if(f.isFile())
                    count++;
                else
                    getFileCount(count,f);
            }
        else count=1;
    }
    int deleted=0;
    int total=0;
    public void deleteAll(final File file) {
        if(file.isFile()||file.list()==null||file.list().length==0) {
            file.delete();
            deleted=0;
            //pb.setProgress(100);
        }else {
            File files[] = file.listFiles();
            for(File f :files) {
                deleted++;
                deleteAll(f);
                f.delete();
                //pb.setProgress((int) ((float)deleted/(float) total*100f));
            }
        }
        if(file.exists()) file.delete();

    }
    public int dip2px(float dpValue) {
        Context context=MainActivity.this;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public PopupWindow showCreateFilePopupWindow(){
        final PopupWindow popupWindow=new PopupWindow(this);
        View content= LayoutInflater.from(this).inflate(R.layout.dialog_create_file,null);
        popupWindow.setWidth(dip2px(310));
        popupWindow.setHeight(dip2px(160));
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.DialogAnimation);
        popupWindow.setContentView(content);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0);
        final EditText file_name_edit=content.findViewById(R.id.file_name_edit);
        TextView quit_text=content.findViewById(R.id.text_quit),
                new_file_text=content.findViewById(R.id.text_quit),
                new_folder_text=content.findViewById(R.id.text_new_folder);
        quit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        new_file_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!new File(fvc.file.getPath()+"/"+file_name_edit.getText().toString()).createNewFile())
                        Toast.makeText(MainActivity.this,R.string.error2,Toast.LENGTH_SHORT).show();
                    fvc.update();
                    popupWindow.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        new_folder_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!new File(fvc.file.getPath()+"/"+file_name_edit.getText().toString()).mkdir())
                    Toast.makeText(MainActivity.this,R.string.error2,Toast.LENGTH_SHORT).show();
                fvc.update();
                popupWindow.dismiss();
            }
        });
        return popupWindow;
    }

    public PopupWindow showInfoPopupWindow(File file) {
        final PopupWindow popupWindow=new PopupWindow(this);
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_info, null);
        popupWindow.setWidth(dip2px(310));
        popupWindow.setHeight(dip2px(300));
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.DialogAnimation);
        popupWindow.setContentView(content);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        TextView path=content.findViewById(R.id.text_file_path),
                date=content.findViewById(R.id.text_file_date),
                name=content.findViewById(R.id.text_file_name),
                size=content.findViewById(R.id.text_file_size),
                right=content.findViewById(R.id.text_file_right),
                type=content.findViewById(R.id.text_file_type),
                child_count=content.findViewById(R.id.text_file_member);
        LinearLayout l=content.findViewById(R.id.l4);
        TextView quit=content.findViewById(R.id.text_quit);
        Date d=new Date(file.lastModified());
        path.setText(file.getPath());
        date.setText(d.getYear()+"年"+d.getMonth()+"月"+d.getDay()+"日"+d.getHours()+":"+d.getHours());
        name.setText(file.getName());
        size.setText(FileViewControl.toReadableSpace(file.length()));
        right.setText(file.canWrite()?"可写":"可读");
        type.setText(getFileTypeBySuffix(file));
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Info.SELECTED_FILE.clear();
                fvc.update();
                popupWindow.dismiss();
            }
        });
        l.setVisibility(View.GONE);
        return popupWindow;
    }

    public ProgressBar showLoadingPopupWindow(String title_str) {
        final PopupWindow popupWindow = new PopupWindow(this);
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_load, null);
        popupWindow.setWidth(dip2px(310));
        popupWindow.setHeight(dip2px(130));
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.DialogAnimation);
        popupWindow.setContentView(content);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        TextView title=content.findViewById(R.id.title);
        title.setText(title_str);
        TextView undo_text=content.findViewById(R.id.text_quit);
        ProgressBar pb=content.findViewById(R.id.progress_bar);
        undo_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        loadPopupWindow=popupWindow;
        return pb;
    }
    public void dirCopy(final String srcPath, final String destPath) {
        File src = new File(srcPath);
        if (!new File(destPath).exists()) {
            new File(destPath).mkdirs();
        }
        for (File s : src.listFiles()) {
            if (s.isFile()) {
                fileCopy(s.getPath(), destPath + File.separator + s.getName());
            } else {
                dirCopy(s.getPath(), destPath + File.separator + s.getName());
            }
        }
    }
    public String getFileSuffix(File file){
        String name=file.getName();
        if(name.lastIndexOf(name.lastIndexOf(".") + 1)!=-1)
            return name.substring(name.lastIndexOf(name.lastIndexOf(".") + 1)).toLowerCase();
        return null;
    }
    public String getFileTypeBySuffix(File file){
        if(file.isDirectory()) return "文件夹";
        if(getFileSuffix(file)==null) return "未知";
        switch (getFileSuffix(file)){
            case "png":
                return "PNG图像";
            case "jpg":
                return "JPG图像";
            case "gif":
                return "GIF动画";
            case "txt":
                return "文本";
            case "ppt":
                return "演示文档";
            case "pptx":
                return "演示文档";
            case "doc":
                return "文档";
            case "docx":
                return "文档";
            case "html":
                return "网页";
            case "htm":
                return "网页";
            case "zip":
                return "ZIP压缩包";
            case "rar":
                return "RAR压缩包";
            case "7z":
                return "7Z压缩包";
            case "apk":
                return "安装包";
            case "ogg":
                return "音效";
            case "wav":
                return "音轨";
            case "mp3":
                return "音乐";
            case "mp4":
                return "MP4视频";
            case "avi":
                return "AVI视频";
            default:
                return getFileSuffix(file).toUpperCase()+"文件";
        }
    }
    public void fileCopy(final String srcPath, final String destPath) {
        File src = new File(srcPath);
        File dest = new File(destPath);
        //使用jdk1.7 try-with-resource 释放资源，并添加了缓存流
        try (InputStream is = new BufferedInputStream(new FileInputStream(src));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] flush = new byte[1024];
            int len = -1;
            while ((len = is.read(flush)) != -1) {
                out.write(flush, 0, len);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        fvc.setDirPath(fvc.file.getParent());
    }
}
