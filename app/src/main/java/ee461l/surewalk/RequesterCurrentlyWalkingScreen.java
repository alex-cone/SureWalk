package ee461l.surewalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import Users.Request;
import Users.Requester;

public class RequesterCurrentlyWalkingScreen extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView txtName;
    private Button btnCallWalker;
    private Button btnMessageWalker;
    private Button btnCancelRequest;
    private String walkerPhoneNumber;
    private Users.Request currentRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requester_currently_walking_screen);
        profilePicture = (ImageView) findViewById(R.id.RequesterPicture);
        txtName = (TextView) findViewById(R.id.WalkerName);
        btnCallWalker = (Button) findViewById(R.id.CallRequester);
        btnMessageWalker = (Button) findViewById(R.id.TextRequeseter);
        btnCancelRequest = (Button) findViewById(R.id.CancelRequest);
        Bundle extras = getIntent().getExtras();
        currentRequest =  new Gson().fromJson(extras.getString("RequestInfo"), Users.Request.class);
      //  profilePicture.setImageResource(currentRequest.getWalker().);
        txtName.setText(currentRequest.getWalker().username +  " is on the way!");
        walkerPhoneNumber = currentRequest.getWalker().phoneNumber;

        FirebaseVariables.getDatabaseReference().child("Requests").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value

                        //currentRequest = dataSnapshot.child(requestKey).getValue(Request.class);

                        Users.Walker walker = currentRequest.getWalker();
                        StorageReference profilePicutreRef = FirebaseVariables.getStorageReference().child("userProfilePictures").child(walker.uid).child("ProfilePicture");
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

        btnCallWalker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:"+ walkerPhoneNumber));
                startActivity(phoneIntent);
            }
        });

        btnMessageWalker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + walkerPhoneNumber);
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(smsIntent);
            }
        });

        btnCancelRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(RequesterCurrentlyWalkingScreen.this, RequesterHomeScreen.class);
                yesOrNoConfirmation(intent);
            }
        });

    }

    public void yesOrNoConfirmation(final Intent option){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        FirebaseVariables.getCurrentRequester().cancelRequest(currentRequest);
                        startActivity(option);
                     //    finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cancel Request?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
