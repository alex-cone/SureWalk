package ee461l.surewalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import Users.Requester;

public class RequesterCurrentlyWalkingScreen extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView txtName;
    private Button btnCallWalker;
    private Button btnMessageWalker;
    private Button btnCancelRequest;
    private String walkerPhoneNumber;
    private Users.Request currentRequest;
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
        Bundle extras = getIntent().getExtras();
        currentRequest =  new Gson().fromJson(extras.getString("RequestInfo"), Users.Request.class);
      //  profilePicture.setImageResource(currentRequest.getWalker().);
        txtName.setText(/*currentRequest.getWalker().username +*/  "Walker is on the way!");
        walkerPhoneNumber = currentRequest.getWalker().phoneNumber;


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
                        startActivity(option);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
