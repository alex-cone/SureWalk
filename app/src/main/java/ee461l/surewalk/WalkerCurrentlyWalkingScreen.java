package ee461l.surewalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

import Users.Request;
import Users.Requester;

public class WalkerCurrentlyWalkingScreen extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final String TAG = WalkerCurrentlyWalkingScreen.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
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
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_currently_walking_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.WalkerCurrentlyWalkingMap);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        profilePicture = (ImageView) findViewById(R.id.RequesterPicture);
        txtName = (TextView) findViewById(R.id.WalkerName);
        btnCallRequester = (Button) findViewById(R.id.CallRequester);
        btnMessageRequester = (Button) findViewById(R.id.TextRequeseter);
        btnCancelWalk = (Button) findViewById(R.id.CancelWalk);
        btnCompleteWalk = (Button) findViewById(R.id.CompleteWalk);
        Bundle extras = getIntent().getExtras();
        currentRequest = new Gson().fromJson(extras.getString("RequestInfo"), Users.Request.class);
        setUpRequestListener();
        txtName.setText(currentRequest.getRequester().username);
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
                phoneIntent.setData(Uri.parse("tel:" + requesterPhoneNumber));
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

        btnCompleteWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalkerCurrentlyWalkingScreen.this, WalkerHomeScreen.class);
                yesOrNoConfirmation(intent, 1, "Complete Walk?");
            }
        });

        btnCancelWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalkerCurrentlyWalkingScreen.this, WalkerHomeScreen.class);
                yesOrNoConfirmation(intent, 0, "Cancel Walk?");
            }
        });
    }

    public void yesOrNoConfirmation(final Intent option, final int status, String message) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if (status == 0) {
                            FirebaseVariables.getCurrentWalker().cancelRequest(currentRequest);
                        } else if (status == 1) {
                            FirebaseVariables.getCurrentWalker().removeEventHandler(currentRequest);
                            currentRequest.setStatus(Request.STATUS.COMPLETED);
                            FirebaseVariables.getDatabaseReference().child("Requests").child(currentRequest.getFirebaseId()).setValue(currentRequest);
                        } else if (status == 2) {
                            startActivity(cancelHandling());
                            finish();
                            break;
                        }
                        startActivity(option);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        if (status == 2) {
                            FirebaseVariables.getCurrentWalker().removeEventHandler(currentRequest);
                            FirebaseVariables.getCurrentWalker().deleteRequest(currentRequest);
                            Intent intent = new Intent(WalkerCurrentlyWalkingScreen.this, WalkerHomeScreen.class);
                            startActivity(intent);
                        }
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
        } else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        /*walkerLocation = location; // Set the current location data to the current location*/


        // currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location")
        //        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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
        //currentLocationMarker.remove();
        //handleNewLocation(location);
    }

    private void setUpRequestListener() {
        FirebaseVariables.setWalkerEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Request currentRequest = dataSnapshot.getValue(Request.class);
                if (currentRequest.getStatus() == Request.STATUS.CANCELED) {
                    yesOrNoConfirmation(null, 2, "Requester Cancelled Request.\nCall Requester?");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("SureWalk", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        FirebaseVariables.getDatabaseReference().child("Requests").child(currentRequest.getFirebaseId())
                .addValueEventListener(FirebaseVariables.getWalkerEventListener());

    }

    public Intent cancelHandling() {
        String phoneNumber = currentRequest.getRequester().phoneNumber;
        FirebaseVariables.getCurrentWalker().deleteRequest(currentRequest);
        FirebaseVariables.getCurrentWalker().removeEventHandler(currentRequest);
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
        return phoneIntent;
    }
}
