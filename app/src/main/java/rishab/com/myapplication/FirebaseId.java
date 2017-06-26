package rishab.com.myapplication;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by RISHAB on 26-Jun-17.
 */

public class FirebaseId extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("FirebaseId", "Refreshed token: " + refreshedToken);

        //  Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String refreshedToken){
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("FirebaseId");
        databaseReference.push().child(refreshedToken);
        firebaseDatabase.goOffline();
    }

}
