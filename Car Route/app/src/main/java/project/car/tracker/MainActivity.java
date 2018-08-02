package project.car.tracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_LOCATION = 1;
    private static final String TAG = "MyActivity";

    private TextView textViewDateTime = null;
    private TextView textViewLongCoord = null;
    private TextView textViewLatCoord = null;
    private MyLocation myLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textViewDateTime = findViewById(R.id.textViewDateTime);
        this.textViewLongCoord = findViewById(R.id.textViewLongitudeCoordonate);
        this.textViewLatCoord = findViewById(R.id.textViewLatitudeCoordonate);

        updateCurrentDateTime();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Log.i(TAG, "Runtime permission");
        }

        if(myLocation == null) {
            myLocation = new MyLocation(this.textViewLongCoord, this.textViewLatCoord, this, this);
        }

        myLocation.updateLocation();
    }


    private void updateCurrentDateTime() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                putDateTimeIntoTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) { }
            }
        }.start();
    }

    private void putDateTimeIntoTextView() {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
        String dateStr = sdf.format(date);
        this.textViewDateTime.setText(dateStr);
    }
}
