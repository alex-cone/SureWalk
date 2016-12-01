package ee461l.surewalk;

import android.app.Activity;
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
    public static String feedbackMsg;
    public static Request request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requester_feedback);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        feedback = (EditText) findViewById(R.id.feedback);

        Bundle extras = getIntent().getExtras();
        final Request currentRequest =  new Gson().fromJson(extras.getString("RequestInfo"), Users.Request.class);
        FirebaseVariables.getCurrentRequester().deleteRequest(currentRequest);
        Log.i("test", "test");
        //Feedback Submit Button Listener
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String feedbackMessage = feedback.getText().toString();
                try {
                    String fromEmail = "utexassurewalk@gmail.com";
                    String fromPassword = "EE461LIsFun";
                    sendFeedback(fromEmail, fromPassword, currentRequest, feedbackMessage, FeedbackActivity.this);
                    Intent intent = new Intent(FeedbackActivity.this, RequesterHomeScreen.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.i("test", "failed");
                }

            }
        });
    }
    public static void sendFeedback(String fromEmail, String fromPassword, Request currentRequest, String feedbackMessage, Activity activity){
        String toEmails = "utexassurewalk@gmail.com";
        feedbackMsg = feedbackMessage;
        List<String> toEmailList;
        toEmailList = new ArrayList<String>();
        toEmailList.add(toEmails);
        String emailSubject = "User Feedback from " + currentRequest.getRequester().username;
        String emailBody = "Username: " + currentRequest.getRequester().username + "\n\n"
                + "Email: " + currentRequest.getRequester().email + "\n\n"
                + "Surewalker: " + currentRequest.getWalker().username +"\n\n"
                + "Message:\n" + feedbackMessage;
        new SendMailTask(activity).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
        return;
    }
}
