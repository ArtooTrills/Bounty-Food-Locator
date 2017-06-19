package rishab.com.myapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CustomerActivity extends AppCompatActivity implements LocationListener {

    public static final String TAG = "CustomerActivity";
    FirebaseDatabase database ;
    private static final int ACCESS_FINE_LOCATION_CODE = 0;
    TextView addressView;
    String value=null;
    Double lat,lon;
    Location userloc = new Location("userloc");
    Float distance = Float.MAX_VALUE;
    LocationManager locationManager;
    private Boolean AppStart=false;
    ArrayList<String> AllLocations;
    ListView AllLocationsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch(Exception e)
        {
            e.getMessage();
        }
        //to check internet connection
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
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
        }
        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            EnableNetwork();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressView = (TextView)findViewById(R.id.addressView);
        //getting the instance of the firebase database
        database = FirebaseDatabase.getInstance();
        if(database==null){
            Toast.makeText(this, "Could not connect to database", Toast.LENGTH_SHORT).show();
            return;
        }
        database.goOnline();
        getUserLoc();
        DatabaseReference myRef = database.getReference("LatestLocation");
        DatabaseReference myRef1 = database.getReference("Location");
        // to get the latest location of the agent
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                value = dataSnapshot.child("String").getValue(String.class);
                lat = dataSnapshot.child("lat").getValue(Double.class);
                lon = dataSnapshot.child("lon").getValue(Double.class);
                userloc.setLatitude(lat);
                userloc.setLatitude(lon);
                //addressView.setText(value);
                Log.d(TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "Databse connection error", Toast.LENGTH_SHORT).show();
                Log.e( "Failed to read value.", error.getMessage());
            }
        });
        // to get all the location of the agents
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AllLocations = new ArrayList<String>();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    AddressLocation addressLocation = messageSnapshot.getValue(AddressLocation.class);
                    AllLocations.add(addressLocation.getLocationAddress());
                    ShowAllLocation(AllLocations);
                    Log.d("addressLocation",addressLocation.getLocationAddress());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Databse connection error", Toast.LENGTH_SHORT).show();
                Log.e("databaseError",databaseError.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager!=null&&!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&AppStart) {
            EnableNetwork();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    //check location btn listener
    public void CheckLoc(View view){
        if(value==null){
            addressView.setText("Location Not available");
        }else {
            addressView.setText(value);
        }
    }
    // method to get the user location
    public void getUserLoc()
    {
        try {

            String provide = locationManager.getBestProvider(new Criteria(), true);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            locationManager.requestLocationUpdates(provide, 300000, 0, this);
            Location location = locationManager.getLastKnownLocation(provide);
            //progressDialog.show();
            if (location != null) {
                //Toast.makeText(this, "LOCATION FOUND", Toast.LENGTH_SHORT).show();
                Log.d("User Last Location", String.valueOf(location.getLatitude()));
                Log.d("User Last Location", String.valueOf(location.getLongitude()));
                //address(location);
                //return location;
            }
            else {
                Log.w("User Last Location", "LOCATION NOT FOUND");
                //Toast.makeText(this, "LOCATION NOT FOUND", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocation", "LOCATION FOUND");
        //Toast.makeText(this, "LOCATION FOUND", Toast.LENGTH_SHORT).show();
        DelayNotification(location.distanceTo(userloc));
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
    // method to issue the notification if the distance between the user location
    // and the agent current location increases afterthe pickup of the delivery
    public void DelayNotification(Float dist){
        if(dist<distance){
            distance=dist;
        }
        else{
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContentTitle("Notification")
                .setContentText("Delivery Delayed")
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify (0,builder.build());
            distance=dist;
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
    //to get permission
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
    // to populate the list view with all the location of the agents
    private void ShowAllLocation(ArrayList<String> allLocations) {
        AllLocationsList = (ListView) findViewById(R.id.AllLocation);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,allLocations);
        AllLocationsList.setAdapter(adapter);
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
}

