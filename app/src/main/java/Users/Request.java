package Users;

/**
 * Created by Sharmistha on 11/5/2016.
 */

public class Request {
    String walkerID;
    String requesterID;
    String currentLocationRequest;
    String destinationRequest;
    STATUS status;

    public enum STATUS {
        SUBMITTED,
        ACCEPTED,
        COMPLETED
    }

    public void Request(){
        this.walkerID = null;
        this.requesterID = null;
        this.currentLocationRequest = null;
        this.destinationRequest = null;
    }

    public void setRequest(String wi, String ri, String currLoc, String dest){
        this.walkerID = wi;
        this.requesterID = ri;
        this.currentLocationRequest = currLoc;
        this.destinationRequest = dest;
        this.status = STATUS.SUBMITTED;
    }

    public String getWalkerID(){
        return this.walkerID;
    }

    public String getRequesterID(){
        return this.requesterID;
    }

    public String getCurrentLocationRequest(){
        return this.currentLocationRequest;
    }

    public String getDestinationRequest(){
        return this.destinationRequest;
    }

    public STATUS getStatus(){
        return this.status;
    }




}
