package Users;

/**
 * Created by Sharmistha on 11/5/2016.
 */

public class Request {
    Walker walker;
    Requester requester;
    String currentLocationRequest;
    String destinationRequest;
    STATUS status;

    public enum STATUS {
        SUBMITTED,
        ACCEPTED,
        COMPLETED
    }

    public void Request(){
        this.walker = null;
        this.requester = null;
        this.currentLocationRequest = null;
        this.destinationRequest = null;
    }

    public void setRequest(Walker walker, Requester requester, String currLoc, String dest){
        this.walker = walker;
        this.requester = requester;
        this.currentLocationRequest = currLoc;
        this.destinationRequest = dest;
        this.status = STATUS.SUBMITTED;
    }

    public Walker getWalker(){
        return this.walker;
    }

    public Requester getRequester(){
        return this.requester;
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
