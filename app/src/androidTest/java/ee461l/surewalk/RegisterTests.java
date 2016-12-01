package ee461l.surewalk;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


/**
 * Created by Sharmistha on 11/30/2016.
 */

public class RegisterTests extends ActivityInstrumentationTestCase2<RegisterActivity>{

    RegisterActivity mActivity;
    private Button btnRegister;
    private Button btnLinkToLogin;
    private ImageButton profilePicture;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText confirmPassword;
    private EditText inputPhoneNumber;

    @SuppressWarnings("deprecation")
    public RegisterTests()
    {
        super("ee461l.surewalk",RegisterActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FirebaseVariables.getFireBaseAuth().signOut();
        mActivity = this.getActivity();

        inputFullName = (EditText) mActivity.findViewById(R.id.reg_fullname);
        inputEmail = (EditText) mActivity.findViewById(R.id.reg_email);
        inputPassword = (EditText) mActivity.findViewById(R.id.reg_password);
        confirmPassword = (EditText) mActivity.findViewById(R.id.confirm_password);
        inputPhoneNumber =  (EditText) mActivity.findViewById(R.id.editTextId);
        btnRegister = (Button) mActivity.findViewById(R.id.btnRegister);
    }
    public void testPreconditions() {
        assertNotNull(inputEmail);
        assertNotNull(inputPassword);
    }
    @Test
    public void testText() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputFullName.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("Bob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputEmail.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("bob@utexas.edu");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("thisisBob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                confirmPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("thisisBob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPhoneNumber.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("5555555555");
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, btnRegister);
        assertNull(FirebaseVariables.getFireBaseAuth().getCurrentUser());
    }

    @Test
    public void testFalseEmail() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputFullName.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("Bob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputEmail.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("bob@utxas.edu");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("thisisBob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                confirmPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("thisisBob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPhoneNumber.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("5555555555");
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, btnRegister);
        assertNull(FirebaseVariables.getFireBaseAuth().getCurrentUser());
    }

    @Test
    public void testIncorrectPassword() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputFullName.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("Bob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputEmail.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("bob@utxas.edu");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("thisisBob");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                confirmPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("blah");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPhoneNumber.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("5555555555");
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, btnRegister);
        assertNull(FirebaseVariables.getFireBaseAuth().getCurrentUser());
    }




}
