package ee461l.surewalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import static android.R.attr.onClick;

/**
 * Created by jiwha_000 on 10/18/2016.
 */
public class UserRequestScreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_request_screen);

        final EditText address1 = (EditText) findViewById(R.id.addressLine1);
        final EditText address2 = (EditText) findViewById(R.id.addressLine2);
        final EditText comments = (EditText) findViewById(R.id.comments);

        final Button requestButton = (Button) findViewById(R.id.requestWalkerButton);
        //Button's original state is 1
        requestButton.setTag(1);
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                int state = (Integer) requestButton.getTag();
                //Original state
                if (state == 1) {
                    requestButton.setText("CANCEL");
                    //Disable all text fields so you cant change while requesting
                    disableText(address1);
                    disableText(address2);
                    disableText(comments);
                    v.setTag(0);
                }
                //Else in the requesting state
                else {
                    requestButton.setText("REQUEST WALKER");
                    //Reenable all the text fields
                    enableText(address1, false);
                    enableText(address2, false);
                    enableText(comments, true);
                    v.setTag(1);
                }
            }
        }
        );
    }

    /*
    Will the a EditText field disabled for editing

    @param  textField   The EditText object to disable
     */
    public static void disableText(EditText textField) {
        textField.setInputType(InputType.TYPE_NULL);
        textField.setFocusable(false);
        textField.setEnabled(false);
    }

    /*
    Will make a EditText field enabled for editing

    @param  textField   The EditText object to enable
    @param  multi       True if multi line, False for others
     */
    public static void enableText(EditText textField, boolean multi) {
        if(multi) {
            textField.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
        else {
            textField.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        textField.setFocusable(true);
        textField.setEnabled(true);
    }
}
