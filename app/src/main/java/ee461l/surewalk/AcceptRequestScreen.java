package ee461l.surewalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Users.Request;
import Users.Requester;
import Users.Walker;

/**
 * Created by Diego on 11/7/2016.
 */
public class AcceptRequestScreen extends Activity {
    private ImageView profilePicture;
    private TextView txtName;
    private TextView txtPhoneNumber;
    private Button btnAcceptRequest;
    private Button btnBackToRequests;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private String requestKey;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_request_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
        btnAcceptRequest = (Button) findViewById(R.id.btnAcceptRequest);
        btnBackToRequests = (Button) findViewById(R.id.btnBackToRequests);

        mDatabase.child("Requests").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        requestKey =  dataSnapshot.getKey();
                        Request request = dataSnapshot.child("-KW41ivvtLISRW7vaoI_").getValue(Request.class);

                        Requester requester = request.getRequester();
                        txtName.setText(requester.username);
                        txtPhoneNumber.setText(requester.phoneNumber);
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
                });

        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        btnBackToRequests.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(AcceptRequestScreen.this, WalkerHomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
