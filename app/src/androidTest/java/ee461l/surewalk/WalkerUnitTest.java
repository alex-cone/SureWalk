package ee461l.surewalk;
import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.SingleLaunchActivityTestCase;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import Users.Request;
import Users.Requester;
import Users.Walker;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WalkerUnitTest extends SingleLaunchActivityTestCase<LoginActivity>{
    LoginActivity mActivity;
    Walker walker;
    Requester requester;
    Request request;
    Request storedRequest;
    @SuppressWarnings("deprecation")
    public WalkerUnitTest()
    {
        super("ee461l.surewalk",LoginActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
        walker = new Walker();
        requester = new Requester();
        requester.setRequester("test", "test@gmail.com", "5555555555", "");
    }
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(storedRequest != null)
            walker.deleteRequest(storedRequest);
    }
    public void testPreconditions() {
        assertNotNull(walker);
        assertNotNull(requester);
    }
    @Test
    public void testDeleteRequest() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                request = requester.newRequest(0,0,0,0,"test");
                getValue(request, Request.STATUS.SUBMITTED);

                walker.deleteRequest(request);
                getValue(request, null);
            }
        });
    }
    @Test
    public void testCancelRequest() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                request = requester.newRequest(0,0,0,0,"test");
                getValue(request, Request.STATUS.SUBMITTED);

                walker.cancelRequest(request);
                getValue(request, Request.STATUS.CANCELED);
            }
        });
    }

    private void getValue(Request request, final Request.STATUS status){
        FirebaseVariables.getDatabaseReference().child("Requests").child(request.getFirebaseId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        storedRequest =  dataSnapshot.getValue(Request.class);
                        if(status == null){
                            assertNull(storedRequest);
                        }
                        else if(status == Request.STATUS.SUBMITTED){
                            assertEquals(storedRequest.getStatus(), Request.STATUS.SUBMITTED);
                        }
                        else if(status == Request.STATUS.CANCELED){
                            assertEquals(storedRequest.getStatus(), Request.STATUS.CANCELED);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

}
