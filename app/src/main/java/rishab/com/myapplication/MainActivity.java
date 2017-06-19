package rishab.com.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "ADDRESS";
    private static final int ACCESS_FINE_LOCATION_CODE = 0;
    private static int locnum = 0;
    LocationManager locationManager;
    TextView addresstextView;
    ProgressDialog progressDialog;
    ConnectivityManager connectivityManager;
    private Boolean AppStart=false;
    String result = null;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch(Exception e)
        {
            e.getMessage();
        }
        //To check internet connection
         connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //To check gps is on or not
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState().equals(NetworkInfo.State.CONNECTED) ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState().equals(NetworkInfo.State.CONNECTED)) {
            addresstextView = (TextView) findViewById(R.id.address);
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Getting location please wait...");
            //progressDialog.setCancelable(false);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                EnableNetwork();
            }
        }
        countDownTimer = new CountDownTimer(45000,1000) {
            @Override
            public void onTick(long l) {
                
            }

            @Override
            public void onFinish() {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Unable to get Location pleasse try again", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager!=null&&!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&AppStart) {
            EnableNetwork();
        }
    }
    //Pickup button action listener
    public void Pickupbtn(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_CODE);
            return;
        }
        checkGps();
        countDownTimer.start();
    }


    //check GPS connectivity and request to get the current location of the agent
    public void checkGps() {
        try {
            //checking for the best available provider
            String provide = locationManager.getBestProvider(new Criteria(), true);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_CODE);
                return;
            }
            //requesting Location listener
            locationManager.requestLocationUpdates(provide, 300000, 0, this);
            Location location = locationManager.getLastKnownLocation(provide);
            progressDialog.show();
            if (location != null) {
                //Toast.makeText(this, "LOCATION FOUND", Toast.LENGTH_SHORT).show();
                Log.d("Last Location","LOCATION FOUND");
                Log.d("getLatitude", String.valueOf(location.getLatitude()));
                Log.d("getLongitude", String.valueOf(location.getLongitude()));
                //SendDataToServer("demo",location);
                //address(location);
                //return location;
            }
            else {
                //Toast.makeText(this, "LOCATION NOT FOUND", Toast.LENGTH_SHORT).show();
                Log.d("Last Location","LOCATION NOT FOUND");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    //  Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_CODE);
//                }
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0, this);
//                 location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                if (location != null) {
//                    Toast.makeText(this, "LOCATION FOUND2", Toast.LENGTH_SHORT).show();
//                    Log.e("getLatitude", String.valueOf(location.getLatitude()));
//                    Log.e("getLongitude", String.valueOf(location.getLongitude()));
//                    //return location;
//                } else {
//                    Toast.makeText(this, "LOCATION NOT FOUND1", Toast.LENGTH_SHORT).show();
//                    //return null;
//                }
//            }
//            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    //  Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_CODE);
//                }
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 0, this);
//                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            if (location != null) {
//                Toast.makeText(this, "LOCATION FOUND2", Toast.LENGTH_SHORT).show();
//                Log.e("getLatitudeNET", String.valueOf(location.getLatitude()));
//                Log.e("getLongitudeNET", String.valueOf(location.getLongitude()));
//                //return location;
//            } else {
//                Toast.makeText(this, "LOCATION NOT FOUND", Toast.LENGTH_SHORT).show();
//                //return null;
//            }
//        }
//        else {
//            Toast.makeText(this, "CONNECTION LOST", Toast.LENGTH_SHORT).show();
//        }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    //To get permission if the permission is not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case ACCESS_FINE_LOCATION_CODE:
                if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplication(),"Permission granted",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplication(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", String.valueOf(location.getLatitude()));
        Log.d("onLocationChanged", String.valueOf(location.getLongitude()));
        progressDialog.cancel();
        countDownTimer.cancel();
        if(location!=null){
            //To get the address of the current location
            address(location);
        }
        else {
            Toast.makeText(this, "LOCATION NOT FOUND ON", Toast.LENGTH_SHORT).show();
        }
        //newlocation=location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    //to enable gps from settings
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
    // method to get address form location
    public void address(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                //sb.append(address.getLocality()).append("\n");
                //sb.append(address.getPostalCode()).append("\n");
                //sb.append(address.getCountryName());
                result = sb.toString();
                addresstextView.setText(result);
                SendDataToServer(result,location);
                Log.d("FirebaseData", String.valueOf(locnum));
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder");
        }
    }
    // sending the address to the firebase database
    public void SendDataToServer(String address,Location location) {
        //SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmmss");
        //Date date = Calendar.getInstance().getTime();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(database==null){
            Toast.makeText(this, "Could not connect to database", Toast.LENGTH_SHORT).show();
            return;
        }
        database.goOnline();
        DatabaseReference myRef = database.getReference("Location");
        String a = myRef.push().getKey();
        DatabaseReference myRef1 = database.getReference("LatestLocation/String");
        AddressLocation address1 = new AddressLocation(address,location.getLatitude(),location.getLongitude());
        myRef.child(a).setValue(address1, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.e("Location","LOCATION Saved");
            }

        });
        //myRef.setValue(address);
        myRef1.setValue(address);
        //myRef = database.getReference("Location"+sdf.format(date)+"/lat");
        myRef1 = database.getReference("LatestLocation/lat");
        //myRef.setValue();
        myRef1.setValue(location.getLatitude());
        //myRef = database.getReference("Location"+sdf.format(date)+"/lat");
        myRef1 = database.getReference("LatestLocation/lon");
        //myRef.setValue(location.getLongitude());
        myRef1.setValue(location.getLongitude());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_CODE);
            return;
        }
        if(locationManager!=null)
            locationManager.removeUpdates(this);
        AppStart=false;
        if(FirebaseDatabase.getInstance()!=null){
            FirebaseDatabase.getInstance().goOffline();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    // to stop the location listener request
    public void Deliveredbtn(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION_CODE);
            return;
        }
        if(locationManager!=null) {
            locationManager.removeUpdates(this);
            addresstextView.setText("Item Delevired");
        }
    }
}
