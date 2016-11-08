package Users;

/**
 * Created by Diego on 10/15/2016.
 */
public abstract class SureWalkProfile {
    protected String name;
    protected Object profilePicture;
    protected String email;

    public abstract void createTestUser();
    public String getEmail(){
        return this.email;
    }
    public String getName() { return this.name;}
    public void setName(String name) { this.name = name;}

}
