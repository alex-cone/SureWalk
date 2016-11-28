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

        import Users.Requester;
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
    private EditText confirmPassword;
    private EditText inputPhoneNumber;
    private ProgressDialog pDialog;
    private Uri uri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        inputFullName = (EditText) findViewById(R.id.reg_fullname);
        inputEmail = (EditText) findViewById(R.id.reg_email);
        inputPassword = (EditText) findViewById(R.id.reg_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        inputPhoneNumber =  (EditText) findViewById(R.id.editTextId);
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
                String phoneNumber = inputPhoneNumber.getText().toString().trim();
                String cPassword = confirmPassword.getText().toString().trim();

                if(!cPassword.equals(password)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match  ", Toast.LENGTH_LONG).show();
                }
                else {
                    checkIfValidUser(username, email, password, phoneNumber);
                }
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
                              final String password, final String phoneNumber) {

        /*Check to see if text fields are filled*/
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Email", Toast.LENGTH_LONG).show();
            return;

        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Username", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Password", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Phone Number", Toast.LENGTH_LONG).show();
            return;
        } else if (uri == null) {
            Toast.makeText(getApplicationContext(), "Please Submit a Photo of Yourself", Toast.LENGTH_LONG).show();
            return;
        } else {
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
        FirebaseVariables.getFireBaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();
                            registerUser(email, username, phoneNumber);
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

    private void registerUser(final String emailToRegister, final String usernameToRegister, final String phoneNumberToRegister) {
        FirebaseVariables.getDatabaseReference().child("Walkers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String emailInDatabase = "noValid";
                            if(snapshot.getValue().getClass().equals(String.class)){
                                emailInDatabase = snapshot.getValue(String.class);
                            }
                            Log.d("SureWalk", snapshot.getValue().getClass().toString());
                            if(emailInDatabase.equals(emailToRegister)){
                                FirebaseUser user = FirebaseVariables.getFireBaseAuth().getCurrentUser();
                                String userId =  user.getUid();
                                Walker walker = new Walker();
                                walker.setWalker(usernameToRegister,emailToRegister, phoneNumberToRegister, userId);

                                /*Remove email from database and update with FirebaseID*/
                                FirebaseVariables.getDatabaseReference().child("Walkers").child(snapshot.getKey().toString()).removeValue();
                                FirebaseVariables.getDatabaseReference().child("Walkers").child(userId).setValue(walker);
                                FirebaseVariables.setCurrentWalker(walker);


                                //Save profile pic
                                StorageReference photoFilePath = FirebaseVariables.getStorageReference().child("userProfilePictures").child(user.getUid()).child(uri.getLastPathSegment());
                                photoFilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    }
                                });
                                Intent intent = new Intent(RegisterActivity.this, WalkerHomeScreen.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                       /*If it goes through entire for loop, User is a Requester*/
                        setUpRequester(usernameToRegister, emailToRegister, phoneNumberToRegister);
                        Intent intent = new Intent(RegisterActivity.this, RequesterHomeScreen.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void setUpRequester(String username, String email, String phoneNumber) {
        FirebaseUser user =  FirebaseVariables.getFireBaseAuth().getCurrentUser();
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

        StorageReference photoFilePath = FirebaseVariables.getStorageReference().child("userProfilePictures").child(user.getUid()).child(uri.getLastPathSegment());

         /*Add user Photo to database*/
        photoFilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        /*Send User Verfication Email*/
        user.sendEmailVerification();

        Requester requester = new Requester();
        requester.setRequester(username, email, phoneNumber, userId);
        Log.d("SureWalk", username + " " + email + " " + phoneNumber + " " + userId);

        FirebaseVariables.getDatabaseReference().child("Requesters").child(userId).setValue(requester);
        FirebaseVariables.setCurrentRequester(requester);
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