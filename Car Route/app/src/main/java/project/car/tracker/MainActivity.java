package project.car.tracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity
{
    static final int REQUEST_LOCATION = 1;

    private TextView textViewDateTime = null;
    private TextView textViewLongCoord = null;
    private TextView textViewLatCoord = null;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textViewDateTime = findViewById(R.id.textViewDateTime);
        this.textViewLongCoord = findViewById(R.id.textViewLongitudeCoordonate);
        this.textViewLatCoord = findViewById(R.id.textViewLatitudeCoordonate);

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(3000);
        this.mLocationRequest.setFastestInterval(3000);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        updateCurrentDateTime();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        statusCheck();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(this.mLocationRequest);
        this.mLocationSettingsRequest = builder.build();

        this.mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location currentLocation = locationResult.getLastLocation();
                if(currentLocation !=  null) {
                        double longitude = currentLocation.getLongitude();
                        double latitude = currentLocation.getLatitude();

                        textViewLatCoord.setText(String.valueOf(latitude));
                        textViewLongCoord.setText(String.valueOf(longitude));
                    } else {
                        textViewLatCoord.setText("Undetectable location!");
                        textViewLongCoord.setText("Undetectable location!");
                    }
            }
        };

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.mFusedLocationClient.requestLocationUpdates(this.mLocationRequest,
                this.mLocationCallback, null);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS is disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
