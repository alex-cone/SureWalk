package ee461l.surewalk;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

public class WalkerCurrentlyWalkingScreen extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView txtName;
    private Button btnCallRequester;
    private Button btnMessageRequester;
    private Button btnCancelWalk;
    private String requesterPhoneNumber;
    private Users.Request currentRequest;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private String requestKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_currently_walking_screen);
        profilePicture = (ImageView) findViewById(R.id.RequesterPicture);
        txtName = (TextView) findViewById(R.id.WalkerName);
        btnCallRequester = (Button) findViewById(R.id.CallRequester);
        btnMessageRequester = (Button) findViewById(R.id.TextRequeseter);
        btnCancelWalk = (Button) findViewById(R.id.CancelWalk);
        Bundle extras = getIntent().getExtras();
        currentRequest =  new Gson().fromJson(extras.getString("RequestInfo"), Users.Request.class);

        txtName.setText("You are on your way to Requester" /*+ currentRequest.getRequester().username*/);
        requesterPhoneNumber = currentRequest.getRequester().phoneNumber;


        btnCallRequester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:1-"+ requesterPhoneNumber));
                startActivity(phoneIntent);
            }
        });

        btnMessageRequester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the phone number in the data
                Uri uri = Uri.parse("smsto:" + requesterPhoneNumber);
                // Create intent with the action and data
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                // smsIntent.setData(uri); // We just set the data in the constructor above
                // Set the message
                //smsIntent.putExtra("sms_body", smsBody);
                startActivity(smsIntent);
            }
        });

        btnCancelWalk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(WalkerCurrentlyWalkingScreen.this, WalkerHomeScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
