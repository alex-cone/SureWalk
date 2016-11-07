package ee461l.surewalk;
/**
 * Created by Diego on 10/30/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Users.Requester;
import Users.Walker;
public class RequesterHomeScreen extends Activity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button btnRequest;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Requester currentRequester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_requester_home_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
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
        btnRequest = (Button) findViewById(R.id.btnRequest);

        mDatabase.child("Requesters").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        currentRequester = dataSnapshot.getValue(Requester.class);
                        txtName.setText(currentRequester.username);

                        //user.email now has your email value
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("SureWalk", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });


        // Displaying the user details on the screen


        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(currentRequester != null) {
                    currentRequester.newRequest("currentLoc", "destination");
                }
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        firebaseAuth.signOut();

        // Launching the login activity
        Intent intent = new Intent(RequesterHomeScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
