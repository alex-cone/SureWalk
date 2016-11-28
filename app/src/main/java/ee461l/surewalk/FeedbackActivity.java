package ee461l.surewalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Users.Request;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedback;
    private Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requester_feedback);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        feedback = (EditText) findViewById(R.id.feedback);

        Bundle extras = getIntent().getExtras();
        final Request currentRequest =  new Gson().fromJson(extras.getString("RequestInfo"), Users.Request.class);
        Log.i("test", "test");
        //Feedback Submit Button Listener
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String feedbackMessage = feedback.getText().toString();
                try {
                    String fromEmail = "utexassurewalk@gmail.com";
                    String fromPassword = "EE461LIsFun";
                    String toEmails = "utexassurewalk@gmail.com";
                    List<String> toEmailList;
                    toEmailList = new ArrayList<String>();
                    toEmailList.add(toEmails);
                    String emailSubject = "User Feedback from " + FirebaseVariables.getCurrentRequester().username;
                    String emailBody = "Username: " + FirebaseVariables.getCurrentRequester().username + "\n\n"
                            + "Email: " + FirebaseVariables.getCurrentRequester().email + "\n\n"
                            + "Surewalker: " + currentRequest.getWalker().username +"\n\n"
                            + "Message:\n" + feedbackMessage;
                    new SendMailTask(FeedbackActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
                    FirebaseVariables.getCurrentRequester().deleteRequest(currentRequest);
                    Intent intent = new Intent(FeedbackActivity.this, RequesterHomeScreen.class);
                    startActivity(intent);
                    Log.i("test","succeed");
                    finish();
                } catch (Exception e) {
                    Log.i("test", "failed");
                }

            }
        });
    }
}
