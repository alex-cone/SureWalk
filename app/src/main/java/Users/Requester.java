package Users;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ee461l.surewalk.FirebaseVariables;

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
        DatabaseReference mypostref = requestDatabase.push();

        Request newRequest = new Request();
        newRequest.setRequest(null, this, currLocLat, currLocLong, destinationLocLat, destinationLocLong, mypostref.getKey());

        mypostref.setValue(newRequest);

        mypostref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("SureWalk", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        Log.d("SureWalk", "You sent a Request");
        return newRequest;
    }

    public void cancelRequest(Request request) {
        final DatabaseReference requestDatabase = FirebaseVariables.getDatabaseReference().child("Requests").child(request.getFirebaseId());
        request.setStatus(Request.STATUS.CANCELED);
        requestDatabase.setValue(request);

    }
}
