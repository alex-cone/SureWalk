package Users;

/**
 * Created by Diego on 10/15/2016.
 */
public abstract class SureWalkProfile {
    protected String username;
    protected Object profilePicture;
    protected String email;
    protected String phoneNumber;

   public SureWalkProfile(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public String getEmail(){return this.email;}
    public String getName() { return this.username;}
    public String getphoneNumber(){return this.phoneNumber;}

}
