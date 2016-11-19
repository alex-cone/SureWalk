package ee461l.surewalk;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import Users.Request;
import Users.Requester;
import Users.Walker;

public class WalkerViewRequests extends AppCompatActivity {
    private LinearLayout requestLinearList;
    private LinearLayout.LayoutParams lparams;
    private String requestKey;
    private HashMap<Integer, String> requestDatabaseKey;
    private Button btnReturnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_view_requests);
        requestDatabaseKey = new HashMap<>();

        requestLinearList = (LinearLayout) findViewById(R.id.requestLinearLayout);
        lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnReturnHome = (Button) findViewById(R.id.btnBackToHome);

        btnReturnHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                    Intent intent = new Intent(WalkerViewRequests.this, WalkerHomeScreen.class);
                    startActivity(intent);
                    finish();
            }
        });

        FirebaseVariables.getDatabaseReference().child("Requests").addValueEventListener(
                new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        clearList();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            requestKey = snapshot.getKey();
                            Log.d("SureWalk", requestKey);

                            Request req = dataSnapshot.child(requestKey).getValue(Request.class);
                            //check req status
                            //if status is accepted/completed remove request from layout

                            printList(req, requestKey);
                            if(req.getStatus().equals(Request.STATUS.ACCEPTED)){
                                updateList(req);
                            }
                            else if(req.getStatus().equals(Request.STATUS.COMPLETED)){

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    public void printList(Request req, final String requestKey){

        TextView tv=new TextView(this);

        tv.setLayoutParams(lparams);
        String name = req.getRequester().username;
        tv.setText(name);
        tv.setTextAppearance(this, R.style.View_Requests_Text);

        this.requestLinearList.addView(tv);
        int index = this.requestLinearList.indexOfChild(tv);
        requestDatabaseKey.put(index, requestKey);
        req.setIndex(index);
        final TextView tvv = tv;
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(WalkerViewRequests.this, AcceptRequestScreen.class);
                int index = requestLinearList.indexOfChild(tvv);
                String requestID = requestDatabaseKey.get(index);
                intent.putExtra("RequestID", requestID);
                startActivity(intent);
                finish();
            }
            });
    }

    public void updateList(Request req){
        //remove request from requestLinearList
        int index = req.getIndex();
        if(index != -1) {
            this.requestLinearList.removeViewAt(index);
            //requestDatabaseKey.remove(index);
            req.setIndex(-1);
        }

    }
    public void clearList(){
        this.requestLinearList.removeAllViews();
    }

}
