package ee461l.surewalk;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private TextView registerScreen;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /*Checks to see if user is logged in*/
        if(firebaseAuth.getCurrentUser() != null){
           chooseLoginScreen();
        }
        else {
            setContentView(R.layout.login);
            inputEmail = (EditText) findViewById(R.id.email);
            inputPassword = (EditText) findViewById(R.id.password);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            registerScreen = (TextView) findViewById(R.id.link_to_register);

            // Progress dialog
            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);

            // Login button Click Event
            btnLogin.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();

                    // Check for empty data in the form
                    if (!email.isEmpty() && !password.isEmpty()) {
                        // login user
                        userLogin(email, password);
                    } else {
                        // Prompt user to enter credentials
                        Toast.makeText(getApplicationContext(),
                                "Please enter your credentials!", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            });


            // Listening to register new account link
            registerScreen.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // Switching to Register screen
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(i);
                }
            });
        }

    }

    /**
     * function to verify login details in mysql db
     * */
    private void userLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideDialog();
                        if(task.isSuccessful()){
                            mDatabase.child("Walkers")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String userId = firebaseAuth.getCurrentUser().getUid();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                /*Check if snapshot is not a string, meaning a walker registered*/
                                                if(!snapshot.getValue().getClass().equals(String.class)){
                                                    if(userId.equals(snapshot.getKey())) {
                                                        Log.d("SureWalk", "Hey, you found a walker. That's pretty good");
                                                        Intent intent = new Intent(LoginActivity.this, WalkerHomeScreen.class); //TODO: needs to go to Walker Screen
                                                        startActivity(intent);
                                                        finish();
                                                        return;
                                                    }
                                                }
                                            }

                                   /*User is not a Walker*/
                                            Intent intent = new Intent(LoginActivity.this, RequesterHomeScreen.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "Not a valid email or password. Please try again.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void chooseLoginScreen() {
        mDatabase.child("Walkers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            String registeredUID = snapshot.getKey();
                            if(registeredUID.equals(userId)){
                                Intent intent = new Intent(LoginActivity.this, WalkerHomeScreen.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                       /*If it goes through entire for loop, User is a Requester*/
                        Intent intent = new Intent(LoginActivity.this, RequesterHomeScreen.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}