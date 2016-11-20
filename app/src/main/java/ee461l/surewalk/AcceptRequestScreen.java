package ee461l.surewalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import Users.Request;
import Users.Requester;
import Users.Walker;

/**
 * Created by Diego on 11/7/2016.
 */
public class AcceptRequestScreen extends Activity {
    private ImageView profilePicture;
    private TextView txtName;
    private TextView txtPhoneNumber;
    private Button btnAcceptRequest;
    private Button btnBackToRequests;

    private String requestKey;
    private Request currentRequest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_accept_request_screen);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            requestKey = extras.getString("RequestID");
        }


        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
        btnAcceptRequest = (Button) findViewById(R.id.btnAcceptRequest);
        btnBackToRequests = (Button) findViewById(R.id.btnBackToRequests);

        FirebaseVariables.getDatabaseReference().child("Requests").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentRequest = dataSnapshot.child(requestKey).getValue(Request.class);

                        Requester requester = currentRequest.getRequester();
                        txtName.setText(requester.username);
                        txtPhoneNumber.setText(requester.phoneNumber);
                        StorageReference profilePicutreRef = FirebaseVariables.getStorageReference().child("userProfilePictures").child(requester.uid).child("ProfilePicture");
                        Glide.with(getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(profilePicutreRef)
                                .into(profilePicture);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("SureWalk", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(currentRequest != null){
                    currentRequest.setStatus(Request.STATUS.ACCEPTED);
                    Walker mockWalker = new Walker();
                    mockWalker.setWalker("Test", "test@utexas.edu", "5555555555", "no");
                    currentRequest.setWalker(mockWalker);
                    FirebaseVariables.getDatabaseReference().child("Requests").child(requestKey).setValue(currentRequest);
                    Intent intent = new Intent(AcceptRequestScreen.this, WalkerCurrentlyWalkingScreen.class);
                    intent.putExtra("RequestInfo",(new Gson()).toJson(currentRequest));
                    startActivity(intent);
                    finish();
                }


            }
        });

        btnBackToRequests.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(AcceptRequestScreen.this, WalkerViewRequests.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
