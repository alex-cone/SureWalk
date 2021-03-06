package ee461l.surewalk;

/**
 * Created by Diego on 10/30/2016.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Users.Request;
import Users.Walker;
public class WalkerHomeScreen extends AppCompatActivity {

    private TextView txtName;
    private Button btnViewRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.walker_home_screen);
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
        btnViewRequests = (Button) findViewById(R.id.btnViewRequest);
        txtName.setText("Welcome " + FirebaseVariables.getCurrentWalker().username + "!");

        FirebaseVariables.getDatabaseReference().child("Requests").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Intent intent = new Intent();
                        PendingIntent pIntent = PendingIntent.getActivity(WalkerHomeScreen.this,0,intent,0);
                        Notification notification = new Notification.Builder(WalkerHomeScreen.this)
                                .setTicker("TickerTitle")
                                .setContentTitle("SureWalk")
                                .setContentText("New Request")
                                .setSmallIcon(R.drawable.sure_walk_logo)
                                .setContentIntent(pIntent).getNotification();

                        notification.flags |= Notification.FLAG_AUTO_CANCEL;
                        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                        nm.notify(0,notification);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        


        // Logout button click event
        btnViewRequests.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalkerHomeScreen.this, WalkerViewRequests.class);
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
        Intent intent = new Intent(WalkerHomeScreen.this, LoginActivity.class);
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
            if (ContextCompat.checkSelfPermission(WalkerHomeScreen.this, permission) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(WalkerHomeScreen.this, permission)) {

                    //This is called if user has denied the permission before
                    //In this case I am just asking the permission again
                    ActivityCompat.requestPermissions(WalkerHomeScreen.this, new String[]{permission}, requestCode);

                } else {

                    ActivityCompat.requestPermissions(WalkerHomeScreen.this, new String[]{permission}, requestCode);
                }
            } else {
                // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}