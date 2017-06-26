package rishab.com.myapplication;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "ADDRESS";
    private static final int ACCESS_FINE_LOCATION_CODE = 0;
    private static int locnum = 0;
    LocationManager locationManager;
    TextView addresstextView;
    ProgressDialog progressDialog;
    ConnectivityManager connectivityManager;
    private Boolean AppStart = false;
    String result = null;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    int stillamt=0;
    FirebaseDatabase database;
    String id=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
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
        DatabaseReference reference = database.getReference("FirebaseId");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                id = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && AppStart) {
            EnableNetwork();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Pickup button action listener
    public void Pickupbtn(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //  Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
            return;
        }
        googleApiClient.connect();
        progressDialog.show();
        stillamt=0;
//        SendNotification sendNotification = new SendNotification();
//        sendNotification.execute("Your package has been picked-up",id);
    }


    //To get permission if the permission is not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplication(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
                AppStart = true;
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
                SendDataToServer(result, location);
                Log.d("FirebaseData", String.valueOf(locnum));
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder");
        }
    }

    // sending the address to the firebase database
    public void SendDataToServer(String address, Location location) {
        //SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmmss");
        //Date date = Calendar.getInstance().getTime();

        if (database == null) {
            Toast.makeText(this, "Could not connect to database", Toast.LENGTH_SHORT).show();
            return;
        }
        database.goOnline();
        DatabaseReference myRef = database.getReference("Location");
        String a = myRef.push().getKey();
        DatabaseReference myRef1 = database.getReference("LatestLocation/String");
        AddressLocation address1 = new AddressLocation(address, location.getLatitude(), location.getLongitude());
        myRef.child(a).setValue(address1, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.e("Location", "LOCATION Saved");
            }

        });
        myRef1.setValue(address);
        myRef1 = database.getReference("LatestLocation/lat");
        myRef1.setValue(location.getLatitude());
        myRef1 = database.getReference("LatestLocation/lon");
        myRef1.setValue(location.getLongitude());

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected())
            googleApiClient.disconnect();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
            return;
        }
//        if (locationManager != null)
//            locationManager.removeUpdates(this);
        AppStart = false;
        if (FirebaseDatabase.getInstance() != null) {
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
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
            return;
        }


        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
            addresstextView.setText("Item Delevired");
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(300000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Location","Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Location","Connection Failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("Location","Location Changed");
        progressDialog.cancel();
        address(location);
    }

    public void delaydelivery(View view){
        SendNotification sendNotification = new SendNotification();
        sendNotification.execute("Your package has been delayed",id);
    }

    public class SendNotification extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            ArrayList<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("id",strings[1]));
            list.add(new BasicNameValuePair("msg",strings[0]));
            JSONParser jsonParser = new JSONParser();
            String result = jsonParser.postHttpRequest("http://hubblebubble.000webhostapp.com/Notification.php",list);
            Log.d("SendNotification",result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("SendNotification",s);
        }
    }
}
