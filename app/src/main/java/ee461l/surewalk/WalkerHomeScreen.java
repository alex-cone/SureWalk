package ee461l.surewalk;

/**
 * Created by Diego on 10/30/2016.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import Users.Request;
import Users.Walker;
public class WalkerHomeScreen extends Activity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button btnViewRequests;
    private String requestKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.walker_home_screen);

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
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnViewRequests = (Button) findViewById(R.id.btnViewRequest);


        FirebaseVariables.getDatabaseReference().child("Walkers").child(user.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            Walker walker = dataSnapshot.getValue(Walker.class);
                            txtName.setText(walker.username);
                            //user.email now has your email value
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w("SureWalk", "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });
        FirebaseVariables.getDatabaseReference().child("Requests").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Toast.makeText(getApplicationContext(),
                                "New Request", Toast.LENGTH_SHORT)
                                .show();
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
        btnLogout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    logoutUser();
                }
            });
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
}