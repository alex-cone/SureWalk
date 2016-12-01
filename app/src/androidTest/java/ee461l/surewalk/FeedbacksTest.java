package ee461l.surewalk;

import org.junit.Before;
import org.junit.Test;

import Users.Request;
import Users.Requester;
import Users.Walker;

import static org.junit.Assert.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by alexc on 11/30/2016.
 */

public class FeedbacksTest{
    public FeedbacksTest(){
       // super("ee461l.surewalk", FeedbackActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        if (Looper.myLooper()==null)
            Looper.prepare();
    }
    //Tests that the email sent contains the feedback entered
    @Test
    public void testFeedbackIsSent(){
        FeedbackActivity test = new FeedbackActivity();
        Walker newWalker = new Walker();
        newWalker.setWalker("Alex", "alexcone@utexas.edu", "8325555555", "aec2975");
        Requester newRequester = new Requester();
        newRequester.setRequester("Joe", "joebob@utexas.edu", "8321111111", "jda1234");
        Request newRequest = new Request();
        newRequest.setWalker(newWalker);
        newRequest.setRequester(newRequester);
        String feedback = "We are testing the function of sending a feedback email";
        String fromEmail = "utexassurewalk@gmail.com";
        String fromPassword = "EE461LIsFun";
        FeedbackActivity.sendFeedback(fromEmail, fromPassword, newRequest, feedback, test);
        assertSame(feedback, FeedbackActivity.feedbackMsg);
    }
    //Tests that the Request is removed after feedback is sent
    @Test
    public void testRequestIsNull(){
        FeedbackActivity test = new FeedbackActivity();
        Walker newWalker = new Walker();
        newWalker.setWalker("Alex", "alexcone@utexas.edu", "8325555555", "aec2975");
        Requester newRequester = new Requester();
        newRequester.setRequester("Joe", "joebob@utexas.edu", "8321111111", "jda1234");
        Request newRequest = new Request();
        newRequest.setWalker(newWalker);
        newRequest.setRequester(newRequester);
        String feedback = "We are testing the function of removing a request";
        String fromEmail = "utexassurewalk@gmail.com";
        String fromPassword = "EE461LIsFun";
        FeedbackActivity.sendFeedback(fromEmail, fromPassword, newRequest, feedback, test);
        assertNull(FirebaseVariables.getCurrentRequester());
    }
}
