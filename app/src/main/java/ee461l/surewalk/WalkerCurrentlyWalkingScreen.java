package ee461l.surewalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import Users.Request;
import Users.Requester;

public class WalkerCurrentlyWalkingScreen extends FragmentActivity implements OnMapReadyCallback {
    private ImageView profilePicture;
    private TextView txtName;
    private Button btnCallRequester;
    private Button btnMessageRequester;
    private Button btnCancelWalk;
    private Button btnCompleteWalk;
    private String requesterPhoneNumber;
    private Users.Request currentRequest;
    private String requestKey;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_currently_walking_screen);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.WalkerCurrentlyWalkingMap);
        mapFragment.getMapAsync(this);

        profilePicture = (ImageView) findViewById(R.id.RequesterPicture);
        txtName = (TextView) findViewById(R.id.WalkerName);
        btnCallRequester = (Button) findViewById(R.id.CallRequester);
        btnMessageRequester = (Button) findViewById(R.id.TextRequeseter);
        btnCancelWalk = (Button) findViewById(R.id.CancelWalk);
        btnCompleteWalk = (Button) findViewById(R.id.CompleteWalk);
        Bundle extras = getIntent().getExtras();
        currentRequest =  new Gson().fromJson(extras.getString("RequestInfo"), Users.Request.class);

        txtName.setText("You are on your way to " + currentRequest.getRequester().username);
        requesterPhoneNumber = currentRequest.getRequester().phoneNumber;
        requestKey = extras.getString("RequestKey");
        FirebaseVariables.getDatabaseReference().child("Requests").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value

                        currentRequest = dataSnapshot.child(requestKey).getValue(Request.class);

                        Requester requester = currentRequest.getRequester();
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

        btnCallRequester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:"+ requesterPhoneNumber));
                startActivity(phoneIntent);
            }
        });

        btnMessageRequester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + requesterPhoneNumber);
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(smsIntent);
            }
        });

        btnCompleteWalk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(WalkerCurrentlyWalkingScreen.this, WalkerHomeScreen.class);
                yesOrNoConfirmation(intent, 1, "Complete Walk?");
            }
        });

        btnCancelWalk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(WalkerCurrentlyWalkingScreen.this, WalkerHomeScreen.class);
                yesOrNoConfirmation(intent, 0, "Cancel Walk?");
            }
        });
    }
    public void yesOrNoConfirmation(final Intent option, final int status, String message){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if(status == 0) {
                            FirebaseVariables.getCurrentWalker().cancelRequest(currentRequest);
                        }
                        else if(status == 1){
                            currentRequest.setStatus(Request.STATUS.COMPLETED);
                        }
                        startActivity(option);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.setMyLocationEnabled(true);
        LatLng requester = new LatLng(currentRequest.getCurrentLatitude(), currentRequest.getCurrentLongitude());
        LatLng destination = new LatLng(currentRequest.getDestinationLatitude(), currentRequest.getDestinationLongitude());
        mMap.addMarker(new MarkerOptions().position(requester));
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }
}
