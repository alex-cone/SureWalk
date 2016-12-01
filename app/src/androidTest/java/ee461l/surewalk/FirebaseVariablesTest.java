package ee461l.surewalk;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import Users.Requester;
import Users.Walker;

import static org.junit.Assert.*;


/**
 * Created by jiwha_000 on 11/30/2016.
 */
public class FirebaseVariablesTest {
    @Test
    public void testFireBaseAuth() throws Exception {
        assertTrue(FirebaseVariables.getFireBaseAuth() != null);
    }

    @Test
    public void testDatabaseReference() throws Exception {
        assertTrue(FirebaseVariables.getDatabaseReference() != null);
    }

    @Test
    public void testStorageReference() throws Exception {
        assertTrue(FirebaseVariables.getStorageReference() != null);
    }

    @Test
    public void testCurrentWalker() throws Exception {
        Walker testWalker = new Walker();
        testWalker.setWalker("Bob", "bob@utexas.edu", "5555555555", "bb777");
        FirebaseVariables.setCurrentWalker(testWalker);
        Walker retrievedWalker = FirebaseVariables.getCurrentWalker();
        assertTrue(testWalker.equals(retrievedWalker));
    }

    @Test
    public void testCurrentRequester() throws Exception {
        Requester testRequester = new Requester();
        testRequester.setRequester("Bob", "bob@utexas.edu", "5555555555", "bb777");
        FirebaseVariables.setCurrentRequester(testRequester);
        Requester retrievedRequester = FirebaseVariables.getCurrentRequester();
        assertTrue(testRequester.equals(retrievedRequester));
    }
}