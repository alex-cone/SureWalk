package ee461l.surewalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by jiwha_000 on 10/18/2016.
 */
public class UserRequestScreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_request_screen);

        Button requestButton = (Button) findViewById(R.id.requestWalkerButton);
    }
}
