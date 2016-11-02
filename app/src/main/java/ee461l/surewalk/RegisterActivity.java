package ee461l.surewalk;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        firebaseAuth = FirebaseAuth.getInstance();
        inputFullName = (EditText) findViewById(R.id.reg_fullname);
        inputEmail = (EditText) findViewById(R.id.reg_email);
        inputPassword = (EditText) findViewById(R.id.reg_password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.link_to_login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                registerUser(username, email, password);
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

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String username, final String email,
                              final String password) {

        /*Check to see if text fields are filled*/
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Email", Toast.LENGTH_LONG).show();
            return;

        }
        else if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please Fillout Username", Toast.LENGTH_LONG).show();
            return;
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please Fillout a Password", Toast.LENGTH_LONG).show();
            return;
        }
        else
         {
             /*Check to see if email is a @utexas.edu email*/
            boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(); //First check if its a valid email in general

            if(!result) {
                Toast.makeText(getApplicationContext(), "Not a valid Utexas email address", Toast.LENGTH_LONG).show();
                return;

            }
            String emailWebsite = email.substring(email.indexOf('@'), email.length());
            //Check to see if it is a @utexas.edu email
            if(!emailWebsite.equals("@utexas.edu")){
                Toast.makeText(getApplicationContext(), "Not a valid Utexas email address", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d("SureWalk", ""+emailWebsite);

    }
        // Tag used to cancel the request

        pDialog.setMessage("Registering ...");
        showDialog();
        Log.d("sureWalk", email + " " + password);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideDialog();
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();
                        }
                        else{

                            Toast.makeText(getApplicationContext(), "User not registered.", Toast.LENGTH_LONG).show();
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
}