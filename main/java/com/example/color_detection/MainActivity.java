package com.example.color_detection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mCameraView;
    int targetHue = 0;      //検出対象のhue(色)
    int hueRange = 30;      //検出幅 targetHue +- hue Range
    int satMin = 30;        //検出対象の最小彩度
    int satMax = 255;       //検出対象の最大彩度
    int valMin = 30;        //検出対象の最小明度
    int valMax = 240;       //検出対象の最大明度
    boolean mode = true;    //描画モード、trueがcoat(上塗り)、falseがonly(検出部分のみ)
    ImageView imageView;
    ImageProc imageProc;
    int counter = 0;
    Mat t = new Mat();
    Mat m = new Mat();
    Mat p = new Mat();

    static{
        System.loadLibrary("opencv_java3");     //ライブラリのロード
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraView = (CameraBridgeViewBase)findViewById(R.id.camera_view);
        mCameraView.setCvCameraViewListener(this);  //interfaceは自クラスに実装

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),ColorSelect.class);
                startActivityForResult(intent,100);
            }
        });

        Button settingButton = findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                intent.putExtra("hueRange",hueRange);
                intent.putExtra("satMin",satMin);
                intent.putExtra("satMax",satMax);
                intent.putExtra("valMin",valMin);
                intent.putExtra("valMax",valMax);
                intent.putExtra("mode",mode);
                startActivityForResult(intent,200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                targetHue = data.getIntExtra("hue",0);
                SetImageOfImageView();
            }
        }else if(requestCode == 200){
            if( resultCode == RESULT_CANCELED){
                hueRange = data.getIntExtra("hueRange",hueRange);
                satMin = data.getIntExtra("satMin",satMin);
                satMax = data.getIntExtra("satMax",satMax);
                valMin = data.getIntExtra("valMin",valMin);
                valMax = data.getIntExtra("valMax",valMax);
                mode = data.getBooleanExtra("mode",mode);
            }
        }
    }

    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();  //フレーム取得終了
            imageProc.Release();    //メモリ解放
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.enableView();   //フレーム取得開始
        imageProc = new ImageProc();    //画像処理オブジェクトの生成
        imageProc.Malloc();
        SetImageOfImageView();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this, mLoaderCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.disableView();  //フレーム取得終了
            imageProc.Release();    //メモリ解放
        }
    }

    //-----CvCameraViewListener2のinterface-----------------
    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    //カメラフレーム読み出し時のコールバック
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        imageProc.Release();
        return imageProc.Proc(inputFrame.rgba(),targetHue,hueRange,satMin,satMax,valMin,valMax,mode);
    }
    //----------------------------------------------------------

    //MainActivityのImageViewに画像をセット
    private void SetImageOfImageView(){
        int cols = 10;
        int rows = 10;
        Mat hsv = Mat.eye(rows,cols,CvType.CV_8UC3);
        Scalar color = new Scalar(targetHue,255,180); //hue:hue,255:saturation,180:value
        hsv.setTo(color);
        Mat rgb = new Mat();
        Imgproc.cvtColor(hsv,rgb,Imgproc.COLOR_HSV2RGB);
        Bitmap bmp = Bitmap.createBitmap(cols,rows, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgb,bmp);
        hsv.release();
        rgb.release();
        imageView.setImageBitmap(bmp);
    }
}
