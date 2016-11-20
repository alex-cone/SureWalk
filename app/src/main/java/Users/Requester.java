package Users;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import ee461l.surewalk.FirebaseVariables;
import ee461l.surewalk.RequesterCurrentlyWalkingScreen;
import ee461l.surewalk.RequesterRequestScreen;

/**
 * Created by Diego on 10/15/2016.
 */
public class Requester {
    public String username;
    public String email;
    public String phoneNumber;
    public String uid;

    public void setRequester(String username, String email, String phoneNumber, String uid) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
    }


    public Request newRequest(/*LatLng currLoc, LatLng dest*/ double currLocLat, double currLocLong,
                              double destinationLocLat, double destinationLocLong) {
        DatabaseReference requestDatabase = FirebaseVariables.getDatabaseReference().child("Requests");
        final DatabaseReference requestReference = requestDatabase.push();

        Request newRequest = new Request();
        newRequest.setRequest(null, this, currLocLat, currLocLong, destinationLocLat, destinationLocLong, requestReference.getKey());

        requestReference.setValue(newRequest);

        Log.d("SureWalk", "You sent a Request");
        return newRequest;
    }

    public void cancelRequest(final DatabaseReference requestReference) {
        requestReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Request request = dataSnapshot.getValue(Request.class);
                        request.setStatus(Request.STATUS.CANCELED);
                        requestReference.setValue(request);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



    }
}
