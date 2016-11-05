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

public class FeedbackActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private EditText feedback;
    private Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        firebaseAuth = FirebaseAuth.getInstance();
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        feedback = (EditText) findViewById(R.id.feedback);
        Log.i("test", "test");
        //Feedback Submit Button Listener
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String feedbackMessage = feedback.getText().toString();
                try {
                    GMailSender sender = new GMailSender("utexassurewalk@gmail.com", "EE461LIsFun");
                    sender.sendMail("User Feedback", feedbackMessage, "utexassurewalk@gmail.com","alex.e.cone@gmail.com");
                    Log.i("test","succeed");
                } catch (Exception e) {
                    Log.i("test", "failed");
                }

            }
        });
    }
}
