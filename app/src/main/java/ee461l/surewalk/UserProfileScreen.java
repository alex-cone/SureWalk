package ee461l.surewalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageView;

/**
 * Created by jiwha_000 on 11/5/2016.
 */

public class UserProfileScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_screen);

        final ImageView profilePic = (ImageView) findViewById(R.id.profilePic);
        final TextView walkerName = (TextView) findViewById(R.id.name);
        final TextView walkerEmail = (TextView) findViewById(R.id.email);


    }
}
