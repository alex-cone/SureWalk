package Users;

import java.util.List;

/**
 * Created by Diego on 10/15/2016.
 */
public class Requester extends  SureWalkProfile{
    protected boolean currentlyRequesting;
    public void createTestUser(){
        this.email = "Requester@utexas.edu";
        this.profilePicture = new Object();
        this.name = "testRequester";
        currentlyRequesting = false;
    }
    public boolean isRequesting(){
        return this.currentlyRequesting;
    }
    public void stateChanged(){
        setChanged();
        notifyObservers();
    }
    public void setRequest(boolean state){
        currentlyRequesting = state;
        stateChanged();
    }
}
