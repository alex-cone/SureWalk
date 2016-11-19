package ee461l.surewalk;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedback;
    private Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        feedback = (EditText) findViewById(R.id.feedback);
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
                    String emailSubject = "User Feedback";
                    String emailBody = feedbackMessage;
                    new SendMailTask(FeedbackActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
                    Log.i("test","succeed");
                } catch (Exception e) {
                    Log.i("test", "failed");
                }

            }
        });
    }
}
