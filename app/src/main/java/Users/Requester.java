package Users;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void newRequest(String currLoc, String dest){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference requestDatabase = mDatabase.child("Requests");

        Request newRequest = new Request();
        newRequest.setRequest(null, this, currLoc, dest);

        requestDatabase.push().setValue(newRequest);
        Log.d("SureWalk", "You sent a Request");

    }

}
