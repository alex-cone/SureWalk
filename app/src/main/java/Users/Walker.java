package Users;

import ee461l.surewalk.FirebaseVariables;

/**
 * Created by Diego on 10/15/2016.
 */
public class Walker {
    public String username;
    public String email;
    public String phoneNumber;
    public String uid;
    public void setWalker(String username, String email, String phoneNumber, String uid) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
    }
    public void deleteRequest(Request currentRequest) {
        FirebaseVariables.getDatabaseReference().child("Requests").child(currentRequest.getFirebaseId()).removeValue();
    }

    public void cancelRequest(Request currentRequest) {
        FirebaseVariables.getDatabaseReference().child("Requests").child(currentRequest.getFirebaseId())
                .removeEventListener(FirebaseVariables.getRequesterEventListener());
        currentRequest.setStatus(Request.STATUS.CANCELED);
        FirebaseVariables.getDatabaseReference().child("Requests").child(currentRequest.getFirebaseId())
                    .setValue(currentRequest);
    }

}
