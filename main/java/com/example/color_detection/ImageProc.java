package com.example.color_detection;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageProc {

    private Mat img;
    private Mat dst;
    private Mat hsv;
    private ArrayList<Mat> channels = new ArrayList<Mat>();
    private Mat hueMask;
    private Mat hueMinMask,hueMaxMask;
    private Mat satMask;
    private Mat satMinMask,satMaxMask;
    private Mat valueMask;
    private Mat valueMinMask,valueMaxMask;
    private Mat mask1;
    private Mat mask2;
    private Mat draw;

    private Mat hierarcy;

    private Scalar color_coat = new Scalar(255,0,255);
    private Scalar color_only = new Scalar(1,1,1);
    private Mat kernel;
    private Point anchor = new Point(-1,-1);


    //hueで指定した色を検出し、該当領域を塗りつぶした画像を返す
    public Mat Proc(Mat src,int targetHue,int hueRange,int satMin,int satMax,int valMin,int valMax,boolean mode){
        //hueは0〜179までで一周、180以上は0からと同色
        int min = targetHue - hueRange;
        int max = targetHue + hueRange;
        if(min < 0){
            min += 180;
            max += 180;
        }

        Imgproc.cvtColor(src,img,Imgproc.COLOR_RGBA2RGB); //HSVへ変換
        Imgproc.cvtColor(img,hsv,Imgproc.COLOR_RGB2HSV); //HSVへ変換
        Core.split(hsv,channels);   //チャンネル分離

        //maskの作成、hue・saturation・valueを各々閾値でマスク
        Imgproc.threshold(channels.get(0),hueMinMask,min,1,Imgproc.THRESH_BINARY);
        Imgproc.threshold(channels.get(0),hueMaxMask,max,1,Imgproc.THRESH_BINARY_INV);
        hueMask = hueMinMask.mul(hueMaxMask);

        Imgproc.threshold(channels.get(1),satMinMask,satMin,1,Imgproc.THRESH_BINARY);
        Imgproc.threshold(channels.get(1),satMaxMask,satMax,1,Imgproc.THRESH_BINARY_INV);
        satMask = satMinMask.mul(satMaxMask);

        Imgproc.threshold(channels.get(2),valueMinMask,valMin,1,Imgproc.THRESH_BINARY);
        Imgproc.threshold(channels.get(2),valueMaxMask,valMax,1,Imgproc.THRESH_BINARY_INV);
        valueMask = valueMinMask.mul(valueMaxMask);

        mask1 = hueMask.mul(satMask);
        mask2 = mask1.mul(valueMask);

        //ノイズ除去、分離領域の結合
        kernel = Mat.ones(3,3, CvType.CV_8UC1);
        Imgproc.morphologyEx(mask2,mask2,Imgproc.MORPH_OPEN,kernel,anchor,3); //ノイズ除去
        Imgproc.morphologyEx(mask2,mask2,Imgproc.MORPH_CLOSE,kernel,anchor,3);  //分離領域の結合
        kernel.release();

        List<MatOfPoint> countors = new ArrayList<>();
        if(mode){
            //maskから、検出領域を上塗りした画像を生成
            Imgproc.findContours(mask2,countors,hierarcy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
            draw = Mat.zeros(img.rows(),img.cols(),CvType.CV_8UC3);
            Imgproc.drawContours(draw,countors,-1,color_coat,10);
            Core.add(img,draw,dst);     //入力フレームに検出領域を加算
        }else {
            //maskから、検出領域のみの画像を生成
            Imgproc.findContours(mask2,countors,hierarcy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
            draw = Mat.zeros(img.rows(),img.cols(),CvType.CV_8UC3);
            Imgproc.drawContours(draw,countors,-1,color_only,-1);
            dst = img.mul(draw);
        }
        for(MatOfPoint countor :countors){
            countor.release();
        }
        countors = null;

        return dst;
    }

//    private void Coat(){
//        //maskから、検出領域を上塗りした画像を生成
//        Imgproc.findContours(mask2,countors,hierarcy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
//        draw = Mat.zeros(img.rows(),img.cols(),CvType.CV_8UC3);
//        Imgproc.drawContours(draw,countors,-1,color_coat,-1);
//        Core.add(img,draw,dst);     //入力フレームに検出領域を加算
//    }
//
//    private void Only(){
//        //maskから、検出領域のみの画像を生成
//        Imgproc.findContours(mask2,countors,hierarcy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
//        draw = Mat.zeros(img.rows(),img.cols(),CvType.CV_8UC3);
//        Imgproc.drawContours(draw,countors,-1,color_only,-1);
//        dst = img.mul(draw);
//    }

    public void Malloc(){
        dst = new Mat();
        img = new Mat();
        hsv = new Mat();
        hueMask = new Mat();
        hueMinMask = new Mat();
        hueMaxMask = new Mat();
        satMask = new Mat();
        satMinMask = new Mat();
        satMaxMask = new Mat();
        valueMask = new Mat();
        valueMinMask = new Mat();
        valueMaxMask = new Mat();
        mask1 = new Mat();
        mask2 = new Mat();
        draw = new Mat();
        hierarcy = new Mat();
    }

    public void Release(){
        dst.release();
        img.release();
        hsv.release();
        for(Mat channel : channels){
            channel.release();
        }
        hueMask.release();
        hueMinMask.release();
        hueMaxMask.release();
        satMask.release();
        satMinMask.release();
        satMaxMask.release();
        valueMask.release();
        valueMinMask.release();
        valueMaxMask.release();
        mask1.release();
        mask2.release();
        draw.release();
//        for(MatOfPoint countor :countors){
//            countor.release();
//        }
        hierarcy.release();
    }
}
