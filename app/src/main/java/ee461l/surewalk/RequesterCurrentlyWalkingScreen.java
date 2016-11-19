package ee461l.surewalk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class RequesterCurrentlyWalkingScreen extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView txtName;
    private Button btnCallWalker;
    private Button btnMessageWalker;
    private Button btnCancelRequest;
    private String walkerPhoneNumber;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private String requestKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requester_currently_walking_screen);
        profilePicture = (ImageView) findViewById(R.id.RequesterPicture);
        txtName = (TextView) findViewById(R.id.WalkerName);
        btnCallWalker = (Button) findViewById(R.id.CallRequester);
        btnMessageWalker = (Button) findViewById(R.id.TextRequeseter);
        btnCancelRequest = (Button) findViewById(R.id.CancelRequest);
        txtName.setText("Alex is on the way!");
        walkerPhoneNumber = "832-931-0841";
        /*
        mDatabase.child("Requests").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        requestKey =  dataSnapshot.getKey();
                        Request request = dataSnapshot.child("-KW41ivvtLISRW7vaoI_").getValue(Request.class);

                        Requester requester = request.getRequester();
                        txtName.setText(requester.username);
                        walkerPhoneNumber = requester.phoneNumber;
                        StorageReference profilePicutreRef = mStorage.child("userProfilePictures").child(requester.uid).child("ProfilePicture");
                        Glide.with(getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(profilePicutreRef)
                                .into(profilePicture);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("SureWalk", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
        });*/

        btnCallWalker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:1-"+ walkerPhoneNumber));
                startActivity(phoneIntent);
            }
        });

        btnMessageWalker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the phone number in the data
                Uri uri = Uri.parse("smsto:" + walkerPhoneNumber);
                // Create intent with the action and data
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                // smsIntent.setData(uri); // We just set the data in the constructor above
                // Set the message
                //smsIntent.putExtra("sms_body", smsBody);
                startActivity(smsIntent);
            }
        });

        btnCancelRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(RequesterCurrentlyWalkingScreen.this, RequesterHomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
