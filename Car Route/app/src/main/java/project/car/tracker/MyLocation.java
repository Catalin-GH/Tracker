package project.car.tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

public class MyLocation {

    private TextView tvLatitude;
    private TextView tvLongitude;
    private Activity activity;
    private Context context;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private int interval = 5000;

    MyLocation(TextView tvLat, TextView tvLong, Activity activity, Context context) {

        this.tvLatitude = tvLat;
        this.tvLongitude = tvLong;
        this.activity = activity;
        this.context = context;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        setUpLocationRequest();
        statusCheck();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
    }

    @SuppressLint("MissingPermission")
    public void updateLocation() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location currentLocation = locationResult.getLastLocation();
                if(currentLocation !=  null) {
                    double longitude = currentLocation.getLongitude();
                    double latitude = currentLocation.getLatitude();

                    tvLatitude.setText(String.valueOf(latitude));
                    tvLongitude.setText(String.valueOf(longitude));
                } else {
                    tvLatitude.setText("Undetectable location!");
                    tvLongitude.setText("Undetectable location!");
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void setUpLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(interval);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) activity.getSystemService(context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage("Location disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = dialog.create();
        alert.show();
    }
}
