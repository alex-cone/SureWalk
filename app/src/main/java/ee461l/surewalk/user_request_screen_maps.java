package ee461l.surewalk;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class user_request_screen_maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_request_screen_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final EditText address1 = (EditText) findViewById(R.id.addressLine1);
        final EditText address2 = (EditText) findViewById(R.id.addressLine2);
        final EditText comments = (EditText) findViewById(R.id.comments);

        final Button requestButton = (Button) findViewById(R.id.requestWalkerButton);
        //Button's original state is 1
        requestButton.setTag(1);
        requestButton.setOnClickListener(new View.OnClickListener() {
             public void onClick (View v){
                 int state = (Integer) requestButton.getTag();
                 //Original state
                 if (state == 1) {
                     requestButton.setText("CANCEL");
                     //Disable all text fields so you cant change while requesting
                     disableText(address1);
                     disableText(address2);
                     disableText(comments);
                     v.setTag(0);
                 }
                 //Else in the requesting state
                 else {
                     requestButton.setText("REQUEST WALKER");
                     //Reenable all the text fields
                     enableText(address1, false);
                     enableText(address2, false);
                     enableText(comments, true);
                     v.setTag(1);
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
        if(multi) {
            textField.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
        else {
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
        LatLng uTexas = new LatLng(30, -97);
        mMap.addMarker(new MarkerOptions().position(uTexas).title("University of TExas"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uTexas));
    }
}
