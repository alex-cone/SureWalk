package ee461l.surewalk;
/**
 * Created by Diego on 10/30/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import Users.Requester;

public class RequesterHomeScreen extends AppCompatActivity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requester_home_screen);
        ArrayList<String> locationPermission = new ArrayList<String>();
        locationPermission.add("android.permission.ACCESS_FINE_LOCATION");
        locationPermission.add("android.permission.ACCESS_COARSE_LOCATION");
        askForPermission(locationPermission);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        FirebaseUser user = FirebaseVariables.getFireBaseAuth().getCurrentUser();
        if(user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else if (user.isEmailVerified()) {
            Log.d("SureWalk", "Email is verified");
        }
        else {
            Log.d("SureWalk", "Email is not verified");
        }


        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnRequest = (Button) findViewById(R.id.btnRequest);

           FirebaseVariables.getDatabaseReference().child("Requesters").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        FirebaseVariables.setCurrentRequester(dataSnapshot.getValue(Requester.class));
                        txtName.setText(FirebaseVariables.getCurrentRequester().username);
                   }
                    @Override
                  public void onCancelled(DatabaseError databaseError) {
                        Log.w("SureWalk", "loadPost:onCancelled", databaseError.toException());


                    }
                });

        btnRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequesterHomeScreen.this, RequesterRequestScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        FirebaseVariables.getFireBaseAuth().signOut();

        // Launching the login activity
        Intent intent = new Intent(RequesterHomeScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
            // User chose the "Settings" item, show the app settings UI...
                logoutUser();
                return true;

            default:
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);

        }
    }

    private void askForPermission(ArrayList<String> permissionList) {
        int requestCode = 0;
        String permission = "";
        //permissionList.add("android.permission.INTERNET");
        for (int i = 0; i < permissionList.size(); i++) {
            permission = permissionList.get(i);
            requestCode = i;
            if (ContextCompat.checkSelfPermission(RequesterHomeScreen.this, permission) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(RequesterHomeScreen.this, permission)) {

                    //This is called if user has denied the permission before
                    //In this case I am just asking the permission again
                    ActivityCompat.requestPermissions(RequesterHomeScreen.this, new String[]{permission}, requestCode);

                } else {

                    ActivityCompat.requestPermissions(RequesterHomeScreen.this, new String[]{permission}, requestCode);
                }
            } else {
                // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
