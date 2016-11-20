package ee461l.surewalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Users.Request;
import Users.Requester;

/**
 * Created by Diego on 11/7/2016.
 */
public class AcceptRequestScreen extends FragmentActivity implements OnMapReadyCallback {
    private ImageView profilePicture;
    private TextView txtName;
    private TextView txtPhoneNumber;
    private Button btnAcceptRequest;
    private Button btnBackToRequests;

    private GoogleMap mMap;

    private String requestKey;
    private Request currentRequest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_accept_request_screen);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                        // Get user value

                        currentRequest = dataSnapshot.child(requestKey).getValue(Request.class);

                        Requester requester = currentRequest.getRequester();
                        txtName.setText(requester.username);
                        txtPhoneNumber.setText(requester.phoneNumber);
                        StorageReference profilePicutreRef = FirebaseVariables.getStorageReference().child("userProfilePictures").child(requester.uid).child("ProfilePicture");
                        Glide.with(getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(profilePicutreRef)
                                .into(profilePicture);

                        //Show location on map
                        LatLng requesterLoc = new LatLng(currentRequest.getCurrentLatitude(), currentRequest.getCurrentLongitude());
                        LatLng destinationLoc = new LatLng(currentRequest.getDestinationLatitude(), currentRequest.getDestinationLongitude());

                        MarkerOptions requesterMarker = new MarkerOptions().position(requesterLoc);
                        MarkerOptions destinationMarker = new MarkerOptions().position(destinationLoc);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(requesterLoc));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

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
                    FirebaseVariables.getDatabaseReference().child("Requests").child(requestKey).setValue(currentRequest);

                    Intent intent = new Intent(AcceptRequestScreen.this, WalkerViewRequests.class);
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

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
