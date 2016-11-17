package ee461l.surewalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Users.Request;
import Users.Requester;

public class WalkerViewRequests extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private LinearLayout requestLinearList;
    private LinearLayout.LayoutParams lparams;
    private String requestKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walker_view_requests);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        requestLinearList = (LinearLayout) findViewById(R.id.requestLinearLayout);
        lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mDatabase.child("Requests").addValueEventListener(
                new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        requestKey =  dataSnapshot.getKey();
                        Request req = dataSnapshot.child("-KW41ivvtLISRW7vaoI_").getValue(Request.class);
                        //check req status
                        //if status is accepted/completed remove request from layout
                        if(req.getStatus().equals(Request.STATUS.ACCEPTED)){
                            updateList(req);
                        }
                        else if(req.getStatus().equals(Request.STATUS.COMPLETED)){

                        }
                        else{
                            printList(req);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    public void printList(Request req){

        TextView tv=new TextView(this);
        tv.setLayoutParams(lparams);
        String name = req.getRequester().username;
        tv.setText(name);

        this.requestLinearList.addView(tv);
        int index = this.requestLinearList.indexOfChild(tv);
        req.setID(index);
    }

    public void updateList(Request req){
        //remove request from requestLinearList
        int index = req.getID();
        if(index != -1) {
            this.requestLinearList.removeViewAt(index);
            req.setID(-1);
        }

    }

}
