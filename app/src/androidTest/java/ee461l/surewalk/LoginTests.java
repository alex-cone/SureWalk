package ee461l.surewalk;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
public class LoginTests extends ActivityInstrumentationTestCase2<LoginActivity>{
    LoginActivity mActivity;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button loginButton;
    @SuppressWarnings("deprecation")
    public LoginTests()
    {
        super("ee461l.surewalk",LoginActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FirebaseVariables.getFireBaseAuth().signOut();
        mActivity = this.getActivity();
        inputEmail = (EditText) mActivity.findViewById(R.id.email);
        inputPassword = (EditText) mActivity.findViewById(R.id.password);
        loginButton = (Button) mActivity.findViewById(R.id.btnLogin);
    }
    public void testPreconditions() {
        assertNotNull(inputEmail);
        assertNotNull(inputPassword);
    }
    @Test
    public void testWalkerLogin() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(WalkerHomeScreen.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    inputPassword.requestFocus();
                }
            });
            getInstrumentation().waitForIdleSync();
            getInstrumentation().sendStringSync("hello123");
            getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputEmail.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("demoWalker@utexas.edu");
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, loginButton);
        assertNotNull(getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000));
        assertNotNull(FirebaseVariables.getFireBaseAuth().getCurrentUser());

    }
    public void testRequesterLogin() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(RequesterHomeScreen.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("hello123");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputEmail.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("demoRequester@utexas.edu");
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, loginButton);
        assertNotNull(getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000));
        assertNotNull(FirebaseVariables.getFireBaseAuth().getCurrentUser());

    }
    public void testFailLogin() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputPassword.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("");
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                inputEmail.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("");
        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, loginButton);
        assertNull(getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000));
        assertNull(FirebaseVariables.getFireBaseAuth().getCurrentUser());

    }
}