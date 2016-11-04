package ee461l.surewalk;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.Toast;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.UserProfileChangeRequest;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;
        import com.soundcloud.android.crop.Crop;

        import java.io.File;

        import Users.Walker;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final int GALLERY_INTENT = 2;

    private Button btnRegister;
    private Button btnLinkToLogin;
    private ImageButton profilePicture;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private Uri uri;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        inputFullName = (EditText) findViewById(R.id.reg_fullname);
        inputEmail = (EditText) findViewById(R.id.reg_email);
        inputPassword = (EditText) findViewById(R.id.reg_password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.link_to_login);
        profilePicture = (ImageButton) findViewById(R.id.uploadProfilePicture);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                checkIfValidUser(username, email, password);
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);

                i.setType("image/*");

                startActivityForResult(i, GALLERY_INTENT);
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void checkIfValidUser(final String username, final String email,
                              final String password) {

        /*Check to see if text fields are filled*/
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Email", Toast.LENGTH_LONG).show();
            return;

        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Username", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please Fillout a Password", Toast.LENGTH_LONG).show();
            return;
        }
        else if(uri == null){
            Toast.makeText(getApplicationContext(), "Please Submit a Photo of Yourself", Toast.LENGTH_LONG).show();
            return;
        }
        else {
             /*Check to see if email is a @utexas.edu email*/
            boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(); //First check if its a valid email in general

            if (!result) {
                Toast.makeText(getApplicationContext(), "Not a valid Utexas email address", Toast.LENGTH_LONG).show();
                return;

            }
            String emailWebsite = email.substring(email.indexOf('@'), email.length());
            //Check to see if it is a @utexas.edu email
            if (!emailWebsite.equals("@utexas.edu")) {
                Toast.makeText(getApplicationContext(), "Not a valid Utexas email address", Toast.LENGTH_LONG).show();
                return;
            }

        }
        // Tag used to cancel the request

        pDialog.setMessage("Registering ...");
        showDialog();
        Log.d("SureWalk", username + " " + email + " " + password);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();
                            registerUser(email, username);
                        } else {
                            Toast.makeText(getApplicationContext(), "User not registered.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void registerUser(String email, String username) {
        final String emailToRegister = email;
        final String usernameToRegister = username;
        mDatabase.child("Walkers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String emailInDatabase = snapshot.getValue(String.class);
                            Log.d("SureWalk", emailInDatabase);
                            if(emailInDatabase.equals(emailToRegister)){
                               /*TODO: Intent intent = new Intent(RegisterActivity.this, Walke rActivity.class);
                                startActivity(intent);*/
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String userId = user.getUid();
                                Walker walker = new Walker();
                                walker.setName(usernameToRegister);

                                mDatabase.child("Walkers").child(snapshot.getKey().toString()).setValue(walker);
                                Log.d("SureWalk", "Hey, you found a walker. That's pretty good");
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class); //TODO: needs to go to Walker Screen
                                startActivity(intent);
                                finish();
                            }
                        }

                       /*User is not a Walker*/
                        setUpRequester(usernameToRegister);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void setUpRequester(String username) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("SureWalk", "User profile updated.");
                        }
                    }
                });

        StorageReference photoFilePath = mStorage.child("userProfilePictures").child(user.getUid()).child(uri.getLastPathSegment());

         /*Add user Photo to database*/
        photoFilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Upload Done.", Toast.LENGTH_LONG).show();

            }
        });

        /*Send User Verfication Email*/
        user.sendEmailVerification();

        Walker walker = new Walker();
        walker.setName(username);

        mDatabase.child("users").child(userId).setValue(walker);
        return;
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "ProfilePicture"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            uri = Crop.getOutput(result);
            profilePicture.setImageURI(uri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}