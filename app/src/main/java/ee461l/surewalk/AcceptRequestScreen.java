package ee461l.surewalk;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
public class AcceptRequestScreen extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    public static final String TAG = AcceptRequestScreen.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private ImageView profilePicture;
    private TextView txtName;
    private TextView txtComments;
    private Button btnAcceptRequest;
    private Button btnBackToRequests;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location walkerLocation;
    private Marker currentLocationMarker;

    private String requestKey;
    private Request currentRequest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_accept_request_screen);

        //Map and Location Services
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.walkerMap);
        mapFragment.getMapAsync(this);
        //Initialize Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            requestKey = extras.getString("RequestID");
        }


        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        txtName = (TextView) findViewById(R.id.txtName);
        txtComments = (TextView) findViewById(R.id.txtComments);
        btnAcceptRequest = (Button) findViewById(R.id.btnAcceptRequest);
        btnBackToRequests = (Button) findViewById(R.id.btnBackToRequests);

        FirebaseVariables.getDatabaseReference().child("Requests").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentRequest = dataSnapshot.child(requestKey).getValue(Request.class);

                        Requester requester = currentRequest.getRequester();
                        txtName.setText(requester.username);
                        txtComments.setText("Comments: " + currentRequest.getComments());
                        StorageReference profilePicutreRef = FirebaseVariables.getStorageReference().child("userProfilePictures").child(requester.uid).child("ProfilePicture");
                        Glide.with(getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(profilePicutreRef)
                                .into(profilePicture);

                        //Show location on map
                        LatLng requesterLoc = new LatLng(currentRequest.getCurrentLatitude(), currentRequest.getCurrentLongitude());
                        LatLng destinationLoc = new LatLng(currentRequest.getDestinationLatitude(), currentRequest.getDestinationLongitude());

                        MarkerOptions requesterMarker = new MarkerOptions().position(requesterLoc);
                        MarkerOptions destinationMarker = new MarkerOptions().position(destinationLoc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        mMap.addMarker(requesterMarker);
                        mMap.addMarker(destinationMarker);
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
                    currentRequest.setWalker(FirebaseVariables.getCurrentWalker());
                    FirebaseVariables.getDatabaseReference().child("Requests").child(requestKey).setValue(currentRequest);
                    Intent intent = new Intent(AcceptRequestScreen.this, WalkerCurrentlyWalkingScreen.class);
                    intent.putExtra("RequestInfo",(new Gson()).toJson(currentRequest));
                    intent.putExtra("RequestKey",requestKey);
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        walkerLocation = location; // Set the current location data to the current location


        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocationMarker.remove();
        handleNewLocation(location);
    }
}
