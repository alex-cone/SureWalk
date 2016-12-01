package ee461l.surewalk;

import android.support.test.espresso.action.TypeTextAction;
import android.test.ActivityInstrumentationTestCase2;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.junit.Assert.*;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jiwha_000 on 11/30/2016.
 */
public class RequesterRequestScreenTest extends ActivityInstrumentationTestCase2<RequesterRequestScreen> {
    RequesterRequestScreen mActivity;
    private EditText destinationAddress;
    private Button requestButton;

    public RequesterRequestScreenTest() {super("ee461l.surewalk",RequesterRequestScreen.class); }

    public void setUp() {
        mActivity = this.getActivity();
        destinationAddress = (EditText) mActivity.findViewById(R.id.addressLine1);
        requestButton = (Button) mActivity.findViewById(R.id.requestWalkerButton);
        setActivityInitialTouchMode(true);
    }

    //Address should be null before request
    @Test
    public void testPrecondition() throws Exception {
        assertNull(mActivity.getAddress());
    }

    //Curront location should not be null
    @Test
    public void testLocation() throws Exception {
        assertNotNull(mActivity.getLocation());
    }

    //Test getAddress after a request
    @Test
    public void testAddress() throws Exception {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                destinationAddress.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync("Pluckers");
        getInstrumentation().waitForIdleSync();

        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        Thread.sleep(2000);
        TouchUtils.clickView(this, requestButton);
        assertNotNull(mActivity.getAddress());
    }

}