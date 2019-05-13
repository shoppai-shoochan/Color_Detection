package com.example.color_detection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class ColorSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);

        RecyclerView recyclerview = findViewById(R.id.colorlist);

        int[] colors = new int[36];     //colorlistのhue値
        for(int i=0 ;i<36; ++i){
            colors[i] = i * 5;
        }

        View.OnClickListener listener = new View.OnClickListener() {    //itemクリック時のリスナー
            @Override
            public void onClick(View view) {
                int hue = view.getId();     //bind時に詰めた値を取得
                Intent intent = new Intent();
                intent.putExtra("hue",hue); //選択した色のhueを詰める
                setResult(RESULT_OK,intent);    //MainActivityで返す
                finish();
            }
        };

        ColorlistAdapter adapter = new ColorlistAdapter(getApplication(),colors,listener);
        recyclerview.setAdapter(adapter);
        RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(this,3, LinearLayoutManager.HORIZONTAL,false);
        recyclerview.setLayoutManager(layoutmanager);
    }
}

