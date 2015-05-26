package com.alex.tossme;

import android.app.Activity;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import java.math.BigDecimal;





public class MainActivity extends Activity implements SensorEventListener{

    private float[] gravityValue = new float[3];
    private ShareDialog shareDialog;

    private TextView hangtime;
    private TextView height;
    private TextView speed;

    private TextView hangtimeValue;
    private TextView heightValue;
    private TextView speedValue;
    private final double g = 9.8;

    private SoundPool fall;
    private SoundPool hit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);



        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,gravitySensor,SensorManager.SENSOR_DELAY_UI);

        shareDialog = new ShareDialog(this);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/smarc.TTF");
        Button shareBt = (Button)findViewById(R.id.shareBt);
        shareBt.setTypeface(tf);
        shareBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Look at my record!!")
                            .setContentDescription(
                                    "I just throw my tablet into "+heightValue.getText().toString()+" height air")
                            .setContentUrl(Uri.parse("http://softlab.cs.tsukuba.ac.jp"))
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });

        Button resetBt = (Button)findViewById(R.id.reset);
        resetBt.setTypeface(tf);
        resetBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        hangtime = (TextView)findViewById(R.id.hangtime);
        speed = (TextView)findViewById(R.id.speed);
        height = (TextView)findViewById(R.id.height);
        hangtimeValue = (TextView)findViewById(R.id.hangtimeValue);
        speedValue = (TextView)findViewById(R.id.speedValue);
        heightValue = (TextView)findViewById(R.id.heightValue);
        hangtime.setTypeface(tf);
        speed.setTypeface(tf);
        height.setTypeface(tf);
        hangtimeValue.setTypeface(tf);
        speedValue.setTypeface(tf);
        heightValue.setTypeface(tf);

        fall = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        fall.load(this,R.raw.fall,1);
        hit = new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
        hit.load(this,R.raw.hit,1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean flag = false;
    private long startTime = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        double lengthVector = Math.sqrt(gravityValue[0] * gravityValue[0]+gravityValue[1]*gravityValue[1]+
                gravityValue[2] *gravityValue[2]);
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                gravityValue = sensorEvent.values.clone();
                //show.setText(""+lengthVector);
                if( !flag && lengthVector < 1){
                    startTime = System.currentTimeMillis();
                    //Toast.makeText(this," "+startTime,Toast.LENGTH_SHORT).show();
                    flag = true;
                    fall.play(1,1,1,0,0,1);
                }
                if(flag && lengthVector > 8){
                    long endTime = System.currentTimeMillis();
                    long durationTime = endTime - startTime;
                    double hangtime = (double)durationTime/1000;

                    String speed = getSpeed(hangtime);
                    String height = getHeight(hangtime);
                    String result =hangtime+"s";
                    hangtimeValue.setText(result);
                    heightValue.setText(height);
                    speedValue.setText(speed);
                    hit.play(1,1,1,0,0,1);
                    flag = false;
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public String getSpeed(double time){
        time /= 2;
        BigDecimal speed = new BigDecimal(g * time);
        String result = speed.setScale(2,BigDecimal.ROUND_HALF_UP).toString()+"m/s";
        return result;
    }
    public String getHeight(double time){
        time /= 2;
        BigDecimal height = new BigDecimal(g * time * time);
        String result = height.setScale(2,BigDecimal.ROUND_HALF_UP).toString()+"m";
        return result;
    }

    public void reset(){
        hangtimeValue.setText(R.string.initValue);
        heightValue.setText(R.string.initHeightValue);
        speedValue.setText(R.string.initSpeedValue);
    }
}
