package Users;

import java.util.Observable;

/**
 * Created by Diego on 10/15/2016.
 */
public abstract class SureWalkProfile extends Observable {
    protected String name;
    protected Object profilePicture;
    protected String email;
    public abstract void createTestUser();
    public String getEmail(){
        return this.email;
    }

}
