package com.example.color_detection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ColorlistAdapter extends RecyclerView.Adapter {

    private int[] colors;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    //コンストラクタ
    public ColorlistAdapter(Context context, int[] colors,View.OnClickListener listener){
        this.colors = colors;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parents, int i) {

        View view = inflater.inflate(R.layout.color_grid_item,parents,false); //ビューの生成

        ColorlistHolder holder = new ColorlistHolder(view);  //ホルダーの生成

        view.setOnClickListener(listener); //クリック時のリスナーを設定

        //imageviewのサイズを設定
        int height = parents.getHeight();     //親のconstraint（画面）の高さ
        int step = height / 3;
        holder.imageview.setMinimumWidth(step);
        holder.imageview.setMaxWidth(step);
        holder.imageview.setMinimumHeight(step);
        holder.imageview.setMaxHeight(step);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //Hueがcolors[i]の色画像を作成し、imageviewにセットする

        //hueの色画像を作成
        ArrayList<Mat> channels = new ArrayList<>();
        Mat hue = Mat.eye(10,10,CvType.CV_8UC1);
        Mat saturation = hue.clone();
        Mat value = hue.clone();
        channels.add(hue.setTo(Scalar.all(colors[i])));
        channels.add(saturation.setTo(Scalar.all(255)));
        channels.add(value.setTo(Scalar.all(180)));
        Mat hsv = new Mat();
        Core.merge(channels,hsv);
        Mat bgr = new Mat();
        Imgproc.cvtColor(hsv,bgr,Imgproc.COLOR_HSV2RGB);

        //imageviewに色画像をセット
        Bitmap bmp = Bitmap.createBitmap(10,10, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(bgr,bmp);
        ColorlistHolder holder = (ColorlistHolder)viewHolder;
        holder.imageview.setImageBitmap(bmp);

        //クリックした色のhueを取り出すため、view(constrait)に値を詰める
        ((ColorlistHolder) viewHolder).parents.setId(colors[i]);
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    public class ColorlistHolder extends RecyclerView.ViewHolder{
        ImageView imageview;
        View parents;
        public ColorlistHolder(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageView);
            parents = itemView;     //クリック時にhue値を詰めるため
        }
    }
}
