package Users;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
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

    public Request newRequest(String currLoc, String dest) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference requestDatabase = mDatabase.child("Requests");
        DatabaseReference mypostref = requestDatabase.push();

        Request newRequest = new Request();
        newRequest.setRequest(null, this, currLoc, dest, mypostref.getKey());

        mypostref.setValue(newRequest);

        Log.d("SureWalk", "You sent a Request");
        return newRequest;
    }

    public void cancelRequest(Request request) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference requestDatabase = mDatabase.child("Requests").child(request.getFirebaseId());
        request.setStatus(Request.STATUS.CANCELED);
        requestDatabase.setValue(request);

    }
}
