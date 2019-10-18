package com.dfile.dbrt.dfile;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ImageActivity extends AppCompatActivity {
    ImageView image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100,0,0,0)));

        image=findViewById(R.id.image);
        Bitmap image_src=Info.IMAGE;
        image.setImageBitmap(image_src);

        Log.d("image_activity","ready");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
    float[] diff=new float[]{0,0};
    float dis=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount()==1) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                diff = new float[]{-event.getX()+image.getX(), -event.getY()+image.getY()};
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                image.setX(diff[0]+event.getX());
                image.setY(diff[1]+event.getY());
            }
        }
        if(event.getPointerCount()==2) {
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                diff = new float[]{-event.getX(1) + image.getX(), -event.getY(1) + image.getY()};
                dis = getDistanceFrom(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                Log.d("scales",""+dis);
            }
            if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                image.setX(diff[0] + event.getX(1));
                image.setY(diff[1] + event.getY(1));
                float dis_now=getDistanceFrom(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                image.setScaleX(dis_now/dis);
                image.setScaleY(dis_now/dis);
                Log.d("scale",""+dis_now+","+dis);
            }
        }
        return super.onTouchEvent(event);
    }

    public float getDistanceFrom(float sx,float ex,float sy,float ey){
        return (float) Math.sqrt(Math.pow(sx-ex,2)+Math.pow(sy-ey,2));
    }
}
