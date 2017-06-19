package rishab.com.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LauncherActivity extends AppCompatActivity {

    private Boolean AppStart=false;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);;
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState().equals(NetworkInfo.State.DISCONNECTED) &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState().equals(NetworkInfo.State.DISCONNECTED)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recreate();
                        }
                    }).setCancelable(false).setMessage("Not connected to internet. Turn on mobile data or wifi and try again.").create();
            alertDialog.setTitle("No Internet");
            alertDialog.show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
            return;
        }
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState().equals(NetworkInfo.State.CONNECTED) ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState().equals(NetworkInfo.State.CONNECTED)) {
             locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                EnableNetwork();
            }
        }

    }
    //to go to agent panel
    public void Agent(View view){
        startActivity(new Intent(LauncherActivity.this,MainActivity.class));
    }

    // method to go to coustomer panel
    public void Customer(View view){
        startActivity(new Intent(LauncherActivity.this,CustomerActivity.class));
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager!=null&&!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&AppStart) {
            EnableNetwork();
        }
    }
    //to get permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 0:
                if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplication(),"Permission granted",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplication(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //to enable gps
    public void EnableNetwork() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS not enabled");
        alertDialog.setMessage("GPS is not enabled. Go to settings to enable GPS");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                AppStart=true;
            }
        }).setCancelable(false);
        alertDialog.show();
    }
}
