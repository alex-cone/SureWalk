package Users;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sharmistha on 11/5/2016.
 */

public class Request {
    Walker walker;
    Requester requester;
    LatLng currentLocationRequest;
    LatLng destinationRequest;
    STATUS status;
    private int viewIndex;
    private String firebaseId;

    public enum STATUS {
        SUBMITTED,
        ACCEPTED,
        COMPLETED,
        CANCELED
    }

    public void Request(){
        this.walker = null;
        this.requester = null;
        this.currentLocationRequest = null;
        this.destinationRequest = null;
        this.viewIndex = -1;
    }

    public void setRequest(Walker walker, Requester requester, LatLng currLoc, LatLng dest, String firebaseId){
        this.walker = walker;
        this.requester = requester;
        this.currentLocationRequest = currLoc;
        this.destinationRequest = dest;
        this.status = STATUS.SUBMITTED;
        this.firebaseId = firebaseId;
    }

    public void setIndex(int id){
        this.viewIndex = id;
    }
    public int getIndex(){
        return this.viewIndex;
    }
    public String getFirebaseId(){return this.firebaseId; }

    public Walker getWalker(){
        return this.walker;
    }

    public Requester getRequester(){
        return this.requester;
    }

    public LatLng getCurrentLocationRequest(){
        return this.currentLocationRequest;
    }

    public LatLng getDestinationRequest(){
        return this.destinationRequest;
    }

    public STATUS getStatus(){
        return this.status;
    }

    public void setStatus(STATUS newStatus){ this.status = newStatus;}




}
