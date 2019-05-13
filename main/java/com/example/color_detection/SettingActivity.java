package com.example.color_detection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    int hueRange=15;
    int satMin=30,satMax=255;
    int valMin=30,valMax=240;
    boolean mode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SeekBar hueRangeBar = findViewById(R.id.hueRange_seekbar);
        SeekBar satMinBar = findViewById(R.id.saturationMin_seekBar);
        SeekBar satMaxBar = findViewById(R.id.saturationMax_seekBar);
        SeekBar valMinBar = findViewById(R.id.valueMin_seekBar);
        SeekBar valMaxBar = findViewById(R.id.valueMax_seekBar);
        Switch modeSwitch = findViewById(R.id.mode_switch);

        //viewの初期値を取得
        Intent data = getIntent();
        hueRange = data.getIntExtra("hueRange",hueRange);
        satMin = data.getIntExtra("satMin",satMin);
        satMax = data.getIntExtra("satMax",satMax);
        valMin = data.getIntExtra("valMin",valMin);
        valMax = data.getIntExtra("valMax",valMax);
        mode = data.getBooleanExtra("mode",mode);

        //viewに初期値を設定
        hueRangeBar.setProgress(hueRange);
        satMinBar.setProgress(satMin);
        satMaxBar.setProgress(satMax);
        valMinBar.setProgress(valMin);
        valMaxBar.setProgress(valMax);
        modeSwitch.setChecked(mode);

        //Seedkbarにリスナーをセット
        hueRangeBar.setOnSeekBarChangeListener(onChaned);
        satMinBar.setOnSeekBarChangeListener(onChaned);
        satMaxBar.setOnSeekBarChangeListener(onChaned);
        valMinBar.setOnSeekBarChangeListener(onChaned);
        valMaxBar.setOnSeekBarChangeListener(onChaned);

        //Switch
        modeSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mode = isChecked;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtra("hueRange",hueRange);
            intent.putExtra("satMin",satMin);
            intent.putExtra("satMax",satMax);
            intent.putExtra("valMin",valMin);
            intent.putExtra("valMax",valMax);
            intent.putExtra("mode",mode);
            setResult(RESULT_CANCELED,intent);
            finish();
        }
        return false;
    }
    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("hueRange",hueRange);
//        outState.putInt("satMin",satMin);
//        outState.putInt("satMax",satMax);
//        outState.putInt("valMin",valMin);
//        outState.putInt("valMax",valMax);
//    }

    //SeekBarのリスナー
    private SeekBar.OnSeekBarChangeListener onChaned = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            seekBar.setProgress(progress);
            int id = seekBar.getId();
            switch(id){
                case R.id.hueRange_seekbar:
                    hueRange = progress;
                    break;
                case R.id.saturationMin_seekBar:
                    satMin = progress;
                    break;
                case R.id.saturationMax_seekBar:
                    satMax = progress;
                    break;
                case R.id.valueMin_seekBar:
                    valMin = progress;
                    break;
                case R.id.valueMax_seekBar:
                    valMax = progress;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
