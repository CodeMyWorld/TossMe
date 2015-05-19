package com.alex.tossme;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.security.MessageDigest;



public class MainActivity extends Activity implements SensorEventListener{

    private float[] gravityValue = new float[3];
    private TextView show;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);




        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,gravitySensor,SensorManager.SENSOR_DELAY_UI);




        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);



        Button bt = (Button)findViewById(R.id.shareBt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Hello Facebook")
                            .setContentDescription(
                                    "The 'Hello Facebook' sample  showcases simple Facebook integration")
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });

        show = (TextView)findViewById(R.id.hello);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/smarc.TTF");
        show.setTypeface(tf);
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
                }
                if(flag && lengthVector > 8){
                    long endTime = System.currentTimeMillis();
                    long durationTime = endTime - startTime;
                    long seconds = durationTime / 1000;
                    long milliSeconds = durationTime % 1000;
                    String result = show.getText().toString();
                    result += "\n"+seconds+"."+milliSeconds;
                    show.setText(result);
                    Toast.makeText(this," "+seconds+"."+milliSeconds,Toast.LENGTH_SHORT).show();
                    flag = false;
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
