package ee461l.surewalk;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseVariables {
    private static FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static StorageReference mStorage =FirebaseStorage.getInstance().getReference();

    public static FirebaseAuth getFireBaseAuth() {
        return firebaseAuth;
    }
    public static DatabaseReference getDatabaseReference() {
        return mDatabase;
    }
    public static StorageReference getStorageReference() {
        return mStorage;
    }

    private FirebaseVariables() {
    }
}
