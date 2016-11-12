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
    private int viewIndex;

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
        this.viewIndex = -1;
    }

    public void setRequest(Walker walker, Requester requester, String currLoc, String dest){
        this.walker = walker;
        this.requester = requester;
        this.currentLocationRequest = currLoc;
        this.destinationRequest = dest;
        this.status = STATUS.SUBMITTED;
    }

    public void setID(int id){
        this.viewIndex = id;
    }
    public int getID(){
        return this.viewIndex;
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

    public void setStatus(STATUS newStatus){ this.status = newStatus;}




}
