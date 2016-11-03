package Users;
import java.util.Observer;
import java.util.Observable;
import java.util.List;
/**
 * Created by Diego on 10/15/2016.
 */
public class Walker extends SureWalkProfile implements Observer{
    protected boolean currentlyWorking;
    protected List<Requester> currentRequesters;
    public void createTestUser(){
        this.email = "walker@utexas.edu";
        this.profilePicture = new Object();
        this.name = "testWalker";
    }
    public void update(Observable obs, Object arg){
        if(obs instanceof  Requester){
            Requester requester = (Requester)obs;
            if(requester.isRequesting()){
                currentRequesters.add(requester);
            }
            else{
                currentRequesters.remove(requester);
            }
        }
    }
}
