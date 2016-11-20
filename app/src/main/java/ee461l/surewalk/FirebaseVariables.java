package ee461l.surewalk;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Users.Walker;

public class FirebaseVariables {
    private static FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static StorageReference mStorage =FirebaseStorage.getInstance().getReference();
    private static Walker currentWalker;

    public static FirebaseAuth getFireBaseAuth() {
        return firebaseAuth;
    }
    public static DatabaseReference getDatabaseReference() {
        return mDatabase;
    }
    public static StorageReference getStorageReference() {
        return mStorage;
    }
    public static void setCurrentWalker(Walker walker) {currentWalker = walker;}
    public static Walker getCurrentWalker() {return currentWalker;}

    private FirebaseVariables() {
    }
}
