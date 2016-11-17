package ee461l.surewalk;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.InputType;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import Users.Request;
import Users.Requester;

public class RequesterRequestScreen extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    //Location Services
    public static final String TAG = RequesterRequestScreen.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap;


    //Location Services
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Marker destinationMarker;

    private Requester currentRequester;
    private Request currentRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requester_request_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            currentRequester =  new Gson().fromJson(extras.getString("Requester"), Requester.class);

        }

        //Location Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        final EditText address1 = (EditText) findViewById(R.id.addressLine1);
        final EditText address2 = (EditText) findViewById(R.id.addressLine2);
        final EditText comments = (EditText) findViewById(R.id.comments);

        final Button requestButton = (Button) findViewById(R.id.requestWalkerButton);
        //Button's original state is 1
        requestButton.setTag(1);
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int state = (Integer) requestButton.getTag();
                //Original state
                if (state == 1) {
                    requestButton.setText("CANCEL");
                    //Disable all text fields so you cant change while requesting
                    disableText(address1);
                    disableText(address2);
                    disableText(comments);
                    v.setTag(0);

                    //Send the request
                    if(currentRequester != null) {
                        currentRequest = currentRequester.newRequest("currentLoc", "destination");
                    }
                    //TODO FIX: Attempt to display destination marker
                    /*String address = address1.getText().toString() + " " + address2.getText().toString();
                    Address destAddress = getLocationFromAddress(address);
                    LatLng destLatLng = new LatLng(destAddress.getLatitude(), destAddress.getLongitude());
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destLatLng).title("Destination"));*/

                }
                //Else in the requesting state
                else {
                    requestButton.setText("REQUEST WALKER");
                    //Reenable all the text fields
                    enableText(address1, false);
                    enableText(address2, false);
                    enableText(comments, true);
                    v.setTag(1);

                    //Cancel the request
                      //currentRequester.cancelRequest(currentRequest);

                    //TODO FIX: Remove destination marker
                    //destinationMarker.remove();
                }
            }
        });
    }

    /*
    Will the a EditText field disabled for editing

    @param  textField   The EditText object to disable
     */
    public static void disableText(EditText textField) {
        textField.setInputType(InputType.TYPE_NULL);
        textField.setFocusable(false);
        textField.setEnabled(false);
    }

    /*
    Will make a EditText field enabled for editing

    @param  textField   The EditText object to enable
    @param  multi       True if multi line, False for others
     */
    public static void enableText(EditText textField, boolean multi) {
        if (multi) {
            textField.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else {
            textField.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        textField.setFocusable(true);
        textField.setFocusableInTouchMode(true);
        textField.setEnabled(true);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng uTexas = new LatLng(30, -97);
        //mMap.addMarker(new MarkerOptions().position(uTexas).title("University of TExas"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(uTexas));
    }

    //Location Services
    @Override
    public void onConnected(@Nullable Bundle bundle) {
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

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Current Location");
        mMap.addMarker(options);
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
        mMap.clear();
        handleNewLocation(location);
    }

    public Address getLocationFromAddress(String destination) {
        Geocoder coder = new Geocoder(this);
        List<Address> addresses;
        Address destinationLocation = new Address(Locale.US);
        try {
            addresses = coder.getFromLocationName(destination, 3);
            if(addresses == null) {
                return null;
            }
            destinationLocation = addresses.get(0);
        }
        catch(Exception e) {

        }
        return destinationLocation;
    }
}
